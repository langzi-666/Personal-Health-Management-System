package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.*;
import com.health.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析服务
 */
@Slf4j
@Service
public class AnalysisService {

    @Autowired
    private WeightMapper weightMapper;

    @Autowired
    private BloodPressureMapper bloodPressureMapper;

    @Autowired
    private BloodGlucoseMapper bloodGlucoseMapper;

    @Autowired
    private ExerciseMapper exerciseMapper;

    @Autowired
    private SleepMapper sleepMapper;

    @Autowired
    private DietMapper dietMapper;

    @Autowired
    private MentalHealthMapper mentalHealthMapper;

    /**
     * 获取日期范围内的数据
     */
    public Map<String, Object> getDataByDateRange(Long userId, String dataType, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("dataType", dataType);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("dataList", new ArrayList<>());
        result.put("statistics", new HashMap<>());

        switch (dataType.toLowerCase()) {
            case "weight":
                result.put("dataList", getWeightDataByRange(userId, startDate, endDate));
                result.put("statistics", getWeightStatistics(userId, startDate, endDate));
                break;
            case "blood_pressure":
                result.put("dataList", getBloodPressureDataByRange(userId, startDate, endDate));
                result.put("statistics", getBloodPressureStatistics(userId, startDate, endDate));
                break;
            case "blood_glucose":
                result.put("dataList", getBloodGlucoseDataByRange(userId, startDate, endDate));
                result.put("statistics", getBloodGlucoseStatistics(userId, startDate, endDate));
                break;
            case "exercise":
                result.put("dataList", getExerciseDataByRange(userId, startDate, endDate));
                result.put("statistics", getExerciseStatistics(userId, startDate, endDate));
                break;
            case "sleep":
                result.put("dataList", getSleepDataByRange(userId, startDate, endDate));
                result.put("statistics", getSleepStatistics(userId, startDate, endDate));
                break;
            case "diet":
                result.put("dataList", getDietDataByRange(userId, startDate, endDate));
                result.put("statistics", getDietStatistics(userId, startDate, endDate));
                break;
            default:
                throw new RuntimeException("不支持的数据类型: " + dataType);
        }

        return result;
    }

    /**
     * 获取对比分析数据
     */
    public Map<String, Object> getComparisonData(Long userId, String dataType, String currentPeriod, String previousPeriod) {
        Map<String, Object> result = new HashMap<>();
        
        // 解析时间段 (format: "2025-11-01_2025-11-07")
        LocalDate[] currentRange = parsePeriod(currentPeriod);
        LocalDate[] previousRange = parsePeriod(previousPeriod);

        Map<String, Object> currentData = getDataByDateRange(userId, dataType, currentRange[0], currentRange[1]);
        Map<String, Object> previousData = getDataByDateRange(userId, dataType, previousRange[0], previousRange[1]);

        result.put("dataType", dataType);
        result.put("currentPeriod", currentPeriod);
        result.put("previousPeriod", previousPeriod);
        result.put("currentData", currentData);
        result.put("previousData", previousData);
        result.put("comparison", compareStatistics(currentData, previousData));

        return result;
    }

