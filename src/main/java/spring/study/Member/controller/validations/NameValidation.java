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
import static spring.study.common.enums.ValidationMsgCode.NAME_NOT_MATCH;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameValidation.NameCustomValidation.class)
public @interface NameValidation {
    String message() default "이름 유효성 검사";
    Class[] groups() default {};
    Class[] payload() default {};

    @Slf4j
    class NameCustomValidation implements ConstraintValidator<NameValidation, String> {

        @Override
        public boolean isValid(String name, ConstraintValidatorContext context) {
            log.info("[NameCustomValidation - isValid] name = {}", name);

            if(name.isBlank()){
                addMsg(context, BLANK_VALIDATE.getValidationMsg());
                return false;
            }

            if (!name.matches("^[가-힣]+$")){ //한글만 가능 (한글이 아닐 경우)
                addMsg(context, NAME_NOT_MATCH.getValidationMsg());
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
