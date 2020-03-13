package com.liu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.liu.entity.User;
import com.liu.service.ITestService;
import com.liu.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class TestController {
    @Reference
    private ITestService testService;

    @Reference
    private IUserService userService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        String result = testService.sayHello("刘延平");
        return result;
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public User getUser(){
        User user = userService.getUserByName("刘延平");
        System.out.println(user);
        return user;
    }

}
