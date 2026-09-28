package com.mall.service;

import com.mall.entity.dto.CreateOrderDTO;
import com.mall.entity.vo.OrderDetailVO;
import com.mall.entity.vo.OrderVO;

import java.util.List;

/**
 * 订单服务：下单（事务）、列表、详情、支付、取消。
 */
public interface OrderService {

    /** 从购物车勾选条目创建订单：校验库存并扣减、写订单与明细快照、清空对应购物车条目 */
    OrderVO createOrder(Long userId, CreateOrderDTO req);

    /** 当前用户订单列表（倒序） */
    List<OrderVO> listOrders(Long userId);

    /** 订单详情（含明细） */
    OrderDetailVO getOrderDetail(Long userId, Long id);

    /** 模拟支付：待支付 -> 已支付 */
    void pay(Long userId, Long id);

    /** 取消订单：待支付可取消，恢复库存 */
    void cancel(Long userId, Long id);
}
