package org.spring.scope;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spring.scope.config.BeanConfig;
import org.spring.scope.model.Person;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScopeApplicationTests {

    @Test
    public void singletonScopeTest() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);

        Person personA = (Person) applicationContext.getBean("personSingleton");
        Person personB = (Person) applicationContext.getBean("personSingleton");

        Assert.assertEquals(personA, personB);
    }

    @Test
    public void prototypeScopeTest() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);

        Person personA = (Person) applicationContext.getBean("personPrototype");
        Person personB = (Person) applicationContext.getBean("personPrototype");

//        personA.setName("Thor");
//        personB.setName("Loki");

//        Assert.assertEquals("Thor", personA.getName());
//        Assert.assertEquals("Loki", personB.getName());
        Assert.assertEquals(personA, personB);
    }
}
