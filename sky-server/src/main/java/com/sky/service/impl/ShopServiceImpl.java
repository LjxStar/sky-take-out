package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    /**
     * 设置店铺状态
     */
    @Override
    public void setShopStatus(Integer status) {
        redisTemplate.opsForValue().set(KEY, status);
    }

    @Override
    public Integer getShopStatus() {
        return (Integer) redisTemplate.opsForValue().get(KEY);
    }
}
