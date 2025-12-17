package kr.hhplus.be.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.member.presentation.MemberController;
import kr.hhplus.be.server.member.application.service.RegisterMemberService;
import kr.hhplus.be.server.order.application.service.PlaceOrderService;
import kr.hhplus.be.server.order.presentation.OrderController;
import kr.hhplus.be.server.point.controller.PointController;
import kr.hhplus.be.server.point.service.PointService;
import kr.hhplus.be.server.product.controller.ProductController;
import kr.hhplus.be.server.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        ProductController.class,
        MemberController.class,
        PointController.class,
        OrderController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected RegisterMemberService memberService;

    @MockitoBean
    protected PointService pointService;

    @MockitoBean
    protected PlaceOrderService placeOrderService;
}
