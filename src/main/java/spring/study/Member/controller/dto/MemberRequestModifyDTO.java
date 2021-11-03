package spring.study.Member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.study.Member.controller.validations.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestModifyDTO {

    @IdValidation
    private Long id; //id (회원 PK)
    //@NotBlank
    @EmailValidation
    private String email; //이메일 (회원 ID)
    @PasswordValidation(min = 4, max = 8)
    private String password; //패스워드
    @NameValidation
    private String name; //이름
    private String address; //주소
    @MobileNumValidation
    private String mobileNum; //전화번호
    private String gender; //성별
    private String birth; //생년월일
    private List<String> roles; //회원 권한
    private String provider; //제공사  (Naver, Google 등)

}
