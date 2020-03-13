package com.liu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.liu.entity.User;
import com.liu.service.IConService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class ProController {

    @Reference(check = false)
    private IConService conService;

    @RequestMapping("/id")
    @ResponseBody
    public User testConController(){
        return conService.getUserById(123456L);
    }
}
