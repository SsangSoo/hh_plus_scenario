package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.member.entity.Member;
import kr.hhplus.be.server.domain.member.repository.MemberRepository;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.service.request.OrderServiceRequest;
import kr.hhplus.be.server.domain.order.service.response.OrderResponse;
import kr.hhplus.be.server.domain.orderproduct.entity.OrderProduct;
import kr.hhplus.be.server.domain.orderproduct.repository.OrderProductRepository;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.payment.service.request.PaymentServiceRequest;
import kr.hhplus.be.server.domain.payment.service.response.PaymentResponse;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.pointhistory.entity.PointHistory;
import kr.hhplus.be.server.domain.pointhistory.entity.State;
import kr.hhplus.be.server.domain.pointhistory.repository.PointHistoryRepository;
import kr.hhplus.be.server.domain.pointhistory.service.request.RegisterPointHistoryRequest;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.stock.entity.Stock;
import kr.hhplus.be.server.domain.stock.repository.StockRepository;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicMessage;
import kr.hhplus.be.server.global.exeption.business.BusinessLogicRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PaymentService paymentService;

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Transactional
    public OrderResponse order(OrderServiceRequest request) {
        // 회원 찾기
        Member member = memberRepository.findMemberByIdAndDeletedFalse(request.memberId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER));

        // 포인트 찾기
        Point point = pointRepository.findPointByMemberIdAndDeletedFalse(member.getId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_MEMBER_POINT));

        // 상품 찾기
        Product product = productRepository.findByIdAndDeletedFalse(request.orderProductRequest().productId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_PRODUCT));

        // 상품 Id로 재고 찾기
        Stock findStock = stockRepository.findByProductIdAndDeletedFalse(product.getId())
                .orElseThrow(() -> new BusinessLogicRuntimeException(BusinessLogicMessage.NOT_FOUND_STOCK));
        // 재고 확인
        findStock.validateStock(request.orderProductRequest().quantity());

        // 총계 계산
        Long totalAmount = calculatePoint(product, request.orderProductRequest().quantity());

        //--- 검증 끝 ---//

        // 주문 생성
        Order order = Order.rigester(member.getId());
        orderRepository.save(order);

        // 주문 상품 생성
        OrderProduct orderProduct = OrderProduct.register(product.getId(), order.getId(), request.orderProductRequest().quantity());
        orderProductRepository.save(orderProduct);

        // 포인트 차감
        point.use(totalAmount);

        // 재고 차감 // 동시성 고려
        stockRepository.findbyIdForUpdate(findStock.getId());

        findStock.deductedStock(request.orderProductRequest().quantity());

        // 포인트 차감 내역 생성
        PointHistory pointHistory = PointHistory.register(new RegisterPointHistoryRequest(member.getId(), point.getId(), totalAmount, point.getModifiedDate(), point.getPoint()), State.USE);
        pointHistoryRepository.save(pointHistory);

        // 결제
        PaymentResponse paymentResponse = paymentService.register(new PaymentServiceRequest(order.getId(), totalAmount));

        return OrderResponse.from(order, paymentResponse);
    }

    private Long calculatePoint(Product product, Long quantity) {
        return product.getPrice() * quantity;
    }

}
