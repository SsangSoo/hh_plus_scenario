package kr.hhplus.be.server.domain.stock.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.stock.controller.request.AddStockRequest;
import kr.hhplus.be.server.domain.stock.service.StockService;
import kr.hhplus.be.server.domain.stock.service.response.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockResponse> add(@RequestBody @Valid AddStockRequest request) {
        StockResponse stockResponse = stockService.addStock(request.toAddStock());
        return  ResponseEntity.ok(stockResponse);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<StockResponse> retrieve(@PathVariable("productId") Long productId) {
        StockResponse stockResponse = stockService.retrieveStock(productId);
        return  ResponseEntity.ok(stockResponse);
    }
}
