package com.hb.MySeckill.service;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {
    private RateLimiter limiter = RateLimiter.create(10); // 限流每秒10个令牌

    public boolean tryAcquirePermission() {
        return limiter.tryAcquire();
    }
}
