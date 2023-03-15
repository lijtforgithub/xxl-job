package com.xxl.job.extend.config;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.extend.biz.client.PlanJobClient;
import com.xxl.job.extend.biz.model.AdminInfo;
import com.xxl.job.extend.helper.XxlJobExtendHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2023-03-09 13:09
 */
@Slf4j
public class XxlJobExtendConfig implements ApplicationContextAware {

    private static List<AdminInfo> adminInfoList;
    private static String xxlJobAppName = null;

    private ApplicationContext applicationContext;

    @Bean
    PlanJobClient planJobClient() {
        return new PlanJobClient();
    }

    @Bean
    XxlJobExtendHelper helper() {
        return new XxlJobExtendHelper();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshed() throws IllegalAccessException {
        String[] beanNames = applicationContext.getBeanNamesForType(XxlJobExecutor.class);
        if (beanNames.length == 0) {
            throw new RuntimeException("请先开启xxl-job客户端配置");
        }

        initAppName();
        initAdminInfo();
        log.info("xxl-job-extend 初始化配置完成");
    }

    void initAppName() throws IllegalAccessException {
        if (Objects.isNull(xxlJobAppName)) {
            XxlJobExecutor xxlJobExecutor = applicationContext.getBean(XxlJobExecutor.class);
            Field appname = ReflectionUtils.findField(XxlJobExecutor.class, "appname");
            Objects.requireNonNull(appname, "appname属性不存在");
            appname.setAccessible(true);
            xxlJobAppName = appname.get(xxlJobExecutor).toString();
            log.info("xxlJobAppName = {}", xxlJobAppName);
        }
    }

    void initAdminInfo() throws IllegalAccessException {
        if (Objects.isNull(adminInfoList)) {
            List<AdminBiz> list = XxlJobExecutor.getAdminBizList();
            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            adminInfoList = new ArrayList<>(list.size());
            Field addressUrl = ReflectionUtils.findField(AdminBizClient.class, "addressUrl");
            Objects.requireNonNull(addressUrl, "addressUrl属性不存在");
            addressUrl.setAccessible(true);
            Field accessToken = ReflectionUtils.findField(AdminBizClient.class, "accessToken");
            Objects.requireNonNull(addressUrl, "accessToken属性不存在");
            accessToken.setAccessible(true);

            for (AdminBiz adminBiz : list) {
                AdminInfo info = new AdminInfo();
                info.setAddressUrl(addressUrl.get(adminBiz).toString());
                info.setAccessToken(accessToken.get(adminBiz).toString());
                adminInfoList.add(info);
            }
            log.info("xxlJobAdmin = {}", adminInfoList.stream().map(AdminInfo::getAddressUrl).collect(Collectors.toList()));
        }
    }

    public static List<AdminInfo> getAdminInfoList() {
        return adminInfoList;
    }

    public static String getXxlJobAppName() {
        return xxlJobAppName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
