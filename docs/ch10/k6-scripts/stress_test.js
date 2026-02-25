/**
 * Stress Test - 한계점 탐색
 *
 * 목적: 시스템의 한계점(Breaking Point)을 찾고 장애 발생 패턴 파악
 * VUs: 점진적 증가 (50 -> 100 -> 200 -> 300 -> 400 -> 500)
 * 지속시간: 10분
 *
 * 실행 방법:
 *   k6 run stress_test.js
 *   k6 run --out json=results/stress_result.json stress_test.js
 */

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter, Gauge } from 'k6/metrics';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const successRate = new Rate('success_rate');
const timeoutRate = new Rate('timeout_rate');

// 응답 시간 트렌드
const pointChargeTrend = new Trend('point_charge_duration');
const productGetTrend = new Trend('product_get_duration');
const orderTrend = new Trend('order_duration');

// 카운터
const totalRequests = new Counter('total_requests');
const failedRequests = new Counter('failed_requests');

// 현재 VU 게이지
const activeVUs = new Gauge('active_vus');

// 테스트 설정
export const options = {
    // 극한 부하까지 점진적 증가
    stages: [
        { duration: '30s', target: 50 },   // 워밍업
        { duration: '1m', target: 100 },   // 기본 부하
        { duration: '1m', target: 200 },   // 증가
        { duration: '1m', target: 300 },   // 고부하
        { duration: '2m', target: 400 },   // 스트레스 시작
        { duration: '2m', target: 500 },   // 최대 부하
        { duration: '1m', target: 300 },   // 회복 단계
        { duration: '1m', target: 100 },   // 안정화
        { duration: '30s', target: 0 },    // 종료
    ],

    // 임계값 (스트레스 테스트용 - 완화된 기준)
    thresholds: {
        http_req_duration: ['p(50)<1000', 'p(95)<5000', 'p(99)<10000'],
        http_req_failed: ['rate<0.20'],     // 에러율 20% 미만
        errors: ['rate<0.20'],
        timeout_rate: ['rate<0.10'],        // 타임아웃 10% 미만
    },

    // 타임아웃 설정
    httpTimeoutOverride: {
        response: '30s',
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
    console.log('=== Stress Test 시작 ===');
    console.log(`대상 서버: ${BASE_URL}`);
    console.log('WARNING: 이 테스트는 시스템에 극한 부하를 발생시킵니다.');

    // 테스트용 회원 50명 생성 (스트레스 테스트용 충분한 데이터)
    for (let i = 0; i < 50; i++) {
        const memberRes = http.post(`${BASE_URL}/api/member`, JSON.stringify({
            name: `stress_test_user_${i}`
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (memberRes.status === 201) {
            const memberData = JSON.parse(memberRes.body);
            testData.memberIds.push(memberData.memberId);
        }
    }
    console.log(`테스트 회원 ${testData.memberIds.length}명 생성 완료`);

    // 테스트용 상품 10개 생성
    for (let i = 0; i < 10; i++) {
        const productRes = http.post(`${BASE_URL}/api/product`, JSON.stringify({
            name: `stress_test_product_${i}`,
            price: 10000 + (i * 1000)
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
    console.log(`테스트 상품 ${testData.productIds.length}개 생성 완료`);

    // 초기 포인트 대량 충전
    testData.memberIds.forEach(memberId => {
        http.post(`${BASE_URL}/api/point/charge`, JSON.stringify({
            memberId: memberId,
            chargePoint: 10000000  // 1000만 포인트
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

    activeVUs.add(__VU);
    totalRequests.add(1);

    // 시나리오별 가중치 (쓰기 작업 비중 증가)
    const scenario = Math.random();

    if (scenario < 0.25) {
        // 25%: 상품 조회
        group('상품 조회 (스트레스)', function() {
            const start = Date.now();
            let res = http.get(`${BASE_URL}/api/product/${productId}`, {
                timeout: '10s',
            });
            productGetTrend.add(Date.now() - start);

            const success = check(res, {
                '상품 조회 성공': (r) => r.status === 200,
            });

            if (!success) {
                errorRate.add(1);
                failedRequests.add(1);
                if (res.timings.duration >= 10000) {
                    timeoutRate.add(1);
                }
            } else {
                successRate.add(1);
            }
        });

    } else if (scenario < 0.40) {
        // 15%: 인기 상품 조회
        group('인기 상품 조회 (스트레스)', function() {
            let res = http.get(`${BASE_URL}/api/product/popular`, {
                timeout: '10s',
            });

            const success = check(res, {
                '인기 상품 조회 성공': (r) => r.status === 200,
            });

            if (!success) {
                errorRate.add(1);
                failedRequests.add(1);
            } else {
                successRate.add(1);
            }
        });

    } else if (scenario < 0.55) {
        // 15%: 재고 조회
        group('재고 조회 (스트레스)', function() {
            let res = http.get(`${BASE_URL}/api/stock/${productId}`, {
                timeout: '10s',
            });

            const success = check(res, {
                '재고 조회 성공': (r) => r.status === 200,
            });

            if (!success) {
                errorRate.add(1);
                failedRequests.add(1);
            } else {
                successRate.add(1);
            }
        });

    } else if (scenario < 0.70) {
        // 15%: 포인트 조회
        group('포인트 조회 (스트레스)', function() {
            let res = http.get(`${BASE_URL}/api/point/${memberId}`, {
                timeout: '10s',
            });

            const success = check(res, {
                '포인트 조회 성공': (r) => r.status === 200,
            });

            if (!success) {
                errorRate.add(1);
                failedRequests.add(1);
            } else {
                successRate.add(1);
            }
        });

    } else if (scenario < 0.85) {
        // 15%: 포인트 충전 (동시성 테스트)
        group('포인트 충전 (스트레스)', function() {
            const start = Date.now();
            let res = http.post(`${BASE_URL}/api/point/charge`, JSON.stringify({
                memberId: memberId,
                chargePoint: 100
            }), {
                headers: { 'Content-Type': 'application/json' },
                timeout: '15s',
            });
            pointChargeTrend.add(Date.now() - start);

            const success = check(res, {
                '포인트 충전 성공': (r) => r.status === 200,
            });

            if (!success) {
                errorRate.add(1);
                failedRequests.add(1);
                if (res.timings.duration >= 15000) {
                    timeoutRate.add(1);
                }
            } else {
                successRate.add(1);
            }
        });

    } else {
        // 15%: 주문 생성 (복잡한 트랜잭션)
        group('주문 생성 (스트레스)', function() {
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
                timeout: '20s',
            });
            orderTrend.add(Date.now() - start);

            const success = check(res, {
                '주문 생성 성공': (r) => r.status === 200,
            });

            if (!success) {
                errorRate.add(1);
                failedRequests.add(1);
                if (res.timings.duration >= 20000) {
                    timeoutRate.add(1);
                }
            } else {
                successRate.add(1);
            }
        });
    }

    // Think Time: 매우 짧게 (스트레스 테스트)
    sleep(0.1 + Math.random() * 0.5);
}

// 테스트 종료 후 요약
export function teardown(data) {
    console.log('=== Stress Test 완료 ===');
    console.log(`테스트 회원 수: ${data.memberIds.length}`);
    console.log(`테스트 상품 수: ${data.productIds.length}`);
    console.log('결과 분석 시 Breaking Point를 확인하세요.');
}

// 실시간 요약 출력 (콘솔)
export function handleSummary(data) {
    console.log('\n=== 스트레스 테스트 요약 ===');

    const metrics = data.metrics;

    if (metrics.http_req_duration) {
        console.log(`HTTP 요청 지연시간:`);
        console.log(`  - p50: ${metrics.http_req_duration.values['p(50)']}ms`);
        console.log(`  - p95: ${metrics.http_req_duration.values['p(95)']}ms`);
        console.log(`  - p99: ${metrics.http_req_duration.values['p(99)']}ms`);
    }

    if (metrics.http_req_failed) {
        console.log(`에러율: ${(metrics.http_req_failed.values.rate * 100).toFixed(2)}%`);
    }

    if (metrics.http_reqs) {
        console.log(`총 요청 수: ${metrics.http_reqs.values.count}`);
        console.log(`초당 요청 수 (RPS): ${metrics.http_reqs.values.rate.toFixed(2)}`);
    }

    return {
        'results/stress_summary.json': JSON.stringify(data, null, 2),
    };
}
