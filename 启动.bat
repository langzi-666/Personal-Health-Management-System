@echo off
chcp 65001 >nul
title 个人健康管理系统

cd backend
start "后端服务" cmd /k "mvn spring-boot:run"
cd ..
cd frontend
start "前端服务" cmd /k "npm run dev"
cd ..
