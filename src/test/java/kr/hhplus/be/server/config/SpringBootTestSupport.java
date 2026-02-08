package kr.hhplus.be.server.config;

import kr.hhplus.be.server.common.redis.RedisUtil;
import kr.hhplus.be.server.coupon.application.service.issuecoupon.IssueCouponTransactionService;
import kr.hhplus.be.server.coupon.application.usecase.DecreaseCouponUseCase;
import kr.hhplus.be.server.coupon.application.usecase.IssueCouponUseCase;
import kr.hhplus.be.server.coupon.application.usecase.RegisterCouponUseCase;
import kr.hhplus.be.server.coupon.application.usecase.RetrieveCouponUseCase;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.infrastructure.persistence.CouponJpaRepository;
import kr.hhplus.be.server.couponhistory.application.usecase.RegisterCouponHistoryUseCase;
import kr.hhplus.be.server.couponhistory.application.usecase.RetrieveCouponHistoryUseCase;
import kr.hhplus.be.server.couponhistory.domain.repository.CouponHistoryRepository;
import kr.hhplus.be.server.couponhistory.infrastructure.persistence.CouponHistoryJpaRepository;
import kr.hhplus.be.server.member.application.usecase.RegisterMemberUseCase;
import kr.hhplus.be.server.member.application.usecase.RemoveMemberUseCase;
import kr.hhplus.be.server.member.application.usecase.RetrieveMemberUseCase;
import kr.hhplus.be.server.member.domain.repository.MemberRepository;
import kr.hhplus.be.server.member.infrastructure.persistence.MemberJpaRepository;
import kr.hhplus.be.server.order.application.usecase.PlaceOrderUseCase;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.infrastructure.persistence.OrderJpaRepository;
import kr.hhplus.be.server.orderproduct.application.usecase.RegisterOrderProductUseCase;
import kr.hhplus.be.server.orderproduct.domain.repository.OrderProductRepository;
import kr.hhplus.be.server.orderproduct.infrastructure.persistence.OrderProductJpaRepository;
import kr.hhplus.be.server.outbox.application.usecase.RegisterOutboxUseCase;
import kr.hhplus.be.server.outbox.application.usecase.RemoveOutboxUseCase;
import kr.hhplus.be.server.outbox.application.usecase.RetrieveOutboxUseCase;
import kr.hhplus.be.server.outbox.domain.repository.OutboxRepository;
import kr.hhplus.be.server.outbox.infrastructure.OutboxJpaRepository;
import kr.hhplus.be.server.payment.application.facade.PaymentFacade;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.application.usecase.RegisterPaymentUseCase;
import kr.hhplus.be.server.payment.application.usecase.RetrievePaymentUseCase;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.payment.infrastructure.persistence.PaymentJpaRepository;
import kr.hhplus.be.server.point.application.service.charge.ChargePointTransactionService;
import kr.hhplus.be.server.point.application.usecase.ChargePointUseCase;
import kr.hhplus.be.server.point.application.usecase.RetrievePointUseCase;
import kr.hhplus.be.server.point.application.usecase.UsePointUseCase;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.infrastructure.persistence.PointJpaRepository;
import kr.hhplus.be.server.pointhistory.domain.repository.PointHistoryRepository;
import kr.hhplus.be.server.pointhistory.infrastructure.persistence.PointHistoryJpaRepository;
import kr.hhplus.be.server.product.application.usecase.RegisterProductUseCase;
import kr.hhplus.be.server.product.application.usecase.RemoveProductUseCase;
import kr.hhplus.be.server.product.application.usecase.popular.RegisterPopularProductUseCase;
import kr.hhplus.be.server.product.application.usecase.popular.RetrievePopularProductUseCase;
import kr.hhplus.be.server.product.application.usecase.RetrieveProductUseCase;
import kr.hhplus.be.server.product.domain.repository.ProductQueryRepository;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.infrastructure.persistence.ProductJpaRepository;
import kr.hhplus.be.server.stock.application.service.AddStockTransactionService;
import kr.hhplus.be.server.stock.application.service.DeductedStockTransactionService;
import kr.hhplus.be.server.stock.application.usecase.AddStockUseCase;
import kr.hhplus.be.server.stock.application.usecase.DeductedStockUseCase;
import kr.hhplus.be.server.stock.application.usecase.RetrieveStockUseCase;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import kr.hhplus.be.server.stock.infrastructure.persistence.StockJpaRepository;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


@SpringBootTest
@Import(TestAsyncConfig.class)
public abstract class SpringBootTestSupport {

