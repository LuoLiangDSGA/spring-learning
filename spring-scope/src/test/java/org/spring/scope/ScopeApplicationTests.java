package org.spring.scope;

import com.sun.javafx.tk.TKPulseListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spring.scope.bean.BeanConfig;
import org.spring.scope.model.Person;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScopeApplicationTests {

    @Test
    public void singletonScopeTest() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);

        Person personA = (Person) applicationContext.getBean("personSingleton");
        Person personB = (Person) applicationContext.getBean("personSingleton");

        personA.setName("luoliang");
        Assert.assertEquals(personA, personB);
    }

    @Test
    public void prototypeScopeTest() {

    }
}
