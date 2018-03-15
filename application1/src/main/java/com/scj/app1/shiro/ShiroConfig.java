package com.scj.app1.shiro;

import com.scj.sso.core.AbstractShiroConfig;
import com.scj.sso.core.SAuthenticationFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig extends AbstractShiroConfig{
    @Override
    public Map<String, String> buildFilterChainDefinitionMap() {
        Map<String, String> config = new HashMap<>();
        config.put("/","authc");
        config.put("/test","sauthc");
        config.put("/**","sauthc");
        return config;
    }

    @Override
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = super.shiroFilter(securityManager);
        Map<String, Filter> filters = new HashMap<>();
        filters.put("sauthc",new SAuthenticationFilter());
        shiroFilterFactoryBean.setFilters(filters);
        return shiroFilterFactoryBean;
    }

    /*@Bean
    public FilterRegistrationBean filterRegistration(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new SAuthenticationFilter());
        filterRegistrationBean.setEnabled(false);//这个是内部filter，放置加载到servlet容器中去
        return filterRegistrationBean;
    }*/
}
