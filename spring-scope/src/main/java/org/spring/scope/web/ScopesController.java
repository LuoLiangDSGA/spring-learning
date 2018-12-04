package org.spring.scope.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.scope.model.HelloMessageGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author luoliang
 * @date 2018/12/4
 */
@RestController
public class ScopesController {
    private static final Logger logger = LoggerFactory.getLogger(ScopesController.class);
    @Resource(name = "requestScopedBean")
    private HelloMessageGenerator requestScopedBean;
    @Resource(name = "sessionScopedBean")
    private HelloMessageGenerator sessionScopedBean;
    @Resource(name = "applicationScopedBean")
    private HelloMessageGenerator applicationScopedBean;

    @RequestMapping("/scopes/request")
    public String getRequestScopeMessage() {
        logger.debug("previousMessage：{}", requestScopedBean.getMessage());
        requestScopedBean.setMessage("Good Morning!");
        logger.debug("currentMessage：{}", requestScopedBean.getMessage());

        return "scopesExample";
    }

    @RequestMapping("/scopes/session")
    public String getSessionScopeMessage() {
        logger.debug("previousMessage：{}", sessionScopedBean.getMessage());
        sessionScopedBean.setMessage("Good Afternoon!");
        logger.debug("currentMessage：{}", sessionScopedBean.getMessage());

        return "scopesExample";
    }

    @RequestMapping("/scopes/application")
    public String getApplicationScopeMessage() {
        logger.debug("previousMessage：{}", applicationScopedBean.getMessage());
        applicationScopedBean.setMessage("Good Afternoon!");
        logger.debug("currentMessage：{}", applicationScopedBean.getMessage());

        return "scopesExample";
    }
}
