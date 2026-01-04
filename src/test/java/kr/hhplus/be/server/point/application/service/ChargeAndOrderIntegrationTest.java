package kr.hhplus.be.server.point.application.service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import kr.hhplus.be.server.config.SpringBootTestSupport;
import kr.hhplus.be.server.payment.domain.model.PaymentState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChargeAndOrderIntegrationTest extends SpringBootTestSupport  {

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
    }

    @Test
    @DisplayName("포인트를 충전하고, 주문할 수 있다.")
    void chargeAndOrderIntegrationTest() {
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


        // 결제
        given()
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
        .then()
            .statusCode(200)
            .body("totalAmount", equalTo(10000))
            .body("paymentState", equalTo(PaymentState.PAYMENT_COMPLETE.name()))
            .body("orderId", equalTo(orderId.intValue()))
            .body("id", equalTo(paymentId.intValue()));

//        // 포인트 확인
        given()
            .pathParams("memberId", memberId)
        .when()
            .get("/api/point/{memberId}")
        .then()
            .statusCode(200)
            .body("memberId", equalTo(memberId.intValue()))
            .body("point", equalTo(290000));
    }

}