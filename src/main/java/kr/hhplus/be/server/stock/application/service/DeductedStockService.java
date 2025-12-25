package kr.hhplus.be.server.stock.application.service;

import kr.hhplus.be.server.stock.application.usecase.DeductedStockUseCase;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.domain.repository.StockRepository;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeductedStockService implements DeductedStockUseCase {

    private final StockRepository stockRepository;

    @Override
    @Transactional
    public List<StockResponse> deductedStock(Map<Long, Long> orderProductMap) {
        List<StockResponse> responses = new ArrayList<>();
        for(Map.Entry<Long, Long> entry : orderProductMap.entrySet()){
            Stock stock = stockRepository.findByProductIdForUpdate(entry.getKey());
            stock.deductedStock(entry.getValue());
            stockRepository.modify(stock);
            responses.add(StockResponse.from(stock));
        }
        return responses;

    }
}
