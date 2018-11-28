package org.spring.scope.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/11/27
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BeanInstance {
    @Resource
    private SessionService sessionService;

    @Resource
    private RequestService requestService;

    public SessionService getSessionService() {
        return sessionService;
    }

    public RequestService getRequestService() {
        return requestService;
    }

}
