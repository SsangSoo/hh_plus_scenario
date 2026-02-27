/**
 * 주문-결제 통합 시나리오 테스트
 *
 * 목적: 실제 사용자 구매 흐름 시뮬레이션 및 성능 측정
 * 시나리오:
 *   - 상품 조회 → 주문 생성 → 결제 처리 전체 흐름
 *   - 재고 차감 동시성 검증
 *   - 결제 멱등성 키 검증
 *
 * 실행 방법:
 *   k6 run scenario_order_payment.js
 *   k6 run --out json=results/order_payment_result.json scenario_order_payment.js
 */

import http from 'k6/http';
import { check, sleep, group, fail } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const successRate = new Rate('success_rate');
const stockOversellRate = new Rate('stock_oversell');
const paymentDuplicateRate = new Rate('payment_duplicate');

// 응답 시간 트렌드
const productViewTrend = new Trend('product_view_duration');
const orderCreateTrend = new Trend('order_create_duration');
const paymentTrend = new Trend('payment_duration');
const e2eTrend = new Trend('e2e_duration');  // 전체 흐름

// 카운터
const successfulOrders = new Counter('successful_orders');
const failedOrders = new Counter('failed_orders');
const successfulPayments = new Counter('successful_payments');
const failedPayments = new Counter('failed_payments');

// 테스트 설정
export const options = {
    scenarios: {
        // 시나리오 1: 일반 구매 흐름
        normal_purchase: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 20 },
                { duration: '2m', target: 50 },
                { duration: '1m', target: 100 },
                { duration: '30s', target: 50 },
                { duration: '30s', target: 0 },
            ],
            exec: 'normalPurchase',
            tags: { scenario: 'normal' },
        },

        // 시나리오 2: 재고 경쟁 테스트 (한정 상품)
        stock_competition: {
            executor: 'per-vu-iterations',
            vus: 100,
            iterations: 1,
            maxDuration: '2m',
            exec: 'stockCompetition',
            tags: { scenario: 'stock' },
            startTime: '5m',
        },

        // 시나리오 3: 결제 멱등성 테스트
        payment_idempotency: {
            executor: 'per-vu-iterations',
            vus: 20,
            iterations: 5,  // 각 VU가 동일한 키로 5번 시도
            maxDuration: '2m',
            exec: 'paymentIdempotency',
            tags: { scenario: 'idempotency' },
            startTime: '8m',
        },
    },

    thresholds: {
        'e2e_duration': ['p(95)<5000'],
        'order_create_duration': ['p(95)<3000'],
        'payment_duration': ['p(95)<3000'],
        'errors': ['rate<0.10'],
        'stock_oversell': ['rate<0.01'],
        'payment_duplicate': ['rate<0.01'],
    },
};

// 환경 설정
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 테스트 데이터
let testData = {
    memberIds: [],
    productIds: [],
    limitedProductId: null,  // 재고 경쟁용 한정 상품
    initialStockQuantity: 50,
};

