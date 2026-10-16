package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 订单分页查询
     *
     * @param ordersPageQueryDTO
     * @param b
     * @return
     */
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 订单统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 根据id查询订单
     * @param orderId
     * @return
     */
    OrderVO getOrderById(Long orderId);

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void takeOrder(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    /**
     * 完成订单
     * @param id
     */
    void completeOrder(Long id);

     /**
     * 配送订单
     * @param id
     */
    void deliveryOrder(Long id);

    /**
     * 订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult historyQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 用户取消订单
     * @param id
     */
    void userCancelOrder(Long id);

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);
}