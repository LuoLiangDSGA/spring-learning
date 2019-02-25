package org.boot.mystarter.config;

import org.aopalliance.aop.Advice;
import org.boot.mystarter.annotation.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2019/2/25
 * 需要调用AOP相关的lib，所以继承AbstractPointcutAdvisor
 */
@SpringBootConfiguration
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration extends AbstractPointcutAdvisor {
    private static Logger logger = LoggerFactory.getLogger(LogAutoConfiguration.class);

    private Pointcut pointcut;

    private Advice advice;

    @Resource
    private LogProperties logProperties;

    @PostConstruct
    public void init() {
        logger.info("init LogAutoConfiguration start.... ");
        this.pointcut = new AnnotationMatchingPointcut(null, Log.class);
        this.advice = new LogMethodInterceptor(logProperties.getExcludeArr());
        logger.info("init LogAutoConfiguration success...");
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }
}
