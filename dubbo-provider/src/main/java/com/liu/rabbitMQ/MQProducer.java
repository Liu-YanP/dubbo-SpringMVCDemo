package com.liu.rabbitMQ;

public interface MQProducer {

    public void sendDataToQueue(String exchange ,String routerKey,Object object);
}
