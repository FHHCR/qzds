package com.mall.entity.vo;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * 订单列表 VO。
 */
@Data
public class OrderVO {

    /** 雪花 id，序列化为字符串（超出 JS 安全整数） */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String orderNo;

    /** 订单总价（单位：分） */
    private Long totalPrice;

    /** 下单用户 id（管理端展示用） */
    private Long userId;

    /** 收货地址快照 */
    private String addressSnapshot;

    /** 状态：0 待支付 / 1 已支付 / 2 已取消 */
    private Integer status;

    /** 商品件数 */
    private Integer itemCount;

    private LocalDateTime createTime;
}
