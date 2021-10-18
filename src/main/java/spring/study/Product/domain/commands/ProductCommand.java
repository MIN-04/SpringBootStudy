package spring.study.Product.domain.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCommand {
    private ProductBasicInfo basicInfo;
}