    // Member
    @Autowired
    protected RegisterMemberUseCase registerMemberUseCase;

    @Autowired
    protected RemoveMemberUseCase removeMemberUseCase;

    @Autowired
    protected RetrieveMemberUseCase retrieveMemberUseCase;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MemberJpaRepository memberJpaRepository;


    // Point
    @Autowired
    protected ChargePointUseCase chargePointUseCase;

    @Autowired
    protected ChargePointTransactionService chargePointTransactionService;

    @Autowired
    protected RetrievePointUseCase retrievePointUseCase;

    @Autowired
    protected PointRepository pointRepository;

    @Autowired
    protected PointJpaRepository pointJpaRepository;

    @Autowired
    protected UsePointUseCase usePointUseCase;



    // PointHistory
    @Autowired
    protected PointHistoryRepository pointHistoryRepository;

    @Autowired
    protected PointHistoryJpaRepository pointHistoryJpaRepository;


    // Order
    @Autowired
    protected PlaceOrderUseCase placeOrderUseCase;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected OrderJpaRepository orderJpaRepository;


    // OrderProduct
    @Autowired
    protected OrderProductRepository orderProductRepository;

    @Autowired
    protected RegisterOrderProductUseCase registerOrderProductUseCase;

    @Autowired
    protected OrderProductJpaRepository orderProductJpaRepository;


    // Payment

    @Autowired
    protected PaymentFacade paymentFacade;

    @Autowired
    protected RegisterPaymentUseCase registerPaymentUseCase;

    @Autowired
    protected RetrievePaymentUseCase retrievePaymentUseCase;

    @Autowired
    protected PaymentUseCase paymentUseCase;

    @Autowired
    protected PaymentRepository paymentRepository;

    @Autowired
    protected PaymentJpaRepository paymentJpaRepository;


//    @Autowired
    @MockitoBean
    protected PaymentDataTransportUseCase paymentDataTransportUseCase;


    // Stock
    @Autowired
    protected AddStockUseCase addStockUseCase;

    @Autowired
    protected AddStockTransactionService addStockTransactionService;

    @Autowired
    protected DeductedStockUseCase deductedStockUseCase;

    @Autowired
    protected DeductedStockTransactionService deductedStockTransactionService;

    @Autowired
    protected RetrieveStockUseCase retrieveStockUseCase;

    @Autowired
    protected StockRepository stockRepository;

    @Autowired
    protected StockJpaRepository stockJpaRepository;




    // Product
    @Autowired
    protected RegisterProductUseCase registerProductUseCase;

    @Autowired
    protected RemoveProductUseCase removeProductUseCase;

    @Autowired
    protected RetrieveProductUseCase retrieveProductUseCase;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected ProductJpaRepository productJpaRepository;

    // Popular
    @Autowired
    protected RegisterPopularProductUseCase registerPopularProductUseCase;

    @Autowired
    protected RetrievePopularProductUseCase retrievePopularProductUseCase;

    @Autowired
    protected ProductQueryRepository productQueryRepository;


    // Coupon
    @Autowired
    protected IssueCouponUseCase issueCouponUseCase;

    @Autowired
    protected IssueCouponTransactionService issueCouponTransactionService;

    @Autowired
    protected RegisterCouponUseCase registerCouponUseCase;

    @Autowired
    protected RetrieveCouponUseCase retrieveCouponUseCase;

    @Autowired
    protected CouponRepository couponRepository;

    @Autowired
    protected CouponJpaRepository couponJpaRepository;

    @Autowired
    protected DecreaseCouponUseCase decreaseCouponUseCase;


    // couponHistory

    @Autowired
    protected RegisterCouponHistoryUseCase registerCouponHistoryUseCase;

    @Autowired
    protected RetrieveCouponHistoryUseCase retrieveCouponHistoryUseCase;

    @Autowired
    protected CouponHistoryRepository couponHistoryRepository;

    @Autowired
    protected CouponHistoryJpaRepository couponHistoryJpaRepository;




    // outbox
    @Autowired
    protected OutboxRepository outboxRepository;

    @Autowired
    protected OutboxJpaRepository outboxJpaRepository;

    @Autowired
    protected RegisterOutboxUseCase registerOutboxUseCase;

    @Autowired
    protected RemoveOutboxUseCase removeOutboxUseCase;

    @Autowired
    protected RetrieveOutboxUseCase retrieveOutboxUseCase;


    // Redis
    @Autowired
    protected RedissonClient redissonClient;

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @Autowired
    protected RedisUtil redisUtil;

    // Event
    @Autowired
    protected ApplicationEventPublisher eventPublisher;
}
