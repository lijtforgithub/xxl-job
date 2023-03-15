package com.xxl.job.admin.extend.helper;

import com.xxl.job.admin.extend.service.PlanJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2023-03-11 16:11
 */
@Slf4j
@Component
public class PlanMonitorHelper implements SmartInitializingSingleton {

    @Autowired
    private PlanJobService planJobService;

    private final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    public void start() {
        long time = TimeUnit.MINUTES.toMillis(1);
        long sleepTime = TimeUnit.MINUTES.toMillis(5);

        EXECUTOR.execute(() -> {
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            while (running) {
                long start = System.currentTimeMillis();
                planJobService.monitor();
                long cost = System.currentTimeMillis() - start;

                if (cost < time) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(sleepTime - cost);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
    }

    @PreDestroy
    public void destroy() {
        running = false;
        EXECUTOR.shutdownNow();
        log.info("计划任务监控线程关闭成功");
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.start();
        log.info("计划任务监控线程启动成功");
    }

}
