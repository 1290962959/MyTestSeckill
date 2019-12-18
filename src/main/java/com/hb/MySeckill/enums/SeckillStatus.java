package com.hb.MySeckill.enums;

public enum SeckillStatus {
    INSECKILL(0, "秒杀中！"),
    NO_GOOD(1, "找不到此货物！"),
    GOOD_NUM_IS_ZERO(2, "货物数量为0！"),
    GOOD_OUTOF_TIME(3, "货物不在秒杀时间内！"),
    GOOD_NOT_IN_KILLSTATUS(4, "货物不为可秒杀状态！"),
    REPEAT_KILL(5, "重复秒杀！"),
    IN_KILL_QUEUE(6, "排队中"),
    SUCCESS_KILL(7, "秒杀成功！"),
    FAIL_KILL(8, "没抢到！"),
    ACCESS_LIMIT(9, "没抢到！");

    private int statusCode;
    private String statusDescription;

    SeckillStatus(int code, String description) {
        this.statusCode = code;
        this.statusDescription = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
