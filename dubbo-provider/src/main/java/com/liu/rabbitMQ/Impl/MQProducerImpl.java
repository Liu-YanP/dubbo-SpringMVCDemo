package com.liu.rabbitMQ.Impl;

import com.liu.rabbitMQ.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 实现生产者逻辑
 */
@Component
public class MQProducerImpl implements MQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    Logger logger = LoggerFactory.getLogger(MQProducerImpl.class);

    /**
     * 数据发送方法
     * @param routerKey 绑定的路由
     * @param object  发送的对象
     */
    @Override
    public void sendDataToQueue(String exchange,String routerKey, Object object) {
        try {
            logger.info("向队列发送了一条消息！");
            rabbitTemplate.convertAndSend(exchange,routerKey,object);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
