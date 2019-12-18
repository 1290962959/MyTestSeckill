package com.hb.MySeckill.service;

import com.hb.MySeckill.entity.MQMessage;
import com.hb.MySeckill.enums.SeckillStatus;
import com.hb.MySeckill.exception.SeckillException;
import com.hb.MySeckill.returns.OpenStatus;
import com.hb.MySeckill.returns.ResultReturn;

public interface SeckKillExecuteService {
    /**
     * 执行秒杀入口
     * @param goodId
     * @param userID
     */
    public SeckillStatus executeSeckill(int goodId, String userID) throws SeckillException;

    /**
     * 判断货物是否开启秒杀
     * @param goodId
     * @return
     */
    public OpenStatus goodOpen(int goodId);

    /**
     * 在redis中执行真正的秒杀程序
     * @param message
     */
    public void executeSeckillInRedis(MQMessage message) throws SeckillException;

    /**
     * 查询秒杀结果
     * @param goodId
     * @param userId
     * @return
     */
    public int killResult(int goodId, String userId);

}
