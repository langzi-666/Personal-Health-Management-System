# 个人健康管理系统

> 大学生毕业设计项目 - Vue.js + Spring Boot + MySQL

## 📚 项目文档概览

本项目已为你生成了完整的技术文档，包括：

| 文件名 | 用途 | 优先级 |
|-------|------|--------|
| **项目说明.md** | 项目总体介绍和快速开始指南 | 🔴 第1阅读 |
| **需求文档.md** | 系统的功能需求和非功能需求 | 🟡 第2阅读 |
| **技术架构文档.md** | 系统设计和技术架构规范 | 🟡 第3阅读 |
| **MySQL数据库设计.md** | 数据库表结构和 SQL 脚本 | 🟢 开发时参考 |
| **周开发计划.md** | 8周详细的任务分解计划 | 🔴 每周必读 |
| **问题记录.md** | 问题追踪和解决记录 | 🟢 遇到问题时更新 |
| **开发总结模板.md** | 周期总结文档模板 | 🟡 每周完成后生成 |

---

## 🚀 快速开始（5分钟）

### Step 1️⃣：了解项目
```
阅读顺序：项目说明.md → 需求文档.md → 技术架构文档.md
预计时间：30分钟
```

### Step 2️⃣：准备开发环境
```bash
# 检查环境要求
✓ Java 11+
✓ Node.js 16+
✓ MySQL 8.0
✓ Git

# MySQL 初始化
主机：localhost
端口：3306
用户名：root
密码：123456
```

### Step 3️⃣：初始化数据库
```bash
# 1. 创建数据库
mysql> CREATE DATABASE health_management_system CHARACTER SET utf8mb4;

# 2. 执行 MySQL数据库设计.md 中的所有 SQL 语句
# 3. 验证 11 个表已创建成功
```

### Step 4️⃣：开始开发
```
参考：周开发计划.md
第1周：项目初始化与架构搭建
第2周：用户认证系统
...以此类推...
```

---

## 📋 项目架构简图

```
前端 (Vue.js)              后端 (Spring Boot)           数据库 (MySQL)
┌─────────────────┐       ┌──────────────────┐        ┌────────────┐
│  Dashboard      │ ←───→ │  Controller      │ ←────→ │   Users    │
│  Data Record    │  API  │  Service         │  SQL   │   Weight   │
│  Analysis       │       │  Mapper          │        │   BP       │
│  Settings       │       │  Entity          │        │   Others   │
└─────────────────┘       └──────────────────┘        └────────────┘
   http://localhost:5173    http://localhost:8080      localhost:3306
```

---

## 📂 项目结构（将要创建）

```
Personal Health Management System/
├── 📄 项目文档/ (已完成)
│   ├── 需求文档.md
│   ├── 技术架构文档.md
│   ├── MySQL数据库设计.md
│   ├── 周开发计划.md
│   ├── 问题记录.md
│   ├── 开发总结模板.md
│   ├── 项目说明.md
│   └── README.md (本文件)
│
├── 🔧 backend/ (待创建)
│   ├── src/main/java/com/health/
│   │   ├── HealthManagementApplication.java
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── config/
│   │   └── utils/
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── sql/init.sql
│   └── pom.xml
│
├── 🎨 frontend/ (待创建)
│   ├── src/
│   │   ├── components/
│   │   ├── views/
│   │   ├── router/
│   │   ├── store/
│   │   ├── api/
│   │   ├── utils/
│   │   ├── styles/
│   │   └── App.vue
│   ├── package.json
│   └── vite.config.js
│
└── 📊 数据库SQL脚本/ (待提取)
    └── init.sql
```

---

## 🎯 关键技术点

### 前端 (Vue.js 3.x)
- ✅ 组件化开发
- ✅ Axios API 请求
- ✅ Pinia 状态管理
- ✅ Vue Router 路由
- ✅ ECharts 数据可视化
- ✅ Element Plus UI 组件

### 后端 (Spring Boot)
- ✅ Spring Web REST API
- ✅ MyBatis Plus ORM
- ✅ Spring Security + JWT 认证
- ✅ Lombok 代码简化
- ✅ MySQL 数据库操作
- ✅ Swagger API 文档

### 数据库 (MySQL 8.0)
- ✅ 11 个核心数据表
- ✅ 外键关系维护
- ✅ 索引性能优化
- ✅ 数据完整性约束

---

## 📅 8周开发计划概览

