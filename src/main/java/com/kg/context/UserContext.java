package com.kg.context;

import com.kg.domain.model.SysUser;

/**
 * 用户上下文 —— 基于 ThreadLocal 存储当前请求的用户信息。
 * <p>
 * 由 {@link com.kg.interceptor.JwtInterceptor} 在请求进入时从 JWT 解析并设置，
 * 请求结束时自动清理。Controller / Service 层通过 {@link #getUser()} 获取当前用户。
 * </p>
 */
public class UserContext {

    /** 线程隔离的用户持有者 */
    private static final ThreadLocal<SysUser> USER_HOLDER = new ThreadLocal<>();

    private UserContext() {
        // 工具类不可实例化
    }

    /**
     * 获取当前请求的用户信息。
     *
     * @return 当前用户，未经拦截器时为 null
     */
    public static SysUser getUser() {
        return USER_HOLDER.get();
    }

    /**
     * 设置当前请求的用户信息。
     *
     * @param user 用户领域模型
     */
    public static void setUser(SysUser user) {
        USER_HOLDER.set(user);
    }

    /**
     * 清理 ThreadLocal，防止内存泄漏。
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
}
