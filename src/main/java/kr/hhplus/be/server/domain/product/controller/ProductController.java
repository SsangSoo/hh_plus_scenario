package kr.hhplus.be.server.domain.product.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.product.controller.request.RegisterProductRequest;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.service.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> registerProduct(@RequestBody @Valid RegisterProductRequest request) {
        ProductResponse productResponse = productService.registerProduct(request.toServiceRequest());
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> retrieveProduct(@PathVariable Long productId) {
        ProductResponse response = productService.retrieveProduct(productId);
        return ResponseEntity.ok(response);
    }
}
