package spring.study.Product.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.study.Product.application.ProductService;
import spring.study.common.enums.SuccessCode;
import spring.study.common.responses.ResponseMessage;

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

    @GetMapping(PRODUCT_FIND_ALL)
    public ResponseEntity<ResponseMessage> findAll(@RequestParam int page, @RequestParam int pageCount) {

        return null;

    }
}
