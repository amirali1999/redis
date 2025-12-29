package com.example.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisCacheService {

    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    private final long defaultTtlSeconds;

    public RedisCacheService(
            JedisPool jedisPool,
            ObjectMapper objectMapper,
            @Value("${redis.cache.ttlSeconds:120}") long defaultTtlSeconds
    ) {
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
        this.defaultTtlSeconds = defaultTtlSeconds;
    }

    public Optional<String> get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return Optional.ofNullable(jedis.get(key));
        }
    }

    public void setex(String key, Duration ttl, String value) {
        long seconds = Math.max(1, ttl.getSeconds());
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    public void setex(String key, String value) {
        setex(key, Duration.ofSeconds(defaultTtlSeconds), value);
    }

    public void cache(String key, Object value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            setex(key, json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize value to JSON for key=" + key, e);
        }
    }

    public <T> Optional<T> getJson(String key, Class<T> clazz) {
        Optional<String> raw = get(key);
        if (raw.isEmpty()) return Optional.empty();

        try {
            return Optional.of(objectMapper.readValue(raw.get(), clazz));
        } catch (Exception e) {
            delete(key);
            return Optional.empty();
        }
    }

    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }
}
