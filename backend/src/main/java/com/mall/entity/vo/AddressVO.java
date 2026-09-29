package com.mall.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址 VO。
 */
@Data
public class AddressVO {

    private Long id;

    private String receiver;

    private String phone;

    private String region;

    private String detail;

    private Integer isDefault;

    private LocalDateTime createTime;
}
