package kr.hhplus.be.server.domain.product.controller;

import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.config.Util;
import kr.hhplus.be.server.domain.product.controller.request.RegisterProductRequest;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.response.ProductResponse;
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

    private final ProductService productService = mock(ProductService.class);

    @Override
    protected Object initContoller() {
        return new ProductController(productService);
    }


    @Test
    void registerProductTest() throws Exception {
        // given
        String productName = "클린 아키텍처";
        Long price = 0L;

        RegisterProductRequest request = new RegisterProductRequest(productName, price);
        ProductResponse response = ProductResponse.from(Product.register(request.toServiceRequest()), 0L);

        Util.setId(response, 1L);


        given(productService.registerProduct(any()))
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
                .andExpect(jsonPath("$.createDate").value(response.getCreateDate().toString()))
                .andExpect(jsonPath("$.modifiedDate").value(response.getModifiedDate().toString()))
                .andDo(document("상품 생성",
                        requestFields(
                                fieldWithPath("productName").description("상품 이름"),
                                fieldWithPath("price").description("상품 가격")
                        ),
                        responseFields(
                                fieldWithPath("id").description("상품 ID"),
                                fieldWithPath("productName").description("상품 이름"),
                                fieldWithPath("price").description("상품 가격"),
                                fieldWithPath("quantity").description("재고"),
                                fieldWithPath("createDate").description("생성일"),
                                fieldWithPath("modifiedDate").description("수정일")
                        )
                ));
    }


    @Test
    void retrieveProductTest() throws Exception {
        // given
        String productName = "클린 아키텍처";
        Long price = 1000L;

        RegisterProductRequest request = new RegisterProductRequest(productName, price);
        ProductResponse response = ProductResponse.from(Product.register(request.toServiceRequest()), 0L);

        Util.setId(response, 1L);


        given(productService.retrieveProduct(any()))
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
                .andExpect(jsonPath("$.createDate").value(response.getCreateDate().toString()))
                .andExpect(jsonPath("$.modifiedDate").value(response.getModifiedDate().toString()))
                .andDo(document("상품 조회",
                        pathParameters(
                            parameterWithName("productId").description("조회할 상품 Id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("상품 ID"),
                                fieldWithPath("productName").description("상품 이름"),
                                fieldWithPath("price").description("상품 가격"),
                                fieldWithPath("quantity").description("재고"),
                                fieldWithPath("createDate").description("생성일"),
                                fieldWithPath("modifiedDate").description("수정일")
                        )
                ));
    }


}