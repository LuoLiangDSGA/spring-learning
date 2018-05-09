package org.spring.ioc.container;

import org.spring.ioc.entity.Blog;
import org.spring.ioc.entity.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/5/9
 **/
public class Main {
    private final static String APPLICATION = "classpath:application-*.xml";

    public static void main(String[] args) {
        //加载xml配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION);
        constructorInject(context);
        setterInject(context);
    }

    private static void constructorInject(ApplicationContext context) {
        //获取bean实例，传入的参数值为xml中配置的id
        User user1 = (User) context.getBean("user1");
        System.out.println(user1.toString());
        User user2 = (User) context.getBean("user2");
        System.out.println(user2.toString());
        User user3 = (User) context.getBean("user3");
        System.out.println(user3.toString());
    }

    private static void setterInject(ApplicationContext context) {
        Blog blog = (Blog) context.getBean("blog");
        System.out.println(blog.toString());
    }
}
