package spring.study.Member.domain.valueObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberBasicInfo {
    private String password; //패스워드
    private String name; //이름
    @Column(name = "MOBILENUM")
    private String mobileNum; //전화번호
    private String gender; //성별
    private String birthday; //생년월일
}
