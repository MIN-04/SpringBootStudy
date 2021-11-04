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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import spring.study.Member.domain.services.CustomUserDetailsService;
import spring.study.Product.application.ProductService;
import spring.study.Product.domain.aggregates.Product;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.responses.ResponseMessage;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.SuccessCode.SUCCESS_FINDALL_PRODUCT;
import static spring.study.common.paths.ProductUrl.PRODUCT_FIND_ALL;
import static spring.study.common.paths.ProductUrl.PRODUCT_ROOT_PATH;

@WebMvcTest(ProductController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Product [Controller] 상품 test")
@WithMockUser(roles = "MEMBER")
class ProductControllerFindAllTest {

    @MockBean
    ProductService productService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    static Product product1;
    static Product product2;

    @BeforeAll
    static void setUp() {
        product1 = Product.builder()
            .id(1L)
            .productBasicInfo(ProductBasicInfo.builder()
                    .name("T-shirts")
                    .price(8000L)
                    .filePath("C:/studyPrj/spring-study-work/src/main/resources/static/image/shirts.jpeg")
                    .discPercent(20L)
                    .color("yellow")
                    .build())
            .build();

        product2 = Product.builder()
                .id(2L)
                .productBasicInfo(ProductBasicInfo.builder()
                        .name("Coat")
                        .price(120000L)
                        .filePath("C:/studyPrj/spring-study-work/src/main/resources/static/image/shirts.jpeg")
                        .discPercent(15L)
                        .color("black")
                        .build())
                .build();
    }

    @Test
    @DisplayName("상품 목록 조회 성공")
    void successFindAll() throws Exception {
        //given
        int page = 0;
        int pageCount = 10;

        Page<Product> testPage = new PageImpl<>(Arrays.asList(product1, product2));

        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(SUCCESS_FINDALL_PRODUCT.getHttpStatus())
                .message(SUCCESS_FINDALL_PRODUCT.getSuccessMsg())
                .resultData(testPage.getContent())
                .build();

        given(productService.findAll(page, pageCount)).willReturn(testPage);

        //when
        //then
        MvcResult resultMvc = mockMvc.perform(get(PRODUCT_ROOT_PATH + PRODUCT_FIND_ALL +"?page="+page+"&pageCount="+pageCount)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print())
                .andReturn();

    }

}