package com.kg.scheduler;

import com.kg.application.service.BatchTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日复盘任务定时器 —— 每天 08:00 创建复盘任务并分配。
 */
@Component
public class DailyReviewScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyReviewScheduler.class);

    private final BatchTaskService batchTaskService;

    public DailyReviewScheduler(BatchTaskService batchTaskService) {
        this.batchTaskService = batchTaskService;
    }

    /**
     * 每天 8:00：创建「每日复盘」任务，分配给所有学生+班长。
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void execute() {
        log.info("DailyReviewScheduler 开始执行");
        try {
            batchTaskService.createDailyTaskIfAbsent("每日复盘", "0", 8, 1);
        } catch (Exception e) {
            log.error("DailyReviewScheduler 执行异常", e);
        }
    }
}
