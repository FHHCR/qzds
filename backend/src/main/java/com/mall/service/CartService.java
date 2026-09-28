package com.mall.service;

import com.mall.entity.dto.CartAddDTO;
import com.mall.entity.vo.CartItemVO;

import java.util.List;

/**
 * 购物车服务：加入、列表、改数量、删除（均按当前登录用户隔离）。
 */
public interface CartService {

    /** 加入购物车：已有条目则累加数量，超库存报错 */
    void add(Long userId, CartAddDTO req);

    /** 当前用户购物车列表（含商品快照） */
    List<CartItemVO> list(Long userId);

    /** 修改数量（校验归属与库存） */
    void updateQuantity(Long userId, Long id, Integer quantity);

    /** 删除条目（校验归属） */
    void remove(Long userId, Long id);
}
