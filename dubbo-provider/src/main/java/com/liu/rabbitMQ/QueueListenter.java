package com.liu.rabbitMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;


/**
 * 监听器，用于监听队列中的消息
 */

//@Component 在xml中配置bean
public class QueueListenter implements MessageListener {

    Logger logger = LoggerFactory.getLogger(QueueListenter.class);
    @Override
    public void onMessage(Message message) {
        try {
            logger.info("接收到消息"+new String(message.getBody(),"utf-8")); //接收端消息还原，设置编码
            System.out.println("消息的属性"+message.getMessageProperties());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
