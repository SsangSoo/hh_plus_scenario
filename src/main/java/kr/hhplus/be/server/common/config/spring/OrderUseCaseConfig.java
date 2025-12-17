//package kr.hhplus.be.server.common.config.spring;
//
//import kr.hhplus.be.server.domain.member.repository.MemberRepository;
//import kr.hhplus.be.server.domain.order.application.in.PlaceOrderService;
//import kr.hhplus.be.server.domain.order.domain.repository.OrderRepository;
//import kr.hhplus.be.server.domain.orderproduct.service.OrderProductService;
//import kr.hhplus.be.server.domain.payment.facade.service.PaymentService;
//import kr.hhplus.be.server.domain.product.repository.ProductRepository;
//import kr.hhplus.be.server.domain.stock.service.StockService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OrderUseCaseConfig {
//
//    @Bean
//    public PlaceOrderService placeOrderService(
//            MemberRepository memberRepository,
//            ProductRepository productRepository,
//            OrderRepository orderRepository,
//            StockService stockService,
//            OrderProductService orderProductService,
//            PaymentService paymentService
//    ) {
//        return new PlaceOrderService(memberRepository, productRepository, orderRepository, stockService, orderProductService, paymentService);
//    }
//}
