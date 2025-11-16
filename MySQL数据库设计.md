# 个人健康管理系统 - MySQL 数据库设计文档

## 1. 数据库配置信息

```
数据库名：health_management_system
主机：localhost
端口：3306
用户名：root
密码：123456
字符集：utf8mb4
排序规则：utf8mb4_general_ci
```

---

## 2. 表设计详解

### 2.1 用户表 (users)

**描述**：存储用户基本信息

```sql
CREATE TABLE `users` (
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
```

**字段说明**：
| 字段名 | 类型 | 是否必填 | 说明 |
|-------|------|--------|------|
| id | BIGINT | 是 | 主键，自动递增 |
| username | VARCHAR(50) | 是 | 用户名，唯一 |
| email | VARCHAR(100) | 是 | 邮箱，唯一 |
| phone | VARCHAR(20) | 否 | 手机号 |
| password | VARCHAR(255) | 是 | 加密密码 |
| real_name | VARCHAR(50) | 否 | 真实姓名 |
| gender | TINYINT | 否 | 性别 |
| birth_date | DATE | 否 | 出生日期 |
| height | DECIMAL(5,2) | 否 | 身高(cm) |
| weight_goal | DECIMAL(5,2) | 否 | 目标体重(kg) |
| avatar_url | VARCHAR(255) | 否 | 头像链接 |
| bio | TEXT | 否 | 个人介绍 |
| is_active | BOOLEAN | 否 | 账户是否活跃 |
| last_login | DATETIME | 否 | 最后登录时间 |
| create_time | DATETIME | 否 | 创建时间 |
| update_time | DATETIME | 否 | 修改时间 |

---

### 2.2 体重记录表 (weight_records)

**描述**：存储用户的体重记录数据

```sql
CREATE TABLE `weight_records` (
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
```

---

### 2.3 血压记录表 (blood_pressure_records)

**描述**：存储用户的血压监测数据

```sql
CREATE TABLE `blood_pressure_records` (
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
```

---

### 2.4 血糖记录表 (blood_glucose_records)

**描述**：存储用户的血糖监测数据

```sql
CREATE TABLE `blood_glucose_records` (
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
```

---

### 2.5 运动记录表 (exercise_records)

**描述**：存储用户的运动数据

```sql
CREATE TABLE `exercise_records` (
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
```

---

### 2.6 睡眠记录表 (sleep_records)

**描述**：存储用户的睡眠数据

```sql
CREATE TABLE `sleep_records` (
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
```

---

### 2.7 饮食记录表 (diet_records)

**描述**：存储用户的饮食数据

```sql
CREATE TABLE `diet_records` (
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
```

---

### 2.8 心理健康记录表 (mental_health_records)

**描述**：存储用户的心理健康数据

```sql
CREATE TABLE `mental_health_records` (
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
```

---

### 2.9 通知表 (notifications)

**描述**：存储系统通知和提醒信息

```sql
CREATE TABLE `notifications` (
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
```

---

### 2.10 提醒规则表 (reminder_rules)

**描述**：存储用户自定义的提醒规则

```sql
CREATE TABLE `reminder_rules` (
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
```

---

### 2.11 告警规则表 (alert_rules)

**描述**：存储用户的数据异常告警规则

```sql
CREATE TABLE `alert_rules` (
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
```

---

## 3. 初始化脚本

将以上所有 SQL 语句保存到 `init.sql` 文件，在项目启动时执行。

---

## 4. 数据关系图

```
users (用户表) - 主表
  │
  ├─── weight_records (体重记录)
  ├─── blood_pressure_records (血压记录)
  ├─── blood_glucose_records (血糖记录)
  ├─── exercise_records (运动记录)
  ├─── sleep_records (睡眠记录)
  ├─── diet_records (饮食记录)
  ├─── mental_health_records (心理健康记录)
  ├─── notifications (通知)
  ├─── reminder_rules (提醒规则)
  └─── alert_rules (告警规则)
```

---

## 5. 索引策略

### 5.1 主键索引
- 所有表的 `id` 字段都是主键

### 5.2 外键索引
- 所有 `user_id` 字段都建立外键关系和索引

### 5.3 查询优化索引
- 在 `record_date` 字段建立索引（时间查询）
- 在 `user_id` 和 `record_date` 组合建立复合索引

---

## 6. 性能优化建议

1. **定期维护**：每月运行一次 ANALYZE 和 OPTIMIZE 命令
2. **备份策略**：每周进行一次全量备份，每天增量备份
3. **连接池**：使用 HikariCP 管理数据库连接
4. **查询优化**：使用 EXPLAIN 分析慢查询
5. **缓存策略**：热点数据使用 Redis 缓存

---

**文档版本**：1.0  
**最后更新**：2025年11月15日
