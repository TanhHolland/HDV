package com.intentionservice.domain.service;

import com.intentionservice.domain.vo.Lock;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toMap;

@Service
public class RedisLockService implements LockService {
    private static final String DEFAULT_LOCK_PREFIX = "qbike.lock.";
    @Autowired
    StringRedisTemplate redisOperations;
    private String prefix = DEFAULT_LOCK_PREFIX;
    @Setter
    private long expiry = 30000;

    @Override
    public Lock create(String name) throws Exception {
        String stored = getValue(name);
        if (stored != null) {
            throw new Exception();
        }
        String value = UUID.randomUUID().toString();
        String key = keyForName(name);
        if (!redisOperations.opsForValue().setIfAbsent(key, value)) {
            throw new Exception();
        }
        redisOperations.expire(key, expiry, TimeUnit.MILLISECONDS);
        Date expires = new Date(System.currentTimeMillis() + expiry);
        return new Lock(name, value, expires);
    }

    @Override
    public boolean release(String name, String value) throws Exception {
        String stored = getValue(name);
        if (stored != null && value.equals(stored)) {
            String key = keyForName(name);
            redisOperations.delete(key);
            return true;
        }
        if (stored != null) {
            throw new Exception();
        }
        return false;
    }

    private String getValue(String name) {
        String key = keyForName(name);
        String stored = redisOperations.opsForValue().get(key);
        return stored;
    }

    private String keyForName(String name) {
        return prefix + name;
    }
}