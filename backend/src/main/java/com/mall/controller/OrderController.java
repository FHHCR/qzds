package com.mall.controller;

import com.mall.common.BusinessException;
import com.mall.common.Result;
import com.mall.entity.dto.CreateOrderDTO;
import com.mall.entity.vo.OrderDetailVO;
import com.mall.entity.vo.OrderVO;
import com.mall.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单接口（均需登录）。
 */
@Tag(name = "订单")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "创建订单（从购物车结算）")
    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody CreateOrderDTO req) {
        return Result.ok(orderService.createOrder(currentUserId(), req));
    }

    @Operation(summary = "订单列表")
    @GetMapping("/list")
    public Result<List<OrderVO>> list() {
        return Result.ok(orderService.listOrders(currentUserId()));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(currentUserId(), id));
    }

    @Operation(summary = "支付（模拟）")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.pay(currentUserId(), id);
        return Result.ok();
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(currentUserId(), id);
        return Result.ok();
    }

    /** 从 SecurityContext 取当前登录用户 id */
    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getDetails() instanceof Long uid)) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return uid;
    }
}
