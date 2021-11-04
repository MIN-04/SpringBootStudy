package spring.study.Product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import spring.study.Member.domain.services.CustomUserDetailsService;
import spring.study.Product.application.ProductService;
import spring.study.Product.controller.dto.ProductRequestDTO;
import spring.study.Product.domain.aggregates.Product;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.responses.ResponseMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.SuccessCode.SUCCESS_SAVE_PRODUCT;
import static spring.study.common.paths.ProductUrl.PRODUCT_NEW;
import static spring.study.common.paths.ProductUrl.PRODUCT_ROOT_PATH;

@WebMvcTest(ProductController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Product [Controller] 상품 test")
class ProductControllerSaveTest {

    @MockBean
    ProductService productService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    static ProductRequestDTO dto;

    @BeforeAll
    static void setUp() {
        dto = ProductRequestDTO.builder()
                .name("T-shirts")
                .price(8000L)
                .filePath("C:/studyPrj/spring-study-work/src/main/resources/static/image/shirts.jpeg")
                .discPercent(20L)
                .color("black")
                .build();
    }

    @Test
    @DisplayName("상품 등록 성공")
    void successSave() throws Exception {
        //given
        Product result = Product.builder()
                .id(1L)
                .productBasicInfo(ProductBasicInfo.builder()
                        .name("T-shirts")
                        .price(8000L)
                        .filePath("C:/studyPrj/spring-study-work/src/main/resources/static/image/shirts.jpeg")
                        .discPercent(20L)
                        .color("black")
                        .build())
                .build();

        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(SUCCESS_SAVE_PRODUCT.getHttpStatus())
                .message(SUCCESS_SAVE_PRODUCT.getSuccessMsg())
                .resultData(result)
                .build();

        given(productService.save(any())).willReturn(result);

        //when
        //then
        mockMvc.perform(post(PRODUCT_ROOT_PATH + PRODUCT_NEW)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());

    }
}