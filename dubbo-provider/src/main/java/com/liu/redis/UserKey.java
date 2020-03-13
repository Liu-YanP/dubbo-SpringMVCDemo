package com.liu.redis;

/**
 * user业务key的前缀以及过期定义
 */
public class UserKey extends BasePrefix {
   private UserKey(String prefix){
       super(prefix);
   }

   public static UserKey getById = new UserKey("id");
   public static UserKey getByName = new UserKey("name");
}
