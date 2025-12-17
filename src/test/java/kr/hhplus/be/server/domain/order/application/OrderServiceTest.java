package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.config.Util;
import kr.hhplus.be.server.member.infrastructure.persistence.MemberJpaEntity;
import kr.hhplus.be.server.member.repository.MemberRepository;
import kr.hhplus.be.server.member.application.dto.RegisterMemberCommand;
import kr.hhplus.be.server.order.application.service.PlaceOrderService;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.presentation.dto.request.OrderProductRequest;
import kr.hhplus.be.server.order.presentation.dto.request.OrderRequest;
import kr.hhplus.be.server.order.presentation.dto.request.PaymentMethod;
import kr.hhplus.be.server.order.application.dto.OrderResult;
import kr.hhplus.be.server.orderproduct.entity.OrderProduct;
import kr.hhplus.be.server.orderproduct.service.OrderProductService;
import kr.hhplus.be.server.orderproduct.service.response.OrderProductResponse;
import kr.hhplus.be.server.payment.entity.Payment;
import kr.hhplus.be.server.payment.entity.PaymentState;
import kr.hhplus.be.server.payment.facade.service.PaymentService;
import kr.hhplus.be.server.payment.facade.service.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.facade.service.response.PaymentResponse;
import kr.hhplus.be.server.point.entity.Point;
import kr.hhplus.be.server.point.service.request.ChargePoint;
import kr.hhplus.be.server.product.entity.Product;
import kr.hhplus.be.server.product.repository.ProductRepository;
import kr.hhplus.be.server.product.service.request.RegisterProductServiceRequest;
import kr.hhplus.be.server.stock.entity.Stock;
import kr.hhplus.be.server.stock.service.StockService;
import kr.hhplus.be.server.stock.service.response.StockResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    PaymentService paymentService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderProductService orderProductService;

    @Mock
    ProductRepository productRepository;

    @Mock
    StockService stockService;

    PlaceOrderService orderService;


    @BeforeEach
    void setUp() {
        orderService = new PlaceOrderService(memberRepository, productRepository, orderRepository, stockService, orderProductService, paymentService);
    }

    @Test
    @DisplayName("회원이 존재하지 않으면 주문은 실패한다.")
    void failToOrderWhenMemberNotFound() {
        // given
        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        OrderRequest request = new OrderRequest(3L, orderProductRequest, "POINT");

        given(memberRepository.findMemberByIdAndDeletedFalse(3L)).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> orderService.order(request.toServiceRequest()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_FOUND_MEMBER.getMessage());
    }

