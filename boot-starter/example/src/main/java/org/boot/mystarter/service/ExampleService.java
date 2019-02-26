package org.boot.mystarter.service;

import my.boot.starter.Log;
import org.springframework.stereotype.Service;

/**
 * @author luoliang
 * @date 2019/2/25
 */
@Service
public class ExampleService {

    @Log
    public void log() {
        System.out.println("this is log method...");
    }

    @Log
    public void core() {
        System.out.println("this is core method...");
    }

    @Log
    public void test() {
        System.out.println("this is test method...");
    }
}
