package com.scj.app1;

import com.scj.sso.core.Principal;
import com.scj.sso.core.WebResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@SpringBootApplication
@Controller
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

    @RequestMapping
    public String app(Model model){
        Subject subject = SecurityUtils.getSubject();
        Principal principal = (Principal) subject.getPrincipal();
        if(principal!=null){
            model.addAttribute("username",principal.getUsername());
            model.addAttribute("phone",principal.getTelephone());
        }
        return "app";
    }

    /**
     * 测试清空session后，会不会通过自定义拦截器返回登陆失效json
     * @return
     */
    @GetMapping("/test")
    @ResponseBody
    public WebResult<String> test(){
        return new WebResult<String>("test","Success",1L,true);
    }
}
