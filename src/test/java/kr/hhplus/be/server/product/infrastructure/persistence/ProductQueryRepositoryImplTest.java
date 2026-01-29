package kr.hhplus.be.server.product.infrastructure.persistence;

import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.member.presentation.dto.response.MemberResponse;
import kr.hhplus.be.server.order.application.dto.OrderCommand;
import kr.hhplus.be.server.order.presentation.dto.response.OrderResponse;
import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.infrastructure.persistence.query.ProductProjection;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import kr.hhplus.be.server.stock.application.dto.request.AddStock;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class ProductQueryRepositoryImplTest extends SpringBootTestSupport {

    @AfterEach
    void tearDown() {
        productJpaRepository.deleteAllInBatch();
        stockJpaRepository.deleteAllInBatch();
        orderJpaRepository.deleteAllInBatch();
        orderProductJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("존재하지 않는 상품 Id를 조회할 경우, Optional.empty()를 반환한다.")
    void ifRetrieveProductByNotExistsProductIdTest() {
        // given
        // 상품 생성
        registerProductUseCase.register(new RegisterProductServiceRequest("아메리카노", 4000L));

        // when
        Optional<ProductProjection> productProjection = productQueryRepository.retrieveProductJoinStock(400L);

        // then
        assertThat(productProjection).isNotPresent();
        assertThat(productProjection).isEmpty();

    }


    @Test
    @DisplayName("상품 Id를 통해서 상품과 재고를 함께 join해서 조회할 수 있다.")
    void retrieveProductJoinStockTest() {
        // given
        // 상품 생성
        ProductResponse americano = registerProductUseCase.register(new RegisterProductServiceRequest("아메리카노", 4000L));

        // 재고 충전
        StockResponse stockResponse = addStockUseCase.addStock(new AddStock(americano.getId(), 100L));

        // when
        ProductProjection productProjection = productQueryRepository.retrieveProductJoinStock(americano.getId())
                .orElseThrow();

        // then
        assertThat(productProjection.getId()).isEqualTo(americano.getId());
        assertThat(productProjection.getId()).isEqualTo(stockResponse.getProductId());
        assertThat(productProjection.getProductName()).isEqualTo(americano.getProductName());
        assertThat(productProjection.getPrice()).isEqualTo(americano.getPrice());
        assertThat(productProjection.getQuantity()).isEqualTo(stockResponse.getQuantity());
    }



    @Test
    @DisplayName("orderId를 통해서 상품의 Id를 가져올 수 있다.")
    void retrieveProductIdByOrderIdFromOrderProduct() {
        // given
        // 회원 생성
        MemberResponse memberResponse = registerMemberUseCase.register(new RegisterMemberCommand("쌩수", "199010101", "오스트레일리아"));

        // 상품 생성
        ProductResponse americano = registerProductUseCase.register(new RegisterProductServiceRequest("아메리카노", 4000L));
        ProductResponse caffeLatte = registerProductUseCase.register(new RegisterProductServiceRequest("카페라떼", 4500L));
        ProductResponse iceTea = registerProductUseCase.register(new RegisterProductServiceRequest("아이스티", 4000L));

        // 재고 충전
        addStockUseCase.addStock(new AddStock(americano.getId(), 100L));
        addStockUseCase.addStock(new AddStock(caffeLatte.getId(), 100L));
        addStockUseCase.addStock(new AddStock(iceTea.getId(), 100L));

        // 주문
        OrderResponse orderResponse = placeOrderUseCase.order(new OrderCommand(memberResponse.getId(), List.of(
                new OrderProductServiceRequest(americano.getId(), 2L),
                new OrderProductServiceRequest(iceTea.getId(), 4L)
        )));

        // when
        List<Long> findProductIds = productQueryRepository.retrieveProductIdWithOrderId(orderResponse.getOrderId());

        // then
        assertThat(findProductIds.size()).isEqualTo(2);
        assertThat(findProductIds).containsOnly(americano.getId(), iceTea.getId());
    }

    @Test
    @DisplayName("orderId를 통해서 상품의 Id를 가져올 수 있다.")
    void retrieveProductByProductIdListFromProduct() {
        // given
        // 상품 생성
        ProductResponse americano = registerProductUseCase.register(new RegisterProductServiceRequest("아메리카노", 4000L));
        ProductResponse caffeLatte = registerProductUseCase.register(new RegisterProductServiceRequest("카페라떼", 4500L));
        ProductResponse iceTea = registerProductUseCase.register(new RegisterProductServiceRequest("아이스티", 4000L));

        // 재고 충전
        addStockUseCase.addStock(new AddStock(americano.getId(), 100L));
        addStockUseCase.addStock(new AddStock(caffeLatte.getId(), 100L));
        addStockUseCase.addStock(new AddStock(iceTea.getId(), 100L));

        // when
        List<ProductProjection> productProjections = productQueryRepository.retrieveProductsInIds(List.of(americano.getId(), caffeLatte.getId(), iceTea.getId()));

        // then
        assertThat(productProjections.size()).isEqualTo(3);
        assertThat(productProjections)
                .extracting("id", "productName", "price", "quantity")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(americano.getId(), "아메리카노", 4000L, 100L),
                        Tuple.tuple(caffeLatte.getId(), "카페라떼", 4500L, 100L),
                        Tuple.tuple(iceTea.getId(), "아이스티", 4000L, 100L)
                );
    }

}