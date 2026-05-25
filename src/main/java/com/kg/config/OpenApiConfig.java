package com.kg.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * OpenAPI 配置 —— 声明 JWT Bearer Token 认证方式与标签展示顺序。
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智能学习系统 API")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .tags(orderedTags());
    }

    /**
     * 标签顺序即 Swagger UI 中的展示顺序。
     */
    private List<Tag> orderedTags() {
        return Arrays.asList(
                new Tag().name("认证").description("登录相关接口"),
                new Tag().name("学生管理").description("学生与班主任的增删改查"),
                new Tag().name("班级管理").description("班级增删改查与学生分配"),
                new Tag().name("任务管理").description("任务的增删改查与完成")
        );
    }
}
