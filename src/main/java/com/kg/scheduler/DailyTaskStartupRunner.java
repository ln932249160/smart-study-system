package com.kg.scheduler;

import com.kg.application.service.BatchTaskService;
import com.kg.enums.TaskTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 每日任务启动补偿 —— 项目启动时检查今日打卡/复盘任务是否已生成，未生成则补生成。
 */
@Component
public class DailyTaskStartupRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DailyTaskStartupRunner.class);

    private final BatchTaskService batchTaskService;

    public DailyTaskStartupRunner(BatchTaskService batchTaskService) {
        this.batchTaskService = batchTaskService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("====== 开始执行每日任务补偿检查 ======");
        LocalDate today = LocalDate.now();

        try {
            // 打卡任务
            batchTaskService.ensureDailyTaskExists(
                    "每日打卡", TaskTypeEnum.CHECK_IN.getCode(), today, 6, 1);
        } catch (Exception e) {
            log.error("每日打卡补偿检查异常", e);
        }

        try {
            // 复盘任务
            batchTaskService.ensureDailyTaskExists(
                    "每日复盘", TaskTypeEnum.DAILY_REVIEW.getCode(), today, 8, 1);
        } catch (Exception e) {
            log.error("每日复盘补偿检查异常", e);
        }

        log.info("====== 每日任务补偿检查完成 ======");
    }
}
