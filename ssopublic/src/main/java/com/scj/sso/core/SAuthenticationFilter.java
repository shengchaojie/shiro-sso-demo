package com.scj.sso.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Writer;

/**
 * Created by shengchaojie on 2018/3/14.
 */
@Slf4j
public class SAuthenticationFilter extends AdviceFilter{

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()){
            log.info("login invalid!");
            response.setContentType("application/json");
            Writer writer = response.getWriter();
            writer.write("{\"code\":501,\"message\":\"登陆失效\",\"flag\":false,\"object\":null}");
            writer.close();
            return false;
        }
        return true;
    }
}
