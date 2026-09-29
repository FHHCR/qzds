package com.mall.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 管理端订单列表查询（跨用户，可按状态筛选）。
 */
@Data
public class AdminOrderQueryDTO {

    /** 状态：0 待支付 / 1 已支付 / 2 已取消，可选 */
    private Integer status;

    @Min(value = 1, message = "页码不能小于 1")
    private Long page = 1L;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private Long size = 10L;
}
