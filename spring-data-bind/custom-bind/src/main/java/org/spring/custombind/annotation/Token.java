package org.spring.custombind.annotation;

import java.lang.annotation.*;

/**
 * @author luoliang
 * @date 2019/10/8
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Token {
}
