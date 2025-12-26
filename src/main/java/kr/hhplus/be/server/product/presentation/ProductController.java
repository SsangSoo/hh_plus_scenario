package kr.hhplus.be.server.product.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.product.application.usecase.RegisterProductUseCase;
import kr.hhplus.be.server.product.application.usecase.RetrieveProductUseCase;
import kr.hhplus.be.server.product.presentation.dto.request.RegisterProductRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
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

    private final RegisterProductUseCase registerProductUseCase;
    private final RetrieveProductUseCase retrieveProductUseCase;

    @PostMapping
    public ResponseEntity<ProductResponse> registerProduct(@RequestBody @Valid RegisterProductRequest request) {
        ProductResponse productResponse = registerProductUseCase.register(request.toServiceRequest());
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> retrieveProduct(@PathVariable Long productId) {
        ProductResponse response = retrieveProductUseCase.retrieveProduct(productId);
        return ResponseEntity.ok(response);
    }
}
