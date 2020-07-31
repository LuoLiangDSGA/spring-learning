package org.boot.config.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.boot.config.valid.StartWithValidator;

/**
 * @author fantasy
 * @date 2020/7/31
 */
@Documented
@Constraint(validatedBy = StartWithValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface StartWithValidation {

    String message() default "不符合要求的初始值";

    String start() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
