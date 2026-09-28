package com.mall.service;

import com.mall.entity.dto.LoginDTO;
import com.mall.entity.dto.RegisterDTO;
import com.mall.entity.po.User;
import com.mall.entity.vo.LoginVO;

/**
 * 用户服务：注册、登录、查询。
 */
public interface UserService {

    void register(RegisterDTO req);

    LoginVO login(LoginDTO req);

    User getById(Long id);
}
