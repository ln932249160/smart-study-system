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
- [2026-06-03] 用户给出了参考 SQL（LEFT JOIN），我自作主张改用别的方案（改 SysUserRepository 批量查询）而非直接按 SQL 写联表查询。被纠正了两次才做对。**规则：用户给了参考 SQL/伪代码/模板代码时，直接按其意图实现，不要自作聪明换方案。**
- [2026-07-15] 批量改 14 个 Service 的 Map 返回值为 PageVO，工作流只改了 2 个就说完成了。没有跑完整 `mvn compile` 验证。被用户质疑 git diff 后才补完。**规则：任何代码改动后必须跑 `mvn compile` 确认通过才能汇报完成，特别是批量改动。**
- [2026-07-15] 为了把 Controller 的 Map 响应改成 AjaxResult，sed/perl 脚本损坏了多个文件，不得已从 git 恢复重来。**规则：批量文本替换不要用 sed/perl 直接改 Java 源码，正则处理不了多行模式。优先用 Edit 工具逐个文件改，或用 Write 工具整体重写文件。**
- [2026-07-15] 项目最初所有 Controller 手工 `new LinkedHashMap()` + `.put("code")` + `.put("message")` + `.put("data")` 拼响应，写了 18 个 Controller、70+ 个方法后才倒回来抽象成 AjaxResult。**规则：遇到重复样板代码，先建统一抽象（AjaxResult/PageVO 等），再写业务逻辑。不要照搬现有代码的坏模式。**

## Decision Log

<!-- Significant technical decisions with rationale. Why X was chosen over Y. -->
