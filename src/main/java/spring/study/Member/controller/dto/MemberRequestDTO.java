package spring.study.Member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.study.Member.controller.validations.EmailValidation;
import spring.study.Member.controller.validations.MobileNumValidation;
import spring.study.Member.controller.validations.NameValidation;
import spring.study.Member.controller.validations.PasswordValidation;

import javax.validation.constraints.NotBlank;

//@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDTO {

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
    private String birthday; //생년월일

}
