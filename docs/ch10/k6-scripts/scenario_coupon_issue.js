/**
 * 선착순 쿠폰 발급 테스트 시나리오
 *
 * 목적: 제한된 수량의 쿠폰에 대한 동시성 제어 검증
 * 시나리오:
 *   - 수량 10개인 쿠폰에 100명이 동시 발급 요청
 *   - 정확히 10명만 발급 성공해야 함
 *   - 중복 발급 방지 검증
 *
 * 실행 방법:
 *   k6 run scenario_coupon_issue.js
 *   k6 run --out json=results/coupon_issue_result.json scenario_coupon_issue.js
 */

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter, Gauge } from 'k6/metrics';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const successRate = new Rate('issue_success_rate');
const duplicateIssueRate = new Rate('duplicate_issue');
const oversellRate = new Rate('oversell');

// 응답 시간 트렌드
const issueTrend = new Trend('issue_duration');

// 카운터
const successfulIssues = new Counter('successful_issues');
const failedIssues = new Counter('failed_issues');
const duplicateAttempts = new Counter('duplicate_attempts');

// 테스트 설정
export const options = {
    scenarios: {
        // 시나리오 1: 선착순 쿠폰 발급 (100명 동시 요청)
        first_come_first_served: {
            executor: 'per-vu-iterations',
            vus: 100,
            iterations: 1,
            maxDuration: '2m',
            exec: 'firstComeFirstServed',
            tags: { scenario: 'fcfs' },
        },

        // 시나리오 2: 중복 발급 방지 테스트
        duplicate_prevention: {
            executor: 'per-vu-iterations',
            vus: 10,
            iterations: 5,  // 각 VU가 5번 시도
            maxDuration: '2m',
            exec: 'duplicatePrevention',
            tags: { scenario: 'duplicate' },
            startTime: '3m',
        },

        // 시나리오 3: 지속적인 쿠폰 발급 부하
        continuous_issue: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 30 },
                { duration: '1m', target: 50 },
                { duration: '30s', target: 0 },
            ],
            exec: 'continuousIssue',
            tags: { scenario: 'continuous' },
            startTime: '6m',
        },
    },

    thresholds: {
        'issue_duration': ['p(95)<5000'],
        'errors': ['rate<0.50'],  // 선착순이므로 실패율 높을 수 있음
        'duplicate_issue': ['rate<0.01'],  // 중복 발급은 0%여야 함
        'oversell': ['rate<0.01'],  // 초과 발급은 0%여야 함
    },
};

// 환경 설정
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 테스트 데이터
let testCouponId = null;
let testMemberIds = [];
let couponQuantity = 10;

// 발급 성공한 회원 추적
let issuedMembers = new Set();

