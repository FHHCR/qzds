package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.po.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细 Mapper。
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
