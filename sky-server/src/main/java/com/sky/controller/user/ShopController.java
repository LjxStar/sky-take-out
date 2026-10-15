package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "C端-店铺相关接口")
public class ShopController {
    @Autowired
    private ShopService shopService;

    /**
     * 获取店铺状态
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result getShopStatus() {
        Integer status = shopService.getShopStatus();
        log.info("获取店铺状态：{}", status == 1 ? "营业" : "打烊");
        return Result.success(status);
    }
}
