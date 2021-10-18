package spring.study.Product.infraStructure.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("Product [Repository] Jpa Repository 테스트")
class ProductJpaRepositoryTest {

    @Autowired
    ProductJpaRepository productRepository;

    @Test
    @DisplayName("상품 목록 조회 성공")
    void findAll() {

    }
}