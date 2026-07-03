package com.kg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** HTTP 日志配置 */
@Component
@ConfigurationProperties(prefix = "log.http")
public class LogHttpProperties {
    /** 是否打印请求日志 */
    private boolean enabled = true;
    /** 是否打印请求体/响应体 */
    private boolean bodyEnabled = true;
    /** 请求体/响应体最大打印长度 */
    private int maxBodyLength = 5000;

    public boolean isEnabled() { return enabled; } public void setEnabled(boolean v) { this.enabled = v; }
    public boolean isBodyEnabled() { return bodyEnabled; } public void setBodyEnabled(boolean v) { this.bodyEnabled = v; }
    public int getMaxBodyLength() { return maxBodyLength; } public void setMaxBodyLength(int v) { this.maxBodyLength = v; }
}
