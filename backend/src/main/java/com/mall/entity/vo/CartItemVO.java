package com.mall.entity.vo;

import lombok.Data;

/**
 * 购物车条目 VO（含商品快照信息）。
 */
@Data
public class CartItemVO {

    private Long id;

    private Long productId;

    /** 商品名称（商品失效时为 null） */
    private String name;

    private String mainImage;

    /** 售价（单位：分） */
    private Long price;

    private Integer stock;

    /** 购物车数量 */
    private Integer quantity;
}
