package com.hb.MySeckill.exception;

import com.hb.MySeckill.enums.SeckillStatus;

public class SeckillException extends Exception {
    private SeckillStatus seckillStatus;

    public SeckillException(SeckillStatus seckillStatus) {
        this.seckillStatus = seckillStatus;
    }
    public SeckillException(String message) {
        super(message);
    }
    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeckillStatus getSeckillStatus() {
        return seckillStatus;
    }

    public void setSeckillStatus(SeckillStatus seckillStatus) {
        this.seckillStatus = seckillStatus;
    }
}
