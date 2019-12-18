package com.hb.MySeckill.mq;

import com.hb.MySeckill.bean.MQConfigBean;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class MQChannelManager {
    private static Logger logger = LoggerFactory.getLogger(MQChannelManager.class);

    @Resource(name = "mqConnection")
    private Connection mqConnection;

    @Autowired
    private MQConfigBean mqConfigBean;

    private ThreadLocal<Channel> sendChannel = new ThreadLocal<Channel>() {
        public Channel initialValue() {
            try {
                Channel channel = mqConnection.createChannel();
                channel.exchangeDeclare(mqConfigBean.getExchange(), BuiltinExchangeType.DIRECT, true);
                channel.queueDeclare(mqConfigBean.getQueue(), true, false, false, null);
                channel.queueBind(mqConfigBean.getQueue(), mqConfigBean.getExchange(), mqConfigBean.getRoutingKey());
                return channel;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    };

    public Channel getSendChannel() {
        logger.info("当前线程号为：{}", Thread.currentThread().getId());
        Channel channel = sendChannel.get();
        if(channel == null) {
            try {
                channel = mqConnection.createChannel();
                channel.exchangeDeclare(mqConfigBean.getExchange(), BuiltinExchangeType.DIRECT, true);
                channel.queueDeclare(mqConfigBean.getQueue(), true, false, false, null);
                channel.queueBind(mqConfigBean.getQueue(), mqConfigBean.getExchange(), mqConfigBean.getRoutingKey());

                sendChannel.set(channel);
            } catch (IOException e) {
                logger.error("创建线程{}发送channel失败！", Thread.currentThread().getId());
                e.printStackTrace();
            }
        }
        return channel;
    }



}
