package spring.study.Product.application;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import spring.study.Product.domain.aggregates.Product;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;
import spring.study.Product.infraStructure.repository.ProductJpaRepository;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product [Service] 상품 등록 Test")
class ProductServiceFindAllTest {

    @Mock
    ProductJpaRepository productRepository;

    @InjectMocks
    ProductService productService;

    static Product product1;
    static Product product2;

    @BeforeAll
    static void setUp() {

        product1 = Product.builder()
                .productBasicInfo(ProductBasicInfo.builder()
                        .name("T-shirts")
                        .price(8000L)
                        .filePath("C:/studyPrj/spring-study-work/src/main/resources/static/image/shirts.jpeg")
                        .discPercent(20L)
                        .color("black")
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
    void successFindAll() {
        //given
        int page = 0;
        int pageCount = 10;

        Page<Product> testPage = new PageImpl<>(Arrays.asList(product1, product2));
        PageRequest pageRequest = PageRequest.of(page, pageCount);
        given(productRepository.findAll(pageRequest)).willReturn(testPage);

        //when
        Page<Product> pageResult = productService.findAll(page, pageCount);

        //then
        assertThat(pageResult).isEqualTo(testPage);
    }

}