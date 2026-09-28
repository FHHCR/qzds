package com.mall.controller;

import com.mall.common.PageVO;
import com.mall.common.Result;
import com.mall.entity.dto.ProductQueryDTO;
import com.mall.entity.po.Category;
import com.mall.entity.po.Product;
import com.mall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品接口（浏览免登录）：分类列表、商品分页、商品详情。
 */
@Tag(name = "商品")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "分类列表")
    @GetMapping("/category/list")
    public Result<List<Category>> categories() {
        return Result.ok(productService.listCategories());
    }

    @Operation(summary = "商品分页列表")
    @GetMapping("/list")
    public Result<PageVO<Product>> list(@Valid ProductQueryDTO query) {
        return Result.ok(productService.pageProducts(query));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productService.getProduct(id));
    }
}
