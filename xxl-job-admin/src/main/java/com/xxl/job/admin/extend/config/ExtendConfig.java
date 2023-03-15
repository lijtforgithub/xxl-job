package com.xxl.job.admin.extend.config;

import com.xxl.job.admin.extend.event.PlanJobEndEvent;
import com.xxl.job.admin.extend.properties.MonitorProperties;
import com.xxl.job.admin.extend.service.PlanJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

import java.util.concurrent.Executors;

import static org.springframework.context.support.AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME;

/**
 * @author LiJingTang
 * @date 2023-03-10 15:24
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MonitorProperties.class)
public class ExtendConfig {

    @Autowired
    private PlanJobService planJobService;

    @EventListener
    public void endPlan(PlanJobEndEvent event) {
        planJobService.endPlan(event.getPlanId());
        log.info("计划任务【{}】结束", event.getPlanId());
    }

    @Bean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
    public ApplicationEventMulticaster multicaster() {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        multicaster.setErrorHandler(Throwable::printStackTrace);
        return multicaster;
    }

}
