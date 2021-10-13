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
import static spring.study.common.enums.ValidationMsgCode.EMAIL_NOT_MATCH;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdValidation.IdCustomValidation.class)
public @interface IdValidation {
    String message() default "이메일 유효성 검사";
    Class[] groups() default {};
    Class[] payload() default {};
    String test() default "";

    @Slf4j
    class IdCustomValidation implements ConstraintValidator<IdValidation, Long> {

        @Override
        public boolean isValid(Long id, ConstraintValidatorContext context) {
            log.info("[IdCustomValidation - isValid] id = {}", id);

            if(id == null) {
                addMsg(context, BLANK_VALIDATE.getValidationMsg());
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
