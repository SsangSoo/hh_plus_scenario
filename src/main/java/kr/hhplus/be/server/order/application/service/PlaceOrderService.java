package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.member.infrastructure.persistence.MemberJpaEntity;
import kr.hhplus.be.server.member.repository.MemberRepository;
import kr.hhplus.be.server.order.application.usecase.PlaceOrderUseCase;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.orderproduct.service.OrderProductService;
import kr.hhplus.be.server.order.application.dto.OrderCommand;
import kr.hhplus.be.server.order.application.dto.OrderResult;
import kr.hhplus.be.server.payment.facade.service.PaymentService;
import kr.hhplus.be.server.payment.facade.service.request.PaymentServiceRequest;
import kr.hhplus.be.server.payment.facade.service.response.PaymentResponse;
import kr.hhplus.be.server.product.entity.Product;
import kr.hhplus.be.server.product.repository.ProductRepository;
import kr.hhplus.be.server.stock.service.StockService;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.common.exeption.business.BusinessLogicRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final StockService stockService;
    private final OrderProductService orderProductService;
    private final PaymentService paymentService;



    @Transactional
    public OrderResult order(OrderCommand orderCommand) {
        // 회원 찾기
        MemberJpaEntity member = findMember(orderCommand.memberId());

        // 상품 찾기
        Product product = findProduct(orderCommand.orderProductRequest().productId());

        // 상품 Id로 재고 찾고 차감 // x-lock으로 조회
        stockService.deductedStock(product.getId(), orderCommand.orderProductRequest().quantity());

        // 주문 생성 -> 클린 아키텍처로 구조 변경
        Order order = orderRepository.save(Order.create(member.getId()));

        // 주문 상품 생성
        orderProductService.register(orderCommand.orderProductRequest(), order.getId());

        Long totalAmount = calculatePoint(product.getPrice(), orderCommand.orderProductRequest().quantity());

        PaymentResponse paymentResponse = paymentService.pay(new PaymentServiceRequest(Order.create(member.getId()).getId(), totalAmount, orderCommand.paymentMethod(), member.getId()));

        return OrderResult.from(order, paymentResponse);
    }


    private MemberJpaEntity findMember(Long memberId) {
        return memberRepository.findMemberByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));
    }

    private Product findProduct(Long productId) {
        return productRepository.findByIdAndDeletedFalse(productId)
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));
    }

    private Long calculatePoint(Long productPrice, Long quantity) {
        return productPrice * quantity;
    }

}