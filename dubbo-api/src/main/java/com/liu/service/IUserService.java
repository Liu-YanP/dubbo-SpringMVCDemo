package com.liu.service;

import com.liu.entity.User;

import java.util.List;

public interface IUserService {

    //通过多种方式查找user
    List<User> getUser(String... args);

    //通过用户名获取用户
    User getUserByName(String name);

    //列出所有用户
    List<User> listUser();
}
