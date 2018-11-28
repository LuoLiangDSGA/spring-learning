package org.spring.scope.web;

import org.spring.scope.service.BeanInstance;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/11/27
 */
@RequestMapping("/user")
@RestController
public class UserController {
    @Resource
    private BeanInstance beanInstance1;
    @Resource
    private BeanInstance beanInstance2;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Object login() {
        System.out.println("SessionService-1");
        beanInstance1.getSessionService().printId();
        System.out.println("SessionService-2");
        beanInstance2.getSessionService().printId();
        System.out.println("RequestService-1");
        beanInstance1.getRequestService().printId();
        System.out.println("RequestService-2");
        beanInstance2.getRequestService().printId();

        return "login";
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public Object check() {
        System.out.println("SessionService-1");
        beanInstance1.getSessionService().printId();
        System.out.println("SessionService-2");
        beanInstance2.getSessionService().printId();
        System.out.println("RequestService-1");
        beanInstance1.getRequestService().printId();
        System.out.println("RequestService-2");
        beanInstance2.getRequestService().printId();

        return "check";
    }
}