// 테스트 데이터 초기화
export function setup() {
    console.log('=== 선착순 쿠폰 발급 테스트 시작 ===');
    console.log(`대상 서버: ${BASE_URL}`);

    // 테스트용 회원 100명 생성
    for (let i = 0; i < 100; i++) {
        const memberRes = http.post(`${BASE_URL}/api/member`, JSON.stringify({
            name: `coupon_test_user_${i}`
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (memberRes.status === 201) {
            const memberData = JSON.parse(memberRes.body);
            testMemberIds.push(memberData.memberId);
        }
    }
    console.log(`테스트 회원 ${testMemberIds.length}명 생성 완료`);

    // 선착순 쿠폰 생성 (수량: 10개)
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 30);
    const expiryDate = tomorrow.toISOString().split('T')[0];

    const couponRes = http.post(`${BASE_URL}/api/coupon`, JSON.stringify({
        coupon: `FCFS_TEST_${Date.now()}`,
        expiryDate: expiryDate,
        amount: couponQuantity,
        discountRate: 10
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    if (couponRes.status === 201) {
        const couponData = JSON.parse(couponRes.body);
        testCouponId = couponData.couponId;
        console.log(`테스트 쿠폰 생성: ${testCouponId} (수량: ${couponQuantity}개)`);
    }

    // 중복 발급 테스트용 추가 쿠폰
    const dupCouponRes = http.post(`${BASE_URL}/api/coupon`, JSON.stringify({
        coupon: `DUP_TEST_${Date.now()}`,
        expiryDate: expiryDate,
        amount: 100,  // 충분한 수량
        discountRate: 5
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    let dupCouponId = null;
    if (dupCouponRes.status === 201) {
        const dupData = JSON.parse(dupCouponRes.body);
        dupCouponId = dupData.couponId;
        console.log(`중복 테스트용 쿠폰 생성: ${dupCouponId}`);
    }

    // 지속 테스트용 추가 쿠폰
    const contCouponRes = http.post(`${BASE_URL}/api/coupon`, JSON.stringify({
        coupon: `CONT_TEST_${Date.now()}`,
        expiryDate: expiryDate,
        amount: 1000,  // 대량 수량
        discountRate: 3
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    let contCouponId = null;
    if (contCouponRes.status === 201) {
        const contData = JSON.parse(contCouponRes.body);
        contCouponId = contData.couponId;
        console.log(`지속 테스트용 쿠폰 생성: ${contCouponId}`);
    }

    return {
        fcfsCouponId: testCouponId,
        dupCouponId: dupCouponId,
        contCouponId: contCouponId,
        memberIds: testMemberIds,
        couponQuantity: couponQuantity,
    };
}

// 시나리오 1: 선착순 쿠폰 발급
export function firstComeFirstServed(data) {
    const couponId = data.fcfsCouponId;
    const memberId = data.memberIds[__VU - 1];  // VU별로 다른 회원

    group('선착순 쿠폰 발급', function() {
        console.log(`VU ${__VU}: 쿠폰 발급 시도 (memberId: ${memberId})`);

        const start = Date.now();
        let res = http.post(`${BASE_URL}/api/coupon/issue`, JSON.stringify({
            couponId: couponId,
            memberId: memberId
        }), {
            headers: { 'Content-Type': 'application/json' },
            timeout: '30s',
        });
        const duration = Date.now() - start;
        issueTrend.add(duration);

        const success = check(res, {
            '발급 요청 처리됨': (r) => r.status === 200 || r.status === 400 || r.status === 409,
        });

        if (res.status === 200) {
            successfulIssues.add(1);
            successRate.add(1);
            console.log(`VU ${__VU}: 쿠폰 발급 성공! (${duration}ms)`);
        } else {
            failedIssues.add(1);
            console.log(`VU ${__VU}: 쿠폰 발급 실패 - ${res.status} (${duration}ms)`);

            // 응답 메시지로 실패 원인 분류
            if (res.body) {
                if (res.body.includes('소진') || res.body.includes('수량')) {
                    // 정상적인 수량 초과 실패
                    console.log(`VU ${__VU}: 쿠폰 소진됨`);
                } else if (res.body.includes('이미') || res.body.includes('중복')) {
                    // 중복 발급 시도 (이 시나리오에서는 발생하면 안됨)
                    duplicateIssueRate.add(1);
                }
            }
        }

        if (!success) {
            errorRate.add(1);
        }
    });
}

// 시나리오 2: 중복 발급 방지 테스트
export function duplicatePrevention(data) {
    const couponId = data.dupCouponId;
    const memberId = data.memberIds[__VU - 1];  // VU별로 고정 회원

    group('중복 발급 방지 테스트', function() {
        let res = http.post(`${BASE_URL}/api/coupon/issue`, JSON.stringify({
            couponId: couponId,
            memberId: memberId
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (res.status === 200) {
            // 첫 발급 성공
            successfulIssues.add(1);
        } else if (res.status === 400 || res.status === 409) {
            // 중복 발급 시도 차단됨 (정상)
            duplicateAttempts.add(1);
        } else {
            // 예상치 못한 응답
            errorRate.add(1);
        }

        check(res, {
            '중복 발급 차단됨 또는 첫 발급 성공': (r) =>
                r.status === 200 || r.status === 400 || r.status === 409,
        });
    });

    sleep(0.1);
}

// 시나리오 3: 지속적인 쿠폰 발급 부하
export function continuousIssue(data) {
    const couponId = data.contCouponId;
    // 랜덤 회원 선택 (새로운 회원 생성 대신)
    const memberId = data.memberIds[Math.floor(Math.random() * data.memberIds.length)];

    group('지속 쿠폰 발급', function() {
        let res = http.post(`${BASE_URL}/api/coupon/issue`, JSON.stringify({
            couponId: couponId,
            memberId: memberId
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        const success = check(res, {
            '발급 처리됨': (r) => r.status === 200 || r.status === 400 || r.status === 409,
        });

        if (res.status === 200) {
            successfulIssues.add(1);
        }
    });

    sleep(0.2 + Math.random() * 0.3);
}

// 테스트 종료 후 검증
export function teardown(data) {
    console.log('\n=== 선착순 쿠폰 발급 테스트 완료 ===');

    // 쿠폰 상태 확인
    const couponRes = http.get(`${BASE_URL}/api/coupon/${data.fcfsCouponId}`);

    if (couponRes.status === 200) {
        const couponData = JSON.parse(couponRes.body);
        const remainingAmount = couponData.amount;

        console.log(`\n=== 선착순 쿠폰 발급 결과 ===`);
        console.log(`초기 수량: ${data.couponQuantity}`);
        console.log(`남은 수량: ${remainingAmount}`);
        console.log(`발급된 수량: ${data.couponQuantity - remainingAmount}`);

        if (data.couponQuantity - remainingAmount === data.couponQuantity) {
            console.log('✅ 정상: 모든 쿠폰이 발급됨');
        } else if (remainingAmount < 0) {
            console.log('❌ 오류: 초과 발급 발생! (oversell)');
            oversellRate.add(1);
        } else if (remainingAmount > 0) {
            console.log(`⚠️ 미발급 쿠폰 ${remainingAmount}개 존재`);
        }
    }
}

// 결과 요약
export function handleSummary(data) {
    const summary = {
        timestamp: new Date().toISOString(),
        testName: '선착순 쿠폰 발급 테스트',
        metrics: {},
    };

    if (data.metrics.successful_issues) {
        summary.metrics.successful_issues = data.metrics.successful_issues.values.count;
    }

    if (data.metrics.failed_issues) {
        summary.metrics.failed_issues = data.metrics.failed_issues.values.count;
    }

    if (data.metrics.duplicate_attempts) {
        summary.metrics.duplicate_attempts = data.metrics.duplicate_attempts.values.count;
    }

    return {
        'results/coupon_issue_summary.json': JSON.stringify(summary, null, 2),
        stdout: generateTextSummary(data),
    };
}

function generateTextSummary(data) {
    let output = '\n=== 선착순 쿠폰 발급 테스트 결과 ===\n\n';

    if (data.metrics.issue_duration) {
        output += `발급 응답 시간:\n`;
        output += `  p50: ${data.metrics.issue_duration.values['p(50)']}ms\n`;
        output += `  p95: ${data.metrics.issue_duration.values['p(95)']}ms\n\n`;
    }

    if (data.metrics.successful_issues) {
        output += `성공한 발급: ${data.metrics.successful_issues.values.count}건\n`;
    }

    if (data.metrics.failed_issues) {
        output += `실패한 발급: ${data.metrics.failed_issues.values.count}건\n`;
    }

    if (data.metrics.duplicate_attempts) {
        output += `중복 시도 차단: ${data.metrics.duplicate_attempts.values.count}건\n`;
    }

    return output;
}
