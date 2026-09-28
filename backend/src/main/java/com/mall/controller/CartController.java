package com.mall.controller;

import com.mall.common.BusinessException;
import com.mall.common.Result;
import com.mall.entity.dto.CartAddDTO;
import com.mall.entity.dto.CartUpdateDTO;
import com.mall.entity.vo.CartItemVO;
import com.mall.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * 购物车接口（均需登录，按当前用户隔离）。
 */
@Tag(name = "购物车")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "加入购物车")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody CartAddDTO req) {
        cartService.add(currentUserId(), req);
        return Result.ok();
    }

    @Operation(summary = "购物车列表")
    @GetMapping("/list")
    public Result<List<CartItemVO>> list() {
        return Result.ok(cartService.list(currentUserId()));
    }

    @Operation(summary = "修改数量")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CartUpdateDTO req) {
        cartService.updateQuantity(currentUserId(), id, req.getQuantity());
        return Result.ok();
    }

    @Operation(summary = "删除条目")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        cartService.remove(currentUserId(), id);
        return Result.ok();
    }

    /** 从 SecurityContext 取当前登录用户 id */
    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getDetails() instanceof Long uid)) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return uid;
    }
}
