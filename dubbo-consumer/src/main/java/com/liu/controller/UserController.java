package com.liu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.liu.Params.Response.BaseResponse;
import com.liu.entity.User;
import com.liu.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Reference
    private IUserService userService;

    @RequestMapping("/listUser")
    @ResponseBody
    public BaseResponse<List<User>> listUser(){
        List<User> userList = userService.listUser();
        return BaseResponse.success(userList);
    }

    @RequestMapping("/getUserByName")
    @ResponseBody
    public BaseResponse<User> getUserByName(@RequestParam("name") String name, HttpServletRequest httpRequest){

        logger.info(httpRequest.getParameter("name")+"编码为："+httpRequest.getCharacterEncoding());

        User user = userService.getUserByName(name);
        if (user == null) {
            return BaseResponse.error("不存在该人!");
        }
        return BaseResponse.success(user);
    }

    @RequestMapping("/getUsers")
    @ResponseBody
    public BaseResponse<List<User>> getUsersByNameAndAge(@RequestParam("name") String name,@RequestParam("age") String age){
        List<User> userList = userService.getUser(name,age);
        if (userList!=null) return BaseResponse.success(userList);
        return BaseResponse.error("查询不到用户！");
    }
}
