package com.example.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Bean
    public JedisPool jedisPool(
            @Value("${redis.host}") String host,
            @Value("${redis.port}") int port,
            @Value("${redis.timeoutMs:2000}") int timeoutMs,
            @Value("${redis.pool.maxTotal:20}") int maxTotal,
            @Value("${redis.pool.maxIdle:10}") int maxIdle,
            @Value("${redis.pool.minIdle:2}") int minIdle
    ) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);

        poolConfig.setJmxEnabled(false);

        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);

        return new JedisPool(poolConfig, host, port, timeoutMs);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om;
    }
}
