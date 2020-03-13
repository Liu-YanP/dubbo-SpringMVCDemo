package com.liu.redis;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisService {
    Logger logger = LoggerFactory.getLogger(RedisService.class);

    private JedisPool jedisPool = null;

    //使用构造器的方式进行依赖注入
    @Autowired
    public RedisService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
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
