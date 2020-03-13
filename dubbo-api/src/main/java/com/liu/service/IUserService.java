package com.liu.service;

import com.liu.entity.User;

import java.util.List;

public interface IUserService {

    //通过用户名获取用户
    public User getUserByName(String name);

    //列出所有用户
    public List<User> listUser();
}
