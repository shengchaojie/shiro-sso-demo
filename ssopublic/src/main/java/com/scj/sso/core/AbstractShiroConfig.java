package com.scj.sso.core;

import com.scj.sso.core.seralization.JDKSerialization;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.Map;

@Configuration
public abstract class AbstractShiroConfig {

    @Value("${sso.successUrl}")
    private String successUrl;

    @Value("${sso.loginUrl}")
    private String loginUrl;

    @Value("${sso.cookie.domain}")
    private String cookieDomain;

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean =new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new DelegatingFilterProxy());
        filterRegistrationBean.setName("shiroFilter");
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("targetFilterLifecycle","true");
        return filterRegistrationBean;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setSuccessUrl(successUrl);
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(buildFilterChainDefinitionMap());
        return shiroFilterFactoryBean;
    }

    public abstract Map<String, String> buildFilterChainDefinitionMap();

    @Bean
    public SecurityManager securityManager(SessionManager sessionManager){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        securityManager.setRealm(new AuthenticationRealm());
        return securityManager;
    }

    @Bean
    public SessionManager sessionManager(SimpleCookie simpleCookie,SessionDAO sessionDAO){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookie(simpleCookie);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setGlobalSessionTimeout(1800000L);
        return sessionManager;
    }

    @Bean
    public SessionDAO sessionDAO(StringRedisTemplate stringRedisTemplate){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setStringRedisTemplate(stringRedisTemplate);
        redisSessionDAO.setSerialization(new JDKSerialization());
        return redisSessionDAO;
    }

    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setPath("/");
        simpleCookie.setDomain(cookieDomain);
        simpleCookie.setName("SCJSESSIONID");
        simpleCookie.setMaxAge(SimpleCookie.ONE_YEAR);
        return simpleCookie;
    }

}
