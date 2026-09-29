package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.po.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址 Mapper。
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
