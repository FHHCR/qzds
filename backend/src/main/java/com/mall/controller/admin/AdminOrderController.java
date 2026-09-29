package com.mall.controller.admin;

import com.mall.common.PageVO;
import com.mall.common.Result;
import com.mall.entity.dto.AdminOrderQueryDTO;
import com.mall.entity.vo.OrderDetailVO;
import com.mall.entity.vo.OrderVO;
import com.mall.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端订单接口（需 ROLE_ADMIN，跨用户只读）。
 */
@Tag(name = "管理端-订单")
@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "订单分页（可按状态筛选）")
    @GetMapping("/list")
    public Result<PageVO<OrderVO>> list(AdminOrderQueryDTO query) {
        return Result.ok(orderService.adminPage(query));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.ok(orderService.adminGetDetail(id));
    }
}
