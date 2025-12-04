package kr.hhplus.be.server.domain.product.controller.request;

import kr.hhplus.be.server.config.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegisterProductRequestTest extends ControllerTestSupport {

    @Test
    @DisplayName("상품 생성 요청시 가격으로 음수는 불가능하다.")
    void registerProductValidateNegativePriceTest() throws Exception {
        // given
        String productName = "클린 아키텍처";
        Long price = -1L;

        RegisterProductRequest request = new RegisterProductRequest(productName, price);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[0].field").value("price"))
                .andExpect(jsonPath("$.errors[0].value").value("-1"))
                .andExpect(jsonPath("$.errors[0].reason").value("가격은 0이상 이어야 합니다."));
    }

    @Test
    @DisplayName("상품 생성 요청시 가격으로 0 이상은 가능하다.")
    void registerProductValidateZeroPriceTest() throws Exception {
        // given
        String productName = "클린 아키텍처";
        Long price = 0L;

        RegisterProductRequest request = new RegisterProductRequest(productName, price);


        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isCreated());
    }


    @Test
    @DisplayName("상품 생성시 요청시 상품이름으로 공백은 불가능하다")
    void registerProductValidateProductNameTest() throws Exception {
        // given
        String productName = "";
        Long price = 0L;

        RegisterProductRequest request = new RegisterProductRequest(productName, price);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[0].field").value("productName"))
                .andExpect(jsonPath("$.errors[0].value").value(""))
                .andExpect(jsonPath("$.errors[0].reason").value("상품 이름은 필수입니다."));
    }

    @Test
    @DisplayName("상품 생성시 요청시 상품이름으로 공백은 불가능하다")
    void registerProductValidateProductNameIsNotNullTest() throws Exception {
        // given
        String productName = null;
        Long price = 0L;

        RegisterProductRequest request = new RegisterProductRequest(productName, price);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[0].field").value("productName"))
                .andExpect(jsonPath("$.errors[0].value").value(""))
                .andExpect(jsonPath("$.errors[0].reason").value("상품 이름은 필수입니다."));
    }

    @Test
    @DisplayName("상품 생성시 요청시 상품 이름과 가격에 대한 두 값 올바르지 않으면 두 값이 다 유효하지 않다는 메세지를 내보낸다.")
    void registerProductValidateProductNameAndPriceTest() throws Exception {
        // given
        String productName = "";
        Long price = -1L;

        RegisterProductRequest request = new RegisterProductRequest(productName, price);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."))
                .andExpect(jsonPath("$.errors[?(@.field == 'productName')].field").value("productName"))
                .andExpect(jsonPath("$.errors[?(@.field == 'productName')].value").value(""))
                .andExpect(jsonPath("$.errors[?(@.field == 'productName')].reason").value("상품 이름은 필수입니다."))
                .andExpect(jsonPath("$.errors[?(@.field == 'price')].field").value("price"))
                .andExpect(jsonPath("$.errors[?(@.field == 'price')].value").value("-1"))
                .andExpect(jsonPath("$.errors[?(@.field == 'price')].reason").value("가격은 0이상 이어야 합니다."))
        ;

    }



}