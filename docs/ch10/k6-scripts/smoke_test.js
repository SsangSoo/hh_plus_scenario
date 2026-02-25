/**
 * Smoke Test - 기본 동작 확인
 *
 * 목적: 시스템이 정상적으로 동작하는지 최소한의 부하로 확인
 * VUs: 1-5명
 * 지속시간: 1분
 *
 * 실행 방법:
 *   k6 run smoke_test.js
 *   k6 run --out json=results/smoke_result.json smoke_test.js
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const pointChargeTrend = new Trend('point_charge_duration');
const productGetTrend = new Trend('product_get_duration');

// 테스트 설정
export const options = {
    // Smoke Test: 최소 부하
    vus: 3,
    duration: '1m',

    // 임계값 설정
    thresholds: {
        http_req_duration: ['p(95)<2000'],  // 95%의 요청이 2초 이내
        http_req_failed: ['rate<0.01'],      // 에러율 1% 미만
        errors: ['rate<0.01'],               // 커스텀 에러율 1% 미만
    },
};

// 환경 설정
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 테스트 데이터
let testMemberId = 1;
let testProductId = 1;

// 테스트 데이터 초기화 (테스트 시작 전 1회 실행)
export function setup() {
    console.log('=== Smoke Test 시작 ===');
    console.log(`대상 서버: ${BASE_URL}`);

    // 회원 생성
    const memberRes = http.post(`${BASE_URL}/api/member`, JSON.stringify({
        name: 'smoke_test_user'
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    if (memberRes.status === 201) {
        const memberData = JSON.parse(memberRes.body);
        testMemberId = memberData.memberId;
        console.log(`테스트 회원 생성: ${testMemberId}`);
    }

    // 상품 생성
    const productRes = http.post(`${BASE_URL}/api/product`, JSON.stringify({
        name: 'smoke_test_product',
        price: 10000
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    if (productRes.status === 201) {
        const productData = JSON.parse(productRes.body);
        testProductId = productData.productId;
        console.log(`테스트 상품 생성: ${testProductId}`);

        // 재고 추가
        http.post(`${BASE_URL}/api/stock`, JSON.stringify({
            productId: testProductId,
            quantity: 1000
        }), {
            headers: { 'Content-Type': 'application/json' },
        });
    }

    return { memberId: testMemberId, productId: testProductId };
}

// 메인 테스트 시나리오
export default function(data) {
    const memberId = data.memberId || testMemberId;
    const productId = data.productId || testProductId;

    // 1. 포인트 조회
    let pointRes = http.get(`${BASE_URL}/api/point/${memberId}`);
    check(pointRes, {
        '포인트 조회 성공': (r) => r.status === 200,
    }) || errorRate.add(1);

    sleep(0.5);

    // 2. 포인트 충전
    const chargeStart = Date.now();
    let chargeRes = http.post(`${BASE_URL}/api/point/charge`, JSON.stringify({
        memberId: memberId,
        chargePoint: 1000
    }), {
        headers: { 'Content-Type': 'application/json' },
    });
    pointChargeTrend.add(Date.now() - chargeStart);

    check(chargeRes, {
        '포인트 충전 성공': (r) => r.status === 200,
    }) || errorRate.add(1);

    sleep(0.5);

    // 3. 상품 조회
    const productStart = Date.now();
    let productRes = http.get(`${BASE_URL}/api/product/${productId}`);
    productGetTrend.add(Date.now() - productStart);

    check(productRes, {
        '상품 조회 성공': (r) => r.status === 200,
    }) || errorRate.add(1);

    sleep(0.5);

    // 4. 인기 상품 조회
    let popularRes = http.get(`${BASE_URL}/api/product/popular`);
    check(popularRes, {
        '인기 상품 조회 성공': (r) => r.status === 200,
    }) || errorRate.add(1);

    sleep(0.5);

    // 5. 재고 조회
    let stockRes = http.get(`${BASE_URL}/api/stock/${productId}`);
    check(stockRes, {
        '재고 조회 성공': (r) => r.status === 200,
    }) || errorRate.add(1);

    sleep(1);
}

// 테스트 종료 후 정리
export function teardown(data) {
    console.log('=== Smoke Test 완료 ===');
    console.log(`테스트 회원 ID: ${data.memberId}`);
    console.log(`테스트 상품 ID: ${data.productId}`);
}
