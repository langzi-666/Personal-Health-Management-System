package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.dto.LoginDTO;
import com.health.dto.LoginResponseDTO;
import com.health.dto.RegisterDTO;
import com.health.entity.User;
import com.health.mapper.UserMapper;
import com.health.utils.EncryptUtil;
import com.health.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户业务逻辑类
 */
@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    public User register(RegisterDTO registerDTO) {
        // 验证两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        existingUser = userMapper.findByEmail(registerDTO.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("邮箱已存在");
        }

        // 创建新用户
        User user = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(registerDTO.getPassword())
                .isActive(true)
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        // 保存用户
        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
        return user;
    }

    /**
     * 用户登录
     */
    public LoginResponseDTO login(LoginDTO loginDTO) {
        // 根据用户名或邮箱查找用户
        User user = userMapper.findByUsernameOrEmail(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码（明文对比）
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查账户是否活跃
        if (!user.getIsActive()) {
            throw new RuntimeException("账户已被禁用");
        }

        // 更新最后登录时间
        user.setLastLogin(new Date());
        userMapper.updateById(user);

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        log.info("用户登录成功: {}", user.getUsername());

        // 返回登录响应
        return LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .realName(user.getRealName())
                .expiresIn(86400000L) // 24小时
                .build();
    }

    /**
     * 获取用户信息
     */
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    /**
     * 更新用户信息
     */
    public User updateUserInfo(Long userId, User updateUser) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新允许修改的字段
        if (updateUser.getRealName() != null) {
            user.setRealName(updateUser.getRealName());
        }
        if (updateUser.getGender() != null) {
            user.setGender(updateUser.getGender());
        }
        if (updateUser.getBirthDate() != null) {
            user.setBirthDate(updateUser.getBirthDate());
        }
        if (updateUser.getHeight() != null) {
            user.setHeight(updateUser.getHeight());
        }
        if (updateUser.getWeightGoal() != null) {
            user.setWeightGoal(updateUser.getWeightGoal());
        }
        if (updateUser.getAvatarUrl() != null) {
            user.setAvatarUrl(updateUser.getAvatarUrl());
        }
        if (updateUser.getBio() != null) {
            user.setBio(updateUser.getBio());
        }

        user.setUpdateTime(new Date());
        userMapper.updateById(user);
        log.info("用户信息已更新: {}", userId);
        return user;
    }

    /**
     * 修改密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码（明文对比）
        if (!oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 验证新密码是否与旧密码相同
        if (oldPassword.equals(newPassword)) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }

        // 更新密码（明文存储）
        user.setPassword(newPassword);
        user.setUpdateTime(new Date());
        userMapper.updateById(user);
        log.info("用户密码已修改: {}", userId);
    }

    /**
     * 重置密码
     */
    public void resetPassword(String email, String newPassword) {
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("邮箱对应的用户不存在");
        }

        user.setPassword(newPassword);
        user.setUpdateTime(new Date());
        userMapper.updateById(user);
        log.info("用户密码已重置: {}", email);
    }
}
