package com.scj.sso.controller;

import com.scj.sso.WebResult;
import com.scj.sso.core.AuthenticationRealm;
import com.scj.sso.core.Principal;
import io.lettuce.core.dynamic.annotation.Param;
import io.netty.util.internal.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Controller
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * 绑定信息可以放到关系型数据库中去，我这边为了方便
     * 注意绑定信息需要随着用户的修改而修改，建议用户中心改变时，发消息到mq
     */
    private static final Map<String,Principal> UserBindInfoCache = new ConcurrentHashMap<>();

    @Value("${sso.successUrl}")
    private String successUrl;

    @Autowired
    private DefaultWebSessionManager sessionManager;

    @GetMapping(value = {"","login"})
    public ModelAndView loginPage(HttpServletRequest request, @Param("redirectUrl")String redirectUrl){
        redirectUrl=redirectUrl==null?successUrl:redirectUrl;
        Subject subject = SecurityUtils.getSubject();
        //如果session有效，重定向到目标页
        if(subject.isAuthenticated()){
            return new ModelAndView("redirect:"+redirectUrl);
        }

        //强制创建session 并且cookie返回sessionid
        WebUtils.saveRequest(request);
        //如果在手机上浏览，就跳转到绑定页面
        String agent = request.getHeader("User-Agent");
        if(!StringUtils.isEmpty(agent)){
            agent =agent.toLowerCase();
            if(agent.contains("android")||agent.contains("iphone")||agent.contains("window")){
                ModelAndView modelAndView = new ModelAndView("login_bind");
                modelAndView.addObject("redirectUrl",redirectUrl);
                return modelAndView;
            }
        }

        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("redirectUrl",redirectUrl);
        return modelAndView;
    }

    @PostMapping("/login")
    @ResponseBody
    public WebResult login(@RequestParam("username")String username,@RequestParam("password")String password){

        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username,password);


        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(usernamePasswordToken);
        }catch(Exception ex){
            logger.error("登录失败",ex);
            return new WebResult(null,false);
        }

        return new WebResult(null,true);
    }

    @GetMapping("/loginUser")
    @ResponseBody
    public WebResult loginUser(){
        Subject subject = SecurityUtils.getSubject();
        Principal principal = (Principal) subject.getPrincipal();
        if(principal==null){
            return new WebResult(null,false);
        }
        return new WebResult(principal,true);
    }

    @GetMapping("/logout")
    public String logout(@Param("redirectUrl")String redirectUrl){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/login?redirectUrl="+redirectUrl;
    }

    /**
     * 将第三方的id和我们内部系统用户绑定
     * @param username
     * @param password
     * @param thirdUserId
     * @return
     */
    @PostMapping("/bind")
    @ResponseBody
    public WebResult bind(@RequestParam("username")String username,
                          @RequestParam("password")String password,
                          @RequestParam("thirdUserId")String thirdUserId){
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username,password);

        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(usernamePasswordToken);
            Principal principal = (Principal) subject.getPrincipal();
            UserBindInfoCache.put(thirdUserId,principal);
        }catch(Exception ex){
            logger.error("登录失败",ex);
            return new WebResult(null,false);
        }

        return new WebResult(null,true);
    }

    /**
     * 第三方平台先调用这个接口判断是否绑定过用户
     * 如果绑定过，返回true
     * 同时在这个方法里面对当前session注入用户信息
     * @param thirdUserId
     * @return
     */
    @GetMapping("/isUserBind")
    @ResponseBody
    public WebResult isUserBind(@RequestParam("thirdUserId")String thirdUserId){
        if(!UserBindInfoCache.containsKey(thirdUserId)){
            return new WebResult(null,false);
        }
        Subject subject =SecurityUtils.getSubject();
        Session session = subject.getSession(false);
        SessionKey sessionKey = new DefaultSessionKey(session.getId());
        Principal principal = UserBindInfoCache.get(thirdUserId);
        SimplePrincipalCollection simplePrincipalCollection = new SimplePrincipalCollection(principal, AuthenticationRealm.class.getName());
        sessionManager.setAttribute(sessionKey, DefaultSubjectContext.PRINCIPALS_SESSION_KEY,simplePrincipalCollection);
        sessionManager.setAttribute(sessionKey, DefaultSubjectContext.AUTHENTICATED_SESSION_KEY,true);
        return new WebResult(null,true);


    }
}
