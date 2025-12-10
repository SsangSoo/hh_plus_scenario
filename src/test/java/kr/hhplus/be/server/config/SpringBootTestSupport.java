package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.member.repository.MemberRepository;
import kr.hhplus.be.server.domain.member.service.MemberService;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.orderproduct.repository.OrderProductRepository;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.payment.facade.service.PaymentDataTransportClient;
import kr.hhplus.be.server.domain.payment.facade.service.PaymentService;
import kr.hhplus.be.server.domain.point.repository.PointRepository;
import kr.hhplus.be.server.domain.point.service.PointService;
import kr.hhplus.be.server.domain.pointhistory.repository.PointHistoryRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.stock.repository.StockRepository;
import kr.hhplus.be.server.domain.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public abstract class SpringBootTestSupport {

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected PointService pointService;

    @Autowired
    protected PointRepository pointRepository;

    @Autowired
    protected PointHistoryRepository pointHistoryRepository;

    @Autowired
    protected OrderService orderService;

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

}
