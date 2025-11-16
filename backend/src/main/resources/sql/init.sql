-- =========================================
-- 个人健康管理系统 数据库初始化脚本
-- =========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS health_management_system CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE health_management_system;

-- =========================================
-- 1. 用户表 (users)
-- =========================================
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `gender` TINYINT COMMENT '性别 (0:未知, 1:男, 2:女)',
  `birth_date` DATE COMMENT '出生日期',
  `height` DECIMAL(5,2) COMMENT '身高 (厘米)',
  `weight_goal` DECIMAL(5,2) COMMENT '目标体重 (公斤)',
  `avatar_url` VARCHAR(255) COMMENT '头像URL',
  `bio` TEXT COMMENT '个人简介',
  `is_active` BOOLEAN DEFAULT TRUE COMMENT '账户是否活跃',
  `last_login` DATETIME COMMENT '最后登录时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  INDEX idx_username (username),
  INDEX idx_email (email),
  INDEX idx_phone (phone),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- =========================================
-- 2. 体重记录表 (weight_records)
-- =========================================
CREATE TABLE IF NOT EXISTS `weight_records` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `weight` DECIMAL(5,2) NOT NULL COMMENT '体重 (公斤)',
  `body_fat_rate` DECIMAL(5,2) COMMENT '体脂率 (%)',
  `muscle_rate` DECIMAL(5,2) COMMENT '肌肉率 (%)',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `record_time` TIME COMMENT '记录时间',
  `notes` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_record_date (record_date),
  INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='体重记录表';

-- =========================================
-- 3. 血压记录表 (blood_pressure_records)
-- =========================================
CREATE TABLE IF NOT EXISTS `blood_pressure_records` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `systolic` INT NOT NULL COMMENT '收缩压 (mmHg)',
  `diastolic` INT NOT NULL COMMENT '舒张压 (mmHg)',
  `pulse` INT COMMENT '脉搏 (次/分)',
  `measurement_location` VARCHAR(50) COMMENT '测量位置 (左臂/右臂等)',
  `bp_status` VARCHAR(20) COMMENT '血压状态 (正常/偏高/高血压)',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `record_time` TIME NOT NULL COMMENT '记录时间',
  `notes` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_record_date (record_date),
  INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='血压记录表';

-- =========================================
-- 4. 血糖记录表 (blood_glucose_records)
-- =========================================
CREATE TABLE IF NOT EXISTS `blood_glucose_records` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `glucose_value` DECIMAL(5,2) NOT NULL COMMENT '血糖值 (mmol/L)',
  `measurement_type` VARCHAR(20) COMMENT '测量类型 (空腹/餐后)',
  `meal_name` VARCHAR(100) COMMENT '关联的餐食名称',
  `glucose_status` VARCHAR(20) COMMENT '血糖状态 (正常/低血糖/高血糖)',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `record_time` TIME NOT NULL COMMENT '记录时间',
  `notes` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_record_date (record_date),
  INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='血糖记录表';

-- =========================================
-- 5. 运动记录表 (exercise_records)
-- =========================================
CREATE TABLE IF NOT EXISTS `exercise_records` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `exercise_type` VARCHAR(50) NOT NULL COMMENT '运动类型 (跑步/骑车/游泳等)',
  `duration` INT NOT NULL COMMENT '运动时长 (分钟)',
  `intensity` VARCHAR(20) COMMENT '运动强度 (低/中/高)',
  `calories_burned` DECIMAL(8,2) COMMENT '消耗卡路里 (kcal)',
  `distance` DECIMAL(8,2) COMMENT '运动距离 (公里)',
  `heart_rate_avg` INT COMMENT '平均心率 (次/分)',
  `heart_rate_max` INT COMMENT '最大心率 (次/分)',
  `location` VARCHAR(100) COMMENT '运动地点',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `start_time` TIME NOT NULL COMMENT '开始时间',
  `end_time` TIME COMMENT '结束时间',
  `notes` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_record_date (record_date),
  INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运动记录表';

