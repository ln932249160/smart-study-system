package com.kg.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类 —— 负责 Token 的生成、解析与校验。
 * <p>
 * Payload 仅包含 userId（sub）和 role（自定义 claim）。
 * MVP 阶段使用硬编码密钥，后续迁移至配置中心。
 * </p>
 */
public class JwtUtil {

    /** JWT 签名密钥（MVP 硬编码，生产须迁移至外部配置） */
    private static final String SECRET = "kg-mvp-jwt-secret-key-2026-min-256bits!!";

    /** Token 过期时间：24 小时（毫秒） */
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L;

    /** HmacSHA256 签名密钥 */
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private JwtUtil() {
        // 工具类不可实例化
    }

    /**
     * 生成 JWT Token。
     *
     * @param userId 用户 ID
     * @param role   用户角色
     * @return JWT Token 字符串
     */
    public static String generateToken(Long userId, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_MS);

        Map<String, Object> claims = new HashMap<>(2);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token，返回 Claims。
     *
     * @param token JWT Token
     * @return JWT Claims
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中提取用户 ID。
     *
     * @param token JWT Token
     * @return 用户 ID
     */
    public static Long getUserId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }

    /**
     * 从 Token 中提取角色。
     *
     * @param token JWT Token
     * @return 角色
     */
    public static String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    /**
     * 校验 Token 是否有效。
     *
     * @param token JWT Token
     * @return true 有效，false 无效（过期、签名错误等）
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
