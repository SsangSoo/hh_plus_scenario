package kr.hhplus.be.server.domain.order.interfaces.web;


import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.order.application.in.PlaceOrderService;
import kr.hhplus.be.server.domain.order.interfaces.web.request.OrderRequest;
import kr.hhplus.be.server.domain.order.application.out.response.OrderResponse;
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

    private final PlaceOrderService placeOrderService;
//    private final PlaceOrderService placeOrderService;

    @PostMapping
    public ResponseEntity<OrderResponse> order(@RequestBody @Valid OrderRequest orderRequest) {
        OrderResponse response = placeOrderService.order(orderRequest.toServiceRequest());
        return ResponseEntity.ok(response);
    }

}
