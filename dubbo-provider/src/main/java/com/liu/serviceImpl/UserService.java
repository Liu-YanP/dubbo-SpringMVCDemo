package com.liu.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liu.dao.UserMapper;
import com.liu.entity.User;
import com.liu.rabbitMQ.Impl.MQProducerImpl;
import com.liu.redis.RedisService;
import com.liu.redis.UserKey;
import com.liu.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Service
public class UserService implements IUserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQProducerImpl mqProducer;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<User> getUser(String... args) {
        String name = args[0];
        String age = args[1];
        List<User> userList= null;
        List<String> idList = null;
        if ((name!=null&&!name.equals("") )&& (age==null&&age.equals(""))){
            idList = redisService.sGet(UserKey.UserNameIdTable, name, String.class);
        }else if ((name==null&&name.equals("")) && (age!=null&&!age.equals(""))){
            idList = redisService.sGet(UserKey.UserAgeIdTable, age, String.class);
        }else{
            idList = redisService.sInter(UserKey.UserNameIdTable, name, UserKey.UserAgeIdTable, age, String.class);
        }
        if (idList!=null){
            for (String key : idList) {
                userList.add(redisService.get(UserKey.UserIdTable,key,User.class));
            }
            if (userList!=null){
                return userList;
            }
        }
        User user = new User();
        if (name!=null && !name.equals("")) user.setName(name);

        if (age!=null && !age.equals("")) user.setAge(Integer.valueOf(age));
        userList = userMapper.getUsers(user);
        if (userList!=null){
            for (User user1 : userList) {
                redisService.set(UserKey.UserIdTable,user1.getId().toString(),user1);
                redisService.sAdd(UserKey.UserNameIdTable,user1.getName(),user1.getId());
                redisService.sAdd(UserKey.UserAgeIdTable,user1.getAge().toString(),user1.getId());
            }
            return userList;
        }
        return userList;
    }

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
