package org.boot.aop.proxy.service;

import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/6
 **/
@Component
public class CglibService {
    public void add() {
        System.out.println("添加操作...");
    }

}
