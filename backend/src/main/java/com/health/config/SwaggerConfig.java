package com.health.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger/Knife4j API文档配置
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 指定controller所在的包路径
                .apis(RequestHandlerSelectors.basePackage("com.health.controller"))
                // 指定生成API文档的路径
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("个人健康管理系统 API 文档")
                .description("Personal Health Management System API Documentation")
                .version("1.0.0")
                .contact(new springfox.documentation.service.Contact(
                        "Developer",
                        "http://localhost:8080",
                        "dev@example.com"
                ))
                .build();
    }
}
