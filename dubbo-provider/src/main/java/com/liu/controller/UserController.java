package com.liu.controller;

import com.liu.Params.Response.BaseResponse;
import com.liu.entity.User;
import com.liu.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private IUserService userService;

    @RequestMapping("/listUser")
    @ResponseBody
    public BaseResponse<List<User>> listUser(){
        List<User> userList = userService.listUser();
        logger.info(userList.toString());
        return BaseResponse.success(userList);
    }
}
