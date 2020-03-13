package com.liu.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.liu.service.ITestService;

@Service
public class TestService implements ITestService {


    public String sayHello(String name) {
        StringBuilder result = new StringBuilder("Hello,");
        return result.append(name).toString();
        //System.out.println("Hello,"+name);
    }
}
