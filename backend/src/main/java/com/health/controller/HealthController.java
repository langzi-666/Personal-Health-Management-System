package com.health.controller;

import com.health.dto.ResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Slf4j
@Api(tags = "系统管理", description = "系统相关接口")
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @ApiOperation(value = "健康检查", notes = "检查系统是否正常运行")
    public ResponseDTO<Map<String, Object>> health() {
        log.info("执行健康检查");
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("message", "服务正常运行");
        data.put("timestamp", System.currentTimeMillis());
        return ResponseDTO.success("健康检查通过", data);
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/system/info")
    @ApiOperation(value = "系统信息", notes = "获取系统基本信息")
    public ResponseDTO<Map<String, Object>> systemInfo() {
        log.info("获取系统信息");
        Map<String, Object> data = new HashMap<>();
        data.put("name", "个人健康管理系统");
        data.put("version", "1.0.0");
        data.put("javaVersion", System.getProperty("java.version"));
        data.put("osName", System.getProperty("os.name"));
        data.put("osVersion", System.getProperty("os.version"));
        return ResponseDTO.success(data);
    }
}
