package com.xxl.job.extend.biz.client;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import com.xxl.job.extend.biz.PlanJobBiz;
import com.xxl.job.extend.biz.model.AdminInfo;
import com.xxl.job.extend.biz.model.CancelJobParam;
import com.xxl.job.extend.biz.model.PlanJobParam;
import com.xxl.job.extend.config.XxlJobExtendConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-09 12:49
 */
@Slf4j
public class PlanJobClient implements PlanJobBiz {

    @Override
    public ReturnT<String> addPlan(PlanJobParam param) {
        if (CollectionUtils.isEmpty(XxlJobExtendConfig.getAdminInfoList())) {
            log.warn("Admin 信息为空");
            return ReturnT.FAIL;
        }

        if (log.isDebugEnabled()) {
            log.debug("添加计划任务：{}", GsonTool.toJson(param));
        }

        try {
            param.verify();
        } catch (Exception e) {
            log.warn("参数不合法", e);
            return new ReturnT<>(400, "参数不合法：" + e.getMessage());
        }

        return post(String.format("api/extend/plan/add/%s", XxlJobExtendConfig.getXxlJobAppName()),
                param, String.class);
    }

    private <T> ReturnT<T> post(String path, Object param, Class<T> clazz) {
        ReturnT<T> result = null;
        for (AdminInfo adminInfo : XxlJobExtendConfig.getAdminInfoList()) {
            result = XxlJobRemotingUtil.postBody(adminInfo.getAddressUrl() + path,
                    adminInfo.getAccessToken(), adminInfo.getTimeout(), param, clazz);
            if (ReturnT.SUCCESS_CODE == result.getCode()) {
                break;
            }
        }

        if (Objects.nonNull(result) && ReturnT.SUCCESS_CODE != result.getCode()) {
            log.warn("请求xxl-job结果不理想【{}】{}", path, result.getMsg());
        }

        return result;
    }

    @Override
    public ReturnT<String> cancelPlanById(String planId) {
        if (CollectionUtils.isEmpty(XxlJobExtendConfig.getAdminInfoList())) {
            log.warn("Admin 信息为空");
            return ReturnT.FAIL;
        }
        if (!StringUtils.hasLength(planId)) {
            return new ReturnT<>(400, "计划ID为空");
        }

        return post(String.format("api/extend/plan/cancel/%s/%s", XxlJobExtendConfig.getXxlJobAppName(), planId),
                null, String.class);
    }

    @Override
    public ReturnT<String> cancelPlanByJobKey(CancelJobParam param) {
        if (CollectionUtils.isEmpty(XxlJobExtendConfig.getAdminInfoList())) {
            log.warn("Admin 信息为空");
            return ReturnT.FAIL;
        }
        if (!StringUtils.hasLength(param.getJobHandler()) || !StringUtils.hasLength(param.getJobKey())) {
            return new ReturnT<>(400, "计划处理器或key为空");
        }

        return post(String.format("api/extend/plan/cancel/%s", XxlJobExtendConfig.getXxlJobAppName()),
                param, String.class);
    }

}
