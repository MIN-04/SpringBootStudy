package spring.study.Member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import spring.study.Member.controller.validations.EmailValidation;
import spring.study.Member.controller.validations.MobileNumValidation;
import spring.study.Member.controller.validations.NameValidation;
import spring.study.Member.controller.validations.PasswordValidation;

import javax.validation.constraints.NotBlank;

//@Data
@Getter
@AllArgsConstructor
@Builder
public class MemberJoinRequestDTO {

    @NotBlank @EmailValidation
    private final String email; //이메일 (회원 ID)
    @PasswordValidation(min = 3, max = 8)
    private final String password; //패스워드
    @NameValidation
    private final String name; //이름
    private final String address; //주소
    @MobileNumValidation
    private final String mobileNum; //전화번호
    private final String gender; //성별
    private final String birthday; //생년월일

}
