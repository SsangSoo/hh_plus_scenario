package kr.hhplus.be.server.product.application.service.popular;

import kr.hhplus.be.server.common.redis.RedisUtil;
import kr.hhplus.be.server.product.application.usecase.popular.RegisterPopularProductUseCase;
import kr.hhplus.be.server.product.domain.repository.ProductQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RegisterPopularProductServiceTest {

    @Mock
    RedisUtil redisUtil;

    @Mock
    ProductQueryRepository productQueryRepository;

    RegisterPopularProductUseCase  registerPopularProductUseCase;

    @BeforeEach
    void setUp() {
        registerPopularProductUseCase = new RegisterPopularProductService(redisUtil, productQueryRepository);
    }


    @Test
    @DisplayName("상품 상품의 종류대로 구매한 만큼 Redis에 저장되어야 한다.")
    void registerProductTest() {
        // given
        List<Long> productIdList = List.of(3L, 5L, 7L, 8L);

        given(productQueryRepository.retrieveProductIdWithOrderId(anyLong())).willReturn(productIdList);

        // when
        registerPopularProductUseCase.registerPopularProducts(1L, 1L);

        // then
        then(redisUtil).should(times(productIdList.size())).incrementZSetScore(any(), any(), anyDouble());

    }

}