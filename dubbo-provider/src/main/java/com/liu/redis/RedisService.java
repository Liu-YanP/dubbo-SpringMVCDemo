package com.liu.redis;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Component
public class RedisService {
    Logger logger = LoggerFactory.getLogger(RedisService.class);

    private JedisPool jedisPool = null;

    //使用构造器的方式进行依赖注入
    @Autowired
    public RedisService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public List<String> getKey(String pattern){
        Jedis jedis = null;
        List<String> list=null;
        try {
            jedis = jedisPool.getResource();
            Set<String> keys = jedis.keys(pattern);
            list = new ArrayList<>(keys);
            return list;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 取两个集合的交集
     * @param prefix1
     * @param key1
     * @param prefix2
     * @param key2
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> sInter(KeyPrefix prefix1,String key1,KeyPrefix prefix2,String key2,Class<T> clazz){
        Jedis jedis = null;
        List<T> list = null;
        try{
            jedis = jedisPool.getResource();
            String realKey1 = prefix1.getPrefix()+"["+key1+"]";
            String realKey2 = prefix2.getPrefix()+"["+key2+"]";
            Set<String> sinter = jedis.sinter(realKey1, realKey2);
            for (String s : sinter) {
                list.add(stringToBean(s,clazz));
            }
            return list;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 集合读取操作
     * @param prefix
     * @param key
     * @param data
     * @param <T>
     * @return
     */
    public <T> List<T> sGet(KeyPrefix prefix, String key, Class<T> data){
        Jedis jedis = null;
        List<T> list = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+"["+key+"]";
            Set<String> members = jedis.smembers(realKey);  //取出set中全部集合
            for (String member : members) {
                T t = stringToBean(member, data);
                list.add(t);
            }
            return list;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 向set集合添加元素
     * @param prefix
     * @param key
     * @param value
     * @return
     */
    public <T> Long sAdd(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try {
            jedis=jedisPool.getResource();
            String realKey = prefix.getPrefix()+"["+key+"]";
            logger.info("set添加的realKey:"+realKey);
            String str = beanToString(value);
            Long result = jedis.sadd(realKey, str);
            logger.info("set集合添加了"+result+"条数据");
            return result;
        }finally {
            this.returnToPool(jedis);
        }
    }

    /**
     * 获取单个对象
     * @param prefix
     * @param key
     * @param data
     * @return
     */
    public <T> T get(KeyPrefix prefix,String key,Class<T> data){
        logger.info("@RedisService-REDIES-GET!");
        Jedis jedis=null;
        //在JedisPool里面取得Jedis
        try {
            jedis=jedisPool.getResource();
            //生成真正的key  className+":"+prefix;  BasePrefix:id1
            String realKey=prefix.getPrefix()+key;
            logger.info("@RedisService-get-realKey:"+realKey);
            String sval=jedis.get(realKey);
            logger.info("@RedisService-getvalue:"+sval);
            //将String转换为Bean入后传出
            T t=stringToBean(sval,data);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 移除对象,删除
     * @param prefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            long ret=jedis.del(realKey);
            return ret>0;//删除成功，返回大于0
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置单个、多个对象
     * @param prefix
     * @param key
     * @param value
     * @return
     */						//MiaoshaUserKey.token, token, user
    public <T> boolean set(KeyPrefix prefix,String key,T value){
        logger.info("@RedisService-REDIES-SET!");
        Jedis jedis=null;
        try {//在JedisPool里面取得Jedis
            jedis=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            logger.info("@RedisService-key:"+key);
            logger.info("@RedisService-getPrefix:"+prefix.getPrefix());
            String s=beanToString(value);//将T类型转换为String类型，json类型？？
            if(s==null||s.length()<=0) {
                return false;
            }
            int seconds=prefix.getExpireSeconds();
            if(seconds<=0) {//有效期：代表不过期，这样才去设置
                jedis.set(realKey, s);
            }else {//没有设置过期时间，即没有设置有效期，那么自己设置。
                jedis.setex(realKey, seconds,s);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 检查key是否存在
     * @param prefix
     * @param key
     * @return
     */
    public <T> boolean exitsKey(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     * @param prefix
     * @param key
     * @return
     */
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     * @param prefix
     * @param key
     * @return
     */
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    private void returnToPool(Jedis jedis) {
        if(jedis!=null) {
            jedis.close();
        }
    }
    /**
     * 将字符串转换为Bean对象
     *
     * parseInt()返回的是基本类型int 而valueOf()返回的是包装类Integer
     * Integer是可以使用对象方法的  而int类型就不能和Object类型进行互相转换 。
     * int a=Integer.parseInt(s);
     Integer b=Integer.valueOf(s);
     */
    public static <T> T stringToBean(String s,Class<T> clazz) {
        if(s==null||s.length()==0||clazz==null) {
            return null;
        }
        if(clazz==int.class||clazz==Integer.class) {
            return ((T) Integer.valueOf(s));
        }else if(clazz==String.class) {
            return (T) s;
        }else if(clazz==long.class||clazz==Long.class) {
            return (T) Long.valueOf(s);
        }else {
            return JSON.parseObject(s,clazz);
        }
    }

    public static <T> String beanToString(T object){
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        if (clazz==Integer.class||clazz==String.class||clazz==int.class||clazz==Long.class||clazz==long.class)
            return object.toString();
        else {
            return JSON.toJSONString(object);
        }
    }
}
