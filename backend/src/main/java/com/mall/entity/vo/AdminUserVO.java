package com.mall.entity.vo;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * 管理端用户列表 VO（脱敏，不含密码）。
 */
@Data
public class AdminUserVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String username;

    private String nickname;

    /** 角色：1 普通用户 / 2 管理员 */
    private Integer role;

    /** 状态：1 正常 / 0 禁用 */
    private Integer status;

    private LocalDateTime createTime;
}
