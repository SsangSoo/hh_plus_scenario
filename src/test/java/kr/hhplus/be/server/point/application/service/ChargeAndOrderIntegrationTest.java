package kr.hhplus.be.server.point.application.service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import kr.hhplus.be.server.config.SpringBootTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
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
        long memberId = given()
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
//        given()
//            .contentType(ContentType.JSON)
//                .body()

        // 상품 등록

        // 주문

        // 포인트 확인

    }

}
