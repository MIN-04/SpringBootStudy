package spring.study.Product.infraStructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.study.Product.domain.aggregates.Product;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
