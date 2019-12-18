package com.hb.MySeckill.returns;

public class OpenStatus {
    private boolean isOpen;
    private int goodId;
    //系统当前时间(毫秒)
    private long now;

    //开启时间
    private long start;

    //结束时间
    private long end;

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public OpenStatus(boolean isOpen, int goodId) {
        this.isOpen = isOpen;
        this.goodId = goodId;
    }

    public OpenStatus(boolean isOpen, int goodId, long start, long end, long now) {
        this.isOpen = isOpen;
        this.goodId = goodId;
        this.start = start;
        this.end = end;
        this.now = now;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }
}
