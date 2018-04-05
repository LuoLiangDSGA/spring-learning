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

    /**
     * 模拟数据库的添加操作
     */
    public void add() {
        System.out.println("执行添加操作...");
    }
}
