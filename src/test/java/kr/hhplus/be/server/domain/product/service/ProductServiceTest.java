package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
import kr.hhplus.be.server.domain.product.controller.request.RegisterProductRequest;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.service.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.domain.product.service.response.ProductResponse;
import kr.hhplus.be.server.domain.stock.entity.Stock;
import kr.hhplus.be.server.domain.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.config.Util.setId;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    StockRepository stockRepository;

    ProductService productService;


    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, stockRepository);
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
        Product product = Product.register(serviceRequest);
        setId(product, productId);

        // Stock 셋업
        Stock stock = Stock.register(productId);
        setId(stock, stockId);

        // repository 호출 부분 Mocking
        given(productRepository.save(any())).willReturn(product);
        given(stockRepository.save(any())).willReturn(stock);

        // when : 상품 생성
        ProductResponse response = productService.registerProduct(serviceRequest);

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
        Product product = Product.register(serviceRequest);
        setId(product, productId);

        // Stock 생성
        Stock stock = Stock.register(productId);
        setId(stock, stockId);

        // repository 호출 부분 Mocking
        given(productRepository.save(any())).willReturn(product);
        given(stockRepository.save(any())).willReturn(stock);

        // when : 상품 생성
        ProductResponse response = productService.registerProduct(serviceRequest);

        // then : 상품 생성 확인
        assertThat(response.getQuantity()).isZero();
    }

    @Test
    @DisplayName("상품을 삭제할 수 있다.")
    void deleteProductTest() {
        // given : 상품 생성 및 재고 생성
        Long productId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;

        // 재고 생성
        Long stockId = 1L;

        // Product 생성
        Product product = Product.register(productName, productPrice);
        setId(product, productId);

        // Stock 생성
        Stock stock = Stock.register(productId);
        setId(stock, stockId);


        // removeProduct 내부 repository 호출 메서드 Mocking처리
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(stockRepository.findByProductId(anyLong())).willReturn(Optional.of(stock));

        // when : 상품 삭제
        productService.removeProduct(productId);

        // then : 상품/재고의 삭제 확인
        assertThat(product.getDeleted()).isTrue();
        assertThat(stock.getDeleted()).isTrue();

    }

    @Test
    @DisplayName("상품을 삭제한 후엔 상품과 재고를 찾을 수 없다.")
    void deleteProductAfterProductNotFoundTest() {
        // given : 상품 생성 및 재고 생성
        Long productId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;

        // 재고 생성
        Long stockId = 1L;

        // Product 생성
        Product product = Product.register(productName, productPrice);
        setId(product, productId);

        // Stock 생성
        Stock stock = Stock.register(productId);
        setId(stock, stockId);

        // removeProduct 내부 repository 호출 메서드 Mocking처리
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(stockRepository.findByProductId(anyLong())).willReturn(Optional.of(stock));

        // 상품 삭제
        productService.removeProduct(productId);

        // 상품/재고의 삭제 확인
        assertThat(product.getDeleted()).isTrue();
        assertThat(stock.getDeleted()).isTrue();

        // when // then
        assertThatThrownBy(() -> productService.retrieveProduct(productId))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage("상품을 찾을 수 없습니다.");

    }


    @Test
    @DisplayName("상품을 조회할 수 있다.")
    void retrieveProductTest() {
        // given : 상품 조회 검증시 확인할 값들을 설정(id, 상품 이름, 상품 가격, 재고)
        Long productId = 1L;
        String productName = "고급볼펜";
        Long productPrice = 1000L;
        Long productQuantity = 23L;

        Product product = Product.register(productName, productPrice);
        setId(product, productId);

        // id로 상품 조회
        given(productRepository.findByIdAndDeletedFalse(anyLong()))
                .willReturn(Optional.of(product));

        // 상품 id로 재고 조회
        given(productRepository.retrieveStockByProductId(anyLong()))
                .willReturn(Optional.of(productQuantity));


        // when : service 에서 상품 조회 호출
        ProductResponse response =  productService.retrieveProduct(1L);

        // then : given에서 검증하고자 했던 값들 확인
        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response.getProductName()).isEqualTo(productName);
        assertThat(response.getPrice()).isEqualTo(productPrice);
        assertThat(response.getQuantity()).isEqualTo(productQuantity);

        // 메서드 호출 횟수 검증
        then(productRepository).should(times(1)).findByIdAndDeletedFalse(productId);
        then(productRepository).should(times(1)).retrieveStockByProductId(productId);
    }


    @Test
    @DisplayName("요청받은 상품Id로 상품을 찾을 수 없는 경우 예외가 발생한다.")
    void retrieveProductShouldThrowExceptionWhenProductDoesNotExistTest() {
        // given : 존재하지 않는 상품 Id값
        Long notFoundProductId = 3L;

        // 상품 id로 재고 조회
        given(productRepository.findByIdAndDeletedFalse(notFoundProductId))
                .willReturn(Optional.empty());

        // when // then : service 에서 존재하지 않는 상품 id로 조회시 예외가 발생한다.
        Assertions.assertThatThrownBy(() -> productService.retrieveProduct(notFoundProductId))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage("상품을 찾을 수 없습니다.");

        // 메서드 호출 횟수 검증
        then(productRepository).should(times(1)).findByIdAndDeletedFalse(notFoundProductId);
        then(productRepository).should(times(0)).retrieveStockByProductId(notFoundProductId);
    }



}