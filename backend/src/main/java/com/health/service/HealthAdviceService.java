package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.*;
import com.health.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 健康建议服务
 */
@Slf4j
@Service
public class HealthAdviceService {

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
     * 生成健康建议
     */
    public Map<String, Object> generateHealthAdvice(Long userId) {
        Map<String, Object> advice = new HashMap<>();
        List<Map<String, Object>> adviceList = new ArrayList<>();

        // 获取最近30天的数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 1. 体重建议
        adviceList.addAll(generateWeightAdvice(userId, startDateStr, endDateStr));

        // 2. 血压建议
        adviceList.addAll(generateBloodPressureAdvice(userId, startDateStr, endDateStr));

        // 3. 血糖建议
        adviceList.addAll(generateBloodGlucoseAdvice(userId, startDateStr, endDateStr));

        // 4. 运动建议
        adviceList.addAll(generateExerciseAdvice(userId, startDateStr, endDateStr));

        // 5. 睡眠建议
        adviceList.addAll(generateSleepAdvice(userId, startDateStr, endDateStr));

        // 6. 饮食建议
        adviceList.addAll(generateDietAdvice(userId, startDateStr, endDateStr));

        // 7. 心理健康建议
        adviceList.addAll(generateMentalHealthAdvice(userId, startDateStr, endDateStr));

        advice.put("adviceList", adviceList);
        advice.put("totalCount", adviceList.size());
        advice.put("generateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return advice;
    }

    /**
     * 生成体重建议
     */
    private List<Map<String, Object>> generateWeightAdvice(Long userId, String startDate, String endDate) {
        List<Map<String, Object>> adviceList = new ArrayList<>();
        QueryWrapper<Weight> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .between("record_date", startDate, endDate)
                .orderByDesc("record_date")
                .last("LIMIT 10");

        List<Weight> weights = weightMapper.selectList(query);
        if (weights.isEmpty()) {
            return adviceList;
        }

        // 计算体重变化趋势
        if (weights.size() >= 2) {
            BigDecimal firstWeight = weights.get(weights.size() - 1).getWeight();
            BigDecimal lastWeight = weights.get(0).getWeight();
            BigDecimal change = lastWeight.subtract(firstWeight);

            if (change.compareTo(BigDecimal.valueOf(2)) > 0) {
                Map<String, Object> advice = new HashMap<>();
                advice.put("category", "体重管理");
                advice.put("type", "warning");
                advice.put("title", "体重增长过快");
                advice.put("content", String.format("最近30天内您的体重增长了%.1f公斤，建议控制饮食并增加运动量。", change.doubleValue()));
                advice.put("priority", 2);
                adviceList.add(advice);
            } else if (change.compareTo(BigDecimal.valueOf(-2)) < 0) {
                Map<String, Object> advice = new HashMap<>();
                advice.put("category", "体重管理");
                advice.put("type", "warning");
                advice.put("title", "体重下降过快");
                advice.put("content", String.format("最近30天内您的体重下降了%.1f公斤，建议咨询医生并注意营养摄入。", Math.abs(change.doubleValue())));
                advice.put("priority", 2);
                adviceList.add(advice);
            }
        }

        // BMI建议
        if (weights.size() > 0) {
            BigDecimal currentWeight = weights.get(0).getWeight();
            // 假设身高1.7米（实际应该从用户信息获取）
            BigDecimal height = BigDecimal.valueOf(1.7);
            BigDecimal bmi = currentWeight.divide(height.multiply(height), 2, RoundingMode.HALF_UP);

            if (bmi.compareTo(BigDecimal.valueOf(25)) > 0) {
                Map<String, Object> advice = new HashMap<>();
                advice.put("category", "体重管理");
                advice.put("type", "info");
                advice.put("title", "BMI偏高");
                advice.put("content", String.format("您当前的BMI为%.1f，建议通过合理饮食和运动来控制体重。", bmi.doubleValue()));
                advice.put("priority", 1);
                adviceList.add(advice);
            } else if (bmi.compareTo(BigDecimal.valueOf(18.5)) < 0) {
                Map<String, Object> advice = new HashMap<>();
                advice.put("category", "体重管理");
                advice.put("type", "info");
                advice.put("title", "BMI偏低");
                advice.put("content", String.format("您当前的BMI为%.1f，建议增加营养摄入，保持健康体重。", bmi.doubleValue()));
                advice.put("priority", 1);
                adviceList.add(advice);
            }
        }

        return adviceList;
    }

    /**
     * 生成血压建议
     */
    private List<Map<String, Object>> generateBloodPressureAdvice(Long userId, String startDate, String endDate) {
        List<Map<String, Object>> adviceList = new ArrayList<>();
        QueryWrapper<BloodPressure> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .between("record_date", startDate, endDate)
                .orderByDesc("record_date")
                .last("LIMIT 10");

        List<BloodPressure> pressures = bloodPressureMapper.selectList(query);
        if (pressures.isEmpty()) {
            return adviceList;
        }

        // 计算平均血压
        BigDecimal avgSystolic = pressures.stream()
                .filter(p -> p.getSystolic() != null)
                .map(p -> BigDecimal.valueOf(p.getSystolic()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(pressures.size()), 2, RoundingMode.HALF_UP);

        BigDecimal avgDiastolic = pressures.stream()
                .filter(p -> p.getDiastolic() != null)
                .map(p -> BigDecimal.valueOf(p.getDiastolic()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(pressures.size()), 2, RoundingMode.HALF_UP);

        // 高血压建议
        if (avgSystolic.compareTo(BigDecimal.valueOf(140)) > 0 || avgDiastolic.compareTo(BigDecimal.valueOf(90)) > 0) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "血压管理");
            advice.put("type", "warning");
            advice.put("title", "血压偏高");
            advice.put("content", String.format("您的平均血压为%.0f/%.0f mmHg，建议减少盐分摄入、适量运动，必要时咨询医生。", 
                    avgSystolic.doubleValue(), avgDiastolic.doubleValue()));
            advice.put("priority", 3);
            adviceList.add(advice);
        }

        // 低血压建议
        if (avgSystolic.compareTo(BigDecimal.valueOf(90)) < 0 || avgDiastolic.compareTo(BigDecimal.valueOf(60)) < 0) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "血压管理");
            advice.put("type", "info");
            advice.put("title", "血压偏低");
            advice.put("content", String.format("您的平均血压为%.0f/%.0f mmHg，建议适当增加盐分摄入和运动量。", 
                    avgSystolic.doubleValue(), avgDiastolic.doubleValue()));
            advice.put("priority", 1);
            adviceList.add(advice);
        }

