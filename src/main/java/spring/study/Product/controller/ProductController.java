package spring.study.Product.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.study.Product.application.ProductService;
import spring.study.Product.controller.dto.ProductRequestDTO;
import spring.study.Product.controller.dto.mapper.ProductRequestMapper;
import spring.study.Product.domain.aggregates.Product;
import spring.study.Product.domain.commands.ProductCommand;
import spring.study.common.enums.SuccessCode;
import spring.study.common.responses.ResponseMessage;

import java.util.List;

import static spring.study.common.enums.SuccessCode.SUCCESS_FINDALL_PRODUCT;
import static spring.study.common.enums.SuccessCode.SUCCESS_SAVE_PRODUCT;
import static spring.study.common.paths.ProductUrl.*;

@Slf4j
@RestController
@RequestMapping(PRODUCT_ROOT_PATH)
public class ProductController {

    private final ProductService productService;
    private final ProductRequestMapper mapper;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
        this.mapper = new ProductRequestMapper();
    }

    public ResponseMessage setResponseMessage(SuccessCode sc, Object result) {
        return ResponseMessage.builder()
                .httpStatus(sc.getHttpStatus())
                .message(sc.getSuccessMsg())
                .resultData(result)
                .build();
    }

    /**
     * 상품 등록
     */
    @PostMapping(PRODUCT_NEW)
    public ResponseEntity<ResponseMessage> save(ProductRequestDTO dto) {
        log.info("Product [Controller - save] dto = {}", dto);

        ProductCommand command = mapper.toCommand(dto);

        //상품 등록
        Product result = productService.save(command);
        log.info("Product [Controller - save] result = {}", result);

        ResponseMessage rm = setResponseMessage(SUCCESS_SAVE_PRODUCT, result);

        return ResponseEntity.ok(rm);

    }

    /**
     * 상품 목록 조회
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
