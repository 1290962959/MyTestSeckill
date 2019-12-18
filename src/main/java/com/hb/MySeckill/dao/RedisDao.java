package com.hb.MySeckill.dao;

import com.alibaba.fastjson.JSON;
import com.hb.MySeckill.constants.RedisPrefix;
import com.hb.MySeckill.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Component
public class RedisDao {
    @Resource(name = "initJedisPool")
    private JedisPool jedisPool;

    @Autowired
    private GoodsDao goodsDao;

    /**
     * 获取货物对象
     * @param goodId
     * @return
     */
    public Goods getGoodById(int goodId) {
        Jedis jedis = jedisPool.getResource();
        try {
            String goodJsonStr = jedis.get(RedisPrefix.GOODS_PREFIX + goodId);
            if (goodJsonStr == null || goodJsonStr.length() == 0) {
                Goods goods = goodsDao.getGoodsByid(goodId);
                if (goods == null) {
                    return null;
                }
                jedis.sadd(RedisPrefix.GOODS_IDS_SET, String.valueOf(goodId));
                jedis.set(RedisPrefix.GOODSNUM_PREFIX + goodId, String.valueOf(goods.getGoodNum()));
                jedis.set(RedisPrefix.GOODS_PREFIX + goodId, JSON.toJSONString(goods));
                return goods;
            } else {
                Goods goods = JSON.parseObject(goodJsonStr, Goods.class);
                return goods;
            }
        } finally {
            jedis.close();
        }
    }

    /**
     * 将good存入redis
     * @param good
     */
    public void saveGoods(Goods good) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(RedisPrefix.GOODS_PREFIX + good.getGoodId(), JSON.toJSONString(good));
        jedis.sadd(RedisPrefix.GOODS_IDS_SET, String.valueOf(good.getGoodId()));
        jedis.set(RedisPrefix.GOODSNUM_PREFIX + good.getGoodId(), String.valueOf(good.getGoodNum()));
        jedis.close();
    }


}
