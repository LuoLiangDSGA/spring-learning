package org.boot.transaction.service;

import org.boot.transaction.dao.UserRepository;
import org.boot.transaction.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author luoliang
 * @date 2020/3/30
 */
@Service
public class DataService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DataService dataService;
    @Autowired
    private ApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Transactional(rollbackFor = RuntimeException.class)
    public void saveA(String user) {
        logger.info("invoke saveA {}", user);
        User u = new User();
        u.setName(user);
        userRepository.save(u);
    }

    public void saveB(String user) {
        logger.info("invoke saveB {}", user);
        try {
            User u = new User();
            u.setName(user);
            this.saveAndRollback(user);
        } catch (RuntimeException e) {
            logger.warn("catch an exception in saveB()");
        }
    }

    public void findAll() {
        logger.info("print data:");
        userRepository.findAll().forEach(user -> logger.info(user.toString()));
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void saveAndRollback(String user) {
        logger.info("invoke saveAndRollback {}", user);
        User u = new User();
        u.setName(user);
        userRepository.save(u);
        throw new RuntimeException();
    }

    public void invokeSelf(String user) {
        try {
            dataService.saveAndRollback(user);
        } catch (RuntimeException e) {
            logger.warn("catch an exception in invokeSelf()");
        }
    }

    public void invokeWithApplicationContext(String user) {
        try {
            ((DataService) applicationContext.getBean("dataService")).saveAndRollback(user);
        } catch (RuntimeException e) {
            logger.warn("catch an exception in invokeWithApplicationContext()");
        }
    }

    public void invokeWithAop(String user) {
        try {
            // 需要把@EnableAspectJAutoProxy注解中的(exposeProxy = true)
            ((DataService) AopContext.currentProxy()).saveAndRollback(user);
        } catch (RuntimeException e) {
            logger.warn("catch an exception in invokeWithAop()");
        }
    }
}
