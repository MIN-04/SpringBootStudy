package spring.study.Member.controller.validations;

import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static spring.study.common.enums.ValidationMsgCode.BLANK_VALIDATE;
import static spring.study.common.enums.ValidationMsgCode.PASSWORD_NOT_MATCH;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidation.PasswordCustomValidation.class)
public @interface PasswordValidation {
    String message() default "비밀번호 유효성 검사";
    Class[] groups() default {};
    Class[] payload() default {};
    int min() default 0;
    int max() default 256;

    class PasswordCustomValidation implements ConstraintValidator<PasswordValidation, String> {

        private int min;        // 비밀번호 최소 문자
        private int max;        // 비밀번호 최대 문자

        @Override
        public void initialize(PasswordValidation constraintAnnotation) {
            // 어노테이션 등록 시 parameter 초기화
            min = constraintAnnotation.min();
            max = constraintAnnotation.max();
        }


        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {

            if (StringUtils.isBlank(password)) {
                addMsg(context, BLANK_VALIDATE.getValidationMsg());
                return false;
            }

            String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{"+min+","+max+"}$";
            if (!password.matches(regex)){
                addMsg(context, PASSWORD_NOT_MATCH.getValidationMsg());
                return false;
            }

            return true;
        }

        // context 메시지 추가
        public void addMsg (ConstraintValidatorContext context, String msg) {
            //기본 메시지 비활성화
            context.disableDefaultConstraintViolation();
            //새로운 메시지 추가
            context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        }
    }
}
