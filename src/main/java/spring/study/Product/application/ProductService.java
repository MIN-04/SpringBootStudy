package spring.study.Product.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import spring.study.Product.domain.aggregates.Product;
import spring.study.Product.domain.commands.ProductCommand;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;
import spring.study.Product.infraStructure.repository.ProductJpaRepository;

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
                .productBasicInfo(ProductBasicInfo.builder()
                        .name(command.getBasicInfo().getName())
                        .price(command.getBasicInfo().getPrice())
                        .filePath(command.getBasicInfo().getFilePath())
                        .discPercent(command.getBasicInfo().getDiscPercent())
                        .color(command.getBasicInfo().getColor())
                        .build())
                .build();

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
