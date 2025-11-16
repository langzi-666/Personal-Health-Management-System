package com.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 健康管理系统启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.health"})
public class HealthManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthManagementApplication.class, args);
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   Health Management System Started     ║");
        System.out.println("║   API Doc: http://localhost:8080/doc.html ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

}
