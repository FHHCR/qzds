package com.mall.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 收货地址新增/修改请求。
 */
@Data
public class AddressDTO {

    @NotBlank(message = "收货人不能为空")
    private String receiver;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 省市区 */
    private String region;

    @NotBlank(message = "详细地址不能为空")
    private String detail;
}
