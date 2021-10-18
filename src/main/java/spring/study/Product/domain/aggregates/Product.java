package spring.study.Product.domain.aggregates;

import lombok.*;
import spring.study.Product.domain.valueObjects.ProductBasicInfo;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    ProductBasicInfo productBasicInfo;

}
