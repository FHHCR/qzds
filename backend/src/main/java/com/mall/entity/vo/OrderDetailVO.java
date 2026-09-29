package com.mall.entity.vo;

import com.mall.entity.po.OrderItem;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情 VO。
 */
@Data
public class OrderDetailVO {

    /** 雪花 id，序列化为字符串（超出 JS 安全整数） */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String orderNo;

    private Long totalPrice;

    /** 收货地址快照 */
    private String addressSnapshot;

    /** 状态：0 待支付 / 1 已支付 / 2 已取消 */
    private Integer status;

    private LocalDateTime createTime;

    private List<OrderItem> items;
}