//    @Test
//    @DisplayName("포인트가 존재하지 않으면 주문은 실패한다.")
//    void failToOrderWhenPointNotFound() {
//        // given
//        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
//        OrderRequest orderRequest = new OrderRequest(3L, orderProductRequest, "POINT");
//
//        RegisterMemberServiceRequest memberServiceRequest = new RegisterMemberServiceRequest("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
//        Member member = Member.register(memberServiceRequest);
//        Util.setId(member, 3L);
//
//        given(memberRepository.findMemberByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(member));
//        given(pointRepository.findPointByMemberIdAndDeletedFalse(anyLong())).willReturn(Optional.empty());
//
//        // when // then
//        assertThatThrownBy(() -> orderService.order(orderRequest.toServiceRequest()))
//                .isInstanceOf(BusinessLogicRuntimeException.class)
//                .hasMessage(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT.getMessage());
//    }

    @Test
    @DisplayName("상품이 존재하지 않으면 주문은 실패한다.")
    void failToOrderWhenProductNotFound() {
        // given
        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        OrderRequest orderRequest = new OrderRequest(3L, orderProductRequest, "POINT");

        RegisterMemberCommand memberServiceRequest = new RegisterMemberCommand("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
        MemberJpaEntity member = MemberJpaEntity.register(memberServiceRequest);
        Util.setId(member, 3L);

        Point point = Point.register(member.getId());
        point.charge(new ChargePoint(member.getId(), 30000L));
        Util.setId(point, 3L);

        given(memberRepository.findMemberByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(member));
        given(productRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> orderService.order(orderRequest.toServiceRequest()))
                .isInstanceOf(BusinessLogicRuntimeException.class)
                .hasMessage(BusinessLogicMessage.NOT_FOUND_PRODUCT.getMessage());
    }

//    @Test
//    @DisplayName("상품에 대한 재고가 존재하지 않으면 주문은 실패한다.")
//    void failToOrderWhenStockOfProductNotFound() {
//        // given
//        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
//        OrderRequest orderRequest = new OrderRequest(3L, orderProductRequest, "POINT");
//
//        RegisterMemberServiceRequest memberServiceRequest = new RegisterMemberServiceRequest("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
//        Member member = Member.register(memberServiceRequest);
//        Util.setId(member, 3L);
//
//        Point point = Point.register(member.getId());
//        point.charge(new ChargePoint(member.getId(), 30000L));
//        Util.setId(point, 3L);
//
//        RegisterProductServiceRequest registerProductServiceRequest = new RegisterProductServiceRequest("아메리카노", 3500L);
//        Product product = Product.register(registerProductServiceRequest);
//        Util.setId(product, 1L);
//
//        given(memberRepository.findMemberByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(member));
//        given(pointRepository.findPointByMemberIdAndDeletedFalse(anyLong())).willReturn(Optional.of(point));
//        given(productRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(product));
//        given(stockRepository.findByProductIdAndDeletedFalse(anyLong())).willReturn(Optional.empty());
//
//        // when // then
//        assertThatThrownBy(() -> orderService.order(orderRequest.toServiceRequest()))
//                .isInstanceOf(BusinessLogicRuntimeException.class)
//                .hasMessage(BusinessLogicMessage.NOT_FOUND_STOCK.getMessage());
//    }



//    @Test
//    @DisplayName("상품에 대한 재고가 부족하면 주문은 실패한다.")
//    void failToOrderWhenStockOfProductNotEnough() {
//        // given
//        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
//        OrderRequest orderRequest = new OrderRequest(3L, orderProductRequest, "POINT");
//
//        RegisterMemberServiceRequest memberServiceRequest = new RegisterMemberServiceRequest("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
//        Member member = Member.register(memberServiceRequest);
//        Util.setId(member, 3L);
//
//        Point point = Point.register(member.getId());
//        point.charge(new ChargePoint(member.getId(), 30000L));
//        Util.setId(point, 3L);
//
//        RegisterProductServiceRequest registerProductServiceRequest = new RegisterProductServiceRequest("아메리카노", 3500L);
//        Product product = Product.register(registerProductServiceRequest);
//        Util.setId(product, 1L);
//
//        Stock stock = Stock.register(product.getId());
//        Util.setId(stock, 1L);
//        stock.addStock(1L);
//
//        given(memberRepository.findMemberByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(member));
//        given(pointRepository.findPointByMemberIdAndDeletedFalse(anyLong())).willReturn(Optional.of(point));
//        given(productRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(product));
//        given(stockRepository.findByProductIdAndDeletedFalse(anyLong())).willReturn(Optional.of(stock));
//
//        // when // then
//        assertThatThrownBy(() -> orderService.order(orderRequest.toServiceRequest()))
//                .isInstanceOf(BusinessLogicRuntimeException.class)
//                .hasMessage(BusinessLogicMessage.STOCK_IS_NOT_ENOUGH.getMessage());
//    }


//    @Test
//    @DisplayName("주문 상품에 대한 금액이 포인트보다 부족하면 주문은 실패한다.")
//    void failToOrderWhenPointNotEnough() {
//        // given
//        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
//        OrderRequest orderRequest = new OrderRequest(3L, orderProductRequest, "POINT");
//
//        RegisterMemberServiceRequest memberServiceRequest = new RegisterMemberServiceRequest("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
//        Member member = Member.register(memberServiceRequest);
//        Util.setId(member, 3L);
//
//        Point point = Point.register(member.getId());
//        point.charge(new ChargePoint(member.getId(), 5000L));
//        Util.setId(point, 3L);
//
//        RegisterProductServiceRequest registerProductServiceRequest = new RegisterProductServiceRequest("아메리카노", 3500L);
//        Product product = Product.register(registerProductServiceRequest);
//        Util.setId(product, 1L);
//
//        Stock stock = Stock.register(product.getId());
//        Util.setId(stock, 1L);
//        stock.addStock(4L);
//
//        given(memberRepository.findMemberByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(member));
//        given(pointRepository.findPointByMemberIdAndDeletedFalse(anyLong())).willReturn(Optional.of(point));
//        given(productRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(product));
//        given(stockRepository.findByProductIdAndDeletedFalse(anyLong())).willReturn(Optional.of(stock));
//
//        // when // then
//        assertThatThrownBy(() -> orderService.order(orderRequest.toServiceRequest()))
//                .isInstanceOf(BusinessLogicRuntimeException.class)
//                .hasMessage(BusinessLogicMessage.POINT_IS_NOT_ENOUGH.getMessage());
//    }

    @Test
    @DisplayName("주문을 하면 결제가 이루어진다.")
    void createPaymentWhenOrderIsPlaced() {
        // given
        // 주문 상품 및 주문 요청
        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        OrderRequest orderRequest = new OrderRequest(3L, orderProductRequest, "POINT");

        // 회원
        RegisterMemberCommand memberServiceRequest = new RegisterMemberCommand("name", LocalDate.of(1990, 1, 1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "주소");
        MemberJpaEntity member = MemberJpaEntity.register(memberServiceRequest);
        Util.setId(member, 3L);

        // 상품
        RegisterProductServiceRequest registerProductServiceRequest = new RegisterProductServiceRequest("아메리카노", 3500L);
        Product product = Product.register(registerProductServiceRequest);
        Util.setId(product, 1L);

        // 재고
        Stock stock = Stock.register(product.getId());
        Util.setId(stock, 1L);
        stock.addStock(4L);

        // 회원, 포인트, 상품, 재고 Mock 처리
        given(memberRepository.findMemberByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(member));
        given(productRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(product));
        given(stockService.deductedStock(anyLong(), anyLong())).willReturn(StockResponse.from(stock));

        // 주문
        Order order = Order.create(member.getId());
        Util.setId(order, 1L);
        given(orderRepository.save(any())).willReturn(order);

        // 주문 상품
        OrderProduct orderProduct = OrderProduct.register(orderProductRequest.toServiceRequest(), order.getId());
        Util.setId(orderProduct, 1L);
        given(orderProductService.register(any(), any())).willReturn(OrderProductResponse.from(orderProduct));

        // 총계 확인
        long totalPoint = product.getPrice() * orderProductRequest.quantity();
        assertThat(totalPoint).isEqualTo(7000L);


        // 결제
        Payment payment = Payment.register(new PaymentServiceRequest(order.getId(), product.getPrice() * orderProductRequest.quantity(), PaymentMethod.POINT, member.getId()));
        Util.setId(payment, 1L);
        PaymentResponse paymentResponse = PaymentResponse.from(payment);
        given(paymentService.pay(any())).willReturn(paymentResponse);


        // when
        OrderResult orderResponse = orderService.order(orderRequest.toServiceRequest());

        // then
        assertThat(orderResponse.getOrderId()).isEqualTo(order.getId());
        assertThat(orderResponse.getOrderDate()).isEqualTo(order.getOrderDate().withNano(0));
        assertThat(orderResponse.getPaymentId()).isEqualTo(payment.getId());
        assertThat(orderResponse.getMemberId()).isEqualTo(member.getId());
        assertThat(orderResponse.getTotalAmount()).isEqualTo(totalPoint);
        assertThat(orderResponse.getPaymentState()).isEqualTo(PaymentState.PAYMENT_COMPLETE.name());
    }

}