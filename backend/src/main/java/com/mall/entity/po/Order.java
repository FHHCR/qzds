package com.mall.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * 订单 PO（主键雪花 id，明细见 order_item 快照）。
 */
@Data
@TableName("`order`")
public class Order {

    /** 主键（雪花 id）；超出 JS 安全整数，序列化为字符串 */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String orderNo;

    private Long userId;

    /** 订单总价（单位：分） */
    private Long totalPrice;

    /** 状态：0 待支付 / 1 已支付 / 2 已取消 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
