package kr.hhplus.be.server.stock.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.stock.application.usecase.AddStockUseCase;
import kr.hhplus.be.server.stock.application.usecase.RetrieveStockUseCase;
import kr.hhplus.be.server.stock.presentation.dto.request.AddStockRequest;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class StockController {

    private final AddStockUseCase addStockUseCase;
    private final RetrieveStockUseCase retrieveStock;

    @PostMapping
    public ResponseEntity<StockResponse> add(@RequestBody @Valid AddStockRequest request) {
        StockResponse stockResponse = addStockUseCase.addStock(request.toAddStock());
        return  ResponseEntity.ok(stockResponse);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<StockResponse> retrieve(@PathVariable("productId") Long productId) {
        StockResponse stockResponse = retrieveStock.retrieveStock(productId);
        return  ResponseEntity.ok(stockResponse);
    }
}
