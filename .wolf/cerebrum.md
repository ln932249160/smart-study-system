# Cerebrum

> OpenWolf's learning memory. Updated automatically as the AI learns from interactions.
> Do not edit manually unless correcting an error.
> Last updated: 2026-05-30

## User Preferences

- 查询接口（分页列表）必须要有复用性，查询条件要覆盖数据库表中有实际含义的字段。不能只给 pageNum + pageSize 两个参数，type、status、title 模糊、时间范围等字段都要暴露出来。

## Key Learnings

- **Project:** backend
- **Description:** 考公培训机构教学管理系统后端，Spring Boot 2.7 + MyBatis-Plus + JWT。

## Do-Not-Repeat

- [2026-06-02] DictConverter.toEntity 漏掉了 dictLabel 字段映射。**规则：新增字段时必须同步更新 Converter 双向映射（toDomain + toEntity），以及所有手动构造对象的代码（Service 层 new DomainModel()、Repository 层 update 方法）。少一个方向就会丢数据。**

## Decision Log

<!-- Significant technical decisions with rationale. Why X was chosen over Y. -->
