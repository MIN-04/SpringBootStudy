package spring.study.Product.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.study.Product.application.ProductService;
import spring.study.Product.domain.aggregates.Product;
import spring.study.common.enums.SuccessCode;
import spring.study.common.responses.ResponseMessage;

import java.util.List;

import static spring.study.common.enums.SuccessCode.SUCCESS_FINDALL_PRODUCT;
import static spring.study.common.paths.ProductUrl.PRODUCT_FIND_ALL;
import static spring.study.common.paths.ProductUrl.PRODUCT_ROOT_PATH;

@Slf4j
@RestController
@RequestMapping(PRODUCT_ROOT_PATH)
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public ResponseMessage setResponseMessage(SuccessCode sc, Object result) {
        return ResponseMessage.builder()
                .httpStatus(sc.getHttpStatus())
                .message(sc.getSuccessMsg())
                .resultData(result)
                .build();
    }

    /**
     * 상품 목록 조회
     * @param page
     * @param pageCount
     */
    @GetMapping(PRODUCT_FIND_ALL)
    public ResponseEntity<ResponseMessage> findAll(@RequestParam int page, @RequestParam int pageCount) {

        log.info("Product [findAll - Controller] page = {}, pageCount = {}", page, pageCount);

        Page<Product> result = productService.findAll(page, pageCount);
        List<Product> resultList = result.getContent();
        log.info("Product [findAll - Controller] resultList = {}", resultList);

        ResponseMessage rm = setResponseMessage(SUCCESS_FINDALL_PRODUCT, resultList);
        return ResponseEntity.ok(rm);

    }
}
