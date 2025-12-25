package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.member.presentation.dto.request.RegisterMemberRequest;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.order.presentation.dto.request.OrderProductRequest;
import kr.hhplus.be.server.order.presentation.dto.request.OrderRequest;
import kr.hhplus.be.server.order.presentation.dto.response.OrderResponse;
import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.point.presentation.dto.request.ChargePointRequest;
import kr.hhplus.be.server.point.presentation.dto.response.PointResponse;
import kr.hhplus.be.server.product.presentation.dto.request.RegisterProductRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import kr.hhplus.be.server.stock.presentation.dto.request.AddStockRequest;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

class OrderIntegratedTest extends SpringBootTestSupport {

    @AfterEach
    void tearDown() {
        memberJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();
        productJpaRepository.deleteAllInBatch();
        orderProductJpaRepository.deleteAllInBatch();
        stockJpaRepository.deleteAllInBatch();
        orderJpaRepository.deleteAllInBatch();
    }

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

        OrderRequest orderRequest = new OrderRequest(memberResponse.getId(), List.of(new OrderProductRequest(productResponse.getId(), 5L)), "POINT");

        // when : 주문을 한다.
        placeOrderUseCase.order(orderRequest.toOrderCommand());
            // 주문시 포인트 차감이 이뤄줘야 하고,
            // 재고가 차감되어야 하며,
            // 결제가 이루어져야 한다.
            // 결제가 이루어지면, 결제 데이터를 전송하는 플랫폼으로 데이터를 보낸다(비동기)

        // then
        // 재고 확인 25
        stockResponse = retrieveStockUseCase.retrieveStock(productResponse.getId());
        assertThat(stockResponse).isNotNull();
        assertThat(stockResponse.getQuantity()).isEqualTo(25L);
        
