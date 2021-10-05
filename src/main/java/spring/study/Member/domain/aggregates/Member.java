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

    //21.10.01 피드백 (10.05 수정)
    //회원 ID는 email로 잘 하지 않는다. → ID를 따로 만들어주기 (ID를 generic 하게 해서 이것만 PK)
    private String email; //이메일 (회원 ID)

    @Embedded
    MemberBasicInfo memberBasicInfo;

    @Embedded
    MemberAddressInfo memberAddressInfo;

}
