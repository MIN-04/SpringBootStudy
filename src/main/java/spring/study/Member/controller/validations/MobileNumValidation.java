package spring.study.Member.controller.validations;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static spring.study.common.enums.ValidationMsgCode.BLANK_VALIDATE;
import static spring.study.common.enums.ValidationMsgCode.MOBILENUM_NOT_MATCH;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MobileNumValidation.MobileNumCustomValidation.class)
public @interface MobileNumValidation {
    String message() default "휴대전화 유효성 검사";
    Class[] groups() default {};
    Class[] payload() default {};

    @Slf4j
    class MobileNumCustomValidation implements ConstraintValidator<MobileNumValidation, String> {

        @Override
        public boolean isValid(String mobileNum, ConstraintValidatorContext context) {
            log.info("[MobileNumCustomValidation - isValid] mobileNum = {}", mobileNum);

            if(StringUtils.isBlank(mobileNum)) {
                addMsg(context, BLANK_VALIDATE.getValidationMsg());
                return false;
            }

            String regex = "01[016789]-[0-9]{3,4}-[0-9]{3,4}";
            if(!mobileNum.matches(regex)) {
                addMsg(context, MOBILENUM_NOT_MATCH.getValidationMsg());
                return false;
            }

            return true;
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
