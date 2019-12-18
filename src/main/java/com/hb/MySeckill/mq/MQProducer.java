package com.hb.MySeckill.mq;

import com.alibaba.fastjson.JSON;
import com.hb.MySeckill.bean.MQConfigBean;
import com.hb.MySeckill.constants.RedisPrefix;
import com.hb.MySeckill.entity.MQMessage;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class MQProducer {
    private static Logger logger = LoggerFactory.getLogger(MQProducer.class);

    @Autowired
    private MQConfigBean mqConfigBean;

    @Autowired
    private MQChannelManager mqChannelManager;

    @Resource(name = "initJedisPool")
    private JedisPool jedisPool;


    public void produce(MQMessage message) {

        Channel channel = mqChannelManager.getSendChannel();
        try {
            channel.exchangeDeclare(mqConfigBean.getExchange(), BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(mqConfigBean.getQueue(), true, false, false, null);
            channel.queueBind(mqConfigBean.getQueue(), mqConfigBean.getExchange(), mqConfigBean.getRoutingKey());

            channel.confirmSelect(); // 开启channel publisher confirm模式
            channel.basicPublish(mqConfigBean.getExchange(),
                    mqConfigBean.getRoutingKey(),
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    JSON.toJSONString(message).getBytes());
        } catch (IOException e) {
            logger.error("队列消息发送失败！");
        }

        boolean isAcked = false;
        try {
            isAcked =  channel.waitForConfirms(100);
        } catch (TimeoutException e) {
            logger.error("消息队列等待确认超时！");
        } catch (InterruptedException e) {
            logger.error("消息队列等待被打断！");
        }

        if(isAcked) {
            logger.info("消费者已确认！");
            Jedis jedis = jedisPool.getResource();
            jedis.sadd(RedisPrefix.IN_QUEUE_USER, message.getGoodId() + "@" + message.getUserId());
            jedis.close();
        } else {
            logger.info("消费者未确认");
        }



    }
}
