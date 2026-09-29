package com.mall.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 管理端用户列表查询。
 */
@Data
public class AdminUserQueryDTO {

    /** 关键词，模糊匹配用户名/昵称，可选 */
    private String keyword;

    @Min(value = 1, message = "页码不能小于 1")
    private Long page = 1L;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private Long size = 10L;
}
