package com.health.controller;

import com.health.dto.LoginDTO;
import com.health.dto.LoginResponseDTO;
import com.health.dto.RegisterDTO;
import com.health.dto.ResponseDTO;
import com.health.entity.User;
import com.health.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@Api(tags = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "新用户注册账户")
    public ResponseDTO<User> register(@Validated @RequestBody RegisterDTO registerDTO) {
        try {
            User user = userService.register(registerDTO);
            // 返回用户信息时隐藏密码
            user.setPassword(null);
            return ResponseDTO.success("注册成功", user);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户使用用户名/邮箱和密码登录")
    public ResponseDTO<LoginResponseDTO> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO response = userService.login(loginDTO);
            return ResponseDTO.success("登录成功", response);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(401, e.getMessage());
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    @ApiOperation(value = "重置密码", notes = "通过邮箱重置密码")
    public ResponseDTO<String> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {
        try {
            userService.resetPassword(email, newPassword);
            return ResponseDTO.success("密码重置成功");
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    @ApiOperation(value = "获取当前用户信息", notes = "获取当前登录用户的信息")
    public ResponseDTO<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // 从Token中获取用户ID（这里需要自定义拦截器或注解注入）
            // 暂时返回示例，实际应该从请求上下文获取
            return ResponseDTO.failure(500, "需要实现用户上下文提取逻辑");
        } catch (Exception e) {
            return ResponseDTO.failure(500, "获取用户信息失败: " + e.getMessage());
        }
    }
}
