/**
 * 포인트 충전 동시성 테스트 시나리오
 *
 * 목적: Redisson 분산락의 동시성 제어 능력 검증
 * 시나리오:
 *   - 동일 사용자에 대해 100개의 동시 충전 요청
 *   - 데이터 정합성 검증 (최종 포인트 = 초기 + 성공 요청 × 충전금액)
 *
 * 실행 방법:
 *   k6 run scenario_point_charge.js
 *   k6 run --out json=results/point_charge_result.json scenario_point_charge.js
 */

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter, Gauge } from 'k6/metrics';
import { SharedArray } from 'k6/data';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const successRate = new Rate('success_rate');
const lockAcquireFailRate = new Rate('lock_acquire_fail');

// 응답 시간 트렌드
const chargeTrend = new Trend('charge_duration');
const lockWaitTrend = new Trend('lock_wait_duration');

// 카운터
const successfulCharges = new Counter('successful_charges');
const failedCharges = new Counter('failed_charges');

// 테스트 설정
export const options = {
    // 시나리오 기반 테스트
    scenarios: {
        // 시나리오 1: 동일 사용자 동시 충전 (100개 요청)
        same_user_concurrent: {
            executor: 'per-vu-iterations',
            vus: 100,
            iterations: 1,
            maxDuration: '2m',
            exec: 'sameUserCharge',
            tags: { scenario: 'same_user' },
        },

        // 시나리오 2: 다중 사용자 동시 충전
        multi_user_concurrent: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 50 },
                { duration: '1m', target: 100 },
                { duration: '30s', target: 0 },
            ],
            exec: 'multiUserCharge',
            tags: { scenario: 'multi_user' },
            startTime: '3m',  // 첫 번째 시나리오 완료 후 시작
        },

        // 시나리오 3: 충전/사용 혼합
        charge_use_mixed: {
            executor: 'constant-vus',
            vus: 50,
            duration: '2m',
            exec: 'mixedChargeUse',
            tags: { scenario: 'mixed' },
            startTime: '6m',  // 두 번째 시나리오 완료 후 시작
        },
    },

    // 임계값 설정
    thresholds: {
        'charge_duration': ['p(95)<3000'],  // 95%가 3초 이내
        'errors': ['rate<0.05'],             // 에러율 5% 미만
        'lock_acquire_fail': ['rate<0.10'],  // Lock 획득 실패 10% 미만
    },
};

// 환경 설정
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 테스트 데이터
let sharedMemberId = null;
let multiMemberIds = [];

