package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单管理")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("分页查询订单")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("分页查询订单：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 订单统计
     */
    @GetMapping("/statistics")
    @ApiOperation("订单统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("订单统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 根据id查询订单
     */
    @GetMapping("/details/{id}")
    @ApiOperation("根据id查询订单")
    public Result<OrderVO> getOrderById(@PathVariable Long id) {
        log.info("根据id查询订单：{}", id);
        OrderVO orderVO = orderService.getOrderById(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result takeOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单：{}", ordersConfirmDTO.getId());
        orderService.takeOrder(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单：{}", ordersRejectionDTO);
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单：{}", ordersCancelDTO);
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 完成订单
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result completeOrder(@PathVariable Long id) {
        log.info("完成订单：{}", id);
        orderService.completeOrder(id);
        return Result.success();
    }

    /**
     * 派送订单
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliveryOrder(@PathVariable Long id) {
        log.info("派送订单：{}", id);
        orderService.deliveryOrder(id);
        return Result.success();
    }
}
