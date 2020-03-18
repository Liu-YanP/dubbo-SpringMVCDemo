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

    public static UserKey UserIdTable = new UserKey("UserId_Data");
    public static UserKey UserNameIdTable = new UserKey("UserName_ID");
    public static UserKey UserAgeIdTable = new UserKey("UserAge_ID");

}
