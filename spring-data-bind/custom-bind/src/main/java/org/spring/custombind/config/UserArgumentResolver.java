package org.spring.custombind.config;

import org.spring.custombind.annotation.Token;
import org.spring.custombind.model.User;
import org.spring.custombind.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author luoliang
 * @date 2019/10/8
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        // 判断是否有Token这个注解
        return methodParameter.hasParameterAnnotation(Token.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String token;
        if (methodParameter.getParameterType().equals(User.class) && Objects.nonNull(token =
                nativeWebRequest.getNativeRequest(HttpServletRequest.class).getHeader("token"))) {
            return redisService.get(token);
        }
        return null;
    }
}
