package kr.hhplus.be.server.product.application.service.popular;

import kr.hhplus.be.server.common.redis.RedisUtil;
import kr.hhplus.be.server.product.domain.repository.ProductQueryRepository;
import kr.hhplus.be.server.product.infrastructure.persistence.query.ProductProjection;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RetrievePopularProductServiceTest {

    @Mock
    RedisUtil redisUtil;

    @Mock
    ProductQueryRepository productQueryRepository;

    RetrievePopularProductService retrievePopularProductService;

    @BeforeEach
    void setUp() {
        retrievePopularProductService = new RetrievePopularProductService(redisUtil, productQueryRepository);
    }

    @Test
    @DisplayName("판매량 순서를 보장하며, 인기 상품들을 보여준다.")
    void getPopularProductsOrderedBySalesCount() {
        // given
        Set<String> sortedIds = new LinkedHashSet<>(List.of("2", "3", "1"));
        given(redisUtil.getZSetReverseRange(anyString(), anyLong(), anyLong())).willReturn(sortedIds);

        List<ProductProjection> productProjections = List.of(
                new ProductProjection(1L, "아메리카노", 4000L, 40L),
                new ProductProjection(2L, "카페라떼", 4500L, 40L),
                new ProductProjection(3L, "아이스티", 4000L, 40L)
        );

        given(productQueryRepository.retrieveProductsInIds(anyList())).willReturn(productProjections);

        // when
        List<ProductResponse> productResponses = retrievePopularProductService.retrievePopularProducts();

        // then
        List<Long> idList = productResponses.stream()
                .map(pp -> pp.getId())
                .toList();

        assertThat(idList).containsExactly(2L, 3L, 1L);
        assertThat(productResponses)
                .extracting("id", "productName", "price", "quantity")
                .containsExactly(
                        tuple(2L, "카페라떼", 4500L, 40L),
                        tuple(3L, "아이스티", 4000L, 40L),
                        tuple(1L, "아메리카노", 4000L, 40L)
                );
    }


}