| 周次 | 主要任务 | 前端 | 后端 | 数据库 |
|------|---------|------|------|--------|
| **第1周** | 项目初始化 | Vite + Router | SpringBoot + JWT | init.sql |
| **第2周** | 用户认证 | Login/Register | Auth API | users表 |
| **第3周** | 体重/血压 | Record Pages | Health API | weight/bp表 |
| **第4周** | 其他数据 | Record Pages | Health API | glucose/exercise等 |
| **第5周** | 仪表板 | Dashboard | Analysis API | 无新表 |
| **第6周** | 数据分析 | Charts/Reports | Analysis API | 无新表 |
| **第7周** | 提醒/通知 | Notification | Reminder API | notification表 |
| **第8周** | 优化/测试 | 性能优化 | 性能优化 | 无新表 |

---

## 🎓 毕业设计建议

### 论文相关
1. 📝 参考 `开发总结_第X周.md` 编写论文技术部分
2. 📊 收集代码统计数据和截图
3. 📈 记录项目的技术创新点和解决的问题

### 答辩演示
1. 🎬 准备演示脚本，展示核心功能
2. 🔧 搭建演示环境，提前测试
3. 💾 准备 MySQL 备份和示例数据

### 代码质量
1. 📋 添加代码注释，特别是复杂逻辑
2. 🏗️ 遵循项目的目录结构和规范
3. 🔄 使用 Git 进行版本控制

---

## ❓ 常见问题

**Q: 我应该从哪里开始？**  
A: 1️⃣ 读项目说明.md → 2️⃣ 读需求文档.md → 3️⃣ 读技术架构文档.md → 4️⃣ 初始化数据库 → 5️⃣ 按周开发计划.md 开发

**Q: 如何使用 MySQL数据库设计.md？**  
A: 打开 MySQL 客户端，复制其中的 SQL 语句逐一执行，创建数据库和所有表。

**Q: 遇到问题怎么办？**  
A: 在 `问题记录.md` 中添加新的问题记录，包括问题描述、原因分析和解决方案。

**Q: 开发完成后如何总结？**  
A: 每周完成后，复制 `开发总结模板.md`，生成 `开发总结_第X周.md`，填写实际完成情况。

**Q: 数据库连接信息是什么？**  
A: 
```
主机：localhost
端口：3306
用户名：root
密码：123456
数据库名：health_management_system
```

---

## 📞 项目配置速查表

### 前端配置
```
框架版本：Vue.js 3.x
构建工具：Vite
UI库：Element Plus
图表库：ECharts 5.x
开发端口：http://localhost:5173
API代理：http://localhost:8080
```

### 后端配置
```
框架：Spring Boot 2.7.x / 3.x
ORM：MyBatis Plus
认证：JWT
日志：Logback
API文档：Swagger/Knife4j
运行端口：http://localhost:8080
```

### 数据库配置
```
主机：localhost
端口：3306
用户名：root
密码：123456
数据库：health_management_system
字符集：utf8mb4
```

---

## 🔗 文档导航

- **第一次看项目？** → 读 `项目说明.md`
- **需要了解功能需求？** → 读 `需求文档.md`
- **需要理解技术架构？** → 读 `技术架构文档.md`
- **需要初始化数据库？** → 读 `MySQL数据库设计.md`
- **需要制定开发计划？** → 读 `周开发计划.md`
- **遇到开发问题？** → 更新 `问题记录.md`
- **完成一周任务？** → 生成 `开发总结_第X周.md`

---

## ✅ 检查清单

在开始开发前，请完成以下检查：

- [ ] 已阅读 `项目说明.md` 了解整体情况
- [ ] 已阅读 `需求文档.md` 理解功能需求
- [ ] 已阅读 `技术架构文档.md` 理解技术方案
- [ ] 已安装 Java 11+
- [ ] 已安装 Node.js 16+
- [ ] 已安装 MySQL 8.0
- [ ] 已创建数据库 `health_management_system`
- [ ] 已在数据库中创建所有 11 个表
- [ ] 已理解数据库连接配置（root/123456）
- [ ] 已准备好编辑器（IntelliJ IDEA/VS Code 等）

---

## 📞 获取帮助

遇到问题时：
1. 检查对应的技术文档
2. 在 `问题记录.md` 中查找是否有类似问题
3. 如果是新问题，添加到 `问题记录.md`

---

## 📄 文件更新历史

| 版本 | 日期 | 更新内容 |
|------|------|--------|
| 1.0 | 2025-11-15 | 初始版本 - 创建所有核心文档 |

---

**祝你开发顺利！🚀**

如有任何问题，请参考各个技术文档或在 `问题记录.md` 中记录。

---

**最后更新**：2025年11月15日  
**项目类型**：大学生毕业设计  
**技术栈**：Vue.js 3.x + Spring Boot + MySQL 8.0
