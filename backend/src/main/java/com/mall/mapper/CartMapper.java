package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.po.Cart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车 Mapper。
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
