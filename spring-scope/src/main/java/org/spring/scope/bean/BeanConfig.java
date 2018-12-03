package org.spring.scope.bean;

import org.spring.scope.model.Person;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * @author luoliang
 * @date 2018/11/28
 */
@SpringBootConfiguration
public class BeanConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Person personSingleton() {
        return new Person("Thor");
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Person personPrototype() {
        return new Person("Thor");
    }
}
