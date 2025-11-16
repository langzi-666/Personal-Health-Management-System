package com.health.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 加密工具类 - 用于密码加密和验证
 */
@Slf4j
@Component
public class EncryptUtil {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     */
    public static String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     */
    public static Boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 生成盐值密码 (使用BCrypt)
     */
    public static String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 验证盐值密码
     */
    public static Boolean verifyPassword(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }
}
