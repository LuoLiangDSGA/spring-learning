package org.boot.config.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.boot.config.annotation.StartWithValidation;
import org.springframework.util.StringUtils;

/**
 * @author fantasy
 * @date 2020/7/31
 */
public class StartWithValidator implements ConstraintValidator<StartWithValidation, String> {

    private String start;

    @Override
    public void initialize(StartWithValidation constraintAnnotation) {
        start = constraintAnnotation.start();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.isEmpty(s)) {
            return s.startsWith(start);
        }
        return false;
    }
}
