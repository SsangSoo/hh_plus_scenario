package kr.hhplus.be.server.payment.presentation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.payment.application.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerIntegrationTest extends SpringBootTestSupport {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        pointJpaRepository.deleteAllInBatch();
        pointHistoryJpaRepository.deleteAllInBatch();
        orderJpaRepository.deleteAllInBatch();
        stockJpaRepository.deleteAllInBatch();
        productJpaRepository.deleteAllInBatch();
        memberJpaRepository.deleteAllInBatch();
        orderProductJpaRepository.deleteAllInBatch();

        Set<String> keys = stringRedisTemplate.keys("idempotency:*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    @Test
    @DisplayName("중복 결제 요청 방지 Test")
    void PreventDuplicatePaymentRequestsTest() throws InterruptedException {

        // 회원 생성
        Long memberId = given()
                .contentType(ContentType.JSON)
                .body("""
                          {
                            "name": "김철수",
                            "address": "주소"
                          }
                        """)
                .when()
                .post("/api/member")
                .then()
                .statusCode(201)
                .body("name", equalTo("김철수"))
                .body("address", equalTo("주소"))
                .extract()
                .jsonPath()
                .getLong("id");

        // 포인트 충전
        given()
                .contentType(ContentType.JSON)
                .body("""
                               {
                                  "memberId": %d,
                                  "chargePoint": 300000
                               }
                        """.formatted(memberId)
                )
                .when()
                .post("/api/point/charge")
                .then()
                .statusCode(200)
                .body("point", equalTo(300000));

        // 상품 등록
        Long productId = given()
                .contentType(ContentType.JSON)
                .body("""
               {
                   "productName": "상품1",
                   "price": 10000
                 }
            """)
                .when()
                .post("/api/product")
                .then()
                .statusCode(201)
                .body("productName", equalTo("상품1"))
                .body("price", equalTo(10000))
                .extract()
                .jsonPath()
                .getLong("id");

        // 재고 증가
        given()
                .contentType(ContentType.JSON)
                .body("""
               {
                  "productId": %d,
                  "addStock": 100
               }
            """.formatted(productId)
                )
                .when()
                .post("/api/stock")
                .then()
                .statusCode(200)
                .body("quantity", equalTo(100));

        // 주문
        JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .body("""
                                   {
                                      "memberId": %d,
                                      "orderProductsRequest": [
                                            {
                                                "productId" : %d,
                                                "quantity" :1
                                            }
                                      ],
                                      "paymentMethod": "POINT"
                                   }
                            """.formatted(memberId, productId)
                )
                .when()
                .post("/api/order")
                .then()
                .statusCode(200)
                .body("totalAmount", equalTo(10000))
                .body("paymentState", equalTo(PaymentState.PENDING.name()))
                .body("memberId", equalTo(memberId.intValue()))
                .extract()
                .jsonPath();

        Long orderId = jsonPath.getLong("orderId");
        Long paymentId = jsonPath.getLong("paymentId");


        // 중복 결제 요청 테스트
        String idempotencyKey = UUID.randomUUID().toString();

        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 결과를 저장할 thread-safe 리스트
        List<PaymentResponse> successResponsesList = new CopyOnWriteArrayList<>();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    PaymentResponse paymentResponse = given()
                        .header("idempotency_key", idempotencyKey)
                        .contentType(ContentType.JSON)
                        .body("""
                           {
                              "orderId": %d,
                              "memberId": %d,
                              "paymentId": %d
                           }
                        """.formatted(orderId, memberId, paymentId)
                        )
                    .when()
                        .post("/api/pay")
                        .as(PaymentResponse.class);
                    successResponsesList.add(paymentResponse);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        assertThat(successResponsesList.size()).isEqualTo(1);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);
    }
}