package com.mall.service;

import com.mall.entity.dto.AddressDTO;
import com.mall.entity.vo.AddressVO;

import java.util.List;

/**
 * 收货地址服务。
 */
public interface AddressService {

    /** 地址列表（默认排前） */
    List<AddressVO> list(Long userId);

    /** 新增（首个地址自动设为默认） */
    AddressVO add(Long userId, AddressDTO req);

    /** 修改（不含默认标记，设默认走 setDefault） */
    void update(Long userId, Long id, AddressDTO req);

    /** 删除（删除默认地址后，最新一条自动补为默认） */
    void delete(Long userId, Long id);

    /** 设为默认地址 */
    void setDefault(Long userId, Long id);
}
