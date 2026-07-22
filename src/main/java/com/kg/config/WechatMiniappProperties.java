package com.kg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 微信小程序配置 */
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniappProperties {
    private String appid;
    private String secret;

    public String getAppid() { return appid; }
    public void setAppid(String v) { this.appid = v; }
    public String getSecret() { return secret; }
    public void setSecret(String v) { this.secret = v; }
}
