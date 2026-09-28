package com.mall.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 商品列表查询 DTO。
 */
@Data
public class ProductQueryDTO {

    /** 分类 id，可选 */
    private Long categoryId;

    @Min(value = 1, message = "页码不能小于 1")
    private Long page = 1L;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private Long size = 10L;
}
