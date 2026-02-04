package kr.hhplus.be.server.product.application.service.popular;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.order.application.dto.OrderCommand;
import kr.hhplus.be.server.order.presentation.dto.response.OrderResponse;
import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import kr.hhplus.be.server.stock.application.dto.request.AddStock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class PopularProductIntegrationTest extends SpringBootTestSupport {

    @AfterEach
    void tearDown() {
        memberJpaRepository.deleteAllInBatch();
        productJpaRepository.deleteAllInBatch();
        stockJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();
        pointHistoryJpaRepository.deleteAllInBatch();
        orderJpaRepository.deleteAllInBatch();
        orderProductJpaRepository.deleteAllInBatch();
        paymentJpaRepository.deleteAllInBatch();
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }

    @Test
    @DisplayName("인기 상품 api 통합 테스트 상품의 판매 건수 순으로 상품이 조회된다.")
    void registerPopularProductIntegrationTest() {
        // given
        // 회원 생성
        MemberResponse member = registerMemberUseCase.register(new RegisterMemberCommand("쌩수", "19900101", "사는 곳"));

        // 상품 생성
        ProductResponse americano = registerProductUseCase.register(new RegisterProductServiceRequest("아메리카노", 4500L));
        ProductResponse iceTea = registerProductUseCase.register(new RegisterProductServiceRequest("아이스티", 4000L));
        ProductResponse caffeLatte = registerProductUseCase.register(new RegisterProductServiceRequest("카페라떼", 4500L));
        ProductResponse vanillaLatte = registerProductUseCase.register(new RegisterProductServiceRequest("바닐라라떼", 5000L));
        ProductResponse macaron = registerProductUseCase.register(new RegisterProductServiceRequest("마카롱", 4000L));

        // 재고 추가
        addStockUseCase.addStock(new AddStock(americano.getId(), 100L));
        addStockUseCase.addStock(new AddStock(iceTea.getId(), 100L));
        addStockUseCase.addStock(new AddStock(caffeLatte.getId(), 100L));
        addStockUseCase.addStock(new AddStock(vanillaLatte.getId(), 100L));
        addStockUseCase.addStock(new AddStock(macaron.getId(), 100L));

        // 포인트 충전
        chargePointUseCase.charge(new ChargePoint(member.getId(), 1000_000L));

        // 상품 구매
            // 아이스티 4번
            // 카페라떼 3번
            // 바닐라라떼 2번
            // 아메리카노 1번
        OrderResponse orderResponse = placeOrderUseCase.order(new OrderCommand(member.getId(), List.of(
                new OrderProductServiceRequest(iceTea.getId(), 1L),
                new OrderProductServiceRequest(caffeLatte.getId(), 1L),
                new OrderProductServiceRequest(vanillaLatte.getId(), 1L),
                new OrderProductServiceRequest(americano.getId(), 1L)
        )));
        PaymentResponse paymentResponse = paymentFacade.payment(new PaymentServiceRequest(orderResponse.getOrderId(), member.getId(), orderResponse.getPaymentId(), null), UUID.randomUUID().toString());

        OrderResponse orderResponse2 = placeOrderUseCase.order(new OrderCommand(member.getId(), List.of(
                new OrderProductServiceRequest(iceTea.getId(), 1L),
                new OrderProductServiceRequest(caffeLatte.getId(), 1L),
                new OrderProductServiceRequest(vanillaLatte.getId(), 1L)
        )));
        PaymentResponse paymentResponse2 = paymentFacade.payment(new PaymentServiceRequest(orderResponse2.getOrderId(), member.getId(), orderResponse2.getPaymentId(), null), UUID.randomUUID().toString());

        OrderResponse orderResponse3 = placeOrderUseCase.order(new OrderCommand(member.getId(), List.of(
                new OrderProductServiceRequest(iceTea.getId(), 1L),
                new OrderProductServiceRequest(caffeLatte.getId(), 1L)
        )));
        PaymentResponse paymentResponse3 = paymentFacade.payment(new PaymentServiceRequest(orderResponse3.getOrderId(), member.getId(), orderResponse3.getPaymentId(), null), UUID.randomUUID().toString());

        OrderResponse orderResponse4 = placeOrderUseCase.order(new OrderCommand(member.getId(), List.of(
                new OrderProductServiceRequest(iceTea.getId(), 1L)
        )));
        PaymentResponse paymentResponse4 = paymentFacade.payment(new PaymentServiceRequest(orderResponse4.getOrderId(), member.getId(), orderResponse4.getPaymentId(), null), UUID.randomUUID().toString());


        // when
        List<ProductResponse> productResponses = retrievePopularProductUseCase.retrievePopularProducts();

        // then
        List<Long> idList = productResponses.stream()
                .map(pp -> pp.getId())
                .toList();

        assertThat(idList).containsExactly(iceTea.getId(), caffeLatte.getId(), vanillaLatte.getId(), americano.getId());
        assertThat(productResponses)
                .extracting("id", "productName", "price", "quantity")
                .containsExactly(
                        tuple(iceTea.getId(), "아이스티", 4000L, 96L),
                        tuple(caffeLatte.getId(), "카페라떼", 4500L, 97L),
                        tuple(vanillaLatte.getId(), "바닐라라떼", 5000L, 98L),
                        tuple(americano.getId(), "아메리카노", 4500L, 99L)
                );
    }

    @Test
    @DisplayName("인기 상품이 없을 때 빈 리스트를 반환한다")
    void 인기_상품이_없을때_빈_리스트를_반환한다() {
        // given - 아무런 구매가 없는 상태

        // when
        List<ProductResponse> productResponses = retrievePopularProductUseCase.retrievePopularProducts();

        // then
        assertThat(productResponses).isEmpty();
    }

    @Test
    @DisplayName("판매량이 같은 상품들의 순서가 일관되게 유지된다")
    void 판매량이_같은_상품들의_순서가_일관되게_유지된다() {
        // given
        // 회원 생성
        MemberResponse member = registerMemberUseCase.register(new RegisterMemberCommand("쌩수", "19900101", "사는 곳"));

        // 상품 생성
        ProductResponse americano = registerProductUseCase.register(new RegisterProductServiceRequest("아메리카노", 4500L));
        ProductResponse iceTea = registerProductUseCase.register(new RegisterProductServiceRequest("아이스티", 4000L));
        ProductResponse caffeLatte = registerProductUseCase.register(new RegisterProductServiceRequest("카페라떼", 4500L));

        // 재고 추가
        addStockUseCase.addStock(new AddStock(americano.getId(), 100L));
        addStockUseCase.addStock(new AddStock(iceTea.getId(), 100L));
        addStockUseCase.addStock(new AddStock(caffeLatte.getId(), 100L));

        // 포인트 충전
        chargePointUseCase.charge(new ChargePoint(member.getId(), 100_000L));

        // 각 상품을 1번씩 구매 (동점 상황)
        OrderResponse orderResponse = placeOrderUseCase.order(new OrderCommand(member.getId(), List.of(
                new OrderProductServiceRequest(americano.getId(), 1L),
                new OrderProductServiceRequest(iceTea.getId(), 1L),
                new OrderProductServiceRequest(caffeLatte.getId(), 1L)
        )));
        paymentFacade.payment(new PaymentServiceRequest(orderResponse.getOrderId(), member.getId(), orderResponse.getPaymentId(), null), UUID.randomUUID().toString());

        // when - 여러 번 조회
        List<ProductResponse> responses1 = retrievePopularProductUseCase.retrievePopularProducts();
        List<ProductResponse> responses2 = retrievePopularProductUseCase.retrievePopularProducts();
        List<ProductResponse> responses3 = retrievePopularProductUseCase.retrievePopularProducts();

        // then - 매번 같은 순서로 반환되어야 함
        List<Long> ids1 = responses1.stream().map(ProductResponse::getId).toList();
        List<Long> ids2 = responses2.stream().map(ProductResponse::getId).toList();
        List<Long> ids3 = responses3.stream().map(ProductResponse::getId).toList();

        assertThat(ids1).containsExactlyElementsOf(ids2);
        assertThat(ids2).containsExactlyElementsOf(ids3);
    }

    @Test
    @DisplayName("Top 10 경계 테스트 - 11개 상품 중 상위 10개만 반환된다")
    void Top_10_경계_테스트_11개_상품_중_상위_10개만_반환된다() {
        // given
        MemberResponse member = registerMemberUseCase.register(new RegisterMemberCommand("쌩수", "19900101", "사는 곳"));

        // 11개 상품 생성
        List<ProductResponse> products = new java.util.ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            ProductResponse product = registerProductUseCase.register(
                    new RegisterProductServiceRequest("상품" + i, 1000L * i));
            products.add(product);
            addStockUseCase.addStock(new AddStock(product.getId(), 100L));
        }

        // 충분한 포인트 충전
        chargePointUseCase.charge(new ChargePoint(member.getId(), 10_000_000L));

        // 각 상품별로 다른 횟수만큼 구매 (상품1: 11회, 상품2: 10회, ..., 상품11: 1회)
        for (int i = 0; i < products.size(); i++) {
            ProductResponse product = products.get(i);
            int purchaseCount = 11 - i;

            for (int j = 0; j < purchaseCount; j++) {
                OrderResponse orderResponse = placeOrderUseCase.order(new OrderCommand(member.getId(), List.of(
                        new OrderProductServiceRequest(product.getId(), 1L)
                )));
                paymentFacade.payment(
                        new PaymentServiceRequest(orderResponse.getOrderId(), member.getId(), orderResponse.getPaymentId(), null),
                        UUID.randomUUID().toString()
                );
            }
        }

        // when
        List<ProductResponse> popularProducts = retrievePopularProductUseCase.retrievePopularProducts();

        // then
        // 정확히 10개만 반환
        assertThat(popularProducts).hasSize(10);

        // 가장 판매량이 적은 상품11(1회 구매)은 제외되어야 함
        List<Long> popularIds = popularProducts.stream().map(ProductResponse::getId).toList();
        assertThat(popularIds).doesNotContain(products.get(10).getId());

        // 상위 10개 상품이 판매량 순서대로 정렬
        assertThat(popularIds.get(0)).isEqualTo(products.get(0).getId()); // 상품1 (11회)
        assertThat(popularIds.get(1)).isEqualTo(products.get(1).getId()); // 상품2 (10회)
    }
}
