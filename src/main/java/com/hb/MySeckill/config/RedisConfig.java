package com.hb.MySeckill.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private int maxWait;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        jedisPoolConfig.setBlockWhenExhausted(true); // 连接耗尽时是否阻塞，如果为false直接报异常，true阻塞直到超时，默认为true
        jedisPoolConfig.setTestOnBorrow(true);  // ?
        jedisPoolConfig.setTestOnReturn(true);  // ?

        return jedisPoolConfig;
    }

    @Bean(name = "initJedisPool")
    public JedisPool initJedisPool(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig) {
        if(password.equals("")) {
            password = null;
        }
        return new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
    }
}
