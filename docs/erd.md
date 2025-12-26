# ERD - e-commerce 서비스

<img src="image/ERD.png">

## 데이터베이스 설계 원칙

### 외래키(FK) 제약조건 미사용
이 프로젝트에서는 **논리적으로는 참조 관계를 가지지만, 물리적으로 FK 제약조건을 설정하지 않습니다.**

**이유:**
- 유연성: 삭제/수정 시 CASCADE 동작으로 인한 예상치 못한 데이터 손실 방지
- 관리
  - 애플리케이션 레벨에서 참조 무결성 제어
  - 데이터를 삭제는 논리적 삭제이고, 추후 관리자에 의한 복구 기능이 추가될 수 있음.
  - 복구 구현시 참조된 데이터도 같이 복구해야 하므로, 개발의 복잡성이 커짐. 
  - 추후 테이블이 더 늘어날 수 있음. 
    - 정합성 문제 및 성능 지연 유발 가능


**참조 무결성 보장 방법:**
- 애플리케이션 레벨에서 트랜잭션 처리
- Soft Delete 패턴 사용 (deleted 플래그)
- 비즈니스 로직에서 명시적 검증

### 논리적 참조 관계 명세
아래는 코드 레벨에서 유지해야 하는 논리적 관계입니다:

| 부모 테이블  | 자식 테이블         | 참조 컬럼                    | 관계  | 설명         |
|---------|----------------|--------------------------|-----|------------|
| PRODUCT | STOCK          | stock.product_id         | 1:1 | 상품-재고      |
| PRODUCT | ORDER_PROUCT   | order_product.product_id | 1:N | 상품-주문_상품   |
| order   | ORDER_PROUCT   | order_product.order_id | 1:N | 주문-주문_상품   |
| ORDERS  | PAYMENT        | payment.order_id         | 1:1 | 주문-결제      |
| MEMBER  | ORDERS         | orders.member_id         | 1:N | 회원-주문      |
| MEMBER  | POINT          | point.member_id          | 1:1 | 회원-포인트     |
| POINT   | POINT_HISTORY  | point_history.point_id   | 1:N | 포인트-포인트_내역 |
| MEMBER  | POINT_HISTORY  | point_history.member_id  | 1:N | 회원-포인트이력   |
| COUPON  | COUPON_HISTORY | coupon_history.coupon_id | 1:N | 쿠폰-쿠폰발행내역  |


## 테이블 개요

| 테이블명 | 설명 | 주요 관계 |
|----------|------|---------|
| MEMBER | 회원 정보 | - |
| PRODUCT | 상품 정보 | STOCK과 1:1 |
| STOCK | 상품 재고 | PRODUCT와 1:1 |
| ORDERS | 주문 정보 | MEMBER와 N:1 |
| ORDER_PRODUCT | 주문-상품 관계 | ORDERS, PRODUCT와 N:1 |
| PAYMENT | 결제 정보 | ORDERS와 1:1 |
| POINT | 포인트 | MEMBER와 N:1 |
| POINT_HISTORY | 포인트 변동 기록 | POINT와 N:1 |
| COUPON | 쿠폰 | - |
| COUPON_HISTORY | 쿠폰 사용 기록 | MEMBER, COUPON과 N:1 |


## 관계도 설명

### 1:1 관계
- **PRODUCT ↔ STOCK**: 한 상품은 정확히 하나의 재고 정보
- **ORDERS ↔ PAYMENT**: 한 주문은 정확히 하나의 결제 정보

### 1:N 관계
- **MEMBER → ORDERS**: 한 회원이 여러 주문 가능
- **MEMBER → POINT**: 한 회원이 여러 포인트 기록
- **ORDERS → ORDER_PRODUCT**: 한 주문에 여러 상품 포함
- **PRODUCT → ORDER_PRODUCT**: 한 상품이 여러 주문에 포함
- **COUPON → COUPON_HISTORY**: 한 쿠폰이 여러 번 사용 기록

## SQL 생성 스크립트

<details>
<summary><b>MYSQL-DDL</b></summary>

```mysql
CREATE TABLE PRODUCT (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(255) NOT NULL,
                         price BIGINT NOT NULL DEFAULT 0,
                         created_date DATETIME NOT NULL DEFAULT current_timestamp,
                         modified_date DATETIME NOT NULL DEFAULT current_timestamp,
                         deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE STOCK (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       product_id BIGINT NOT NULL,
                       quantity BIGINT NOT NULL DEFAULT 0,
                       created_date DATETIME NOT NULL DEFAULT current_timestamp,
                       modified_date DATETIME NOT NULL DEFAULT current_timestamp,
                       deleted TINYINT NOT NULL DEFAULT 0
);


CREATE TABLE MEMBER (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name CHAR(30) NOT NULL,
                        birthDate CHAR(8) ,
                        address CHAR(50) ,
                        created_date DATETIME NOT NULL DEFAULT current_timestamp,
                        modified_date DATETIME NOT NULL DEFAULT current_timestamp,
                        deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE POINT (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       member_id BIGINT NOT NULL UNIQUE,
                       point BIGINT NOT NULL DEFAULT 0,
                       created_date DATETIME NOT NULL DEFAULT current_timestamp,
                       modified_date DATETIME NOT NULL DEFAULT current_timestamp,
                       deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE POINT_HISTORY (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       member_id BIGINT NOT NULL,
                       point_id BIGINT NOT NULL,
                       point_amount BIGINT NOT NULL,
                       state CHAR(20) NOT NULL,
                       created_date DATETIME NOT NULL DEFAULT current_timestamp,
                       total_point BIGINT NOT NULL
);


CREATE TABLE ORDERS (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       member_id BIGINT NOT NULL,
                       orderDate DATETIME NOT NULL DEFAULT current_timestamp
);

CREATE TABLE ORDER_PRODUCT (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        product_id BIGINT NOT NULL,
                        order_id BIGINT NOT NULL,
                        quantity BIGINT NOT NULL,
                        created_date DATETIME NOT NULL DEFAULT current_timestamp
);

CREATE TABLE PAYMENT (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        order_id BIGINT NOT NULL,
                        total_amount BIGINT NOT NULL,
                        payment_state CHAR(30) NOT NULL,
                        created_date DATETIME NOT NULL DEFAULT current_timestamp,
                        modified_date DATETIME NOT NULL DEFAULT current_timestamp
);

```

</details>