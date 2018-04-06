package org.boot.aop.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/6
 **/
public class CglibAopProxy implements MethodInterceptor {
    private Object target;

    public CglibAopProxy(Object target) {
        this.target = target;
    }

    public Object getProxyInstance() {
        //增强工具
        Enhancer enhancer = new Enhancer();
        //设置父类
        enhancer.setSuperclass(target.getClass());
        //设置回调
        enhancer.setCallback(this);

        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("开始事务...");
        Object result = methodProxy.invoke(target, args);
        System.out.println("结束事务...");

        return result;
    }
}
