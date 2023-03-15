package com.xxl.job.extend.helper;

import com.xxl.job.extend.biz.client.PlanJobClient;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-12 14:11
 */
public class XxlJobExtendHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        XxlJobExtendHelper.applicationContext = applicationContext;
    }


    public static PlanJobClient buildClient() {
        Objects.requireNonNull(applicationContext, "请检查是否开启此功能 @EnableXxlJobExtend");
        return applicationContext.getBean(PlanJobClient.class);
    }

}
