package spring.study.Product.controller.dto.mapper;

import spring.study.Product.controller.dto.ProductRequestDTO;
import spring.study.Product.domain.commands.ProductCommand;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;

public class ProductRequestMapper {

    public ProductCommand toCommand(ProductRequestDTO dto) {
        return ProductCommand.builder()
                .basicInfo(ProductBasicInfo.builder()
                        .name(dto.getName())
                        .price(dto.getPrice())
                        .filePath(dto.getFilePath())
                        .discPercent(dto.getDiscPercent())
                        .color(dto.getColor())
                        .build())
                .build();
    }
}
