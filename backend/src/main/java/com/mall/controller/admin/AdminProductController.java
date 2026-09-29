package com.mall.controller.admin;

import com.mall.common.PageVO;
import com.mall.common.Result;
import com.mall.entity.dto.AdminProductQueryDTO;
import com.mall.entity.dto.ProductDTO;
import com.mall.entity.po.Product;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端商品接口（需 ROLE_ADMIN，由 SecurityConfig 拦截）。
 */
@Tag(name = "管理端-商品")
@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "商品分页（含下架）")
    @GetMapping("/list")
    public Result<PageVO<Product>> list(AdminProductQueryDTO query) {
        return Result.ok(productService.adminPage(query));
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ProductDTO req) {
        productService.adminCreate(req);
        return Result.ok();
    }

    @Operation(summary = "修改商品")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductDTO req) {
        productService.adminUpdate(id, req);
        return Result.ok();
    }

    @Operation(summary = "上架/下架（status: 1 上架 / 0 下架）")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.adminUpdateStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "删除商品（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.adminDelete(id);
        return Result.ok();
    }
}
