package com.health.controller;

import com.health.dto.ResponseDTO;
import com.health.entity.User;
import com.health.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * 用户管理控制器
 */
@Slf4j
@Api(tags = "用户管理", description = "用户信息管理相关接口")
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "获取用户信息", notes = "根据用户ID获取用户信息")
    public ResponseDTO<User> getUserInfo(@PathVariable Long id) {
        try {
            User user = userService.getUserInfo(id);
            // 隐藏密码字段
            user.setPassword(null);
            return ResponseDTO.success(user);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "更新用户信息", notes = "更新用户的个人信息")
    public ResponseDTO<User> updateUserInfo(
            @PathVariable Long id,
            @RequestBody User updateUser) {
        try {
            User user = userService.updateUserInfo(id, updateUser);
            // 隐藏密码字段
            user.setPassword(null);
            return ResponseDTO.success("用户信息更新成功", user);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/{id}/change-password")
    @ApiOperation(value = "修改密码", notes = "用户修改自己的密码")
    public ResponseDTO<String> changePassword(
            @PathVariable Long id,
            @RequestParam @NotBlank(message = "旧密码不能为空") String oldPassword,
            @RequestParam @NotBlank(message = "新密码不能为空") String newPassword) {
        try {
            userService.changePassword(id, oldPassword, newPassword);
            return ResponseDTO.success("密码修改成功");
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }
}
