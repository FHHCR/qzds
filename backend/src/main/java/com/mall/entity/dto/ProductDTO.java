package com.mall.entity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品新增/编辑请求（管理端）。
 */
@Data
public class ProductDTO {

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    /** 主图 URL */
    private String mainImage;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于 0")
    private Long price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于 0")
    private Integer stock;
}
