package com.xxl.job.admin.extend.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.extend.dto.CancelJobParamDTO;
import com.xxl.job.admin.extend.dto.PlanJobParamDTO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.extend.biz.PlanJobBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author LiJingTang
 * @date 2023-03-09 14:24
 */
@Slf4j
@RestController
@RequestMapping("/api/extend")
public class ExtendApiController {

    @Autowired
    private PlanJobBiz planJobBiz;

    @PostMapping("/plan/add/{appName}")
    @PermissionLimit(limit = false)
    public ReturnT<String> add(@PathVariable String appName, @RequestBody PlanJobParamDTO param) {
        try {
            param.setAppName(appName);
            return planJobBiz.addPlan(param);
        } catch (Exception e) {
            log.error("添加计划任务异常", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @PostMapping("/plan/cancel/{appName}/{planId}")
    @PermissionLimit(limit = false)
    public ReturnT<String> cancel(@PathVariable String appName, @PathVariable String planId) {
        try {
            return planJobBiz.cancelPlanById(planId);
        } catch (Exception e) {
            log.error("取消计划任务异常", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @PostMapping("/plan/cancel/{appName}")
    @PermissionLimit(limit = false)
    public ReturnT<String> cancel(@PathVariable String appName, @RequestBody CancelJobParamDTO param) {
        try {
            param.setAppName(appName);
            return planJobBiz.cancelPlanByJobKey(param);
        } catch (Exception e) {
            log.error("取消计划任务异常", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

}
