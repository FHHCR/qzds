package com.mall.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * 订单明细 PO（商品信息下单时快照）。
 */
@Data
@TableName("order_item")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单 id（雪花），序列化为字符串 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    private Long productId;

    /** 商品名称（快照） */
    private String productName;

    /** 商品主图（快照） */
    private String productImage;

    /** 成交单价（单位：分，快照） */
    private Long price;

    private Integer quantity;

    private LocalDateTime createTime;
}
