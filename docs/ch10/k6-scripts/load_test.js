/**
 * Load Test - 일반 부하 테스트
 *
 * 목적: 예상되는 일반적인 트래픽 상황에서 시스템 성능 측정
 * VUs: 점진적 증가 (10 -> 50 -> 100 -> 50 -> 10)
 * 지속시간: 5분
 *
 * 실행 방법:
 *   k6 run load_test.js
 *   k6 run --out json=results/load_result.json load_test.js
 */

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const successRate = new Rate('success_rate');
const pointChargeCounter = new Counter('point_charge_total');
const orderCounter = new Counter('order_total');

// 응답 시간 트렌드
const pointChargeTrend = new Trend('point_charge_duration');
const productGetTrend = new Trend('product_get_duration');
const stockGetTrend = new Trend('stock_get_duration');
const popularGetTrend = new Trend('popular_get_duration');

// 테스트 설정
export const options = {
    // 단계별 부하 증가
    stages: [
        { duration: '30s', target: 10 },   // 워밍업: 10 VUs
        { duration: '1m', target: 50 },    // 일반 부하: 50 VUs
        { duration: '2m', target: 100 },   // 피크 부하: 100 VUs
        { duration: '1m', target: 50 },    // 부하 감소: 50 VUs
        { duration: '30s', target: 10 },   // 쿨다운: 10 VUs
    ],

    // 임계값 설정
    thresholds: {
        http_req_duration: ['p(50)<500', 'p(95)<2000', 'p(99)<3000'],
        http_req_failed: ['rate<0.05'],      // 에러율 5% 미만
        errors: ['rate<0.05'],
        success_rate: ['rate>0.95'],         // 성공률 95% 이상
        point_charge_duration: ['p(95)<2000'],
        product_get_duration: ['p(95)<500'],
    },
};

// 환경 설정
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 공유 테스트 데이터
const testData = {
    memberIds: [],
    productIds: [],
};

// 테스트 데이터 초기화
export function setup() {
    console.log('=== Load Test 시작 ===');
    console.log(`대상 서버: ${BASE_URL}`);

    // 테스트용 회원 10명 생성
    for (let i = 0; i < 10; i++) {
        const memberRes = http.post(`${BASE_URL}/api/member`, JSON.stringify({
            name: `load_test_user_${i}`
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (memberRes.status === 201) {
            const memberData = JSON.parse(memberRes.body);
            testData.memberIds.push(memberData.memberId);
        }
    }
    console.log(`테스트 회원 ${testData.memberIds.length}명 생성 완료`);

    // 테스트용 상품 5개 생성
    for (let i = 0; i < 5; i++) {
        const productRes = http.post(`${BASE_URL}/api/product`, JSON.stringify({
            name: `load_test_product_${i}`,
            price: 10000 + (i * 1000)
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (productRes.status === 201) {
            const productData = JSON.parse(productRes.body);
            testData.productIds.push(productData.productId);

            // 재고 추가
            http.post(`${BASE_URL}/api/stock`, JSON.stringify({
                productId: productData.productId,
                quantity: 10000
            }), {
                headers: { 'Content-Type': 'application/json' },
            });
        }
    }
    console.log(`테스트 상품 ${testData.productIds.length}개 생성 완료`);

    // 초기 포인트 충전
    testData.memberIds.forEach(memberId => {
        http.post(`${BASE_URL}/api/point/charge`, JSON.stringify({
            memberId: memberId,
            chargePoint: 1000000
        }), {
            headers: { 'Content-Type': 'application/json' },
        });
    });
    console.log('초기 포인트 충전 완료');

    return testData;
}

// 랜덤 선택 헬퍼 함수
function randomElement(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

// 메인 테스트 시나리오
export default function(data) {
    const memberId = randomElement(data.memberIds) || 1;
    const productId = randomElement(data.productIds) || 1;

    // 시나리오별 가중치 적용 (실제 사용 패턴 시뮬레이션)
    const scenario = Math.random();

    if (scenario < 0.4) {
        // 40%: 상품 조회 (가장 빈번)
        group('상품 조회', function() {
            const start = Date.now();
            let res = http.get(`${BASE_URL}/api/product/${productId}`);
            productGetTrend.add(Date.now() - start);

            const success = check(res, {
                '상품 조회 성공': (r) => r.status === 200,
                '응답 시간 500ms 이내': (r) => r.timings.duration < 500,
            });

            success ? successRate.add(1) : errorRate.add(1);
        });

    } else if (scenario < 0.6) {
        // 20%: 인기 상품 조회
        group('인기 상품 조회', function() {
            const start = Date.now();
            let res = http.get(`${BASE_URL}/api/product/popular`);
            popularGetTrend.add(Date.now() - start);

            const success = check(res, {
                '인기 상품 조회 성공': (r) => r.status === 200,
            });

            success ? successRate.add(1) : errorRate.add(1);
        });

    } else if (scenario < 0.75) {
        // 15%: 재고 조회
        group('재고 조회', function() {
            const start = Date.now();
            let res = http.get(`${BASE_URL}/api/stock/${productId}`);
            stockGetTrend.add(Date.now() - start);

            const success = check(res, {
                '재고 조회 성공': (r) => r.status === 200,
            });

            success ? successRate.add(1) : errorRate.add(1);
        });

    } else if (scenario < 0.85) {
        // 10%: 포인트 조회
        group('포인트 조회', function() {
            let res = http.get(`${BASE_URL}/api/point/${memberId}`);

            const success = check(res, {
                '포인트 조회 성공': (r) => r.status === 200,
            });

            success ? successRate.add(1) : errorRate.add(1);
        });

    } else if (scenario < 0.95) {
        // 10%: 포인트 충전
        group('포인트 충전', function() {
            const start = Date.now();
            let res = http.post(`${BASE_URL}/api/point/charge`, JSON.stringify({
                memberId: memberId,
                chargePoint: 1000
            }), {
                headers: { 'Content-Type': 'application/json' },
            });
            pointChargeTrend.add(Date.now() - start);
            pointChargeCounter.add(1);

            const success = check(res, {
                '포인트 충전 성공': (r) => r.status === 200,
                '충전 응답 시간 2초 이내': (r) => r.timings.duration < 2000,
            });

            success ? successRate.add(1) : errorRate.add(1);
        });

    } else {
        // 5%: 주문 생성 (가장 복잡한 트랜잭션)
        group('주문 생성', function() {
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
            orderCounter.add(1);

            const success = check(res, {
                '주문 생성 성공': (r) => r.status === 200,
            });

            success ? successRate.add(1) : errorRate.add(1);
        });
    }

    // Think Time: 0.5 ~ 2초 (실제 사용자 행동 시뮬레이션)
    sleep(0.5 + Math.random() * 1.5);
}

// 테스트 종료 후 요약
export function teardown(data) {
    console.log('=== Load Test 완료 ===');
    console.log(`테스트 회원 수: ${data.memberIds.length}`);
    console.log(`테스트 상품 수: ${data.productIds.length}`);
}
