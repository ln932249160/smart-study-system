package com.kg.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 系统用户持久化实体，严格映射 sys_user 表（16 列全部覆盖）。
 */
@TableName("sys_user")
public class SysUserEntity {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 账号（登录用，唯一） */
    private String account;

    /** 姓名（显示名，非唯一） */
    private String name;

    /** 密码（BCrypt 加密） */
    private String password;

    /** 角色：teacher / headmaster / student */
    private String role;

    /** 性别：0未知 1男 2女 */
    private Integer gender;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 微信openid（小程序唯一标识） */
    private String openid;

    /** 描述 */
    private String description;

    /** 班级ID */
    private Long classId;

    /** 状态：1正常 0禁用 */
    private Integer status;

    /** 创建人 */
    private Long createBy;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 修改人 */
    private Long updateBy;

    /** 修改时间 */
    private LocalDateTime updateTime;

    // ======================== getters / setters ========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getCreateBy() { return createBy; }
    public void setCreateBy(Long createBy) { this.createBy = createBy; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public Long getUpdateBy() { return updateBy; }
    public void setUpdateBy(Long updateBy) { this.updateBy = updateBy; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
