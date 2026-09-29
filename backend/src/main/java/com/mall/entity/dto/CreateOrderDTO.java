package com.mall.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求（结算的购物车条目 + 收货地址）。
 */
@Data
public class CreateOrderDTO {

    @NotEmpty(message = "请选择要结算的商品")
    private List<Long> cartIds;

    @NotNull(message = "请选择收货地址")
    private Long addressId;
}
