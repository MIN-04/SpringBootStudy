package spring.study.Product.domain.valueObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBasicInfo {
    private String name;
    private Long price;
    @Column(name = "FILE_PATH")
    private String filePath;
    @Column(name = "DISC_PERCENT")
    private Long discPercent;
    private String color;
}
