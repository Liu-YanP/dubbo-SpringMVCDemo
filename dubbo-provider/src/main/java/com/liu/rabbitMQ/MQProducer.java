package com.liu.rabbitMQ;

public interface MQProducer {

    public void sendDataToQueue(String queueKey,Object object);
}
