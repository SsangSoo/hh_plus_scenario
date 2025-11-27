package kr.hhplus.be.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.member.controller.MemberController;
import kr.hhplus.be.server.domain.member.service.MemberService;
import kr.hhplus.be.server.domain.point.controller.PointController;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.product.controller.ProductController;
import kr.hhplus.be.server.domain.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        ProductController.class,
        MemberController.class,
        PointController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected MemberService memberService;

    @MockitoBean
    protected PointService pointService;
}
