package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.member.domain.model.Member;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.order.application.usecase.PlaceOrderUseCase;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.application.dto.OrderCommand;
import kr.hhplus.be.server.order.presentation.dto.response.OrderResponse;
import kr.hhplus.be.server.orderproduct.application.dto.request.OrderProductServiceRequest;
import kr.hhplus.be.server.orderproduct.application.dto.response.OrderProductResponse;
import kr.hhplus.be.server.orderproduct.application.usecase.RegisterOrderProductUseCase;
import kr.hhplus.be.server.payment.application.dto.request.RegisterPaymentInfoRequest;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.application.usecase.RegisterPaymentUseCase;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.stock.application.usecase.DeductedStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final RegisterOrderProductUseCase registerOrderProductUseCase;

    private final DeductedStockUseCase deductedStockUseCase;

    private final RegisterPaymentUseCase registerPaymentUseCase;


    @Transactional
    public OrderResponse order(OrderCommand orderCommand) {
        // 회원 찾기
        Member member =  memberRepository.retrieve(orderCommand.memberId());

        // 상품 찾기
        List<Long> productIdList = orderCommand.orderProductsRequest()
                .stream()
                .map(OrderProductServiceRequest::productId)
                .sorted()
                .toList();

        // 상품들 Id 리스트로 찾기 // 찾을 수 없는 상품이 존재할 경우 예외발생
        List<Product> findProductList = productRepository.findByIds(productIdList);

        // 상품 Id와 수량으로 확인
        Map<Long, Long> orderProductMap = orderCommand.orderProductsRequest()
                .stream()
                .collect(Collectors.toMap(OrderProductServiceRequest::productId, OrderProductServiceRequest::quantity));

        // 상품 Id로 재고 찾고 차감
        deductedStockUseCase.deductedStock(orderProductMap);

        // 주문 생성
        Order order = orderRepository.save(Order.create(member.getId()));

        // 주문 상품 생성
        List<OrderProductResponse> responseList = registerOrderProductUseCase.register(orderCommand.orderProductsRequest(), order.getId());

        Long totalAmount = calculatePoint(findProductList, responseList);

        PaymentResponse paymentResponse = registerPaymentUseCase.registerPaymentInfo(new RegisterPaymentInfoRequest(order.getId(), totalAmount, member.getId()));

        return OrderResponse.from(order, paymentResponse);
    }


    private Long calculatePoint(List<Product> productList, List<OrderProductResponse> responseList) {

        // id, price를 map으로 만듬.
        Map<Long, Long> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, Product::getPrice));

        // id, quantity를 map으로 만듬.
        Map<Long, Long> orderProductMap = responseList.stream()
                .collect(Collectors.toMap(OrderProductResponse::getProductId, OrderProductResponse::getQuantity));

        long totalAmount = 0L;

        for (Map.Entry<Long, Long> idPriceEntry : productMap.entrySet()) {
            totalAmount += idPriceEntry.getValue() * orderProductMap.get(idPriceEntry.getKey());
        }

        return totalAmount;
    }

}