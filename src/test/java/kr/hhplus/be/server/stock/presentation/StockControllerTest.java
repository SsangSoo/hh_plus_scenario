package kr.hhplus.be.server.stock.presentation;

import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.stock.application.usecase.AddStockUseCase;
import kr.hhplus.be.server.stock.application.usecase.RetrieveStockUseCase;
import kr.hhplus.be.server.stock.domain.model.Stock;
import kr.hhplus.be.server.stock.presentation.dto.request.AddStockRequest;
import kr.hhplus.be.server.stock.presentation.dto.response.StockResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StockControllerTest extends RestDocsControllerSupport {

    private final AddStockUseCase addStockUseCase = mock(AddStockUseCase.class);
    private final RetrieveStockUseCase retrieveStockUseCase = mock(RetrieveStockUseCase.class);

    @Override
    protected Object initContoller() {
        return new StockController(addStockUseCase, retrieveStockUseCase);
    }

    @Test
    @DisplayName("재고 추가 테스트")
    void 재고_추가_테스트() throws Exception {
        // given
        Long productId = 1L;
        Long addStock = 100L;
        AddStockRequest request = new AddStockRequest(productId, addStock);

        Stock stock = Stock.of(1L, productId, addStock);
        StockResponse response = StockResponse.from(stock);

        given(addStockUseCase.addStock(any()))
                .willReturn(response);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/stock")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.quantity").value(addStock))
                .andDo(document("재고 추가",
                        requestFields(
                                fieldWithPath("productId").description("상품 Id"),
                                fieldWithPath("addStock").description("추가할 재고 수량")
                        ),
                        responseFields(
                                fieldWithPath("productId").description("상품 Id"),
                                fieldWithPath("quantity").description("재고 수량")
                        )
                ));
    }

    @Test
    @DisplayName("재고 조회 테스트")
    void 재고_조회_테스트() throws Exception {
        // given
        Long productId = 1L;
        Long quantity = 50L;

        Stock stock = Stock.of(1L, productId, quantity);
        StockResponse response = StockResponse.from(stock);

        given(retrieveStockUseCase.retrieveStock(productId))
                .willReturn(response);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/stock/{productId}", productId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.quantity").value(quantity))
                .andDo(document("재고 조회",
                        pathParameters(
                                parameterWithName("productId").description("조회할 상품 Id")
                        ),
                        responseFields(
                                fieldWithPath("productId").description("상품 Id"),
                                fieldWithPath("quantity").description("재고 수량")
                        )
                ));
    }
}
