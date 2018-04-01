package org.boot.aop;

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
public class BootKafkaApplication implements CommandLineRunner{
    @Resource
    private DatabaseService databaseService;

    public static void main(String[] args) {
        SpringApplication.run(BootKafkaApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        databaseService.add();
    }
}
