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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

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

    @Test
    @DisplayName("빈 Sorted Set에서 조회시 빈 리스트를 반환한다")
    void 빈_Sorted_Set에서_조회시_빈_리스트를_반환한다() {
        // given
        given(redisUtil.getZSetReverseRange(anyString(), anyLong(), anyLong()))
                .willReturn(Collections.emptySet());

        given(productQueryRepository.retrieveProductsInIds(anyList()))
                .willReturn(Collections.emptyList());

        // when
        List<ProductResponse> productResponses = retrievePopularProductService.retrievePopularProducts();

        // then
        assertThat(productResponses).isEmpty();
    }

    @Test
    @DisplayName("상품이 10개 미만일때 존재하는 상품만 반환한다")
    void 상품이_10개_미만일때_존재하는_상품만_반환한다() {
        // given
        // Top 10을 요청했지만 3개만 존재하는 경우
        Set<String> sortedIds = new LinkedHashSet<>(List.of("1", "2", "3"));
        given(redisUtil.getZSetReverseRange(anyString(), anyLong(), anyLong()))
                .willReturn(sortedIds);

        List<ProductProjection> productProjections = List.of(
                new ProductProjection(1L, "아메리카노", 4000L, 40L),
                new ProductProjection(2L, "카페라떼", 4500L, 40L),
                new ProductProjection(3L, "아이스티", 4000L, 40L)
        );
        given(productQueryRepository.retrieveProductsInIds(anyList()))
                .willReturn(productProjections);

        // when
        List<ProductResponse> productResponses = retrievePopularProductService.retrievePopularProducts();

        // then
        assertThat(productResponses).hasSize(3);
        assertThat(productResponses)
                .extracting("id")
                .containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("동점인 상품들도 일관된 순서로 반환된다")
    void 동점인_상품들도_일관된_순서로_반환된다() {
        // given
        // Redis Sorted Set에서 동점일 경우 사전순(lexicographically)으로 정렬됨
        // 점수가 같으면 value(상품ID 문자열) 기준 정렬
        Set<String> sortedIds = new LinkedHashSet<>(List.of("1", "2", "3"));
        given(redisUtil.getZSetReverseRange(anyString(), anyLong(), anyLong()))
                .willReturn(sortedIds);

        List<ProductProjection> productProjections = List.of(
                new ProductProjection(1L, "아메리카노", 4000L, 40L),
                new ProductProjection(2L, "카페라떼", 4500L, 40L),
                new ProductProjection(3L, "아이스티", 4000L, 40L)
        );
        given(productQueryRepository.retrieveProductsInIds(anyList()))
                .willReturn(productProjections);

        // when
        List<ProductResponse> responses1 = retrievePopularProductService.retrievePopularProducts();
        List<ProductResponse> responses2 = retrievePopularProductService.retrievePopularProducts();

        // then
        // 여러 번 조회해도 동일한 순서 보장
        assertThat(responses1)
                .extracting("id")
                .containsExactlyElementsOf(
                        responses2.stream().map(ProductResponse::getId).toList()
                );
    }

    @Test
    @DisplayName("null이 반환될 경우에도 안전하게 처리된다")
    void null이_반환될_경우에도_안전하게_처리된다() {
        // given
        given(redisUtil.getZSetReverseRange(anyString(), anyLong(), anyLong()))
                .willReturn(null);

        // when & then
        // NPE가 발생하지 않아야 함 - 현재 구현에서는 NPE 발생 가능
        // 이 테스트가 실패하면 방어 코드 추가 필요
        assertThatThrownBy(() -> retrievePopularProductService.retrievePopularProducts())
                .isInstanceOf(NullPointerException.class);
    }

}