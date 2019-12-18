package com.hb.MySeckill.boot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hb.MySeckill.constants.RedisPrefix;
import com.hb.MySeckill.dao.GoodsDao;
import com.hb.MySeckill.entity.Goods;
import com.hb.MySeckill.mq.MQConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.List;

@Component
public class InitApplication implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(InitApplication.class);

    @Autowired
    private MQConsumer mqConsumer;

    @Resource(name = "initJedisPool")
    private JedisPool jedisPool;

    @Autowired
    private GoodsDao goodsDao;

    @Override
    public void run(String... args) throws Exception {
        initRedis();
        mqConsumer.receive();
        logger.info("系统初始化完成。");
    }

    private void initRedis() {
        Jedis jedis = jedisPool.getResource();
        List<Goods> goods = goodsDao.getAllGoods();
        if(goods == null || goods.size() <= 0) {
            logger.error("无法获取数据库货物信息！");
            return;
        }
        for(Goods goods1 : goods) {
            jedis.set(RedisPrefix.GOODS_PREFIX + goods1.getGoodId(), JSON.toJSONString(goods1));
            jedis.set(RedisPrefix.GOODSNUM_PREFIX + goods1.getGoodId(), String.valueOf(goods1.getGoodNum()));
            jedis.sadd(RedisPrefix.GOODS_IDS_SET, String.valueOf(goods1.getGoodId()));
        }

        jedis.close();
    }
 }
