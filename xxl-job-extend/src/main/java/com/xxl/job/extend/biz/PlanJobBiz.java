package com.xxl.job.extend.biz;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.extend.biz.model.CancelJobParam;
import com.xxl.job.extend.biz.model.PlanJobParam;

/**
 * @author LiJingTang
 * @date 2023-03-09 11:31
 */
public interface PlanJobBiz {

    /**
     * 添加一个计划任务
     *
     * @param param 参数
     * @return 任务ID
     */
    ReturnT<String> addPlan(PlanJobParam param);

    /**
     * 取消计划
     *
     * @param planId 计划任务ID
     * @return 上次执行时间
     */
    ReturnT<String> cancelPlanById(String planId);

    /**
     * 取消计划
     *
     * @param param 计划任务KEY
     * @return 上次执行时间
     */
    ReturnT<String> cancelPlanByJobKey(CancelJobParam param);

}