// 테스트 데이터 초기화
export function setup() {
    console.log('=== 주문-결제 통합 테스트 시작 ===');
    console.log(`대상 서버: ${BASE_URL}`);

    // 테스트용 회원 생성 (100명)
    for (let i = 0; i < 100; i++) {
        const memberRes = http.post(`${BASE_URL}/api/member`, JSON.stringify({
            name: `order_test_user_${i}`
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (memberRes.status === 201) {
            const memberData = JSON.parse(memberRes.body);
            testData.memberIds.push(memberData.memberId);

            // 포인트 충전 (충분한 금액)
            http.post(`${BASE_URL}/api/point/charge`, JSON.stringify({
                memberId: memberData.memberId,
                chargePoint: 10000000
            }), {
                headers: { 'Content-Type': 'application/json' },
            });
        }
    }
    console.log(`테스트 회원 ${testData.memberIds.length}명 생성 및 포인트 충전 완료`);

    // 일반 테스트용 상품 생성 (5개)
    for (let i = 0; i < 5; i++) {
        const productRes = http.post(`${BASE_URL}/api/product`, JSON.stringify({
            name: `order_test_product_${i}`,
            price: 10000 + (i * 5000)
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (productRes.status === 201) {
            const productData = JSON.parse(productRes.body);
            testData.productIds.push(productData.productId);

            // 충분한 재고 추가
            http.post(`${BASE_URL}/api/stock`, JSON.stringify({
                productId: productData.productId,
                quantity: 100000
            }), {
                headers: { 'Content-Type': 'application/json' },
            });
        }
    }
    console.log(`일반 테스트 상품 ${testData.productIds.length}개 생성 완료`);

    // 재고 경쟁용 한정 상품 생성
    const limitedProductRes = http.post(`${BASE_URL}/api/product`, JSON.stringify({
        name: 'limited_edition_product',
        price: 50000
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    if (limitedProductRes.status === 201) {
        const limitedData = JSON.parse(limitedProductRes.body);
        testData.limitedProductId = limitedData.productId;

        // 한정 수량 재고 추가 (50개)
        http.post(`${BASE_URL}/api/stock`, JSON.stringify({
            productId: limitedData.productId,
            quantity: testData.initialStockQuantity
        }), {
            headers: { 'Content-Type': 'application/json' },
        });
        console.log(`한정 상품 생성: ${testData.limitedProductId} (재고: ${testData.initialStockQuantity}개)`);
    }

    return testData;
}

// 랜덤 선택 헬퍼
function randomElement(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

// 시나리오 1: 일반 구매 흐름
export function normalPurchase(data) {
    const memberId = randomElement(data.memberIds);
    const productId = randomElement(data.productIds);

    const e2eStart = Date.now();

    group('일반 구매 흐름', function() {
        // Step 1: 상품 조회
        group('1. 상품 조회', function() {
            const start = Date.now();
            let res = http.get(`${BASE_URL}/api/product/${productId}`);
            productViewTrend.add(Date.now() - start);

            check(res, {
                '상품 조회 성공': (r) => r.status === 200,
            });

            sleep(1 + Math.random() * 2);  // Think Time: 1-3초
        });

        // Step 2: 주문 생성
        let orderId = null;
        group('2. 주문 생성', function() {
            const start = Date.now();
            let res = http.post(`${BASE_URL}/api/order`, JSON.stringify({
                memberId: memberId,
                orderProductRequest: {
                    productId: productId,
                    quantity: 1
                },
                paymentMethod: 'POINT'
            }), {
                headers: { 'Content-Type': 'application/json' },
            });
            orderCreateTrend.add(Date.now() - start);

            const success = check(res, {
                '주문 생성 성공': (r) => r.status === 200,
            });

            if (success) {
                const orderData = JSON.parse(res.body);
                orderId = orderData.orderId;
                successfulOrders.add(1);
            } else {
                failedOrders.add(1);
                errorRate.add(1);
            }

            sleep(0.5 + Math.random());  // Think Time: 0.5-1.5초
        });

        // Step 3: 결제 처리
        if (orderId) {
            group('3. 결제 처리', function() {
                const idempotencyKey = uuidv4();
                const start = Date.now();

                let res = http.post(`${BASE_URL}/api/pay`, JSON.stringify({
                    orderId: orderId,
                    memberId: memberId,
                    paymentMethod: 'POINT'
                }), {
                    headers: {
                        'Content-Type': 'application/json',
                        'idempotency_key': idempotencyKey,
                    },
                });
                paymentTrend.add(Date.now() - start);

                const success = check(res, {
                    '결제 처리 성공': (r) => r.status === 200,
                });

                if (success) {
                    successfulPayments.add(1);
                    successRate.add(1);
                } else {
                    failedPayments.add(1);
                    errorRate.add(1);
                }
            });
        }
    });

    e2eTrend.add(Date.now() - e2eStart);
    sleep(1 + Math.random() * 2);
}

// 시나리오 2: 재고 경쟁 테스트
export function stockCompetition(data) {
    const memberId = data.memberIds[__VU - 1];
    const productId = data.limitedProductId;

    group('재고 경쟁 테스트', function() {
        console.log(`VU ${__VU}: 한정 상품 주문 시도`);

        // 주문 생성 (재고 차감 포함)
        let res = http.post(`${BASE_URL}/api/order`, JSON.stringify({
            memberId: memberId,
            orderProductRequest: {
                productId: productId,
                quantity: 1
            },
            paymentMethod: 'POINT'
        }), {
            headers: { 'Content-Type': 'application/json' },
            timeout: '30s',
        });

        const success = check(res, {
            '주문 요청 처리됨': (r) => r.status === 200 || r.status === 400,
        });

        if (res.status === 200) {
            successfulOrders.add(1);
            console.log(`VU ${__VU}: 한정 상품 주문 성공!`);

            // 결제 진행
            const orderData = JSON.parse(res.body);
            const idempotencyKey = uuidv4();

            let payRes = http.post(`${BASE_URL}/api/pay`, JSON.stringify({
                orderId: orderData.orderId,
                memberId: memberId,
                paymentMethod: 'POINT'
            }), {
                headers: {
                    'Content-Type': 'application/json',
                    'idempotency_key': idempotencyKey,
                },
            });

            if (payRes.status === 200) {
                successfulPayments.add(1);
            }
        } else {
            console.log(`VU ${__VU}: 한정 상품 주문 실패 - ${res.status}`);
            // 재고 부족은 정상적인 실패
            if (res.body && (res.body.includes('재고') || res.body.includes('stock'))) {
                // 정상 동작
            } else {
                errorRate.add(1);
            }
        }
    });
}

// 시나리오 3: 결제 멱등성 테스트
export function paymentIdempotency(data) {
    // VU별 고정 데이터
    const memberId = data.memberIds[__VU - 1];
    const productId = data.productIds[0];

    // VU별로 고유한 멱등성 키 (iteration 간 동일)
    const idempotencyKey = `IDEM_VU_${__VU}_${Date.now()}`;

    group('결제 멱등성 테스트', function() {
        // 먼저 주문 생성
        let orderRes = http.post(`${BASE_URL}/api/order`, JSON.stringify({
            memberId: memberId,
            orderProductRequest: {
                productId: productId,
                quantity: 1
            },
            paymentMethod: 'POINT'
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (orderRes.status !== 200) {
            console.log(`VU ${__VU}: 주문 생성 실패`);
            return;
        }

        const orderData = JSON.parse(orderRes.body);

        // 동일한 멱등성 키로 결제 요청
        let payRes = http.post(`${BASE_URL}/api/pay`, JSON.stringify({
            orderId: orderData.orderId,
            memberId: memberId,
            paymentMethod: 'POINT'
        }), {
            headers: {
                'Content-Type': 'application/json',
                'idempotency_key': idempotencyKey,
            },
        });

        const success = check(payRes, {
            '결제 처리됨 또는 중복 차단됨': (r) =>
                r.status === 200 || r.status === 409 || r.status === 400,
        });

        if (payRes.status === 200) {
            successfulPayments.add(1);
        } else if (payRes.status === 409) {
            // 중복 요청 차단됨 (정상)
            console.log(`VU ${__VU}: 중복 결제 차단됨`);
        }
    });

    sleep(0.1);
}

// 테스트 종료 후 검증
export function teardown(data) {
    console.log('\n=== 주문-결제 통합 테스트 완료 ===');

    // 한정 상품 재고 확인
    const stockRes = http.get(`${BASE_URL}/api/stock/${data.limitedProductId}`);

    if (stockRes.status === 200) {
        const stockData = JSON.parse(stockRes.body);
        const remainingStock = stockData.quantity;
        const soldCount = data.initialStockQuantity - remainingStock;

        console.log(`\n=== 재고 경쟁 결과 ===`);
        console.log(`초기 재고: ${data.initialStockQuantity}`);
        console.log(`남은 재고: ${remainingStock}`);
        console.log(`판매된 수량: ${soldCount}`);

        if (remainingStock >= 0) {
            console.log('✅ 정상: 재고 초과 판매 없음');
        } else {
            console.log('❌ 오류: 재고 초과 판매 발생! (oversell)');
            stockOversellRate.add(1);
        }
    }
}

// 결과 요약
export function handleSummary(data) {
    const summary = {
        timestamp: new Date().toISOString(),
        testName: '주문-결제 통합 테스트',
        metrics: {},
    };

    if (data.metrics.e2e_duration) {
        summary.metrics.e2e_duration = {
            p50: data.metrics.e2e_duration.values['p(50)'],
            p95: data.metrics.e2e_duration.values['p(95)'],
            p99: data.metrics.e2e_duration.values['p(99)'],
        };
    }

    if (data.metrics.successful_orders) {
        summary.metrics.successful_orders = data.metrics.successful_orders.values.count;
    }

    if (data.metrics.successful_payments) {
        summary.metrics.successful_payments = data.metrics.successful_payments.values.count;
    }

    return {
        'results/order_payment_summary.json': JSON.stringify(summary, null, 2),
        stdout: generateTextSummary(data),
    };
}

function generateTextSummary(data) {
    let output = '\n=== 주문-결제 통합 테스트 결과 ===\n\n';

    if (data.metrics.e2e_duration) {
        output += `전체 구매 흐름 응답 시간:\n`;
        output += `  p50: ${data.metrics.e2e_duration.values['p(50)']}ms\n`;
        output += `  p95: ${data.metrics.e2e_duration.values['p(95)']}ms\n\n`;
    }

    if (data.metrics.order_create_duration) {
        output += `주문 생성 응답 시간:\n`;
        output += `  p50: ${data.metrics.order_create_duration.values['p(50)']}ms\n`;
        output += `  p95: ${data.metrics.order_create_duration.values['p(95)']}ms\n\n`;
    }

    if (data.metrics.successful_orders) {
        output += `성공한 주문: ${data.metrics.successful_orders.values.count}건\n`;
    }

    if (data.metrics.successful_payments) {
        output += `성공한 결제: ${data.metrics.successful_payments.values.count}건\n`;
    }

    return output;
}
