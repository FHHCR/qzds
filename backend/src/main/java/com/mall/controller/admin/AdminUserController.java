package com.mall.controller.admin;

import com.mall.common.BusinessException;
import com.mall.common.PageVO;
import com.mall.common.Result;
import com.mall.entity.dto.AdminUserQueryDTO;
import com.mall.entity.vo.AdminUserVO;
import com.mall.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端用户接口（需 ROLE_ADMIN）。
 */
@Tag(name = "管理端-用户")
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /** 当前登录管理员 id（由 JwtAuthenticationFilter 放入 details） */
    private Long currentUid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getDetails() instanceof Long uid)) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return uid;
    }

    @Operation(summary = "用户分页（用户名/昵称搜索）")
    @GetMapping("/list")
    public Result<PageVO<AdminUserVO>> list(AdminUserQueryDTO query) {
        return Result.ok(userService.adminPage(query));
    }

    @Operation(summary = "启用/禁用用户（status: 1 启用 / 0 禁用）")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.adminUpdateStatus(id, status, currentUid());
        return Result.ok();
    }

    @Operation(summary = "删除用户（逻辑删除；不能删除自己与其他管理员）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.adminDelete(id, currentUid());
        return Result.ok();
    }
}
