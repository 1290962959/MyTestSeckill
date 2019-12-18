package com.hb.MySeckill.mq;

import com.alibaba.fastjson.JSON;
import com.hb.MySeckill.bean.MQConfigBean;
import com.hb.MySeckill.constants.RedisPrefix;
import com.hb.MySeckill.entity.MQMessage;
import com.hb.MySeckill.enums.SeckillStatus;
import com.hb.MySeckill.exception.SeckillException;
import com.hb.MySeckill.service.SeckKillExecuteService;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class MQConsumer {
    private static Logger logger = LoggerFactory.getLogger(MQConsumer.class);

    @Autowired
    private MQConfigBean mqConfigBean;

    @Resource(name = "mqRecConnection")
    private Connection mqRecConnection;

    @Autowired
    private SeckKillExecuteService seckKillExecuteService;

    @Resource(name = "initJedisPool")
    private JedisPool jedisPool;

    public void receive() {
        Channel channel = null;

        try {
            channel = mqRecConnection.createChannel();

            //channel.exchangeDeclare(mqConfigBean.getExchange(), BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(mqConfigBean.getQueue(), true, false, false, null);
            //channel.queueBind(mqConfigBean.getQueue(), mqConfigBean.getExchange(), mqConfigBean.getRoutingKey());
            /**
             *  basicQos() Channel prefetch count
             *  The value defines the max number of unacknowledged deliveries that are permitted on a channel.
             *  Once the number reaches the configured count,
             *  RabbitMQ will stop delivering more messages on the channel unless at least one of the outstanding ones is acknowledged.
             */
            channel.basicQos(100); // 一般建议100-300

            channel.basicConsume(mqConfigBean.getQueue(), false, new MyDefaultConsumer(channel));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private class MyDefaultConsumer extends DefaultConsumer {
        private Channel channel;

        public MyDefaultConsumer(Channel channel) {
            super(channel);
            this.channel = channel;
        }

        @Override
        public void handleDelivery(String consumerTag,
                                   Envelope envelope,
                                   AMQP.BasicProperties properties,
                                   byte[] body)
                throws IOException {
            String routingKey = envelope.getRoutingKey();
            String exchange = envelope.getExchange();
            long delievryTag = envelope.getDeliveryTag();
            String messageBody = new String(body, "utf-8");
            logger.info("routingKey :" + routingKey + ",exchange : " + exchange  + ",delievryTag : " + delievryTag + ",messageBody : " + messageBody);
            //channel.basicAck(delievryTag, false);
            //channel.basicReject(delievryTag, true);
            //channel.basicNack(delievryTag, false, false);

            MQMessage message = JSON.parseObject(messageBody, MQMessage.class);

            boolean seckillStatus = true;
            try {
                seckKillExecuteService.executeSeckillInRedis(message);
            } catch (SeckillException e) {
                if(e.getSeckillStatus() == SeckillStatus.REPEAT_KILL || e.getSeckillStatus() == SeckillStatus.GOOD_NUM_IS_ZERO) {
                    seckillStatus = false;
                }
            } finally {
                if(seckillStatus == true) {
                    channel.basicAck(delievryTag, false);
                } else {
                    channel.basicNack(delievryTag, false, false);

                }

                Jedis jedis = jedisPool.getResource();
                jedis.srem(RedisPrefix.IN_QUEUE_USER, message.getGoodId() + "@" + message.getUserId());
                jedis.close();
            }
        }
    }

}
