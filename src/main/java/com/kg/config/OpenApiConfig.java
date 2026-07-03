package com.kg.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * OpenAPI 配置 —— 声明 JWT Bearer Token 认证方式与标签展示顺序。
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    /** 标签展示顺序 */
    private static final List<String> TAG_ORDER = Arrays.asList(
            "认证", "首页统计", "打卡统计", "我的任务", "个人中心", "通知消息", "请假管理", "用户管理", "班级管理", "模板任务管理", "任务列表", "学习阶段", "系统管理"
    );

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
                                        .bearerFormat("JWT")));
    }

    /**
     * 在所有 @Tag 注解处理完后，按 TAG_ORDER 重排标签顺序。
     */
    @Bean
    public OpenApiCustomiser sortTagsCustomiser() {
        return openApi -> {
            List<Tag> tags = openApi.getTags();
            if (tags != null) {
                tags.sort(Comparator.comparingInt(
                        t -> {
                            int idx = TAG_ORDER.indexOf(t.getName());
                            return idx >= 0 ? idx : Integer.MAX_VALUE;
                        }));
            }
        };
    }
}
