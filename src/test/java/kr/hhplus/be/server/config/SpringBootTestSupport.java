package kr.hhplus.be.server.config;

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
import kr.hhplus.be.server.payment.application.service.payment_method.BankTransferPayment;
import kr.hhplus.be.server.payment.application.service.payment_method.CardPayment;
import kr.hhplus.be.server.payment.application.service.payment_method.PointPayment;
import kr.hhplus.be.server.payment.application.usecase.PaymentDataTransportUseCase;
import kr.hhplus.be.server.payment.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.point.application.usecase.ChargePointUseCase;
import kr.hhplus.be.server.point.application.usecase.RetrievePointUseCase;
import kr.hhplus.be.server.point.domain.repository.PointRepository;
import kr.hhplus.be.server.point.infrastructure.persistence.PointJpaRepository;
import kr.hhplus.be.server.pointhistory.domain.repository.PointHistoryRepository;
import kr.hhplus.be.server.pointhistory.infrastructure.persistence.PointHistoryJpaRepository;
import kr.hhplus.be.server.product.application.usecase.RegisterProductUseCase;
import kr.hhplus.be.server.product.application.usecase.RemoveProductUseCase;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.product.infrastructure.persistence.ProductJpaRepository;
import kr.hhplus.be.server.stock.application.usecase.AddStockUseCase;
import kr.hhplus.be.server.stock.application.usecase.DeductedStockUseCase;
import kr.hhplus.be.server.stock.application.usecase.RetrieveStockUseCase;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import kr.hhplus.be.server.stock.infrastructure.persistence.StockJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
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
    protected RetrievePointUseCase retrievePointUseCase;

    @Autowired
    protected PointRepository pointRepository;

    @Autowired
    protected PointJpaRepository pointJpaRepository;


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
    protected PaymentUseCase paymentUseCase;

    @Autowired
    protected PaymentRepository paymentRepository;

    @Autowired
    protected PaymentDataTransportUseCase paymentDataTransportUseCase;

    @Autowired
    protected BankTransferPayment bankTransferPayment;

    @Autowired
    protected CardPayment cardPayment;

    @Autowired
    protected PointPayment pointPayment;


    // Stock
    @Autowired
    protected AddStockUseCase addStockUseCase;

    @Autowired
    protected DeductedStockUseCase deductedStockUseCase;

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
    protected ProductRepository productRepository;

    @Autowired
    protected ProductJpaRepository productJpaRepository;











}
