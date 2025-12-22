package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.usecase.RegisterProductUseCase;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.presentation.dto.request.RegisterProductRequest;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class RegisterProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    StockRepository stockRepository;

    RegisterProductUseCase registerProductUseCase;


    @BeforeEach
    void setUp() {
        registerProductUseCase = new RegisterProductService(productRepository, stockRepository);
    }

    @Test
    @DisplayName("상품을 생성한다.")
    void registerProductTest() {
        // given : 상품 생성시 필요한 요청 값 설정
        Long productId = 1L;
        Long stockId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;

        // 요청시 생성된 DTO
        RegisterProductRequest request = new RegisterProductRequest(productName, productPrice);
        // 서비스 계층 DTO로 변환
        RegisterProductServiceRequest serviceRequest = request.toServiceRequest();

        // Product 셋업
        Product product = Product.create(serviceRequest);
        product.assignId(productId);

        // Stock 셋업
        Stock stock = Stock.create(productId);
        stock.assignId(stockId);

        // repository 호출 부분 Mocking
        given(productRepository.save(any())).willReturn(product);
        given(stockRepository.save(any())).willReturn(stock);

        // when : 상품 생성
        ProductResponse response = registerProductUseCase.register(serviceRequest);

        // then : 상품 생성 확인
        assertThat(response.getProductName()).isEqualTo(productName);
        assertThat(response.getPrice()).isEqualTo(productPrice);

        // 메서드 호출 여부
        then(productRepository).should(times(1)).save(any());
        then(stockRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("상품 생성한 후의 재고는 0이다.")
    void whenRegisterProductStockIsZeroTest() {
        // given : 상품 생성시 필요한 요청 값 설정
        Long productId = 1L;
        Long stockId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;

        // 요청시 생성된 DTO
        RegisterProductRequest request = new RegisterProductRequest(productName, productPrice);
        // 서비스 계층 DTO로 변환
        RegisterProductServiceRequest serviceRequest = request.toServiceRequest();

        // Product 생성
        Product product = Product.create(serviceRequest);
        product.assignId(productId);

        // Stock 생성
        Stock stock = Stock.create(productId);
        stock.assignId(stockId);

        // repository 호출 부분 Mocking
        given(productRepository.save(any())).willReturn(product);
        given(stockRepository.save(any())).willReturn(stock);

        // when : 상품 생성
        ProductResponse response = registerProductUseCase.register(serviceRequest);

        // then : 상품 생성 확인
        assertThat(response.getQuantity()).isZero();
    }







}