        // 포인트 확인 11000
        pointResponse = retrievePointUseCase.retrieve(memberResponse.getId());
        assertThat(pointResponse).isNotNull();
        assertThat(pointResponse.getPoint()).isEqualTo(11000L);

    }

    @Test
    @DisplayName("주문 도중 포인트가 부족하면, 재고는 차감되지 않고, 그대로여야 한다.")
    void ifPointNotEnoughThenStockShouldKeep() {
        // given
        Long addStock = 30L;
        MemberResponse memberResponse = registerMemberUseCase.register(new RegisterMemberRequest("상남자", LocalDate.now(), "주소").toServiceRequest());
        ProductResponse productResponse = registerProductUseCase.register(new RegisterProductRequest("아메리카노", 3800L).toServiceRequest());
        addStockUseCase.addStock(new AddStockRequest(productResponse.getId(), addStock).toAddStock());

        OrderRequest orderRequest = new OrderRequest(memberResponse.getId(), List.of(new OrderProductRequest(productResponse.getId(), 5L)), "POINT");

        // when
        assertThatThrownBy(() -> placeOrderUseCase.order(orderRequest.toOrderCommand()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.POINT_IS_NOT_ENOUGH.getMessage());

        // then
        StockResponse stockResponse = retrieveStockUseCase.retrieveStock(productResponse.getId());
        assertThat(stockResponse.getProductId()).isEqualTo(productResponse.getId());
        assertThat(stockResponse.getQuantity()).isEqualTo(addStock);

    }

    @Test
    @DisplayName("주문 도중 하나의 상품이라도 존재하지 않으면 주문은 실패한다.")
    void whenNotFoundSomeProductTest() {
        // given
        // 회원생성
        MemberResponse memberResponse = registerMemberUseCase.register(new RegisterMemberRequest("상남자", LocalDate.now(), "주소").toServiceRequest());

        // 상품 생성
        ProductResponse firstProductResponse = registerProductUseCase.register(new RegisterProductRequest("아메리카노", 3800L).toServiceRequest());
        ProductResponse secondProductResponse = registerProductUseCase.register(new RegisterProductRequest("카페라떼", 3800L).toServiceRequest());
        ProductResponse thirdProductResponse = registerProductUseCase.register(new RegisterProductRequest("콜드브루", 3800L).toServiceRequest());

        // 존재하지 않는 상품 Id
        long notExistProductId = firstProductResponse.getId() + 30L;

        OrderRequest orderRequest = new OrderRequest(
                memberResponse.getId(),
                List.of(
                        new OrderProductRequest(firstProductResponse.getId(), 5L),
                        new OrderProductRequest(secondProductResponse.getId(), 5L),
                        new OrderProductRequest(thirdProductResponse.getId(), 5L),
                        new OrderProductRequest(notExistProductId, 5L)
                ),
                "POINT");

        // when
        assertThatThrownBy(() -> placeOrderUseCase.order(orderRequest.toOrderCommand()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_FOUND_SOME_PRODUCT.getMessage());
    }


    @Test
    @DisplayName("주문 도중 하나의 상품이라도 재고가 부족하면, 주문은 실패해야하며, 재고는 그대로이다.")
    void whenNotFoundSomeProductStockNotEnoughThenStockShouldKeepTest() {
        // given
        // 회원생성
        MemberResponse memberResponse = registerMemberUseCase.register(new RegisterMemberRequest("상남자", LocalDate.now(), "주소").toServiceRequest());

        // 상품 생성
        ProductResponse firstProductResponse = registerProductUseCase.register(new RegisterProductRequest("아메리카노", 3800L).toServiceRequest());
        ProductResponse secondProductResponse = registerProductUseCase.register(new RegisterProductRequest("카페라떼", 3800L).toServiceRequest());
        ProductResponse thirdProductResponse = registerProductUseCase.register(new RegisterProductRequest("콜드브루", 3800L).toServiceRequest());

        // 재고 충전 // 세 번째 상품만 재고 1개 충전
        Long addStock = 30L;
        addStockUseCase.addStock(new AddStockRequest(firstProductResponse.getId(), addStock).toAddStock());
        addStockUseCase.addStock(new AddStockRequest(secondProductResponse.getId(), addStock).toAddStock());
        addStockUseCase.addStock(new AddStockRequest(thirdProductResponse.getId(), 1L).toAddStock());


        OrderRequest orderRequest = new OrderRequest(
                memberResponse.getId(),
                List.of(
                        new OrderProductRequest(firstProductResponse.getId(), 5L),
                        new OrderProductRequest(secondProductResponse.getId(), 5L),
                        new OrderProductRequest(thirdProductResponse.getId(), 5L)
                ),
                "POINT");

        // when
        assertThatThrownBy(() -> placeOrderUseCase.order(orderRequest.toOrderCommand()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.STOCK_IS_NOT_ENOUGH.getMessage());

        // then
        StockResponse firstStockResponse = retrieveStockUseCase.retrieveStock(firstProductResponse.getId());
        StockResponse secondStockResponse = retrieveStockUseCase.retrieveStock(secondProductResponse.getId());
        StockResponse thridStockResponse = retrieveStockUseCase.retrieveStock(thirdProductResponse.getId());
        assertThat(firstStockResponse.getProductId()).isEqualTo(firstProductResponse.getId());
        assertThat(secondStockResponse.getProductId()).isEqualTo(secondProductResponse.getId());
        assertThat(thridStockResponse.getProductId()).isEqualTo(thirdProductResponse.getId());
        assertThat(firstStockResponse.getQuantity()).isEqualTo(addStock);
        assertThat(secondStockResponse.getQuantity()).isEqualTo(addStock);
        assertThat(thridStockResponse.getQuantity()).isEqualTo(1L);
    }

    @Test
    @DisplayName("여러 상품을 주문하면, 하나의 주문에 주문한 상품 개수만큼의 주문 상품이 조회되어야 한다. ")
    void whenOrderMultiProductThenCountOfOrderProductIsProductCount() {
        // given
        // 회원생성
        MemberResponse memberResponse = registerMemberUseCase.register(new RegisterMemberRequest("상남자", LocalDate.now(), "주소").toServiceRequest());

        // 포인트 충전
        chargePointUseCase.charge(new ChargePoint(memberResponse.getId(), 100000L));

        // 상품 생성
        ProductResponse firstProductResponse = registerProductUseCase.register(new RegisterProductRequest("아메리카노", 3800L).toServiceRequest());
        ProductResponse secondProductResponse = registerProductUseCase.register(new RegisterProductRequest("카페라떼", 3800L).toServiceRequest());
        ProductResponse thirdProductResponse = registerProductUseCase.register(new RegisterProductRequest("콜드브루", 3800L).toServiceRequest());

        // 재고 충전 // 세 번째 상품만 재고 1개 충전
        Long addStock = 30L;
        addStockUseCase.addStock(new AddStockRequest(firstProductResponse.getId(), addStock).toAddStock());
        addStockUseCase.addStock(new AddStockRequest(secondProductResponse.getId(), addStock).toAddStock());
        addStockUseCase.addStock(new AddStockRequest(thirdProductResponse.getId(), addStock).toAddStock());


        OrderRequest orderRequest = new OrderRequest(
                memberResponse.getId(),
                List.of(
                        new OrderProductRequest(firstProductResponse.getId(), 5L),
                        new OrderProductRequest(secondProductResponse.getId(), 5L),
                        new OrderProductRequest(thirdProductResponse.getId(), 5L)
                ),
                "POINT");

        // when
        OrderResponse orderResponse = placeOrderUseCase.order(orderRequest.toOrderCommand());

        // then
        List<OrderProduct> findOrderProducts = orderProductRepository.findByOrderId(orderResponse.getOrderId());
        assertThat(findOrderProducts.size()).isEqualTo(3);

        // orderId가 하나이므로, 하나여야 한다.
        Set<Long> orderIdSet = findOrderProducts.stream()
                .map(OrderProduct::getOrderId)
                .collect(Collectors.toSet());
        assertThat(orderIdSet.size()).isEqualTo(1);

    }

}