        return adviceList;
    }

    /**
     * 生成血糖建议
     */
    private List<Map<String, Object>> generateBloodGlucoseAdvice(Long userId, String startDate, String endDate) {
        List<Map<String, Object>> adviceList = new ArrayList<>();
        QueryWrapper<BloodGlucose> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .between("record_date", startDate, endDate)
                .orderByDesc("record_date")
                .last("LIMIT 10");

        List<BloodGlucose> glucoses = bloodGlucoseMapper.selectList(query);
        if (glucoses.isEmpty()) {
            return adviceList;
        }

        // 计算平均血糖
        BigDecimal avgGlucose = BigDecimal.valueOf(glucoses.stream()
                .filter(g -> g.getGlucoseValue() != null)
                .mapToDouble(BloodGlucose::getGlucoseValue)
                .average()
                .orElse(0.0))
                .setScale(2, RoundingMode.HALF_UP);

        // 高血糖建议
        if (avgGlucose.compareTo(BigDecimal.valueOf(7.0)) > 0) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "血糖管理");
            advice.put("type", "warning");
            advice.put("title", "血糖偏高");
            advice.put("content", String.format("您的平均血糖为%.1f mmol/L，建议控制糖分摄入、规律运动，必要时咨询医生。", avgGlucose.doubleValue()));
            advice.put("priority", 3);
            adviceList.add(advice);
        }

        // 低血糖建议
        if (avgGlucose.compareTo(BigDecimal.valueOf(3.9)) < 0) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "血糖管理");
            advice.put("type", "warning");
            advice.put("title", "血糖偏低");
            advice.put("content", String.format("您的平均血糖为%.1f mmol/L，建议适当增加糖分摄入，注意监测血糖变化。", avgGlucose.doubleValue()));
            advice.put("priority", 2);
            adviceList.add(advice);
        }

        return adviceList;
    }

    /**
     * 生成运动建议
     */
    private List<Map<String, Object>> generateExerciseAdvice(Long userId, String startDate, String endDate) {
        List<Map<String, Object>> adviceList = new ArrayList<>();
        QueryWrapper<Exercise> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .between("record_date", startDate, endDate);

        List<Exercise> exercises = exerciseMapper.selectList(query);
        if (exercises.isEmpty()) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "运动管理");
            advice.put("type", "info");
            advice.put("title", "缺乏运动");
            advice.put("content", "最近30天内没有运动记录，建议每周至少进行150分钟中等强度运动。");
            advice.put("priority", 2);
            adviceList.add(advice);
            return adviceList;
        }

        // 计算总运动时长（分钟）
        int totalMinutes = exercises.stream()
                .mapToInt(e -> e.getDuration() != null ? e.getDuration() : 0)
                .sum();

        // 计算平均每周运动时长
        int weeklyMinutes = totalMinutes / 4; // 假设30天约4周

        if (weeklyMinutes < 150) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "运动管理");
            advice.put("type", "info");
            advice.put("title", "运动量不足");
            advice.put("content", String.format("您最近30天的平均每周运动时长为%d分钟，建议增加到至少150分钟。", weeklyMinutes));
            advice.put("priority", 1);
            adviceList.add(advice);
        } else {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "运动管理");
            advice.put("type", "success");
            advice.put("title", "运动量充足");
            advice.put("content", String.format("您最近30天的平均每周运动时长为%d分钟，继续保持！", weeklyMinutes));
            advice.put("priority", 0);
            adviceList.add(advice);
        }

        return adviceList;
    }

    /**
     * 生成睡眠建议
     */
    private List<Map<String, Object>> generateSleepAdvice(Long userId, String startDate, String endDate) {
        List<Map<String, Object>> adviceList = new ArrayList<>();
        QueryWrapper<Sleep> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .between("record_date", startDate, endDate)
                .orderByDesc("record_date")
                .last("LIMIT 10");

        List<Sleep> sleeps = sleepMapper.selectList(query);
        if (sleeps.isEmpty()) {
            return adviceList;
        }

        // 计算平均睡眠时长（小时）
        double avgHours = sleeps.stream()
                .filter(s -> s.getSleepDuration() != null)
                .mapToDouble(s -> s.getSleepDuration().doubleValue())
                .average()
                .orElse(0);

        if (avgHours < 6) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "睡眠管理");
            advice.put("type", "warning");
            advice.put("title", "睡眠不足");
            advice.put("content", String.format("您的平均睡眠时长为%.1f小时，建议保证每天7-9小时睡眠。", avgHours));
            advice.put("priority", 2);
            adviceList.add(advice);
        } else if (avgHours > 9) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "睡眠管理");
            advice.put("type", "info");
            advice.put("title", "睡眠时间偏长");
            advice.put("content", String.format("您的平均睡眠时长为%.1f小时，建议保持7-9小时最佳睡眠时长。", avgHours));
            advice.put("priority", 1);
            adviceList.add(advice);
        } else {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "睡眠管理");
            advice.put("type", "success");
            advice.put("title", "睡眠充足");
            advice.put("content", String.format("您的平均睡眠时长为%.1f小时，睡眠质量良好，继续保持！", avgHours));
            advice.put("priority", 0);
            adviceList.add(advice);
        }

        return adviceList;
    }

    /**
     * 生成饮食建议
     */
    private List<Map<String, Object>> generateDietAdvice(Long userId, String startDate, String endDate) {
        List<Map<String, Object>> adviceList = new ArrayList<>();
        QueryWrapper<Diet> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .between("record_date", startDate, endDate);

        List<Diet> diets = dietMapper.selectList(query);
        if (diets.isEmpty()) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "饮食管理");
            advice.put("type", "info");
            advice.put("title", "记录饮食数据");
            advice.put("content", "建议记录每日饮食，以便更好地管理健康。");
            advice.put("priority", 1);
            adviceList.add(advice);
            return adviceList;
        }

        // 计算平均每日卡路里
        double avgCalories = diets.stream()
                .filter(d -> d.getCalories() != null)
                .mapToDouble(d -> d.getCalories().doubleValue())
                .average()
                .orElse(0);

        // 假设每日推荐卡路里为2000
        if (avgCalories > 2500) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "饮食管理");
            advice.put("type", "warning");
            advice.put("title", "卡路里摄入偏高");
            advice.put("content", String.format("您的平均每日卡路里摄入为%.0f，建议控制在2000-2500之间。", avgCalories));
            advice.put("priority", 2);
            adviceList.add(advice);
        } else if (avgCalories < 1500) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "饮食管理");
            advice.put("type", "info");
            advice.put("title", "卡路里摄入偏低");
            advice.put("content", String.format("您的平均每日卡路里摄入为%.0f，建议保证营养充足。", avgCalories));
            advice.put("priority", 1);
            adviceList.add(advice);
        }

        return adviceList;
    }

    /**
     * 生成心理健康建议
     */
    private List<Map<String, Object>> generateMentalHealthAdvice(Long userId, String startDate, String endDate) {
        List<Map<String, Object>> adviceList = new ArrayList<>();
        QueryWrapper<MentalHealth> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .between("record_date", startDate, endDate)
                .orderByDesc("record_date")
                .last("LIMIT 10");

        List<MentalHealth> mentalHealths = mentalHealthMapper.selectList(query);
        if (mentalHealths.isEmpty()) {
            return adviceList;
        }

        // 计算平均情绪评分（1-10分）
        double avgMood = mentalHealths.stream()
                .filter(m -> m.getMoodScore() != null)
                .mapToDouble(m -> m.getMoodScore().doubleValue())
                .average()
                .orElse(0);

        if (avgMood < 5) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "心理健康");
            advice.put("type", "warning");
            advice.put("title", "情绪评分偏低");
            advice.put("content", String.format("您的平均情绪评分为%.1f分，建议多进行放松活动，必要时寻求专业帮助。", avgMood));
            advice.put("priority", 2);
            adviceList.add(advice);
        } else if (avgMood >= 7) {
            Map<String, Object> advice = new HashMap<>();
            advice.put("category", "心理健康");
            advice.put("type", "success");
            advice.put("title", "情绪状态良好");
            advice.put("content", String.format("您的平均情绪评分为%.1f分，继续保持良好的心态！", avgMood));
            advice.put("priority", 0);
            adviceList.add(advice);
        }

        return adviceList;
    }
}

