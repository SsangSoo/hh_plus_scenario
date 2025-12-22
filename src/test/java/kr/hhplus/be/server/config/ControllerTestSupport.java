package kr.hhplus.be.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.member.application.usecase.RegisterMemberUseCase;
import kr.hhplus.be.server.member.presentation.MemberController;
import kr.hhplus.be.server.order.application.usecase.PlaceOrderUseCase;
import kr.hhplus.be.server.order.presentation.OrderController;
import kr.hhplus.be.server.point.application.usecase.ChargePointUseCase;
import kr.hhplus.be.server.point.application.usecase.RetrievePointUseCase;
import kr.hhplus.be.server.point.presentation.PointController;
import kr.hhplus.be.server.product.application.usecase.RegisterProductUseCase;
import kr.hhplus.be.server.product.application.usecase.RetrieveProductUseCase;
import kr.hhplus.be.server.product.presentation.ProductController;
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


    // Product
    @MockitoBean
    protected RegisterProductUseCase registerProductUseCase;

    @MockitoBean
    protected RetrieveProductUseCase retrieveProductUseCase;


    // Member
    @MockitoBean
    protected RegisterMemberUseCase registerMemberUseCase;


    // Order
    @MockitoBean
    protected PlaceOrderUseCase placeOrderUseCase;


    // Point
    @MockitoBean
    protected ChargePointUseCase chargePointUseCase;

    @MockitoBean
    protected RetrievePointUseCase retrievePointUseCase;
}