    /**
     * 解析时间段字符串
     */
    private LocalDate[] parsePeriod(String period) {
        String[] dates = period.split("_");
        if (dates.length != 2) {
            throw new RuntimeException("时间段格式错误");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new LocalDate[]{
            LocalDate.parse(dates[0], formatter),
            LocalDate.parse(dates[1], formatter)
        };
    }

    /**
     * 对比统计数据
     */
    private Map<String, Object> compareStatistics(Map<String, Object> currentData, Map<String, Object> previousData) {
        Map<String, Object> comparison = new HashMap<>();
        
        Map<String, Object> currentStats = (Map<String, Object>) currentData.get("statistics");
        Map<String, Object> previousStats = (Map<String, Object>) previousData.get("statistics");

        if (currentStats != null && previousStats != null) {
            // 计算同比增长率
            if (currentStats.containsKey("average") && previousStats.containsKey("average")) {
                BigDecimal current = new BigDecimal(currentStats.get("average").toString());
                BigDecimal previous = new BigDecimal(previousStats.get("average").toString());
                if (previous.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal rate = current.subtract(previous).divide(previous, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    comparison.put("averageGrowthRate", rate);
                }
            }
        }

        return comparison;
    }

    // ===== 数据查询方法 =====

    private List<Map<String, Object>> getWeightDataByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Weight> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .ge("record_date", startDate)
                .le("record_date", endDate)
                .orderByAsc("record_date");
        return weightMapper.selectList(query).stream()
                .map(w -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", w.getRecordDate());
                    map.put("weight", w.getWeight());
                    map.put("bodyFatRate", w.getBodyFatRate());
                    return map;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getBloodPressureDataByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<BloodPressure> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .ge("record_date", startDate)
                .le("record_date", endDate)
                .orderByAsc("record_date");
        return bloodPressureMapper.selectList(query).stream()
                .map(bp -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", bp.getRecordDate());
                    map.put("systolic", bp.getSystolic());
                    map.put("diastolic", bp.getDiastolic());
                    map.put("pulse", bp.getPulse());
                    return map;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getBloodGlucoseDataByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<BloodGlucose> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .ge("record_date", startDate)
                .le("record_date", endDate)
                .orderByAsc("record_date");
        return bloodGlucoseMapper.selectList(query).stream()
                .map(bg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", bg.getRecordDate());
                    map.put("glucoseValue", bg.getGlucoseValue());
                    map.put("measurementType", bg.getMeasurementType());
                    return map;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getExerciseDataByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Exercise> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .ge("record_date", startDate)
                .le("record_date", endDate)
                .orderByAsc("record_date");
        return exerciseMapper.selectList(query).stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", e.getRecordDate());
                    map.put("exerciseType", e.getExerciseType());
                    map.put("duration", e.getDuration());
                    map.put("caloriesBurned", e.getCaloriesBurned());
                    return map;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getSleepDataByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Sleep> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .ge("record_date", startDate)
                .le("record_date", endDate)
                .orderByAsc("record_date");
        return sleepMapper.selectList(query).stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", s.getRecordDate());
                    map.put("sleepDuration", s.getSleepDuration());
                    map.put("qualityScore", s.getSleepQuality());
                    return map;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getDietDataByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Diet> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .ge("record_date", startDate)
                .le("record_date", endDate)
                .orderByAsc("record_date");
        return dietMapper.selectList(query).stream()
                .map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", d.getRecordDate());
                    map.put("foodName", d.getFoodName());
                    map.put("calories", d.getCalories());
                    map.put("category", d.getCategory());
                    return map;
                }).collect(Collectors.toList());
    }

    // ===== 统计方法 =====

    private Map<String, Object> getWeightStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Weight> list = getWeightListByRange(userId, startDate, endDate);
        return calculateWeightStats(list);
    }

    private Map<String, Object> getBloodPressureStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<BloodPressure> list = getBloodPressureListByRange(userId, startDate, endDate);
        Map<String, Object> stats = new HashMap<>();
        if (!list.isEmpty()) {
            BigDecimal avgSystolic = list.stream().map(BloodPressure::getSystolic).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(list.size()), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal avgDiastolic = list.stream().map(BloodPressure::getDiastolic).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(list.size()), 2, BigDecimal.ROUND_HALF_UP);
            stats.put("count", list.size());
            stats.put("averageSystolic", avgSystolic);
            stats.put("averageDiastolic", avgDiastolic);
        }
        return stats;
    }

    private Map<String, Object> getBloodGlucoseStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<BloodGlucose> list = getBloodGlucoseListByRange(userId, startDate, endDate);
        Map<String, Object> stats = new HashMap<>();
        if (!list.isEmpty()) {
            BigDecimal avg = list.stream().map(BloodGlucose::getGlucoseValue).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(list.size()), 2, BigDecimal.ROUND_HALF_UP);
            stats.put("count", list.size());
            stats.put("average", avg);
        }
        return stats;
    }

    private Map<String, Object> getExerciseStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Exercise> list = getExerciseListByRange(userId, startDate, endDate);
        Map<String, Object> stats = new HashMap<>();
        if (!list.isEmpty()) {
            BigDecimal totalCalories = list.stream().map(Exercise::getCaloriesBurned).reduce(BigDecimal.ZERO, BigDecimal::add);
            Integer totalDuration = list.stream().map(Exercise::getDuration).reduce(0, Integer::sum);
            stats.put("count", list.size());
            stats.put("totalCalories", totalCalories);
            stats.put("totalDuration", totalDuration);
            stats.put("averageCalories", totalCalories.divide(new BigDecimal(list.size()), 2, BigDecimal.ROUND_HALF_UP));
        }
        return stats;
    }

    private Map<String, Object> getSleepStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Sleep> list = getSleepListByRange(userId, startDate, endDate);
        Map<String, Object> stats = new HashMap<>();
        if (!list.isEmpty()) {
            Double totalDuration = list.stream().map(Sleep::getSleepDuration).reduce(0.0, Double::sum);
            Double avgDuration = totalDuration / list.size();
            BigDecimal avgQuality = list.stream().map(Sleep::getSleepQuality).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(list.size()), 2, BigDecimal.ROUND_HALF_UP);
            stats.put("count", list.size());
            stats.put("totalDuration", totalDuration);
            stats.put("averageDuration", avgDuration);
            stats.put("averageQuality", avgQuality);
        }
        return stats;
    }

    private Map<String, Object> getDietStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Diet> list = getDietListByRange(userId, startDate, endDate);
        Map<String, Object> stats = new HashMap<>();
        if (!list.isEmpty()) {
            BigDecimal totalCalories = list.stream().map(Diet::getCalories).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avgCalories = totalCalories.divide(new BigDecimal(list.size()), 2, BigDecimal.ROUND_HALF_UP);
            stats.put("count", list.size());
            stats.put("totalCalories", totalCalories);
            stats.put("averageCalories", avgCalories);
        }
        return stats;
    }

    private Map<String, Object> calculateWeightStats(List<Weight> list) {
        Map<String, Object> stats = new HashMap<>();
        if (!list.isEmpty()) {
            BigDecimal totalWeight = list.stream().map(Weight::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avgWeight = totalWeight.divide(new BigDecimal(list.size()), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal maxWeight = list.stream().map(Weight::getWeight).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
            BigDecimal minWeight = list.stream().map(Weight::getWeight).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
            BigDecimal weightChange = maxWeight.subtract(minWeight);

            stats.put("count", list.size());
            stats.put("average", avgWeight);
            stats.put("max", maxWeight);
            stats.put("min", minWeight);
            stats.put("change", weightChange);
        }
        return stats;
    }

    // ===== 数据获取辅助方法 =====

    private List<Weight> getWeightListByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Weight> query = new QueryWrapper<>();
        query.eq("user_id", userId).ge("record_date", startDate).le("record_date", endDate).orderByAsc("record_date");
        return weightMapper.selectList(query);
    }

    private List<BloodPressure> getBloodPressureListByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<BloodPressure> query = new QueryWrapper<>();
        query.eq("user_id", userId).ge("record_date", startDate).le("record_date", endDate).orderByAsc("record_date");
        return bloodPressureMapper.selectList(query);
    }

    private List<BloodGlucose> getBloodGlucoseListByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<BloodGlucose> query = new QueryWrapper<>();
        query.eq("user_id", userId).ge("record_date", startDate).le("record_date", endDate).orderByAsc("record_date");
        return bloodGlucoseMapper.selectList(query);
    }

    private List<Exercise> getExerciseListByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Exercise> query = new QueryWrapper<>();
        query.eq("user_id", userId).ge("record_date", startDate).le("record_date", endDate).orderByAsc("record_date");
        return exerciseMapper.selectList(query);
    }

    private List<Sleep> getSleepListByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Sleep> query = new QueryWrapper<>();
        query.eq("user_id", userId).ge("record_date", startDate).le("record_date", endDate).orderByAsc("record_date");
        return sleepMapper.selectList(query);
    }

    private List<Diet> getDietListByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Diet> query = new QueryWrapper<>();
        query.eq("user_id", userId).ge("record_date", startDate).le("record_date", endDate).orderByAsc("record_date");
        return dietMapper.selectList(query);
    }

    /**
     * 导出数据为CSV格式
     */
    public String exportDataToCsv(Long userId, String dataType, LocalDate startDate, LocalDate endDate) {
        StringBuilder csv = new StringBuilder();
        
        switch (dataType.toLowerCase()) {
            case "weight":
                csv.append("日期,体重(kg),体脂率(%)\n");
                List<Weight> weights = getWeightListByRange(userId, startDate, endDate);
                for (Weight w : weights) {
                    csv.append(String.format("%s,%s,%s\n", 
                        w.getRecordDate(), 
                        w.getWeight() != null ? w.getWeight() : "",
                        w.getBodyFatRate() != null ? w.getBodyFatRate() : ""));
                }
                break;
            case "blood_pressure":
                csv.append("日期,收缩压(mmHg),舒张压(mmHg),脉搏(bpm),测量位置\n");
                List<BloodPressure> pressures = getBloodPressureListByRange(userId, startDate, endDate);
                for (BloodPressure bp : pressures) {
                    csv.append(String.format("%s,%s,%s,%s,%s\n",
                        bp.getRecordDate(),
                        bp.getSystolic(),
                        bp.getDiastolic(),
                        bp.getPulse() != null ? bp.getPulse() : "",
                        bp.getMeasurementLocation() != null ? bp.getMeasurementLocation() : ""));
                }
                break;
            case "blood_glucose":
                csv.append("日期,血糖值(mmol/L),测量类型,测量时间\n");
                List<BloodGlucose> glucoses = getBloodGlucoseListByRange(userId, startDate, endDate);
                for (BloodGlucose bg : glucoses) {
                    csv.append(String.format("%s,%s,%s,%s\n",
                        bg.getRecordDate(),
                        bg.getGlucoseValue(),
                        bg.getMeasurementType() != null ? bg.getMeasurementType() : "",
                        bg.getRecordTime() != null ? bg.getRecordTime() : ""));
                }
                break;
            case "exercise":
                csv.append("日期,运动类型,时长(分钟),强度,卡路里消耗(kcal),运动时间\n");
                List<Exercise> exercises = getExerciseListByRange(userId, startDate, endDate);
                for (Exercise e : exercises) {
                    csv.append(String.format("%s,%s,%s,%s,%s,%s\n",
                        e.getRecordDate(),
                        e.getExerciseType(),
                        e.getDuration() != null ? e.getDuration() : "",
                        e.getIntensity() != null ? e.getIntensity() : "",
                        e.getCaloriesBurned() != null ? e.getCaloriesBurned() : "",
                        e.getRecordTime() != null ? e.getRecordTime() : ""));
                }
                break;
            case "sleep":
                csv.append("日期,入睡时间,起床时间,睡眠时长(小时),睡眠质量(1-10)\n");
                List<Sleep> sleeps = getSleepListByRange(userId, startDate, endDate);
                for (Sleep s : sleeps) {
                    csv.append(String.format("%s,%s,%s,%s,%s\n",
                        s.getRecordDate(),
                        s.getBedTime() != null ? s.getBedTime() : "",
                        s.getWakeTime() != null ? s.getWakeTime() : "",
                        s.getSleepDuration() != null ? s.getSleepDuration() : "",
                        s.getSleepQuality() != null ? s.getSleepQuality() : ""));
                }
                break;
            case "diet":
                csv.append("日期,食物名称,分类,份量,卡路里(kcal),蛋白质(g),脂肪(g),碳水化合物(g),用餐时间\n");
                List<Diet> diets = getDietListByRange(userId, startDate, endDate);
                for (Diet d : diets) {
                    csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        d.getRecordDate(),
                        d.getFoodName(),
                        d.getCategory() != null ? d.getCategory() : "",
                        d.getQuantity() != null ? d.getQuantity() : "",
                        d.getCalories() != null ? d.getCalories() : "",
                        d.getProtein() != null ? d.getProtein() : "",
                        d.getFat() != null ? d.getFat() : "",
                        d.getCarbohydrates() != null ? d.getCarbohydrates() : "",
                        d.getRecordTime() != null ? d.getRecordTime() : ""));
                }
                break;
            default:
                throw new RuntimeException("不支持的数据类型: " + dataType);
        }
        
        return csv.toString();
    }
}
