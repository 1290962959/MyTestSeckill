package com.hb.MySeckill.dao;

import com.hb.MySeckill.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsDao {
    /**
     * 获取所有货物信息
     * @return
     */
    public List<Goods> getAllGoods();

    /**
     * 通过货物id返回货物信息
     * @param GoodId
     * @return
     */
    public Goods getGoodsByid(int GoodId);

    /**
     * 通过货物ID修改货物数量
     * @param GoodId
     * @return
     */
    public int reduceGoodNumById(int GoodId);

}
