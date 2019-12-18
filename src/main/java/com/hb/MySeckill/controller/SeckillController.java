package com.hb.MySeckill.controller;

import com.hb.MySeckill.dao.GoodsDao;
import com.hb.MySeckill.entity.Goods;
import com.hb.MySeckill.enums.SeckillStatus;
import com.hb.MySeckill.exception.SeckillException;
import com.hb.MySeckill.returns.ExecuteStatus;
import com.hb.MySeckill.returns.OpenStatus;
import com.hb.MySeckill.returns.ResultReturn;
import com.hb.MySeckill.service.RateLimiterService;
import com.hb.MySeckill.service.SeckKillExecuteService;
import com.rabbitmq.client.AMQP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/kill")
public class SeckillController {
    @Autowired
    private SeckKillExecuteService service;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private RateLimiterService rateLimiterService;



    @RequestMapping(value = "/list")
    public String listGoods(Model model) {

        List<Goods> goods = goodsDao.getAllGoods();
        model.addAttribute("goods", goods);

        return "list";
    }

    @RequestMapping(value = "/detail/{goodId}")
    public String goodDetail(@PathVariable("goodId") int goodId, Model model) {
        Goods goods = goodsDao.getGoodsByid(goodId);
        model.addAttribute("good", goods);

        return "detail";
    }

    @RequestMapping("/time/now")
    @ResponseBody
    public ResultReturn<Long> now() {

        return new ResultReturn<>(true, new Date().getTime());
    }

    @RequestMapping(value = "/isGrab/{goodId}/{userId}")
    @ResponseBody
    public String checkStatus(@PathVariable("goodId") int goodId, @PathVariable("userId") String userId) {
        return "" + service.killResult(goodId, userId);
    }


    @RequestMapping(value = "/isOpen/{goodId}")
    @ResponseBody
    public ResultReturn<OpenStatus> isOpen(@PathVariable("goodId") int goodId) {
            OpenStatus openStatus = service.goodOpen(goodId);
            return new ResultReturn<>(true, openStatus);
    }

    @RequestMapping("/execution/{goodId}/{userId}")
    @ResponseBody
    public ResultReturn<ExecuteStatus> execute(@PathVariable("goodId") int goodId, @PathVariable("userId") String userId) {


        if(!rateLimiterService.tryAcquirePermission()) {
            return new ResultReturn<>(true, new ExecuteStatus(SeckillStatus.ACCESS_LIMIT.getStatusCode(), SeckillStatus.ACCESS_LIMIT.getStatusDescription()));
        }

        SeckillStatus status = null;

        try {

            status = service.executeSeckill(goodId, userId);
        } catch (SeckillException e) {
            status = e.getSeckillStatus();
        }
        return new ResultReturn<>(true, new ExecuteStatus(status.getStatusCode(), status.getStatusDescription()));
    }

}
