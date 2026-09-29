package com.mall.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应 VO：返回 JWT 与基础用户信息。
 */
@Data
@AllArgsConstructor
public class LoginVO {

    private String token;

    private Long id;

    private String username;

    private String nickname;

    /** 角色：1 普通用户 / 2 管理员 */
    private Integer role;
}
