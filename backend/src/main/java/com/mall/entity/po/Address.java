package com.mall.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址 PO。
 */
@Data
@TableName("address")
public class Address {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 收货人 */
    private String receiver;

    /** 手机号 */
    private String phone;

    /** 省市区 */
    private String region;

    /** 详细地址 */
    private String detail;

    /** 是否默认：1 是 / 0 否 */
    private Integer isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
