package spring.study.Product.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import spring.study.Product.domain.aggregates.Product;
import spring.study.Product.domain.commands.ProductCommand;
import spring.study.Product.infraStructure.repository.ProductJpaRepository;

@Slf4j
@Service
public class ProductService {

    private ProductJpaRepository productRepository;

    @Autowired
    public ProductService(ProductJpaRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품 등록
     */
    public Product save(ProductCommand command) {
        Product product = Product.builder()
                .productBasicInfo(command.getBasicInfo())
                .build();
        log.info("Product [Service- save] product = {}", product.toString());

        return productRepository.save(product);
    }

    /**
     * 상품 목록 조회
     */
    public Page<Product> findAll(int page, int pageCount) {
        PageRequest pageRequest = PageRequest.of(page, pageCount);

        //상품 목록 조회
        return productRepository.findAll(pageRequest);
    }
}
