package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.application.usecase.RemoveProductUseCase;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RemoveProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    StockRepository stockRepository;

    RemoveProductUseCase removeProductUseCase;

    @BeforeEach
    void setUp() {
        removeProductUseCase = new RemoveProductService(productRepository, stockRepository);
    }

    @Test
    @DisplayName("상품을 삭제할 수 있다. 재고도 함께 삭제된다.")
    void deleteProductTest() {
        // given : 상품 생성 및 재고 생성
        Long productId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;

        // 재고 생성
        Long stockId = 1L;

        // Product 생성
        Product product = Product.create(new RegisterProductServiceRequest(productName, productPrice));
        product.assignId(productId);

        // Stock 생성
        Stock stock = Stock.create(productId);
        stock.assignId(stockId);

        // removeProduct 내부 repository 호출 메서드 Mocking처리
        given(productRepository.findById(anyLong())).willReturn(product);
        given(stockRepository.findByProductId(anyLong())).willReturn(stock);

        // when : 상품 삭제
        removeProductUseCase.removeProduct(productId);

        // then
        then(productRepository).should(times(1)).remove(productId);
        then(stockRepository).should(times(1)).remove(stockId);
    }

}