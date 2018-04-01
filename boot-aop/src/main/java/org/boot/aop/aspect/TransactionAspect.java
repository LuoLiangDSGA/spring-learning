package org.boot.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/1
 **/
@Aspect
@Component
public class TransactionAspect {
    @Pointcut("execution(* org.boot.aop.service..*(..))")
    public void pointcut() {
    }

    /**
     * 前置通知
     */
    @Before("pointcut()")
    public void before() {
        System.out.println("前置通知---->开始事务");
    }
    /**
     * 后置通知
     */
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("后置通知---->提交事务");
    }

    /**
     * 环绕通知
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Around("pointcut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕通知---->开始事务");
        joinPoint.proceed();
        System.out.println("环绕通知---->提交事务");
    }
}
