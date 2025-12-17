package kr.hhplus.be.server.config;

import kr.hhplus.be.server.member.repository.MemberRepository;
import kr.hhplus.be.server.member.application.service.RegisterMemberService;
import kr.hhplus.be.server.order.application.service.PlaceOrderService;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.orderproduct.repository.OrderProductRepository;
import kr.hhplus.be.server.payment.facade.payment_method.BankTransferPayment;
import kr.hhplus.be.server.payment.facade.payment_method.CardPayment;
import kr.hhplus.be.server.payment.facade.payment_method.PointPayment;
import kr.hhplus.be.server.payment.repository.PaymentRepository;
import kr.hhplus.be.server.payment.facade.service.PaymentDataTransportClient;
import kr.hhplus.be.server.payment.facade.service.PaymentService;
import kr.hhplus.be.server.point.repository.PointRepository;
import kr.hhplus.be.server.point.service.PointService;
import kr.hhplus.be.server.pointhistory.repository.PointHistoryRepository;
import kr.hhplus.be.server.product.repository.ProductRepository;
import kr.hhplus.be.server.product.service.ProductService;
import kr.hhplus.be.server.stock.repository.StockRepository;
import kr.hhplus.be.server.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public abstract class SpringBootTestSupport {

    @Autowired
    protected RegisterMemberService memberService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected PointService pointService;

    @Autowired
    protected PointRepository pointRepository;

    @Autowired
    protected PointHistoryRepository pointHistoryRepository;

    @Autowired
    protected PlaceOrderService placeOrderService;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected PaymentService paymentService;

    @Autowired
    protected PaymentRepository paymentRepository;

    @Autowired
    protected PaymentDataTransportClient paymentDataTransportClient;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected OrderProductRepository orderProductRepository;

    @Autowired
    protected StockService stockService;

    @Autowired
    protected StockRepository stockRepository;

    @Autowired
    protected BankTransferPayment bankTransferPayment;

    @Autowired
    protected CardPayment cardPayment;

    @Autowired
    protected PointPayment pointPayment;
}
