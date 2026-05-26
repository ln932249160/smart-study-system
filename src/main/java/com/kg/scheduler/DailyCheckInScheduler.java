package com.kg.scheduler;

import com.kg.application.service.BatchTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日打卡任务定时器 —— 每天 06:00 创建打卡任务并分配。
 */
@Component
public class DailyCheckInScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyCheckInScheduler.class);

    private final BatchTaskService batchTaskService;

    public DailyCheckInScheduler(BatchTaskService batchTaskService) {
        this.batchTaskService = batchTaskService;
    }

    /**
     * 每天 6:00：创建「每日打卡」任务，分配给所有学生+班长。
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void execute() {
        log.info("DailyCheckInScheduler 开始执行");
        try {
            batchTaskService.createDailyTaskIfAbsent("每日打卡", "1", 6, 1);
        } catch (Exception e) {
            log.error("DailyCheckInScheduler 执行异常", e);
        }
    }
}
