package spring.study.Product.infraStructure.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Product.domain.aggregates.Product;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@DisplayName("Product [Repository] Jpa Repository 테스트")
class ProductJpaRepositoryTest {

    @Autowired
    ProductJpaRepository productRepository;

    Product product1;
    Product product2;

    @BeforeEach
    void setUp() {
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
    @DisplayName("상품 등록 성공")
    void save() {
        //given
        //when
        Product saveResult = productRepository.save(product1);

        //then
        assertThat(saveResult).usingRecursiveComparison().isEqualTo(product1);
    }

    @Test
    @DisplayName("상품 목록 조회 성공")
    void findAll() {
        //given
        int page = 0;
        int pageCount = 10;
        PageRequest pageRequest = PageRequest.of(page, pageCount);

        productRepository.save(product1);
        productRepository.save(product2);

        //when
        Page<Product> pageResult = productRepository.findAll(pageRequest);

        //then
        List<Product> contents = pageResult.getContent();
        assertFalse(contents.isEmpty());

        int dataSize = pageResult.getContent().size();
        assertThat(dataSize <= pageCount).isTrue();

    }
}