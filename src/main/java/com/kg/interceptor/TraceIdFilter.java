package com.kg.interceptor;

import com.kg.config.LogHttpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * TraceId + 请求/响应日志。
 * CachedBodyRequestWrapper 在构造时缓存请求体 → START 日志即可打印 requestBody。
 * ContentCachingResponseWrapper 缓存响应体 → END 日志打印 responseBody。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TraceIdFilter.class);
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_KEY = "traceId";

    private static final String[] SKIP_LOG_PREFIXES = {
            "/swagger", "/v3/api-docs", "/doc.html", "/favicon.ico", "/actuator", "/hello"
    };

    private final LogHttpProperties logHttpProps;

    public TraceIdFilter(LogHttpProperties logHttpProps) {
        this.logHttpProps = logHttpProps;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(TRACE_ID_KEY, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        if (!logHttpProps.isEnabled()) {
            try { filterChain.doFilter(request, response); }
            finally { MDC.clear(); }
            return;
        }

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        boolean shouldLog = shouldLog(uri);
        boolean cacheBody = logHttpProps.isBodyEnabled() && canCacheBody(request);

        // 构造时立即缓存请求体，后续 Controller 从缓存重放
        CachedBodyRequestWrapper reqWrapper = null;
        if (cacheBody) {
            try { reqWrapper = new CachedBodyRequestWrapper(request); }
            catch (IOException e) { reqWrapper = null; }
        }
        // 响应用 Spring 的 ContentCachingResponseWrapper
        ContentCachingResponseWrapper respWrapper = cacheBody ? new ContentCachingResponseWrapper(response) : null;

        // START：请求进来立刻打印参数（含 requestBody）
        String reqBody = readReqBody(reqWrapper);
        if (shouldLog) {
            log.info("HTTP_REQUEST_START method={} uri={} query={} requestBody={} ip={} userAgent={}",
                    method, uri, query, reqBody, ip, userAgent);
        }

        try {
            filterChain.doFilter(
                    reqWrapper != null ? reqWrapper : request,
                    respWrapper != null ? respWrapper : response);

            if (shouldLog) {
                long cost = System.currentTimeMillis() - startTime;
                String respBody = readRespBody(respWrapper);
                log.info("HTTP_REQUEST_END method={} uri={} status={} cost={}ms responseBody={}",
                        method, uri, response.getStatus(), cost, respBody);
            }
        } catch (Exception e) {
            if (shouldLog) {
                long cost = System.currentTimeMillis() - startTime;
                log.error("HTTP_REQUEST_ERROR method={} uri={} status={} cost={}ms",
                        method, uri, response.getStatus(), cost, e);
            }
            throw e;
        } finally {
            if (respWrapper != null) {
                try { respWrapper.copyBodyToResponse(); } catch (IOException ignored) {}
            }
            MDC.clear();
        }
    }

    // ======================== 工具 ========================

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path != null && (path.startsWith("/swagger") || path.startsWith("/v3/api-docs")
                || path.startsWith("/doc.html") || path.equals("/favicon.ico"));
    }

    private boolean shouldLog(String uri) {
        if (uri == null) return true;
        for (String p : SKIP_LOG_PREFIXES) if (uri.startsWith(p)) return false;
        return true;
    }

    private boolean canCacheBody(HttpServletRequest req) {
        String ct = req.getContentType();
        if (ct == null) return true;
        ct = ct.toLowerCase();
        return !ct.contains("multipart/form-data") && !ct.contains("octet-stream")
                && !ct.startsWith("image/") && !ct.startsWith("video/");
    }

    private String readReqBody(CachedBodyRequestWrapper wrapper) {
        if (wrapper == null) return "";
        byte[] buf = wrapper.getCachedBody();
        if (buf.length == 0) return "";
        try {
            return truncate(new String(buf, "UTF-8"), logHttpProps.getMaxBodyLength());
        } catch (UnsupportedEncodingException e) {
            return "[ENCODING_ERROR]";
        }
    }

    private String readRespBody(ContentCachingResponseWrapper wrapper) {
        if (wrapper == null) return "";
        String ct = wrapper.getContentType();
        if (ct != null) {
            ct = ct.toLowerCase();
            if (ct.contains("octet-stream") || ct.contains("excel") || ct.contains("pdf")
                    || ct.startsWith("image/") || ct.startsWith("video/")
                    || ct.contains("vnd.openxmlformats")) {
                return "[SKIPPED_BINARY_RESPONSE]";
            }
        }
        byte[] buf = wrapper.getContentAsByteArray();
        if (buf.length == 0) return "";
        try {
            return truncate(new String(buf, "UTF-8"), logHttpProps.getMaxBodyLength());
        } catch (UnsupportedEncodingException e) {
            return "[ENCODING_ERROR]";
        }
    }

    private String truncate(String content, int max) {
        if (content == null || content.length() <= max) return content;
        return content.substring(0, max) + "...[TRUNCATED length=" + content.length() + "]";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            int idx = ip.indexOf(',');
            return idx > 0 ? ip.substring(0, idx).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) return ip.trim();
        return request.getRemoteAddr();
    }
}
