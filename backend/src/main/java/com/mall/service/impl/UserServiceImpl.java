package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.BusinessException;
import com.mall.common.JwtUtil;
import com.mall.common.PageVO;
import com.mall.entity.dto.AdminUserQueryDTO;
import com.mall.entity.dto.LoginDTO;
import com.mall.entity.dto.RegisterDTO;
import com.mall.entity.po.User;
import com.mall.entity.vo.AdminUserVO;
import com.mall.entity.vo.LoginVO;
import com.mall.mapper.UserMapper;
import com.mall.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现：注册、登录、查询。
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(RegisterDTO req) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname() == null || req.getNickname().isBlank()
                ? req.getUsername() : req.getNickname());
        user.setRole(1);
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO req) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginVO(token, user.getId(), user.getUsername(), user.getNickname(), user.getRole());
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    // ==================== 管理端 ====================

    @Override
    public PageVO<AdminUserVO> adminPage(AdminUserQueryDTO query) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String kw = query.getKeyword().trim();
            wrapper.and(w -> w.like(User::getUsername, kw).or().like(User::getNickname, kw));
        }
        wrapper.orderByAsc(User::getId);
        Page<User> page = userMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);
        List<AdminUserVO> records = page.getRecords().stream().map(u -> {
            AdminUserVO vo = new AdminUserVO();
            vo.setId(u.getId());
            vo.setUsername(u.getUsername());
            vo.setNickname(u.getNickname());
            vo.setRole(u.getRole());
            vo.setStatus(u.getStatus());
            vo.setCreateTime(u.getCreateTime());
            return vo;
        }).toList();
        PageVO<AdminUserVO> pv = new PageVO<>();
        pv.setRecords(records);
        pv.setTotal(page.getTotal());
        pv.setPage(page.getCurrent());
        pv.setSize(page.getSize());
        return pv;
    }

    @Override
    public void adminUpdateStatus(Long id, Integer status, Long operatorId) {
        User user = getUserById(id);
        checkNotSelfAndNotAdmin(user, operatorId, "禁用/启用");
        user.setStatus(status == null || status != 1 ? 0 : 1);
        userMapper.updateById(user);
    }

    @Override
    public void adminDelete(Long id, Long operatorId) {
        User user = getUserById(id);
        checkNotSelfAndNotAdmin(user, operatorId, "删除");
        userMapper.deleteById(id);
    }

    private User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private void checkNotSelfAndNotAdmin(User target, Long operatorId, String action) {
        if (target.getId().equals(operatorId)) {
            throw new BusinessException(400, "不能" + action + "自己");
        }
        if (target.getRole() != null && target.getRole() == 2) {
            throw new BusinessException(400, "管理员账号不允许" + action);
        }
    }
}
