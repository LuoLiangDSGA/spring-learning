package org.boot.aop;

import org.boot.aop.proxy.CglibAopProxy;
import org.boot.aop.proxy.service.CglibService;
import org.boot.aop.service.DatabaseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.Resource;

/**
 * @author luoliang
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class BootAopApplication implements CommandLineRunner {
    @Resource
    private DatabaseService databaseService;
    @Resource
    private CglibService cglibService;

    public static void main(String[] args) {
        SpringApplication.run(BootAopApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
//        databaseService.add();
        cglib();
    }

    public void cglib() {
        cglibService = (CglibService) new CglibAopProxy(cglibService).getProxyInstance();

        cglibService.add();
    }
}
