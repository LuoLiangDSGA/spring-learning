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
    /**
     * 切入点
     * execution表达式匹配org.boot.aop.service包下所有类的所有方法，包括任意参数
     */
    @Pointcut("execution(* org.boot.aop.service..*(..))")
    public void pointcut() {
    }

    /**
     * 前置通知
     */
    @Before("pointcut()")
    public void before() {
        System.out.println("前置通知---->记录方法开始日志");
    }

    /**
     * 后置通知
     */
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("后置通知---->记录方法结束日志");
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
