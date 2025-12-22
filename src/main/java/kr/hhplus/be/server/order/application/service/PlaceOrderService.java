package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.member.domain.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.order.application.usecase.PlaceOrderUseCase;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.application.dto.OrderCommand;
import kr.hhplus.be.server.order.presentation.dto.response.OrderResponse;
import kr.hhplus.be.server.orderproduct.application.usecase.RegisterOrderProductUseCase;
import kr.hhplus.be.server.payment.application.service.PaymentService;
import kr.hhplus.be.server.payment.application.dto.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.stock.application.usecase.DeductedStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final DeductedStockUseCase deductedStockUseCase;
    private final RegisterOrderProductUseCase registerOrderProductUseCase;
    private final PaymentUseCase paymentUseCase;


    @Transactional
    public OrderResponse order(OrderCommand orderCommand) {
        // 회원 찾기
        Member member =  memberRepository.retrieve(orderCommand.memberId());

        // 상품 찾기
        Product product = productRepository.findById(orderCommand.orderProductRequest().productId());

        // 상품 Id로 재고 찾고 차감 // x-lock으로 조회
        deductedStockUseCase.deductedStock(product.getId(), orderCommand.orderProductRequest().quantity());

        // 주문 생성
        Order order = orderRepository.save(Order.create(member.getId()));

        // 주문 상품 생성
        registerOrderProductUseCase.register(orderCommand.orderProductRequest(), order.getId());

        Long totalAmount = calculatePoint(product.getPrice(), orderCommand.orderProductRequest().quantity());

        PaymentResponse paymentResponse = paymentUseCase.pay(new PaymentServiceRequest(Order.create(member.getId()).getId(), totalAmount, orderCommand.paymentMethod(), member.getId()));

        return OrderResponse.from(order, paymentResponse);
    }



    private Long calculatePoint(Long productPrice, Long quantity) {
        return productPrice * quantity;
    }

}