package com.mall.service;

import com.mall.common.PageVO;
import com.mall.entity.dto.AdminUserQueryDTO;
import com.mall.entity.dto.LoginDTO;
import com.mall.entity.dto.RegisterDTO;
import com.mall.entity.po.User;
import com.mall.entity.vo.AdminUserVO;
import com.mall.entity.vo.LoginVO;

/**
 * 用户服务：注册、登录、查询，以及管理端用户维护。
 */
public interface UserService {

    void register(RegisterDTO req);

    LoginVO login(LoginDTO req);

    User getById(Long id);

    /** 管理端：用户分页（脱敏，可按用户名/昵称搜索） */
    PageVO<AdminUserVO> adminPage(AdminUserQueryDTO query);

    /** 管理端：启用/禁用用户（不能操作自己与其他管理员） */
    void adminUpdateStatus(Long id, Integer status, Long operatorId);

    /** 管理端：删除用户（逻辑删除，不能删除自己与其他管理员） */
    void adminDelete(Long id, Long operatorId);
}
