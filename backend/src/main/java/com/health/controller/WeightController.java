package com.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.dto.ResponseDTO;
import com.health.entity.Weight;
import com.health.service.WeightService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 体重管理控制器
 */
@Slf4j
@Api(tags = "健康数据", description = "体重管理相关接口")
@RestController
@RequestMapping("/api/health/weight")
public class WeightController {

    @Autowired
    private WeightService weightService;

    /**
     * 添加体重记录
     */
    @PostMapping
    @ApiOperation(value = "添加体重记录", notes = "添加新的体重记录")
    public ResponseDTO<Weight> addWeight(@RequestBody Weight weight) {
        try {
            Weight result = weightService.addWeight(weight);
            return ResponseDTO.success("体重记录添加成功", result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取体重记录列表（分页）
     */
    @GetMapping
    @ApiOperation(value = "查询体重记录列表", notes = "分页查询用户的体重记录")
    public ResponseDTO<Page<Weight>> getWeightList(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            Page<Weight> result = weightService.getWeightList(userId, pageNum, pageSize, startDate, endDate);
            return ResponseDTO.success(result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取单条体重记录
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "获取体重记录", notes = "根据ID获取体重记录详情")
    public ResponseDTO<Weight> getWeightById(@PathVariable Long id) {
        try {
            Weight result = weightService.getWeightById(id);
            return ResponseDTO.success(result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取最新体重记录
     */
    @GetMapping("/latest/{userId}")
    @ApiOperation(value = "获取最新体重记录", notes = "获取用户最新的体重记录")
    public ResponseDTO<Weight> getLatestWeight(@PathVariable Long userId) {
        try {
            Weight result = weightService.getLatestWeight(userId);
            if (result == null) {
                return ResponseDTO.failure(404, "暂无体重记录");
            }
            return ResponseDTO.success(result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 更新体重记录
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "更新体重记录", notes = "更新指定的体重记录")
    public ResponseDTO<Weight> updateWeight(
            @PathVariable Long id,
            @RequestBody Weight weight) {
        try {
            Weight result = weightService.updateWeight(id, weight);
            return ResponseDTO.success("体重记录更新成功", result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 删除体重记录
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除体重记录", notes = "删除指定的体重记录")
    public ResponseDTO<String> deleteWeight(@PathVariable Long id) {
        try {
            weightService.deleteWeight(id);
            return ResponseDTO.success("体重记录删除成功");
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }
}
