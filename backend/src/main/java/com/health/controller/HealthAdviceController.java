package com.health.controller;

import com.health.dto.ResponseDTO;
import com.health.service.HealthAdviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 健康建议控制器
 */
@Slf4j
@Api(tags = "健康建议", description = "健康建议相关接口")
@RestController
@RequestMapping("/api/advice")
@Validated
public class HealthAdviceController {

    @Autowired
    private HealthAdviceService healthAdviceService;

    /**
     * 生成健康建议
     */
    @GetMapping
    @ApiOperation(value = "获取健康建议", notes = "根据用户数据生成个性化健康建议")
    public ResponseDTO<Map<String, Object>> getHealthAdvice(@RequestParam Long userId) {
        try {
            Map<String, Object> advice = healthAdviceService.generateHealthAdvice(userId);
            return ResponseDTO.success("获取健康建议成功", advice);
        } catch (Exception e) {
            log.error("获取健康建议失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }
}

