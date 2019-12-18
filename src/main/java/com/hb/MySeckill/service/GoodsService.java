package com.hb.MySeckill.service;

import com.hb.MySeckill.dao.GoodsDao;
import com.hb.MySeckill.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;

    public void TestReadAllGoods() {
        List<Goods> goods = goodsDao.getAllGoods();
        if(goods != null) {
            for(Goods goods1 : goods) {
                System.out.println(goods1.getGoodName() + goods1.getCreateTime() + goods1.getGoodStatus());
            }
        }

        Goods goods1 = goodsDao.getGoodsByid(1);
        System.out.println(goods1.getGoodName() + goods1.getCreateTime() + goods1.getGoodStatus());

//        int num = goodsDao.reduceGoodNumById(1);
//        System.out.println(num);
    }


}
