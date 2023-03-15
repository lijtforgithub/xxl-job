package com.xxl.job.admin.extend.helper;

import com.xxl.job.admin.extend.service.PlanJobService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * @author LiJingTang
 * @date 2023-03-09 18:13
 */
@Component
public class PlanHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PlanHelper.applicationContext = applicationContext;
    }

    public static PlanService checkAndGetPlanService(int planType) {
        PlanService service = planService(planType);
        Assert.notNull(service, "计划类型【" + planType + "】不合法");
        return service;
    }

    public static PlanService planService(int planType) {
        Collection<PlanService> values = applicationContext.getBeansOfType(PlanService.class).values();
        for (PlanService service : values) {
            if (planType == service.getType().getValue()) {
                return service;
            }
        }

        return null;
    }

    public static PlanJobService planJobService() {
        return applicationContext.getBean(PlanJobService.class);
    }

}
