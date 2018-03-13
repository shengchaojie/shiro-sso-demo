package com.scj.sso.core;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;

public class AuthenticationRealm extends AuthenticatingRealm{
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken =(UsernamePasswordToken)authenticationToken;
        if("scj".equals(usernamePasswordToken.getUsername())&&"123456".equals(new String(usernamePasswordToken.getPassword()))){
            Principal principal = new Principal();
            principal.setUserId(1L);
            principal.setUsername("盛超杰");
            principal.setTelephone("13388611621");
            return new SimpleAuthenticationInfo(principal,((UsernamePasswordToken) authenticationToken).getPassword(),getName());
        }

        throw new IncorrectCredentialsException("账户名或密码错误");
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }
}
