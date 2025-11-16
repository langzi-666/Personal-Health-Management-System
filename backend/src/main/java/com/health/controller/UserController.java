package com.health.controller;

import com.health.dto.ResponseDTO;
import com.health.entity.User;
import com.health.service.UserService;
import com.health.utils.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;

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

    /**
     * 上传头像
     */
    @PostMapping("/{id}/avatar")
    @ApiOperation(value = "上传头像", notes = "用户上传头像图片")
    public ResponseDTO<String> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            // 上传文件
            String avatarUrl = FileUtil.uploadAvatar(file, id);
            
            // 更新用户头像URL
            User user = userService.getUserInfo(id);
            String oldAvatarUrl = user.getAvatarUrl();
            user.setAvatarUrl(avatarUrl);
            userService.updateUserInfo(id, user);
            
            // 删除旧头像
            if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                FileUtil.deleteFile(oldAvatarUrl);
            }
            
            return ResponseDTO.success("头像上传成功", avatarUrl);
        } catch (IOException e) {
            log.error("头像上传失败: userId={}", id, e);
            return ResponseDTO.failure(500, "头像上传失败: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }
}
