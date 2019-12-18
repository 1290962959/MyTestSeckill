package com.hb.MySeckill.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hb.MySeckill.constants.RedisPrefix;
import com.hb.MySeckill.dao.RedisDao;
import com.hb.MySeckill.entity.Goods;
import com.hb.MySeckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@RestController
public class Test {
    @Autowired
    private GoodsService service;

    @Resource(name = "initJedisPool")
    private JedisPool jedisPool;

    @Autowired
    private RedisDao redisDao;

    @RequestMapping("/test")
    public void test() {
        //service.TestReadAllGoods();

        Jedis jedis = jedisPool.getResource();
        Goods goods = JSON.parseObject(jedis.get(RedisPrefix.GOODS_PREFIX + 1), Goods.class);

        System.out.println(goods);

        System.out.println(jedis.get(RedisPrefix.GOODSNUM_PREFIX + 1));
        jedis.close();

        System.out.println(redisDao.getGoodById(2));
    }
}
