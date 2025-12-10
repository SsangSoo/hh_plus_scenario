package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.domain.member.controller.request.RegisterMemberRequest;
import kr.hhplus.be.server.domain.member.service.response.MemberResponse;
import kr.hhplus.be.server.domain.order.controller.request.OrderProductRequest;
import kr.hhplus.be.server.domain.order.controller.request.OrderRequest;
import kr.hhplus.be.server.domain.point.controller.request.ChargePointRequest;
import kr.hhplus.be.server.domain.point.service.response.PointResponse;
import kr.hhplus.be.server.domain.product.controller.request.RegisterProductRequest;
import kr.hhplus.be.server.domain.product.service.response.ProductResponse;
import kr.hhplus.be.server.domain.stock.controller.request.AddStockRequest;
import kr.hhplus.be.server.domain.stock.service.response.StockResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

class OrderIntegratedTest extends SpringBootTestSupport {

    @Test
    @DisplayName("주문 / 결제 Happy Case")
    void orderAndPaymentTest() {
        // given : 주문 결제를 위한 환경 셋팅

        // 회원을 생성한다.
        MemberResponse memberResponse = memberService.register(new RegisterMemberRequest("상남자", LocalDate.now(), "주소").toServiceRequest());

        // 회원의 포인트를 충전한다.
        PointResponse pointResponse = pointService.charge(new ChargePointRequest(memberResponse.getId(), 30000L).toChargePoint());

        // 상품을 생성한다.
        ProductResponse productResponse = productService.registerProduct(new RegisterProductRequest("아메리카노", 3800L).toServiceRequest());

        // 상품의 재고를 채운다.
        StockResponse stockResponse = stockService.addStock(new AddStockRequest(productResponse.getId(), 30L).toAddStock());

        OrderRequest orderRequest = new OrderRequest(memberResponse.getId(), new OrderProductRequest(productResponse.getId(), 5L), "POINT");

        // when : 주문을 한다.
        orderService.order(orderRequest.toServiceRequest());
            // 주문시 포인트 차감이 이뤄줘야 하고,
            // 재고가 차감되어야 하며,
            // 결제가 이루어져야 한다.
            // 결제가 이루어지면, 결제 데이터를 전송하는 플랫폼으로 데이터를 보낸다(비동기)

        // then
        // 재고 확인 25
        stockResponse = stockService.retrieveStock(productResponse.getId());
        assertThat(stockResponse).isNotNull();
        assertThat(stockResponse.getQuantity()).isEqualTo(25L);
        
        // 포인트 확인 11000
        pointResponse = pointService.retrieve(memberResponse.getId());
        assertThat(pointResponse).isNotNull();
        assertThat(pointResponse.getPoint()).isEqualTo(11000L);

    }

}
