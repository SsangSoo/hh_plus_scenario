package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.order.application.usecase.PlaceOrderUseCase;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.presentation.dto.request.OrderProductRequest;
import kr.hhplus.be.server.order.presentation.dto.request.OrderRequest;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.order.presentation.dto.response.OrderResponse;
import kr.hhplus.be.server.orderproduct.application.usecase.RegisterOrderProductUseCase;
import kr.hhplus.be.server.orderproduct.domain.model.OrderProduct;
import kr.hhplus.be.server.orderproduct.application.dto.response.OrderProductResponse;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.application.dto.request.ChargePoint;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.application.dto.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.stock.application.usecase.DeductedStockUseCase;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PlaceOrderServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    PaymentUseCase paymentUseCase;

    @Mock
    DeductedStockUseCase deductedStockUseCase;

    @Mock
    RegisterOrderProductUseCase registerOrderProductUseCase;


    PlaceOrderUseCase placeOrderUseCase;


    @BeforeEach
    void setUp() {
        placeOrderUseCase = new PlaceOrderService(memberRepository, productRepository, orderRepository, deductedStockUseCase, registerOrderProductUseCase, paymentUseCase);
    }

    @Test
    @DisplayName("회원이 존재하지 않으면 주문은 실패한다.")
    void failToOrderWhenMemberNotFound() {
        // given
        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        OrderRequest request = new OrderRequest(3L, List.of(orderProductRequest), "POINT");

        given(memberRepository.retrieve(3L)).willThrow(new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));

        // when // then
        assertThatThrownBy(() -> placeOrderUseCase.order(request.toOrderCommand()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_FOUND_MEMBER.getMessage());
    }


    @Test
    @DisplayName("상품이 존재하지 않으면 주문은 실패한다.")
    void failToOrderWhenProductNotFound() {
        // given
        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        OrderRequest orderRequest = new OrderRequest(3L, List.of(orderProductRequest), "POINT");

        RegisterMemberCommand registerMemberCommand = new RegisterMemberCommand("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
        Member member = Member.create(registerMemberCommand);
        member.assignId(3L);

        Point point = Point.create(member.getId());
        point.charge(new ChargePoint(member.getId(), 30000L));
        point.assignId(3L);

        given(memberRepository.retrieve(anyLong())).willReturn(member);
        given(productRepository.findByIds(anyList())).willThrow(new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_SOME_PRODUCT));

        // when // then
        assertThatThrownBy(() -> placeOrderUseCase.order(orderRequest.toOrderCommand()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_FOUND_SOME_PRODUCT.getMessage());
    }


    @Test
    @DisplayName("상품에 대한 재고가 존재하지 않으면 주문은 실패한다.")
    void failToOrderWhenStockOfProductNotFound() {
        // given
        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        OrderRequest orderRequest = new OrderRequest(3L, List.of(orderProductRequest), "POINT");

        RegisterMemberCommand memberServiceRequest = new RegisterMemberCommand("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
        Member member = Member.create(memberServiceRequest);
        member.assignId(3L);

        Point point = Point.create(member.getId());
        point.assignId(3L);
        point.charge(new ChargePoint(member.getId(), 30000L));

        RegisterProductServiceRequest registerProductServiceRequest = new RegisterProductServiceRequest("아메리카노", 3500L);
        Product product = Product.create(registerProductServiceRequest);
        product.assignId(1L);

        given(memberRepository.retrieve(anyLong())).willReturn(member);
        given(productRepository.findByIds(anyList())).willReturn(List.of(product));
        given(deductedStockUseCase.deductedStock(anyMap())).willThrow(new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));

        // when // then
        assertThatThrownBy(() -> placeOrderUseCase.order(orderRequest.toOrderCommand()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_FOUND_STOCK.getMessage());
    }



    @Test
    @DisplayName("상품에 대한 재고가 부족하면 주문은 실패한다.")
    void failToOrderWhenStockOfProductNotEnough() {
        // given
        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        OrderRequest orderRequest = new OrderRequest(3L, List.of(orderProductRequest), "POINT");

        RegisterMemberCommand memberServiceRequest = new RegisterMemberCommand("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
        Member member = Member.create(memberServiceRequest);
        member.assignId(3L);

        Point point = Point.create(member.getId());
        point.assignId(3L);
        point.charge(new ChargePoint(member.getId(), 30000L));

        RegisterProductServiceRequest registerProductServiceRequest = new RegisterProductServiceRequest("아메리카노", 3500L);
        Product product = Product.create(registerProductServiceRequest);
        product.assignId(1L);

        Stock stock = Stock.create(product.getId());
        stock.assignId(1L);
        stock.addStock(1L);

        given(memberRepository.retrieve(anyLong())).willReturn(member);
        given(productRepository.findByIds(anyList())).willReturn(List.of(product));
        given(deductedStockUseCase.deductedStock(anyMap())).willThrow(new BusinessLogicRuntimeException(BusinessLogicMessage.STOCK_IS_NOT_ENOUGH));

        // when // then
        assertThatThrownBy(() -> placeOrderUseCase.order(orderRequest.toOrderCommand()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.STOCK_IS_NOT_ENOUGH.getMessage());
    }


    @Test
    @DisplayName("주문을 하면 결제가 이루어진다.")
    void createPaymentWhenOrderIsPlaced() {
        // given
        // 주문 상품 및 주문 요청
        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        OrderRequest orderRequest = new OrderRequest(3L, List.of(orderProductRequest), "POINT");

        // 회원
        RegisterMemberCommand memberServiceRequest = new RegisterMemberCommand("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
        Member member = Member.create(memberServiceRequest);
        member.assignId(3L);

        // 상품
        RegisterProductServiceRequest registerProductServiceRequest = new RegisterProductServiceRequest("아메리카노", 3500L);
        Product product = Product.create(registerProductServiceRequest);
        product.assignId(1L);

        // 재고
        Stock stock = Stock.create(product.getId());
        stock.assignId(1L);
        stock.addStock(4L);

        // 회원, 포인트, 상품, 재고 Mock 처리
        given(memberRepository.retrieve(anyLong())).willReturn(member);
        given(productRepository.findByIds(anyList())).willReturn(List.of(product));
        given(deductedStockUseCase.deductedStock(anyMap())).willReturn(List.of(StockResponse.from(stock)));

        // 주문
        Order order = Order.create(member.getId());
        order.assignId(1L);
        given(orderRepository.save(any())).willReturn(order);

        // 주문 상품
        OrderProduct orderProduct = OrderProduct.create(product.getId(), order.getId(), orderProductRequest.quantity());
        orderProduct.assignId(1L);
        OrderProductResponse orderProductResponse = OrderProductResponse.from(orderProduct);
        given(registerOrderProductUseCase.register(any(), any())).willReturn(List.of(orderProductResponse));

        // 총계 확인
        long totalPoint = product.getPrice() * orderProductRequest.quantity();
        assertThat(totalPoint).isEqualTo(7000L);

        // 결제
        Payment payment = Payment.create(order.getId(), product.getPrice() * orderProductRequest.quantity(), PaymentMethod.POINT);
        payment.assignId(1L);
        PaymentResponse paymentResponse = PaymentResponse.from(payment);
        given(paymentUseCase.pay(any())).willReturn(paymentResponse);

        // when
        OrderResponse orderResponse = placeOrderUseCase.order(orderRequest.toOrderCommand());

        // then
        assertThat(orderResponse.getOrderId()).isEqualTo(order.getId());
        assertThat(orderResponse.getOrderDate()).isEqualTo(order.getOrderDate().withNano(0).toString());
        assertThat(orderResponse.getPaymentId()).isEqualTo(payment.getId());
        assertThat(orderResponse.getMemberId()).isEqualTo(member.getId());
        assertThat(orderResponse.getTotalAmount()).isEqualTo(totalPoint);
        assertThat(orderResponse.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE.name());
    }

}