// 테스트 데이터 초기화
export function setup() {
    console.log('=== 포인트 충전 동시성 테스트 시작 ===');
    console.log(`대상 서버: ${BASE_URL}`);

    // 공용 테스트 회원 생성 (동일 사용자 테스트용)
    const memberRes = http.post(`${BASE_URL}/api/member`, JSON.stringify({
        name: 'point_test_shared_user'
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    if (memberRes.status === 201) {
        const memberData = JSON.parse(memberRes.body);
        sharedMemberId = memberData.memberId;
        console.log(`공용 테스트 회원 생성: ${sharedMemberId}`);
    }

    // 다중 사용자 테스트용 회원 생성 (20명)
    for (let i = 0; i < 20; i++) {
        const res = http.post(`${BASE_URL}/api/member`, JSON.stringify({
            name: `point_test_user_${i}`
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        if (res.status === 201) {
            const data = JSON.parse(res.body);
            multiMemberIds.push(data.memberId);
        }
    }
    console.log(`다중 사용자 ${multiMemberIds.length}명 생성 완료`);

    // 초기 포인트 확인
    const initialPointRes = http.get(`${BASE_URL}/api/point/${sharedMemberId}`);
    const initialPoint = initialPointRes.status === 200
        ? JSON.parse(initialPointRes.body).point
        : 0;

    console.log(`공용 회원 초기 포인트: ${initialPoint}`);

    return {
        sharedMemberId: sharedMemberId,
        multiMemberIds: multiMemberIds,
        initialPoint: initialPoint,
        chargeAmount: 1000,  // 각 충전당 1000 포인트
    };
}

// 시나리오 1: 동일 사용자 동시 충전
export function sameUserCharge(data) {
    const memberId = data.sharedMemberId;
    const chargeAmount = data.chargeAmount;

    group('동일 사용자 동시 충전', function() {
        console.log(`VU ${__VU}: 충전 시작 (memberId: ${memberId})`);

        const start = Date.now();
        let res = http.post(`${BASE_URL}/api/point/charge`, JSON.stringify({
            memberId: memberId,
            chargePoint: chargeAmount
        }), {
            headers: { 'Content-Type': 'application/json' },
            timeout: '30s',
        });
        const duration = Date.now() - start;
        chargeTrend.add(duration);

        // Lock 대기 시간 추정 (응답 시간 - 예상 처리 시간)
        const estimatedProcessTime = 100;  // ms
        if (duration > estimatedProcessTime) {
            lockWaitTrend.add(duration - estimatedProcessTime);
        }

        const success = check(res, {
            '충전 성공 (200)': (r) => r.status === 200,
            '응답 시간 5초 이내': (r) => r.timings.duration < 5000,
        });

        if (success) {
            successfulCharges.add(1);
            successRate.add(1);
            console.log(`VU ${__VU}: 충전 성공 (${duration}ms)`);
        } else {
            failedCharges.add(1);
            errorRate.add(1);

            // Lock 획득 실패 여부 확인
            if (res.body && res.body.includes('Lock')) {
                lockAcquireFailRate.add(1);
            }
            console.log(`VU ${__VU}: 충전 실패 - ${res.status} (${duration}ms)`);
        }
    });
}

// 시나리오 2: 다중 사용자 동시 충전
export function multiUserCharge(data) {
    const memberIds = data.multiMemberIds;
    const chargeAmount = data.chargeAmount;

    // VU별로 다른 사용자 선택
    const memberId = memberIds[__VU % memberIds.length] || memberIds[0];

    group('다중 사용자 동시 충전', function() {
        const start = Date.now();
        let res = http.post(`${BASE_URL}/api/point/charge`, JSON.stringify({
            memberId: memberId,
            chargePoint: chargeAmount
        }), {
            headers: { 'Content-Type': 'application/json' },
            timeout: '15s',
        });
        chargeTrend.add(Date.now() - start);

        const success = check(res, {
            '다중 사용자 충전 성공': (r) => r.status === 200,
        });

        success ? successRate.add(1) : errorRate.add(1);
    });

    sleep(0.5 + Math.random());
}

// 시나리오 3: 충전/사용 혼합
export function mixedChargeUse(data) {
    const memberIds = data.multiMemberIds;
    const memberId = memberIds[__VU % memberIds.length] || memberIds[0];

    // 70% 충전, 30% 포인트 조회 (사용은 주문에서 처리되므로 여기선 조회로 대체)
    const action = Math.random();

    if (action < 0.7) {
        // 포인트 충전
        group('혼합 시나리오 - 충전', function() {
            let res = http.post(`${BASE_URL}/api/point/charge`, JSON.stringify({
                memberId: memberId,
                chargePoint: 500
            }), {
                headers: { 'Content-Type': 'application/json' },
            });

            check(res, {
                '혼합 충전 성공': (r) => r.status === 200,
            });
        });
    } else {
        // 포인트 조회
        group('혼합 시나리오 - 조회', function() {
            let res = http.get(`${BASE_URL}/api/point/${memberId}`);

            check(res, {
                '포인트 조회 성공': (r) => r.status === 200,
            });
        });
    }

    sleep(0.2 + Math.random() * 0.5);
}

// 테스트 종료 후 데이터 정합성 검증
export function teardown(data) {
    console.log('\n=== 포인트 충전 동시성 테스트 완료 ===');

    // 최종 포인트 확인
    const finalPointRes = http.get(`${BASE_URL}/api/point/${data.sharedMemberId}`);

    if (finalPointRes.status === 200) {
        const finalData = JSON.parse(finalPointRes.body);
        const finalPoint = finalData.point;
        const expectedPoint = data.initialPoint + (100 * data.chargeAmount);  // 100 VU × 1000원

        console.log(`\n=== 데이터 정합성 검증 ===`);
        console.log(`초기 포인트: ${data.initialPoint}`);
        console.log(`최종 포인트: ${finalPoint}`);
        console.log(`예상 포인트 (100회 성공 시): ${expectedPoint}`);
        console.log(`실제 충전 횟수: ${(finalPoint - data.initialPoint) / data.chargeAmount}`);

        if (finalPoint === expectedPoint) {
            console.log('✅ 데이터 정합성 검증 성공: 모든 충전이 정확히 반영됨');
        } else if (finalPoint <= expectedPoint) {
            console.log(`⚠️ 일부 충전 실패: ${expectedPoint - finalPoint}원 차이`);
        } else {
            console.log('❌ 데이터 정합성 오류: 초과 충전 발생!');
        }
    }
}

// 결과 요약
export function handleSummary(data) {
    const summary = {
        timestamp: new Date().toISOString(),
        testName: '포인트 충전 동시성 테스트',
        metrics: {},
    };

    if (data.metrics.charge_duration) {
        summary.metrics.charge_duration = {
            p50: data.metrics.charge_duration.values['p(50)'],
            p95: data.metrics.charge_duration.values['p(95)'],
            p99: data.metrics.charge_duration.values['p(99)'],
        };
    }

    if (data.metrics.successful_charges) {
        summary.metrics.successful_charges = data.metrics.successful_charges.values.count;
    }

    if (data.metrics.failed_charges) {
        summary.metrics.failed_charges = data.metrics.failed_charges.values.count;
    }

    return {
        'results/point_charge_summary.json': JSON.stringify(summary, null, 2),
        stdout: generateTextSummary(data),
    };
}

function generateTextSummary(data) {
    let output = '\n=== 포인트 충전 동시성 테스트 결과 ===\n\n';

    if (data.metrics.charge_duration) {
        output += `충전 응답 시간:\n`;
        output += `  p50: ${data.metrics.charge_duration.values['p(50)']}ms\n`;
        output += `  p95: ${data.metrics.charge_duration.values['p(95)']}ms\n`;
        output += `  p99: ${data.metrics.charge_duration.values['p(99)']}ms\n\n`;
    }

    if (data.metrics.successful_charges) {
        output += `성공한 충전: ${data.metrics.successful_charges.values.count}건\n`;
    }

    if (data.metrics.failed_charges) {
        output += `실패한 충전: ${data.metrics.failed_charges.values.count}건\n`;
    }

    return output;
}
