package kr.hhplus.be.server.product.presentation;

import kr.hhplus.be.server.product.application.service.RegisterProductService;
import kr.hhplus.be.server.product.application.service.RetrieveProductService;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.product.presentation.dto.request.RegisterProductRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest extends RestDocsControllerSupport {

    private final RegisterProductService registerProductService = mock(RegisterProductService.class);
    private final RetrieveProductService retrieveProductService = mock(RetrieveProductService.class);

    @Override
    protected Object initContoller() {
        return new ProductController(registerProductService, retrieveProductService);
    }


    @Test
    void registerProductTest() throws Exception {
        // given
        String productName = "클린 아키텍처";
        Long price = 0L;

        RegisterProductRequest request = new RegisterProductRequest(productName, price);
        Product product = Product.create(request.toServiceRequest());
        product.assignId(1L);
        ProductResponse response = ProductResponse.from(product, 0L);

        given(registerProductService.register(any()))
                .willReturn(response);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productName").value("클린 아키텍처"))
                .andExpect(jsonPath("$.price").value(0L))
                .andExpect(jsonPath("$.quantity").value(0L))
                .andDo(document("상품 생성",
                        requestFields(
                                fieldWithPath("productName").description("상품 이름"),
                                fieldWithPath("price").description("상품 가격")
                        ),
                        responseFields(
                                fieldWithPath("id").description("상품 ID"),
                                fieldWithPath("productName").description("상품 이름"),
                                fieldWithPath("price").description("상품 가격"),
                                fieldWithPath("quantity").description("재고")
                        )
                ));
    }


    @Test
    void retrieveProductTest() throws Exception {
        // given
        String productName = "클린 아키텍처";
        Long price = 1000L;

        RegisterProductRequest request = new RegisterProductRequest(productName, price);
        Product product = Product.create(request.toServiceRequest());
        product.assignId(1L);
        ProductResponse response = ProductResponse.from(product, 0L);

        given(retrieveProductService.retrieveProduct(any()))
                .willReturn(response);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/product/{productId}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productName").value(productName))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.quantity").value(0L))
                .andDo(document("상품 조회",
                        pathParameters(
                            parameterWithName("productId").description("조회할 상품 Id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("상품 ID"),
                                fieldWithPath("productName").description("상품 이름"),
                                fieldWithPath("price").description("상품 가격"),
                                fieldWithPath("quantity").description("재고")
                        )
                ));
    }


}