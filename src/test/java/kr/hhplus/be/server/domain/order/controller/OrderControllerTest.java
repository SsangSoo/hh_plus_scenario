package kr.hhplus.be.server.domain.order.controller;

import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.domain.order.service.OrderService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class OrderControllerTest extends RestDocsControllerSupport {
    private final OrderService orderService = mock(OrderService.class);

    @Override
    protected Object initContoller() {
        return new OrderController(orderService);
    }


    @Test
    void orderTest() {
        // given


//        given(orderService.order())
//                .then

        // when // then


    }
}