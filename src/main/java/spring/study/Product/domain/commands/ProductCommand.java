package spring.study.Product.domain.commands;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;

@Getter
@Builder
@RequiredArgsConstructor
public class ProductCommand {
    private final ProductBasicInfo basicInfo;
}
