package com.sky.service;

public interface ShopService {
    /**
     * 设置店铺状态
     */
    void setShopStatus(Integer status);

    /**
     * 获取店铺状态
     */
    Integer getShopStatus();
}
