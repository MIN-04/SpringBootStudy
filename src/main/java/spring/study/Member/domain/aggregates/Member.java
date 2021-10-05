package spring.study.Member.domain.aggregates;

import lombok.*;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 테이블 ID

    private String email; //이메일 (회원 ID)

    @Embedded
    MemberBasicInfo memberBasicInfo;

    @Embedded
    MemberAddressInfo memberAddressInfo;

}
