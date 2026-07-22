package com.kg.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kg.config.WechatMiniappProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.locks.ReentrantLock;

/** 微信小程序服务 —— code2Session + 手机号 + access_token 缓存 */
@Service
public class WechatMiniappService {
    private static final Logger log = LoggerFactory.getLogger(WechatMiniappService.class);
    private static final String CODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    private static final String TOKEN_URL =
            "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    private static final String PHONE_URL =
            "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s";

    private final WechatMiniappProperties props;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();
    private final ReentrantLock tokenLock = new ReentrantLock();

    private volatile String accessToken;
    private volatile long tokenExpireTime;

    public WechatMiniappService(WechatMiniappProperties props) {
        this.props = props;
    }

    /** code2Session：loginCode 换 openid */
    public String code2Session(String jsCode) {
        String url = String.format(CODE2SESSION_URL, props.getAppid(), props.getSecret(), jsCode);
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            JsonNode node = om.readTree(resp.getBody());
            if (node.has("errcode") && node.get("errcode").asInt() != 0) {
                log.error("code2Session失败: {}", node);
                throw new RuntimeException("微信登录失败: " + node.path("errmsg").asText("请稍后重试"));
            }
            String openid = node.get("openid").asText();
            log.info("code2Session成功: openid={}", mask(openid));
            return openid;
        } catch (RuntimeException e) { throw e;
        } catch (Exception e) { throw new RuntimeException("微信登录失败，请稍后重试"); }
    }

    /** phoneCode 换手机号 */
    public String getPhoneNumber(String phoneCode) {
        String token = getAccessToken();
        String url = String.format(PHONE_URL, token);
        try {
            String body = "{\"code\":\"" + phoneCode + "\"}";
            ResponseEntity<String> resp = restTemplate.postForEntity(url, body, String.class);
            JsonNode node = om.readTree(resp.getBody());
            if (node.has("errcode") && node.get("errcode").asInt() != 0) {
                log.error("获取手机号失败: {}", node);
                throw new RuntimeException("获取手机号失败，请稍后重试");
            }
            String phone = node.path("phone_info").path("purePhoneNumber").asText();
            log.info("获取手机号成功: phone={}", maskPhone(phone));
            return phone;
        } catch (RuntimeException e) { throw e;
        } catch (Exception e) { throw new RuntimeException("获取手机号失败，请稍后重试"); }
    }

    /** 带缓存的 access_token */
    private String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime - 60000) {
            return accessToken;
        }
        tokenLock.lock();
        try {
            if (accessToken != null && System.currentTimeMillis() < tokenExpireTime - 60000) return accessToken;
            String url = String.format(TOKEN_URL, props.getAppid(), props.getSecret());
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            JsonNode node = om.readTree(resp.getBody());
            if (node.has("errcode") && node.get("errcode").asInt() != 0) {
                throw new RuntimeException("获取access_token失败: " + node.path("errmsg").asText());
            }
            accessToken = node.get("access_token").asText();
            tokenExpireTime = System.currentTimeMillis() + node.get("expires_in").asLong() * 1000L;
            log.info("access_token刷新成功");
            return accessToken;
        } catch (RuntimeException e) { throw e;
        } catch (Exception e) { throw new RuntimeException("获取access_token失败"); }
        finally { tokenLock.unlock(); }
    }

    private String mask(String s) { return s == null ? null : s.substring(0, Math.min(6, s.length())) + "***"; }
    private String maskPhone(String s) { return s == null ? null : s.substring(0, 3) + "****" + s.substring(7); }
}
