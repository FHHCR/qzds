package com.mall.controller;

import com.mall.common.BusinessException;
import com.mall.common.Result;
import com.mall.entity.dto.LoginDTO;
import com.mall.entity.dto.RegisterDTO;
import com.mall.entity.po.User;
import com.mall.entity.vo.LoginVO;
import com.mall.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口：注册、登录（无需认证），获取当前登录用户（需认证）。
 */
@Tag(name = "用户")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO req) {
        userService.register(req);
        return Result.ok();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO req) {
        return Result.ok(userService.login(req));
    }

    @Operation(summary = "获取当前登录用户")
    @GetMapping("/me")
    public Result<User> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getDetails() instanceof Long uid)) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        User user = userService.getById(uid);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        user.setPassword(null);
        return Result.ok(user);
    }
}
