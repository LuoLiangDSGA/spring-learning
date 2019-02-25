package org.boot.mystarter.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author luoliang
 * @date 2019/2/25
 */
public class LogMethodInterceptor implements MethodInterceptor {
    private static Logger logger = LoggerFactory.getLogger(LogMethodInterceptor.class);
    private List<String> exclude;

    public LogMethodInterceptor(String[] exclude) {
        this.exclude = Arrays.asList(exclude);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String methodName = methodInvocation.getMethod().getName();
        if (exclude.contains(methodName)) {
            return methodInvocation.proceed();
        }
        long start = System.currentTimeMillis();
        Object result = methodInvocation.proceed();
        long end = System.currentTimeMillis();
        logger.info("===== method ({}), run time ({})", methodName, (end - start));

        return result;
    }
}
