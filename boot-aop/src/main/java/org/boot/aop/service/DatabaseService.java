package org.boot.aop.service;

import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/1
 **/
@Service
public class DatabaseService {

    public void update() {
        System.out.println("执行更新操作...");
    }

    public void add() {
        System.out.println("执行添加操作...");
    }
}
