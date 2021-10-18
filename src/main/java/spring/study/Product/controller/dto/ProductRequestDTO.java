package spring.study.Product.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductRequestDTO {
    private final String name;
    private final Long price;
    private final String filePath;
    private final Long discPercent;
    private final String color;

}
