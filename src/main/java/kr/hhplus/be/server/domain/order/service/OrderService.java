package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.member.entity.Member;
import kr.hhplus.be.server.domain.member.repository.MemberRepository;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.orderproduct.service.OrderProductService;
import kr.hhplus.be.server.domain.order.service.request.OrderServiceRequest;
import kr.hhplus.be.server.domain.order.service.response.OrderResponse;
import kr.hhplus.be.server.domain.orderproduct.service.response.OrderProductResponse;
import kr.hhplus.be.server.domain.payment.facade.service.PaymentService;
import kr.hhplus.be.server.domain.payment.facade.service.request.PaymentServiceRequest;
import kr.hhplus.be.server.domain.payment.facade.service.response.PaymentResponse;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.stock.service.StockService;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final StockService stockService;
    private final OrderProductService orderProductService;
    private final PaymentService paymentService;

    @Transactional
    public OrderResponse order(OrderServiceRequest request) {
        // 회원 찾기
        Member member = findMember(request.memberId());

        // 상품 찾기
        Product product = findProduct(request.orderProductRequest().productId());

        // 상품 Id로 재고 찾고 차감 // x-lock으로 조회
        stockService.deductedStock(product.getId(), request.orderProductRequest().quantity());

        // 주문 생성
        Order order = orderRepository.save(Order.rigester(member.getId()));

        // 주문 상품 생성
        OrderProductResponse orderProductResponse = orderProductService.register(request.orderProductRequest(), order.getId());

        Long totalAmount = calculatePoint(product.getPrice(), request.orderProductRequest().quantity());

        PaymentResponse paymentResponse = paymentService.pay(new PaymentServiceRequest(order.getId(), totalAmount, request.paymentMethod(), member.getId()));

        return OrderResponse.from(order, paymentResponse);
    }

    private Member findMember(Long memberId) {
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
