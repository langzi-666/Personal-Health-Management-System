package com.health.controller;

import com.health.dto.ResponseDTO;
import com.health.entity.Alert;
import com.health.service.AlertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 告警规则控制器
 */
@Slf4j
@Api(tags = "告警管理", description = "用户告警规则相关接口")
@RestController
@RequestMapping("/api/alerts")
@Validated
public class AlertController {

    @Autowired
    private AlertService alertService;

    /**
     * 创建告警规则
     */
    @PostMapping
    @ApiOperation(value = "创建告警规则", notes = "创建新的用户告警规则")
    public ResponseDTO<Alert> createAlert(@Validated @RequestBody Alert alert) {
        try {
            Alert result = alertService.createAlert(alert);
            return ResponseDTO.success("创建告警规则成功", result);
        } catch (Exception e) {
            log.error("创建告警规则失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取用户的告警规则列表
     */
    @GetMapping
    @ApiOperation(value = "获取告警规则列表", notes = "获取当前用户的所有告警规则")
    public ResponseDTO<List<Alert>> getAlertList(@RequestParam Long userId) {
        try {
            List<Alert> list = alertService.getAlertList(userId);
            return ResponseDTO.success("获取告警规则列表成功", list);
        } catch (Exception e) {
            log.error("获取告警规则列表失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取单个告警规则
     */
    @GetMapping("/{alertId}")
    @ApiOperation(value = "获取告警规则详情", notes = "获取单个告警规则的详细信息")
    public ResponseDTO<Alert> getAlertById(@PathVariable Long alertId) {
        try {
            Alert alert = alertService.getAlertById(alertId);
            return ResponseDTO.success("获取告警规则成功", alert);
        } catch (Exception e) {
            log.error("获取告警规则失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 更新告警规则
     */
    @PutMapping("/{alertId}")
    @ApiOperation(value = "更新告警规则", notes = "更新已有的告警规则")
    public ResponseDTO<Alert> updateAlert(
            @PathVariable Long alertId,
            @Validated @RequestBody Alert updateAlert) {
        try {
            Alert result = alertService.updateAlert(alertId, updateAlert);
            return ResponseDTO.success("更新告警规则成功", result);
        } catch (Exception e) {
            log.error("更新告警规则失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 删除告警规则
     */
    @DeleteMapping("/{alertId}")
    @ApiOperation(value = "删除告警规则", notes = "删除指定的告警规则")
    public ResponseDTO<String> deleteAlert(@PathVariable Long alertId) {
        try {
            alertService.deleteAlert(alertId);
            return ResponseDTO.success("删除告警规则成功");
        } catch (Exception e) {
            log.error("删除告警规则失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 启用/禁用告警规则
     */
    @PutMapping("/{alertId}/toggle")
    @ApiOperation(value = "启用/禁用告警规则", notes = "更改告警规则的启用状态")
    public ResponseDTO<String> toggleAlert(
            @PathVariable Long alertId,
            @RequestParam Boolean enabled) {
        try {
            alertService.toggleAlert(alertId, enabled);
            String message = enabled ? "启用告警规则成功" : "禁用告警规则成功";
            return ResponseDTO.success(message);
        } catch (Exception e) {
            log.error("更改告警规则状态失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }
}
