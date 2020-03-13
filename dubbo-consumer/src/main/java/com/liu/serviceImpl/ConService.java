package com.liu.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.liu.entity.User;
import com.liu.service.IConService;

import java.time.LocalDateTime;

@Service
public class ConService implements IConService {
    @Override
    public User getUserById(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("汤猪猪");
        user.setAge(27);
        user.setEmail("tang@qq.com");
        user.setCreateTime(LocalDateTime.now());

        return user;
    }
}
