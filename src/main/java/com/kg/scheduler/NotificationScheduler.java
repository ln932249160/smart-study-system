package com.kg.scheduler;

import com.kg.application.service.BatchTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 消息提醒定时任务 —— 每分钟扫描 task 表生成通知。
 */
@Component
public class NotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    private final BatchTaskService batchTaskService;

    public NotificationScheduler(BatchTaskService batchTaskService) {
        this.batchTaskService = batchTaskService;
    }

    /**
     * 每分钟整点执行：扫描 task 开始/即将截止/结束，生成通知消息。
     */
    @Scheduled(cron = "0 * * * * ?")
    public void execute() {
        log.debug("NotificationScheduler 开始执行");
        try {
            batchTaskService.generateNotifications();
        } catch (Exception e) {
            log.error("NotificationScheduler 执行异常", e);
        }
    }
}
