package com.health.controller;

import com.health.dto.ResponseDTO;
import com.health.service.AnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Map;

/**
 * 数据分析控制器
 */
@Slf4j
@Api(tags = "数据分析管理", description = "用户数据分析相关接口")
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    /**
     * 获取日期范围内的数据
     */
    @GetMapping("/range")
    @ApiOperation(value = "获取日期范围内的数据", notes = "支持多种数据类型: weight, blood_pressure, blood_glucose, exercise, sleep, diet")
    public ResponseDTO<Map<String, Object>> getDataByDateRange(
            @RequestParam Long userId,
            @RequestParam String dataType,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            Map<String, Object> data = analysisService.getDataByDateRange(userId, dataType, startDate, endDate);
            return ResponseDTO.success("获取数据成功", data);
        } catch (Exception e) {
            log.error("获取日期范围数据失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取对比分析数据
     */
    @GetMapping("/comparison")
    @ApiOperation(value = "获取对比分析数据", notes = "时间段格式: yyyy-MM-dd_yyyy-MM-dd，如: 2025-11-01_2025-11-07")
    public ResponseDTO<Map<String, Object>> getComparisonData(
            @RequestParam Long userId,
            @RequestParam String dataType,
            @RequestParam String currentPeriod,
            @RequestParam String previousPeriod) {
        try {
            Map<String, Object> data = analysisService.getComparisonData(userId, dataType, currentPeriod, previousPeriod);
            return ResponseDTO.success("获取对比分析数据成功", data);
        } catch (Exception e) {
            log.error("获取对比分析数据失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 导出数据（CSV格式）
     */
    @PostMapping("/export")
    @ApiOperation(value = "导出数据为CSV", notes = "导出指定时间段和数据类型的数据")
    public void exportData(
            @RequestParam Long userId,
            @RequestParam String dataType,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "csv") String format,
            HttpServletResponse response) {
        try {
            String csvContent = analysisService.exportDataToCsv(userId, dataType, startDate, endDate);
            
            // 设置响应头
            String fileName = String.format("%s_%s_%s.csv", dataType, startDate, endDate);
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + 
                new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            
            // 写入CSV内容
            response.getWriter().write("\uFEFF"); // BOM for Excel UTF-8 support
            response.getWriter().write(csvContent);
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("导出数据失败: {}", e.getMessage());
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("导出失败: " + e.getMessage());
            } catch (Exception ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }
}
