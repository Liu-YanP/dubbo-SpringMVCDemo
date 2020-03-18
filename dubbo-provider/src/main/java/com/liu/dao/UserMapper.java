package com.liu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liu.entity.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    public List<User> getUsers(User user);
}
