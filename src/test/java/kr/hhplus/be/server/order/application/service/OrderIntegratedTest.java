package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.member.presentation.dto.request.RegisterMemberRequest;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.order.presentation.dto.request.OrderProductRequest;
import kr.hhplus.be.server.order.presentation.dto.request.OrderRequest;
import kr.hhplus.be.server.point.presentation.dto.request.ChargePointRequest;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import kr.hhplus.be.server.product.presentation.dto.request.RegisterProductRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import kr.hhplus.be.server.stock.application.service.RetrieveStockService;
import kr.hhplus.be.server.stock.presentation.dto.request.AddStockRequest;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class OrderIntegratedTest extends SpringBootTestSupport {

    @Autowired
    private RetrieveStockService retrieveStockService;

    @Test
    @DisplayName("주문 / 결제 Happy Case")
    void orderAndPaymentTest() {
        // given : 주문 결제를 위한 환경 셋팅
        // 회원을 생성한다.
        MemberResponse memberResponse = registerMemberUseCase.register(new RegisterMemberRequest("상남자", LocalDate.now(), "주소").toServiceRequest());

        // 회원의 포인트를 충전한다.
        PointResponse pointResponse = chargePointUseCase.charge(new ChargePointRequest(memberResponse.getId(), 30000L).toChargePoint());

        // 상품을 생성한다.
        ProductResponse productResponse = registerProductUseCase.register(new RegisterProductRequest("아메리카노", 3800L).toServiceRequest());

        // 상품의 재고를 채운다.
        StockResponse stockResponse = addStockUseCase.addStock(new AddStockRequest(productResponse.getId(), 30L).toAddStock());

        OrderRequest orderRequest = new OrderRequest(memberResponse.getId(), new OrderProductRequest(productResponse.getId(), 5L), "POINT");

        // when : 주문을 한다.
        placeOrderUseCase.order(orderRequest.toOrderCommand());
            // 주문시 포인트 차감이 이뤄줘야 하고,
            // 재고가 차감되어야 하며,
            // 결제가 이루어져야 한다.
            // 결제가 이루어지면, 결제 데이터를 전송하는 플랫폼으로 데이터를 보낸다(비동기)

        // then
        // 재고 확인 25
        stockResponse = retrieveStockService.retrieveStock(productResponse.getId());
        assertThat(stockResponse).isNotNull();
        assertThat(stockResponse.getQuantity()).isEqualTo(25L);
        
        // 포인트 확인 11000
        pointResponse = retrievePointUseCase.retrieve(memberResponse.getId());
        assertThat(pointResponse).isNotNull();
        assertThat(pointResponse.getPoint()).isEqualTo(11000L);

    }

}
