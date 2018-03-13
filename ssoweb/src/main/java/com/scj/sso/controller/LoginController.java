package com.scj.sso.controller;

import com.scj.sso.WebResult;
import com.scj.sso.core.Principal;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);


    @Value("${sso.successUrl}")
    private String successUrl;

    @GetMapping(value = {"","login"})
    public ModelAndView loginPage(@Param("redirectUrl")String redirectUrl){
        redirectUrl=redirectUrl==null?successUrl:redirectUrl;
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            return new ModelAndView("redirect:"+redirectUrl);
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

}
