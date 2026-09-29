package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.BusinessException;
import com.mall.entity.dto.AddressDTO;
import com.mall.entity.po.Address;
import com.mall.entity.vo.AddressVO;
import com.mall.mapper.AddressMapper;
import com.mall.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址服务实现。
 */
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    public AddressServiceImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public List<AddressVO> list(Long userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getId))
                .stream().map(this::toVO).toList();
    }

    @Override
    public AddressVO add(Long userId, AddressDTO req) {
        long count = addressMapper.selectCount(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId));
        Address address = new Address();
        address.setUserId(userId);
        address.setReceiver(req.getReceiver());
        address.setPhone(req.getPhone());
        address.setRegion(req.getRegion());
        address.setDetail(req.getDetail());
        // 首个地址自动设为默认
        address.setIsDefault(count == 0 ? 1 : 0);
        addressMapper.insert(address);
        return toVO(address);
    }

    @Override
    public void update(Long userId, Long id, AddressDTO req) {
        Address address = getOwnedAddress(userId, id);
        address.setReceiver(req.getReceiver());
        address.setPhone(req.getPhone());
        address.setRegion(req.getRegion());
        address.setDetail(req.getDetail());
        addressMapper.updateById(address);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        Address address = getOwnedAddress(userId, id);
        boolean wasDefault = address.getIsDefault() != null && address.getIsDefault() == 1;
        addressMapper.deleteById(id);
        // 删除的是默认地址时，最新一条自动补为默认
        if (wasDefault) {
            Address latest = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                    .eq(Address::getUserId, userId)
                    .orderByDesc(Address::getId)
                    .last("LIMIT 1"));
            if (latest != null) {
                latest.setIsDefault(1);
                addressMapper.updateById(latest);
            }
        }
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long id) {
        Address target = getOwnedAddress(userId, id);
        // 该用户全部置 0
        List<Address> all = addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId));
        for (Address a : all) {
            if (a.getIsDefault() != null && a.getIsDefault() == 1) {
                a.setIsDefault(0);
                addressMapper.updateById(a);
            }
        }
        target.setIsDefault(1);
        addressMapper.updateById(target);
    }

    private Address getOwnedAddress(Long userId, Long id) {
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在");
        }
        return address;
    }

    private AddressVO toVO(Address a) {
        AddressVO vo = new AddressVO();
        vo.setId(a.getId());
        vo.setReceiver(a.getReceiver());
        vo.setPhone(a.getPhone());
        vo.setRegion(a.getRegion());
        vo.setDetail(a.getDetail());
        vo.setIsDefault(a.getIsDefault());
        vo.setCreateTime(a.getCreateTime());
        return vo;
    }
}
