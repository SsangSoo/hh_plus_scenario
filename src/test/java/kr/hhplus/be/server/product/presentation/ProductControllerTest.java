package kr.hhplus.be.server.product.presentation;

import kr.hhplus.be.server.product.application.service.RegisterProductService;
import kr.hhplus.be.server.product.application.service.popular.RetrievePopularProductService;
import kr.hhplus.be.server.product.application.service.RetrieveProductService;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.config.RestDocsControllerSupport;
import kr.hhplus.be.server.product.infrastructure.persistence.query.ProductProjection;
import kr.hhplus.be.server.product.presentation.dto.request.RegisterProductRequest;
import kr.hhplus.be.server.product.presentation.dto.response.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private final RetrievePopularProductService retrievePopularProductService = mock(RetrievePopularProductService.class);


    @Override
    protected Object initContoller() {
        return new ProductController(registerProductService, retrieveProductService,  retrievePopularProductService);
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

    @Test
    void popularProductTest() throws Exception {
        // given
        List<ProductResponse> productResponses = List.of(
                new ProductProjection(2L, "카페라떼", 4500L, 40L),
                new ProductProjection(1L, "아메리카노", 4000L, 40L),
                new ProductProjection(3L, "아이스티", 4000L, 40L)
        ).stream()
                .map(ProductResponse::from)
                .toList();

        given(retrievePopularProductService.retrievePopularProducts()).willReturn(productResponses);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/product/popular")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].productName").value("카페라떼"))
                .andExpect(jsonPath("$[0].price").value(4500L))
                .andExpect(jsonPath("$[0].quantity").value(40L))
                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(jsonPath("$[1].productName").value("아메리카노"))
                .andExpect(jsonPath("$[1].price").value(4000L))
                .andExpect(jsonPath("$[1].quantity").value(40L))
                .andExpect(jsonPath("$[2].id").value(3L))
                .andExpect(jsonPath("$[2].productName").value("아이스티"))
                .andExpect(jsonPath("$[2].price").value(4000L))
                .andExpect(jsonPath("$[2].quantity").value(40L))
                .andDo(document("인기 상품 조회",
                        responseFields(
                                fieldWithPath("[].id").description("상품 ID"),
                                fieldWithPath("[].productName").description("상품명"),
                                fieldWithPath("[].price").description("가격"),
                                fieldWithPath("[].quantity").description("재고 수량")
                        )
                ));
    }
}