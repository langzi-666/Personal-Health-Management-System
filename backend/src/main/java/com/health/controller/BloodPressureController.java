package com.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.dto.ResponseDTO;
import com.health.entity.BloodPressure;
import com.health.service.BloodPressureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 血压管理控制器
 */
@Slf4j
@Api(tags = "健康数据", description = "血压管理相关接口")
@RestController
@RequestMapping("/api/health/blood-pressure")
public class BloodPressureController {

    @Autowired
    private BloodPressureService bloodPressureService;

    /**
     * 添加血压记录
     */
    @PostMapping
    @ApiOperation(value = "添加血压记录", notes = "添加新的血压记录")
    public ResponseDTO<BloodPressure> addBloodPressure(@RequestBody BloodPressure bloodPressure) {
        try {
            BloodPressure result = bloodPressureService.addBloodPressure(bloodPressure);
            return ResponseDTO.success("血压记录添加成功", result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取血压记录列表（分页）
     */
    @GetMapping
    @ApiOperation(value = "查询血压记录列表", notes = "分页查询用户的血压记录")
    public ResponseDTO<Page<BloodPressure>> getBloodPressureList(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            Page<BloodPressure> result = bloodPressureService.getBloodPressureList(userId, pageNum, pageSize, startDate, endDate);
            return ResponseDTO.success(result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取单条血压记录
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "获取血压记录", notes = "根据ID获取血压记录详情")
    public ResponseDTO<BloodPressure> getBloodPressureById(@PathVariable Long id) {
        try {
            BloodPressure result = bloodPressureService.getBloodPressureById(id);
            return ResponseDTO.success(result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取最新血压记录
     */
    @GetMapping("/latest/{userId}")
    @ApiOperation(value = "获取最新血压记录", notes = "获取用户最新的血压记录")
    public ResponseDTO<BloodPressure> getLatestBloodPressure(@PathVariable Long userId) {
        try {
            BloodPressure result = bloodPressureService.getLatestBloodPressure(userId);
            if (result == null) {
                return ResponseDTO.failure(404, "暂无血压记录");
            }
            return ResponseDTO.success(result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 更新血压记录
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "更新血压记录", notes = "更新指定的血压记录")
    public ResponseDTO<BloodPressure> updateBloodPressure(
            @PathVariable Long id,
            @RequestBody BloodPressure bloodPressure) {
        try {
            BloodPressure result = bloodPressureService.updateBloodPressure(id, bloodPressure);
            return ResponseDTO.success("血压记录更新成功", result);
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 删除血压记录
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除血压记录", notes = "删除指定的血压记录")
    public ResponseDTO<String> deleteBloodPressure(@PathVariable Long id) {
        try {
            bloodPressureService.deleteBloodPressure(id);
            return ResponseDTO.success("血压记录删除成功");
        } catch (RuntimeException e) {
            return ResponseDTO.failure(400, e.getMessage());
        }
    }
}
