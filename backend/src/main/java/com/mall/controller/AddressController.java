package com.mall.controller;

import com.mall.common.BusinessException;
import com.mall.common.Result;
import com.mall.entity.dto.AddressDTO;
import com.mall.entity.vo.AddressVO;
import com.mall.service.AddressService;
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
 * 收货地址接口（均需登录，按当前用户隔离）。
 */
@Tag(name = "收货地址")
@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "地址列表")
    @GetMapping("/list")
    public Result<List<AddressVO>> list() {
        return Result.ok(addressService.list(currentUserId()));
    }

    @Operation(summary = "新增地址")
    @PostMapping
    public Result<AddressVO> add(@Valid @RequestBody AddressDTO req) {
        return Result.ok(addressService.add(currentUserId(), req));
    }

    @Operation(summary = "修改地址")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AddressDTO req) {
        addressService.update(currentUserId(), id, req);
        return Result.ok();
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(currentUserId(), id);
        return Result.ok();
    }

    @Operation(summary = "设为默认地址")
    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(currentUserId(), id);
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
