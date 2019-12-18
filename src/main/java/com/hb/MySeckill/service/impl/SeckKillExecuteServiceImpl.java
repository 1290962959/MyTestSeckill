package com.hb.MySeckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.hb.MySeckill.constants.RedisPrefix;
import com.hb.MySeckill.dao.GoodsDao;
import com.hb.MySeckill.dao.RedisDao;
import com.hb.MySeckill.entity.Goods;
import com.hb.MySeckill.entity.MQMessage;
import com.hb.MySeckill.enums.SeckillStatus;
import com.hb.MySeckill.exception.SeckillException;
import com.hb.MySeckill.mq.MQProducer;
import com.hb.MySeckill.returns.OpenStatus;
import com.hb.MySeckill.returns.ResultReturn;
import com.hb.MySeckill.service.SeckKillExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SeckKillExecuteServiceImpl implements SeckKillExecuteService {

    @Resource(name = "initJedisPool")
    private JedisPool jedisPool;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private MQProducer mqProducer;

    /**
     * 执行秒杀入口
     * @param goodId
     * @param userID
     */
    public SeckillStatus executeSeckill(int goodId, String userID) throws SeckillException {
        Jedis jedis = jedisPool.getResource();

        // 判断货物剩余数量
        int goodNum = Integer.valueOf(jedis.get(RedisPrefix.GOODSNUM_PREFIX + goodId));
        if(goodNum <= 0) {
            jedis.close();
            throw new SeckillException(SeckillStatus.GOOD_NUM_IS_ZERO);
        }

        // 判断该用户是否为已购买客户
        if(jedis.sismember(RedisPrefix.BOUGHT_USERS_SET + goodId, userID)) {
            jedis.close();
            throw new SeckillException(SeckillStatus.REPEAT_KILL);
        }

        jedis.close();

        MQMessage message = new MQMessage();
        message.setGoodId(goodId);
        message.setUserId(userID);
        mqProducer.produce(message);

        return SeckillStatus.IN_KILL_QUEUE;
    }

    /**
     * 判断货物是否开启秒杀
     * @param goodId
     * @return
     */
    public OpenStatus goodOpen(int goodId) {
        Goods goods = redisDao.getGoodById(goodId);
        if(goods == null) {
            goods = goodsDao.getGoodsByid(goodId);
            if(goods == null)
                return new OpenStatus(false, goodId); // 无法找到货物
            redisDao.saveGoods(goods);
            Date now = new Date();
            if(now.before(goods.getKillStartTime()) || now.after(goods.getKillEndTime()))
                return new OpenStatus(false, goodId, goods.getKillStartTime().getTime(), goods.getKillEndTime().getTime(), now.getTime()); // 不在秒杀状态
        }

        return new OpenStatus(true, goodId); // 开启秒杀
    }

    /**
     * 在redis中执行真正的秒杀程序
     * @param message
     */
    public void executeSeckillInRedis(MQMessage message) throws SeckillException {
        Jedis jedis = jedisPool.getResource();

        // 判断货物剩余数量
        int goodNum = Integer.valueOf(jedis.get(RedisPrefix.GOODSNUM_PREFIX + message.getGoodId()));
        if(goodNum <= 0) {
            jedis.close();
            throw new SeckillException(SeckillStatus.GOOD_NUM_IS_ZERO);
        }

        // 判断该用户是否为已购买客户
        if(jedis.sismember(RedisPrefix.BOUGHT_USERS_SET + message.getGoodId(), message.getUserId())) {
            jedis.close();
            throw new SeckillException(SeckillStatus.REPEAT_KILL);
        }

        jedis.decr(RedisPrefix.GOODSNUM_PREFIX + message.getGoodId());
        jedis.sadd(RedisPrefix.BOUGHT_USERS_SET + message.getGoodId(), message.getUserId());

        jedis.close();
    }

    /**
     * 查询秒杀结果
     * @param goodId
     * @param userId
     * @return
     */
    public int killResult(int goodId, String userId) {
        Jedis jedis = jedisPool.getResource();

        if(jedis.sismember(RedisPrefix.BOUGHT_USERS_SET + goodId, userId)) {
            jedis.close();
            return 1;
        }

        if(jedis.sismember(RedisPrefix.IN_QUEUE_USER, goodId + "@" + userId)) {
            jedis.close();
            return 0;
        }

        return 2;
    }
}
