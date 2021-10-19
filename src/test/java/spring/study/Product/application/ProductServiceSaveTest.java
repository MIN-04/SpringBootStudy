package spring.study.Product.application;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.study.Product.domain.aggregates.Product;
import spring.study.Product.domain.commands.ProductCommand;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;
import spring.study.Product.infraStructure.repository.ProductJpaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product [Service] 상품 등록 test")
class ProductServiceSaveTest {

    @Mock
    ProductJpaRepository productRepository;

    @InjectMocks
    ProductService productService;

    static ProductCommand command;
    static Product product;
    static Product resultProduct;

    @BeforeAll
    static void setUp() {
        command = ProductCommand.builder()
                .basicInfo(ProductBasicInfo.builder()
                        .name("T-shirts")
                        .price(8000L)
                        .filePath("C:/studyPrj/spring-study-work/src/main/resources/static/image/shirts.jpeg")
                        .discPercent(20L)
                        .color("black")
                        .build())
                .build();

        product = Product.builder()
                .productBasicInfo(ProductBasicInfo.builder()
                        .name("T-shirts")
                        .price(8000L)
                        .filePath("C:/studyPrj/spring-study-work/src/main/resources/static/image/shirts.jpeg")
                        .discPercent(20L)
                        .color("black")
                        .build())
                .build();

        resultProduct = Product.builder()
                .id(1L)
                .productBasicInfo(ProductBasicInfo.builder()
                        .name("T-shirts")
                        .price(8000L)
                        .filePath("C:/studyPrj/spring-study-work/src/main/resources/static/image/shirts.jpeg")
                        .discPercent(20L)
                        .color("black")
                        .build())
                .build();
    }

    @Test
    @DisplayName("상품 목록 등록 성공")
    void successSave() {
        //given
        given(productRepository.save(any())).willReturn(resultProduct);

        //when
        Product result = productService.save(command);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(resultProduct);
    }

}