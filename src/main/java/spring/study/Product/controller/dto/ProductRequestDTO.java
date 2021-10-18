package spring.study.Product.controller.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductRequestDTO {
    private String name;
    private Long price;
    private String filePath;
    private Long discPercent;
    private String color;

}
