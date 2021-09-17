package spring.study.Member.controller.validations;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static spring.study.common.enums.ValidationMsgCode.BLANK_VALIDATE;
import static spring.study.common.enums.ValidationMsgCode.EMAIL_NOT_MATCH;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidation.EmailCustomValidation.class)
public @interface EmailValidation {
    String message() default "이메일 유효성 검사";
    Class[] groups() default {};
    Class[] payload() default {};
    String test() default "";

    @Slf4j
    class EmailCustomValidation implements ConstraintValidator<EmailValidation, String> {

        @Override
        public boolean isValid(String email, ConstraintValidatorContext context) {
            log.info("[EmailCustomValidation - isValid] email = {}", email);

            if(email.isBlank()) {
                addMsg(context, BLANK_VALIDATE.getValidationMsg());
                return false;
            }

            addMsg(context, EMAIL_NOT_MATCH.getValidationMsg());
            return Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", email);
        }

        // context에 메세지 설정
        private void addMsg(ConstraintValidatorContext context, String msg) {
            //기본 메시지 비활성화
            context.disableDefaultConstraintViolation();
            //새로운 메시지 추가
            context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        }

    }
}
