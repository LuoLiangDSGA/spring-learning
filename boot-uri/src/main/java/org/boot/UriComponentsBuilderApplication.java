package org.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class UriComponentsBuilderApplication implements ApplicationRunner {
    @Autowired
    private ApplicationContext applicationContext;
    public static void main(String[] args) {
        SpringApplication.run(UriComponentsBuilderApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        applicationContext.getBean(Aclass.class);
    }

    @Component(value = "AClass")
    public class Aclass  {
        @Autowired
        private Bclass bclass;
    }

    @Component
    public class Bclass {
        @Autowired
        private Aclass aclass;
    }
}
