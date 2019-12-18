package com.hb.MySeckill.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Goods {
    private int GoodId;
    private String GoodName;
    private int GoodNum;
    private int GoodStatus;
    private Date KillStartTime;
    private Date KillEndTime;
    private Date CreateTime;

    public int getGoodId() {
        return GoodId;
    }

    public void setGoodId(int goodId) {
        GoodId = goodId;
    }

    public String getGoodName() {
        return GoodName;
    }

    public void setGoodName(String goodName) {
        GoodName = goodName;
    }

    public int getGoodNum() {
        return GoodNum;
    }

    public void setGoodNum(int goodNum) {
        GoodNum = goodNum;
    }

    public int getGoodStatus() {
        return GoodStatus;
    }

    public void setGoodStatus(int goodStatus) {
        GoodStatus = goodStatus;
    }

    public Date getKillStartTime() {
        return KillStartTime;
    }

    public void setKillStartTime(Date killStartTime) {
        KillStartTime = killStartTime;
    }

    public Date getKillEndTime() {
        return KillEndTime;
    }

    public void setKillEndTime(Date killEndTime) {
        KillEndTime = killEndTime;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    /**
     * 方便打印展示信息
     * @return
     */
    public String toString() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return "产品信息为：" +
                "GoodId = " + GoodId +
                ",GoodName = " + GoodName +
                ",GoodNum = " + GoodNum +
                ",GoodStaus = " + GoodStatus +
                ",KillStartTime = " + dateFormat.format(KillStartTime) +
                ",KillEndTime = " + dateFormat.format(KillEndTime);
    }
}
