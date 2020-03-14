package com.liu.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liu.dao.UserMapper;
import com.liu.entity.User;
import com.liu.rabbitMQ.Impl.MQProducerImpl;
import com.liu.redis.RedisService;
import com.liu.redis.UserKey;
import com.liu.service.IUserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQProducerImpl mqProducer;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public User getUserByName(String name) {
        //首先在redis中查找

        User user;
        user = redisService.get(UserKey.getByName, name, User.class);
        if (user==null){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",name);
             user= userMapper.selectOne(queryWrapper);
             if (user!=null){
                 redisService.set(UserKey.getByName,name,user);
             }
        }
        mqProducer.sendDataToQueue("topicExchange","top.1",user);//使用topicExchange交换机
        mqProducer.sendDataToQueue("topicExchange","topic.",user);//使用topicExchange交换机
        mqProducer.sendDataToQueue("directExchange","queue1",user);//使用directExchange交换机

        return user;
    }

    @Override
    public List<User> listUser() {
        List<User> userList = userMapper.selectList(null);

        return userList;
    }

}
