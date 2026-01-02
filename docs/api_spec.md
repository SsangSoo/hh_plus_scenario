# API SPEC - e-commerce 서비스


## Error Response Format

- 요청 값이 유효하지 않은 경우, 다음 형식을 따릅니다.

| 필드                | 타입 | 설명                              |
|-------------------|-|---------------------------------|
| `code`            |`string`| HTTP 상태 코드                      |
| `message`         |`string`| 요청값 에러 메세지                      |
| `errors`          |`array`| 개별 필드 에러 목록 (validation 에러의 경우) |
| `errors[].field`  |`string`| 요청 에러가 발생한 필드명                  |
| `errors[].value` |`string`| 사용자가 입력한 값                      |
| `errors[].reason` |`string`| 요청 에러가 발생한 이유                   |





---

### Endpoint
`GET` `/api/product/{productId}`

### Description
상품을 조회한다.

### Header
> none

### Parameter
|parameter|required| type |description|
|-|-|------|-|
|productId|Y| Long |조회할 상품 Id|

### Response
- Status : 200(OK)
```json
[
  {
    "id": 1,
    "productName": "클린 아키텍처",
    "price": 1000,
    "quantity": 1
  }
]
```



---

### Endpoint
`POST` `/api/point/charge`

### Description
포인트를 충전한다.

### Header

> none

### Parameter 
|parameter| required | type  | description |
|-|----------|-------|-------------|
|memberId| Y | Long  | 충전할 사용자의 Id |
|chargePoint| Y | Long | 충전 표인트 |

### Response
- Status : 201(CREATED)
```json
[
  {
    "id": 1,
    "memberId": 1,
    "point": 1000
  }
]
```



---

### Endpoint
`GET` `/api/point/{memberId}`

### Description
포인트를 조회한다.

### Header

> none

### Parameter
|parameter| required | type  | description |
|-|----------|-------|-------------|
|memberId|Y|Long|포인트를 조회할 사용자의 Id|

### Response
- Status : 200(OK)
```json
[
  {
    "id": 1,
    "memberId": 1,
    "point": 1000
  }
]
```



---

### Endpoint
`POST` `/api/order`

### Description
상품을 주문한다.

### Header

> none

### Parameter
| parameter                     | required | type                                             | description   |
|-------------------------------|----------|--------------------------------------------------|---------------|
| memberId                      | Y        | Long                                             | 주문하는 사용자의 Id  |
| orderProductRequest.productId | Y        | Long                                             | 주문하려는 상품의 Id  |
| orderProductRequest.quantity  | Y       | Long                                             | 주문하려는 상품의 수량  |
| paymentMethod  | Y       | PaymentMethod(CREDIT_CARD, BANK_TRANSFER, POINT) | 주문하려는 상품의 수량  |

### Response
- Status : 201(CREATED)
```json
[
  {
    "orderId":1,
    "memberId":1,
    "orderDate":"2025-11-24T13:50:30",
    "paymentId":1,
    "totalAmount":12000,
    "paymentState":"PENDING"
  }
]
```


---

### Endpoint
`POST` `/api/coupon`

### Description
쿠폰을 생성한다.

### Header
> none

### Parameter
| parameter                     | required | type      | description |
|-------------------------------|----------|-----------|-------------|
| coupon                      | Y        | String    | 쿠폰 번호       |
| expiryDate | Y        | LocalDate | 쿠폰 유효기간     |
| amount  | Y       | Integer   | 쿠폰 생성개수     |
| discountRate  | Y       | Integer   | 쿠폰 할인율      |

### Response
- Status : 201(CREATED)
```json
[
  {
    "couponId":1,
    "coupon":"1234567890aa",
    "expiryDate":"2025-11-24",
    "amount":10000,
    "discountRate":10
  }
]
```

---

### Endpoint
`GET` `/api/coupon/{coupon-id}`

### Description
쿠폰을 조회한다.

### Header
> none

### Parameter
| parameter | required | type | description |
|-----------|----------|------|-------------|
| couponId  | Y        | Long | 쿠폰 Id       |

### Response
- Status : 200(OK)
```json
[
  {
    "couponId":1,
    "coupon":"1234567890aa",
    "expiryDate":"2025-11-24",
    "amount":10000,
    "discountRate":10
  }
]
```

---

### Endpoint
`POST` `/api/coupon/issue`

### Description
쿠폰을 발행한다.

### Header
> none

### Parameter
| parameter    | required | type | description |
|--------------|----------|------|-------------|
| couponId     | Y        | Long | 쿠폰 Id      |
| memberId     | Y        | Long | 회원 Id      |

### Response
- Status : 200(OK)
```json
[
  {
    "couponHistoryId":1,
    "couponId":"1",
    "memberId":"1",
    "couponIssuance":"2025-01-02'T'11:15:03",
    "couponUsed":"false"
  }
]