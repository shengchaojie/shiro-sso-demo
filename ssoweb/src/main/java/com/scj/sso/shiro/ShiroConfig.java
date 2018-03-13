package com.scj.sso.shiro;

import com.scj.sso.core.AbstractShiroConfig;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig extends AbstractShiroConfig{


    @Override
    public Map<String, String> buildFilterChainDefinitionMap() {
        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        filterChainDefinitionMap.put("/login","anon");
        return filterChainDefinitionMap;
    }
}
