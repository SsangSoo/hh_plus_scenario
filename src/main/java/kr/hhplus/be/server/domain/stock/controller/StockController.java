package kr.hhplus.be.server.domain.stock.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.stock.controller.request.AddStockRequest;
import kr.hhplus.be.server.domain.stock.entity.Stock;
import kr.hhplus.be.server.domain.stock.service.StockService;
import kr.hhplus.be.server.domain.stock.service.response.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    @PostMapping("/add")
    public ResponseEntity<StockResponse> add(@RequestBody @Valid AddStockRequest request) {
        StockResponse stockResponse = stockService.addStock(request.toAddStock());
        return  ResponseEntity.ok(stockResponse);
    }

}