-- =========================================
-- 6. 睡眠记录表 (sleep_records)
-- =========================================
CREATE TABLE IF NOT EXISTS `sleep_records` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `sleep_date` DATE NOT NULL COMMENT '睡眠日期',
  `bedtime` DATETIME NOT NULL COMMENT '入睡时间',
  `wake_time` DATETIME NOT NULL COMMENT '起床时间',
  `sleep_duration` INT NOT NULL COMMENT '睡眠时长 (分钟)',
  `quality_score` INT COMMENT '睡眠质量评分 (1-10)',
  `deep_sleep_duration` INT COMMENT '深睡眠时长 (分钟)',
  `light_sleep_duration` INT COMMENT '浅睡眠时长 (分钟)',
  `sleep_issues` VARCHAR(255) COMMENT '睡眠问题 (失眠/打鼾等)',
  `notes` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_sleep_date (sleep_date),
  INDEX idx_user_date (user_id, sleep_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='睡眠记录表';

-- =========================================
-- 7. 饮食记录表 (diet_records)
-- =========================================
CREATE TABLE IF NOT EXISTS `diet_records` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `food_name` VARCHAR(100) NOT NULL COMMENT '食物名称',
  `food_category` VARCHAR(50) COMMENT '食物分类 (谷物/蛋白质/蔬菜等)',
  `quantity` DECIMAL(8,2) NOT NULL COMMENT '分量',
  `unit` VARCHAR(20) COMMENT '单位 (克/毫升/份等)',
  `calories` DECIMAL(8,2) COMMENT '热量 (kcal)',
  `protein` DECIMAL(8,2) COMMENT '蛋白质 (克)',
  `fat` DECIMAL(8,2) COMMENT '脂肪 (克)',
  `carbohydrates` DECIMAL(8,2) COMMENT '碳水化合物 (克)',
  `fiber` DECIMAL(8,2) COMMENT '纤维 (克)',
  `meal_type` VARCHAR(20) COMMENT '餐食类型 (早餐/午餐/晚餐/零食)',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `record_time` TIME NOT NULL COMMENT '记录时间',
  `notes` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_record_date (record_date),
  INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='饮食记录表';

-- =========================================
-- 8. 心理健康记录表 (mental_health_records)
-- =========================================
CREATE TABLE IF NOT EXISTS `mental_health_records` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `mood_score` INT NOT NULL COMMENT '心情评分 (1-10)',
  `stress_level` INT COMMENT '压力等级 (1-10)',
  `anxiety_level` INT COMMENT '焦虑程度 (1-10)',
  `emotion` VARCHAR(50) COMMENT '情绪标签 (开心/悲伤/焦虑/平静等)',
  `mood_notes` TEXT COMMENT '心理健康笔记',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `record_time` TIME COMMENT '记录时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_record_date (record_date),
  INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='心理健康记录表';

-- =========================================
-- 9. 通知表 (notifications)
-- =========================================
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `notification_type` VARCHAR(50) NOT NULL COMMENT '通知类型 (提醒/告警/建议)',
  `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
  `content` TEXT NOT NULL COMMENT '通知内容',
  `related_data_type` VARCHAR(50) COMMENT '关联数据类型 (weight/blood_pressure等)',
  `related_data_id` BIGINT COMMENT '关联数据ID',
  `is_read` BOOLEAN DEFAULT FALSE COMMENT '是否已读',
  `read_time` DATETIME COMMENT '阅读时间',
  `action_url` VARCHAR(255) COMMENT '跳转链接',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_is_read (is_read),
  INDEX idx_create_time (create_time),
  INDEX idx_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知表';

-- =========================================
-- 10. 提醒规则表 (reminder_rules)
-- =========================================
CREATE TABLE IF NOT EXISTS `reminder_rules` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `reminder_type` VARCHAR(50) NOT NULL COMMENT '提醒类型 (数据记录/运动/饮水等)',
  `reminder_name` VARCHAR(100) NOT NULL COMMENT '提醒名称',
  `reminder_time` TIME NOT NULL COMMENT '提醒时间',
  `frequency` VARCHAR(50) COMMENT '频率 (每天/每周/自定义)',
  `is_enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
  `notification_method` VARCHAR(50) COMMENT '通知方式 (应用内/邮件/短信)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_is_enabled (is_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='提醒规则表';

-- =========================================
-- 11. 告警规则表 (alert_rules)
-- =========================================
CREATE TABLE IF NOT EXISTS `alert_rules` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '告警规则ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `alert_type` VARCHAR(50) NOT NULL COMMENT '告警类型 (blood_pressure/blood_glucose等)',
  `alert_name` VARCHAR(100) NOT NULL COMMENT '告警名称',
  `condition_type` VARCHAR(50) COMMENT '条件类型 (高于/低于/异常变化)',
  `threshold_value` DECIMAL(8,2) COMMENT '阈值',
  `is_enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
  `notification_method` VARCHAR(50) COMMENT '通知方式',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_is_enabled (is_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='告警规则表';

-- =========================================
-- 初始化完成
-- =========================================
SELECT '数据库初始化完成！所有表已创建。' AS message;

-- =========================================
-- 插入测试数据
-- =========================================

-- 插入测试用户 (密码: 123456)
INSERT INTO `users` (`username`, `email`, `phone`, `password`, `real_name`, `gender`, `birth_date`, `height`, `weight_goal`, `bio`, `is_active`, `create_time`, `update_time`) VALUES
('zhangsan', 'zhangsan@example.com', '13800138000', '123456', '张三', 1, '1990-05-15', 175.00, 75.00, '健身爱好者，坡持运动', TRUE, NOW(), NOW()),
('lisi', 'lisi@example.com', '13800138001', '123456', '李四', 2, '1995-08-20', 162.00, 60.00, '关注健康管理', TRUE, NOW(), NOW()),
('wangwu', 'wangwu@example.com', '13800138002', '123456', '王五', 1, '1992-03-10', 180.00, 80.00, '体育教练', TRUE, NOW(), NOW());

-- 插入体重记录
INSERT INTO `weight_records` (`user_id`, `weight`, `body_fat_rate`, `muscle_rate`, `record_date`, `record_time`, `notes`, `create_time`, `update_time`) VALUES
(1, 78.50, 22.5, 38.0, DATE_SUB(CURDATE(), INTERVAL 30 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 78.20, 22.3, 38.2, DATE_SUB(CURDATE(), INTERVAL 27 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 77.80, 22.0, 38.5, DATE_SUB(CURDATE(), INTERVAL 24 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 77.50, 21.8, 38.7, DATE_SUB(CURDATE(), INTERVAL 21 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 77.20, 21.5, 39.0, DATE_SUB(CURDATE(), INTERVAL 18 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 76.80, 21.2, 39.2, DATE_SUB(CURDATE(), INTERVAL 15 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 76.50, 21.0, 39.5, DATE_SUB(CURDATE(), INTERVAL 12 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 76.20, 20.8, 39.7, DATE_SUB(CURDATE(), INTERVAL 9 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 75.80, 20.5, 40.0, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 75.50, 20.3, 40.2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '08:00:00', '早晨测量', NOW(), NOW()),
(1, 75.20, 20.0, 40.5, CURDATE(), '08:00:00', '早晨测量', NOW(), NOW()),
(2, 62.00, 25.0, 35.0, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '07:30:00', '早晨', NOW(), NOW()),
(2, 61.80, 24.8, 35.2, CURDATE(), '07:30:00', '早晨', NOW(), NOW()),
(3, 82.00, 24.0, 40.0, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '06:30:00', '晨练后', NOW(), NOW()),
(3, 81.50, 23.8, 40.2, CURDATE(), '06:30:00', '晨练后', NOW(), NOW());

-- 插入血压记录
INSERT INTO `blood_pressure_records` (`user_id`, `systolic`, `diastolic`, `pulse`, `measurement_location`, `bp_status`, `record_date`, `record_time`, `notes`, `create_time`, `update_time`) VALUES
(1, 115, 75, 68, '左臂', '正常', DATE_SUB(CURDATE(), INTERVAL 10 DAY), '08:30:00', '平静状态', NOW(), NOW()),
(1, 118, 76, 70, '左臂', '正常', DATE_SUB(CURDATE(), INTERVAL 7 DAY), '08:30:00', '平静状态', NOW(), NOW()),
(1, 120, 78, 72, '左臂', '偏高', DATE_SUB(CURDATE(), INTERVAL 4 DAY), '08:30:00', '早上测量', NOW(), NOW()),
(1, 116, 75, 68, '左臂', '正常', CURDATE(), '08:30:00', '平静状态', NOW(), NOW()),
(2, 110, 70, 65, '右臂', '正常', DATE_SUB(CURDATE(), INTERVAL 3 DAY), '19:00:00', '晚间测量', NOW(), NOW()),
(2, 112, 72, 66, '右臂', '正常', CURDATE(), '19:00:00', '晚间测量', NOW(), NOW()),
(3, 135, 88, 80, '左臂', '高血压', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '07:00:00', '晨测', NOW(), NOW()),
(3, 132, 85, 78, '左臂', '高血压', CURDATE(), '07:00:00', '晨测', NOW(), NOW());

-- 插入血糖记录
INSERT INTO `blood_glucose_records` (`user_id`, `glucose_value`, `measurement_type`, `meal_name`, `glucose_status`, `record_date`, `record_time`, `notes`, `create_time`, `update_time`) VALUES
(1, 5.2, '空腹', NULL, '正常', DATE_SUB(CURDATE(), INTERVAL 8 DAY), '08:00:00', '空腹血糖', NOW(), NOW()),
(1, 7.8, '饭后', '早餐', '正常', DATE_SUB(CURDATE(), INTERVAL 8 DAY), '09:00:00', '餐后2小时', NOW(), NOW()),
(1, 5.1, '空腹', NULL, '正常', DATE_SUB(CURDATE(), INTERVAL 4 DAY), '08:00:00', '空腹血糖', NOW(), NOW()),
(1, 7.5, '饭后', '午餐', '正常', DATE_SUB(CURDATE(), INTERVAL 4 DAY), '13:00:00', '餐后2小时', NOW(), NOW()),
(1, 5.3, '空腹', NULL, '正常', CURDATE(), '08:00:00', '空腹血糖', NOW(), NOW()),
(1, 7.6, '饭后', '早餐', '正常', CURDATE(), '09:00:00', '餐后2小时', NOW(), NOW()),
(2, 4.8, '空腹', NULL, '正常', CURDATE(), '07:30:00', '空腹血糖', NOW(), NOW()),
(3, 6.2, '空腹', NULL, '正常', CURDATE(), '06:30:00', '空腹血糖', NOW(), NOW());

-- 插入运动记录
INSERT INTO `exercise_records` (`user_id`, `exercise_type`, `duration`, `intensity`, `calories_burned`, `distance`, `heart_rate_avg`, `heart_rate_max`, `location`, `record_date`, `start_time`, `end_time`, `notes`, `create_time`, `update_time`) VALUES
(1, '跑步', 45, '中', 450.00, 6.5, 145, 165, '公园', DATE_SUB(CURDATE(), INTERVAL 8 DAY), '06:00:00', '06:45:00', '晨跑', NOW(), NOW()),
(1, '健身房', 60, '高', 600.00, NULL, 135, 155, '健身房', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '19:00:00', '20:00:00', '力量训练', NOW(), NOW()),
(1, '跑步', 40, '中', 400.00, 6.0, 140, 160, '公园', CURDATE(), '06:00:00', '06:40:00', '晨跑', NOW(), NOW()),
(2, '瑜伽', 50, '低', 150.00, NULL, 100, 120, '家里', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '19:00:00', '19:50:00', '放松瑜伽', NOW(), NOW()),
(2, '骑行', 60, '中', 500.00, 15.0, 130, 150, '郊区', CURDATE(), '09:00:00', '10:00:00', '周末骑行', NOW(), NOW()),
(3, '篮球', 90, '高', 800.00, NULL, 155, 180, '篮球场', DATE_SUB(CURDATE(), INTERVAL 3 DAY), '19:30:00', '21:00:00', '对抗赛', NOW(), NOW()),
(3, '跑步', 50, '高', 550.00, 8.0, 150, 170, '公园', CURDATE(), '06:00:00', '06:50:00', '速度训练', NOW(), NOW());

-- 插入睡眠记录
INSERT INTO `sleep_records` (`user_id`, `sleep_date`, `bedtime`, `wake_time`, `sleep_duration`, `quality_score`, `deep_sleep_duration`, `light_sleep_duration`, `sleep_issues`, `notes`, `create_time`, `update_time`) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY) + INTERVAL 22 HOUR + INTERVAL 30 MINUTE, DATE_SUB(CURDATE(), INTERVAL 9 DAY) + INTERVAL 6 HOUR + INTERVAL 30 MINUTE, 480, 8, 200, 280, NULL, '睡眠质量好', NOW(), NOW()),
(1, DATE_SUB(CURDATE(), INTERVAL 7 DAY), DATE_SUB(CURDATE(), INTERVAL 7 DAY) + INTERVAL 23 HOUR, DATE_SUB(CURDATE(), INTERVAL 6 DAY) + INTERVAL 7 HOUR, 480, 8, 200, 280, NULL, '睡眠质量好', NOW(), NOW()),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_SUB(CURDATE(), INTERVAL 4 DAY) + INTERVAL 23 HOUR + INTERVAL 30 MINUTE, DATE_SUB(CURDATE(), INTERVAL 3 DAY) + INTERVAL 7 HOUR, 450, 7, 180, 270, NULL, '睡眠略短', NOW(), NOW()),
(1, CURDATE() - INTERVAL 1 DAY, CURDATE() - INTERVAL 1 DAY + INTERVAL 22 HOUR, CURDATE() + INTERVAL 6 HOUR + INTERVAL 30 MINUTE, 480, 8, 210, 270, NULL, '睡眠质量好', NOW(), NOW()),
(2, CURDATE() - INTERVAL 2 DAY, CURDATE() - INTERVAL 2 DAY + INTERVAL 23 HOUR, CURDATE() - INTERVAL 1 DAY + INTERVAL 8 HOUR, 510, 9, 220, 290, NULL, '睡眠充足', NOW(), NOW()),
(2, CURDATE() - INTERVAL 1 DAY, CURDATE() - INTERVAL 1 DAY + INTERVAL 23 HOUR + INTERVAL 30 MINUTE, CURDATE() + INTERVAL 7 HOUR + INTERVAL 30 MINUTE, 480, 8, 200, 280, NULL, '睡眠质量好', NOW(), NOW()),
(3, CURDATE() - INTERVAL 3 DAY, CURDATE() - INTERVAL 3 DAY + INTERVAL 21 HOUR + INTERVAL 30 MINUTE, CURDATE() - INTERVAL 2 DAY + INTERVAL 5 HOUR + INTERVAL 30 MINUTE, 420, 6, 150, 270, '运动后易入睡', '睡眠不足', NOW(), NOW()),
(3, CURDATE() - INTERVAL 1 DAY, CURDATE() - INTERVAL 1 DAY + INTERVAL 22 HOUR, CURDATE() + INTERVAL 6 HOUR, 480, 8, 200, 280, NULL, '睡眠质量好', NOW(), NOW());

-- 插入饮食记录
INSERT INTO `diet_records` (`user_id`, `food_name`, `food_category`, `quantity`, `unit`, `calories`, `protein`, `fat`, `carbohydrates`, `fiber`, `meal_type`, `record_date`, `record_time`, `notes`, `create_time`, `update_time`) VALUES
(1, '白粥', '谷物', 200, '克', 150.00, 3.00, 0.50, 35.00, 1.00, '早餐', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '07:00:00', '清粥配咸菜', NOW(), NOW()),
(1, '荷包蛋', '蛋白质', 50, '克', 150.00, 6.00, 11.00, 1.00, 0.00, '早餐', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '07:15:00', '一个蛋', NOW(), NOW()),
(1, '青菜', '蔬菜', 100, '克', 25.00, 2.00, 0.20, 5.00, 2.00, '早餐', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '07:20:00', '清炒', NOW(), NOW()),
(1, '米饭', '谷物', 150, '克', 200.00, 4.00, 0.50, 45.00, 1.00, '午餐', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '12:00:00', '一碗米饭', NOW(), NOW()),
(1, '鸡肉', '蛋白质', 100, '克', 165.00, 31.00, 3.60, 0.00, 0.00, '午餐', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '12:10:00', '炖鸡', NOW(), NOW()),
(1, '蔬菜汤', '蔬菜', 300, '毫升', 50.00, 3.00, 0.50, 10.00, 2.00, '午餐', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '12:20:00', '清汤', NOW(), NOW()),
(1, '米饭', '谷物', 150, '克', 200.00, 4.00, 0.50, 45.00, 1.00, '晚餐', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '18:00:00', '一碗米饭', NOW(), NOW()),
(1, '鱼', '蛋白质', 100, '克', 120.00, 20.00, 4.00, 0.00, 0.00, '晚餐', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '18:10:00', '清蒸鱼', NOW(), NOW()),
(1, '蒸菜', '蔬菜', 150, '克', 40.00, 2.00, 0.30, 8.00, 2.50, '晚餐', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '18:20:00', '混合蔬菜', NOW(), NOW()),
(1, '苹果', '水果', 150, '克', 80.00, 0.30, 0.20, 21.00, 3.00, '零食', CURDATE(), '10:00:00', '中等大小', NOW(), NOW()),
(1, '牛奶', '乳制品', 200, '毫升', 120.00, 6.50, 3.50, 10.00, 0.00, '零食', CURDATE(), '15:00:00', '低脂牛奶', NOW(), NOW()),
(2, '燕麦粥', '谷物', 50, '克', 200.00, 7.00, 5.00, 35.00, 8.00, '早餐', CURDATE(), '07:30:00', '配水果', NOW(), NOW()),
(2, '全麦面包', '谷物', 100, '克', 250.00, 8.00, 3.00, 45.00, 7.00, '早餐', CURDATE(), '07:45:00', '两片', NOW(), NOW()),
(3, '米饭', '谷物', 200, '克', 280.00, 5.00, 0.70, 60.00, 1.50, '早餐', CURDATE(), '06:30:00', '大碗', NOW(), NOW()),
(3, '肉类', '蛋白质', 150, '克', 300.00, 35.00, 15.00, 0.00, 0.00, '早餐', CURDATE(), '06:50:00', '猪肉', NOW(), NOW());

-- 插入心理健康记录
INSERT INTO `mental_health_records` (`user_id`, `mood_score`, `stress_level`, `anxiety_level`, `emotion`, `mood_notes`, `record_date`, `record_time`, `create_time`, `update_time`) VALUES
(1, 7, 4, 2, '开心', '今天工作进展顺利，心情不错', DATE_SUB(CURDATE(), INTERVAL 6 DAY), '20:00:00', NOW(), NOW()),
(1, 8, 3, 1, '平静', '运动后心态平和', DATE_SUB(CURDATE(), INTERVAL 3 DAY), '20:00:00', NOW(), NOW()),
(1, 6, 5, 3, '焦虑', '工作压力较大，有些焦虑', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '20:00:00', NOW(), NOW()),
(1, 7, 4, 2, '开心', '周末休息，心情舒畅', CURDATE(), '20:00:00', NOW(), NOW()),
(2, 8, 3, 1, '开心', '完成了重要任务，很开心', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '19:00:00', NOW(), NOW()),
(2, 7, 4, 2, '平静', '日常生活平稳', CURDATE(), '19:00:00', NOW(), NOW()),
(3, 8, 2, 1, '开心', '运动让我心情很好', CURDATE(), '20:00:00', NOW(), NOW());

-- 插入提醒规则
INSERT INTO `reminder_rules` (`user_id`, `reminder_type`, `reminder_name`, `reminder_time`, `frequency`, `is_enabled`, `notification_method`, `create_time`, `update_time`) VALUES
(1, '数据记录', '早晨体重测量', '08:00:00', '每天', TRUE, '应用内', NOW(), NOW()),
(1, '运动', '下午运动提醒', '18:00:00', '每天', TRUE, '应用内', NOW(), NOW()),
(1, '饮水', '定时饮水', '10:00:00', '每天', TRUE, '应用内', NOW(), NOW()),
(2, '数据记录', '血压测量', '19:00:00', '每天', TRUE, '应用内', NOW(), NOW()),
(2, '运动', '瑜伽练习', '19:30:00', '每周三', FALSE, '应用内', NOW(), NOW()),
(3, '运动', '晨跑', '06:00:00', '每天', TRUE, '应用内', NOW(), NOW()),
(3, '数据记录', '睡眠记录', '09:00:00', '每天', TRUE, '应用内', NOW(), NOW());

-- 插入告警规则
INSERT INTO `alert_rules` (`user_id`, `alert_type`, `alert_name`, `condition_type`, `threshold_value`, `is_enabled`, `notification_method`, `create_time`, `update_time`) VALUES
(1, 'blood_pressure', '收缩压超标', '高于', 130.00, TRUE, '应用内', NOW(), NOW()),
(1, 'blood_pressure', '舒张压超标', '高于', 85.00, TRUE, '应用内', NOW(), NOW()),
(2, 'blood_glucose', '血糖过高', '高于', 8.00, TRUE, '应用内', NOW(), NOW()),
(2, 'blood_glucose', '血糖过低', '低于', 4.00, TRUE, '应用内', NOW(), NOW()),
(3, 'weight', '体重增加过快', '异常变化', 2.50, TRUE, '应用内', NOW(), NOW());

SELECT '✓ 测试数据插入完成！' AS message;
