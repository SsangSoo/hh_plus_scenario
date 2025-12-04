package kr.hhplus.be.server.domain.order.controller;


import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.order.controller.request.OrderRequest;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.order.service.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<OrderResponse> order(@RequestBody @Valid OrderRequest orderRequest) {
        OrderResponse response = orderService.order(orderRequest.toServiceRequest());
        return ResponseEntity.ok(response);
    }

}
