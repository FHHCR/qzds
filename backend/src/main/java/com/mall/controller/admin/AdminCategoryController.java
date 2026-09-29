package com.mall.controller.admin;

import com.mall.common.Result;
import com.mall.entity.dto.CategoryDTO;
import com.mall.entity.po.Category;
import com.mall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端分类接口（需 ROLE_ADMIN）。
 */
@Tag(name = "管理端-分类")
@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

    private final ProductService productService;

    public AdminCategoryController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "分类列表（含禁用）")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(productService.adminListCategories());
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CategoryDTO req) {
        productService.adminCreateCategory(req);
        return Result.ok();
    }

    @Operation(summary = "修改分类")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO req) {
        productService.adminUpdateCategory(id, req);
        return Result.ok();
    }

    @Operation(summary = "删除分类（分类下有商品时禁止）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.adminDeleteCategory(id);
        return Result.ok();
    }
}
