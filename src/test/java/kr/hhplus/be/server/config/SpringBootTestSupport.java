package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.member.repository.MemberRepository;
import kr.hhplus.be.server.domain.member.service.MemberService;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.pointhistory.repository.PointHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public abstract class SpringBootTestSupport {

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected PointService pointService;

    @Autowired
    protected PointRepository pointRepository;

    @Autowired
    protected PointHistoryRepository pointHistoryRepository;
}
