package com.mall.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求（传入要结算的购物车条目 id）。
 */
@Data
public class CreateOrderDTO {

    @NotEmpty(message = "请选择要结算的商品")
    private List<Long> cartIds;
}
