package org.boot.transaction;

import org.boot.transaction.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
public class Application implements ApplicationRunner {

    @Autowired
    private DataService dataService;
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        demo1();
        demo2();
    }

    private void demo1() {
        dataService.saveA("frank");
        dataService.findAll();
        try {
            dataService.saveAndRollback("frank");
        } catch (Exception e) {
            logger.warn("catch an save exception");
        }
        dataService.findAll();
        dataService.saveB("jack");
        dataService.findAll();
    }

    private void demo2() {
        dataService.saveA("frank");
        dataService.invokeSelf("frank");
        dataService.findAll();
        dataService.invokeWithApplicationContext("tom");
        dataService.findAll();
        dataService.invokeWithAop("jack");
        dataService.findAll();
    }
}
