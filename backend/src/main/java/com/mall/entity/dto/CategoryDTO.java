package com.mall.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类新增/编辑请求（管理端）。
 */
@Data
public class CategoryDTO {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    /** 排序（越小越靠前） */
    private Integer sort = 0;
}
