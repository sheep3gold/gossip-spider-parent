package cn.itcast.dubboxdemo.controller;

import cn.itcast.dubboxdemo.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {
    @Reference//使用alibaba的注解包
    private UserService userService;

    @RequestMapping("/showName")
    @ResponseBody
    public String showName() {
        return userService.getName();
    }
}
