package com.xxl.job.extend.biz.model.builder;

import com.xxl.job.extend.biz.enums.PlanEnum;
import com.xxl.job.extend.biz.model.CyclePlanJobParam;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-10 13:35
 */
@Getter
public class CyclePlanJobBuilder extends PlanJobBuilder<CyclePlanJobParam, CyclePlanJobBuilder> {

    /**
     * 计划类型 必填
     */
    private PlanEnum planType;

    /**
     * 计划类型选项 WEEK/MONTH 必填
     */
    private List<Integer> planOption = new ArrayList<>();
    /**
     * 周期间隔
     */
    private Integer cycleInterval;
    /**
     * 周期执行时间（HH:mm:ss） 必填
     */
    private String cycleExeTime;
    /**
     * 创建后是否立即执行一次
     */
    private Boolean exeOnce;

    /**
     * 计划任务开始时间（yyyy-MM-dd HH:mm:ss） 非必填
     */
    private String startDateTime;
    /**
     * 计划任务结束时间（yyyy-MM-dd HH:mm:ss） 非必填
     */
    private String endDateTime;


    @Override
    public CyclePlanJobParam build() {
        CyclePlanJobParam planJob = new CyclePlanJobParam(planType);
        super.init(planJob);
        planJob.setPlanOption(planOption);
        planJob.setCycleInterval(cycleInterval);
        planJob.setCycleExeTime(cycleExeTime);
        planJob.setExeOnce(exeOnce);
        planJob.setStartDateTime(startDateTime);
        planJob.setEndDateTime(endDateTime);
        return planJob;
    }


    @Override
    public CyclePlanJobBuilder withJobKey(String jobKey) {
        return super.withJobKey(jobKey);
    }

    @Override
    public CyclePlanJobBuilder withJobHandler(String jobHandler) {
        return super.withJobHandler(jobHandler);
    }

    @Override
    public CyclePlanJobBuilder withJobDesc(String jobDesc) {
        return super.withJobDesc(jobDesc);
    }

    @Override
    public CyclePlanJobBuilder withHandlerParam(String handlerParam) {
        return super.withHandlerParam(handlerParam);
    }


    public CyclePlanJobBuilder withPlanType(PlanEnum planType) {
        this.planType = planType;
        return this;
    }

    public CyclePlanJobBuilder withPlanOption(Integer planOption) {
        this.planOption.add(planOption);
        return this;
    }

    public CyclePlanJobBuilder withPlanOption(Integer... planOption) {
        if (Objects.nonNull(planOption)) {
            this.planOption.addAll(Arrays.asList(planOption));
        }
        return this;
    }

    public CyclePlanJobBuilder withPlanOption(List<Integer> planOptionList) {
        if (Objects.nonNull(planOptionList)) {
            planOption.addAll(planOptionList);
        }
        return this;
    }

    public CyclePlanJobBuilder withCycleInterval(Integer cycleInterval) {
        this.cycleInterval = cycleInterval;
        return this;
    }

    public CyclePlanJobBuilder withCycleExeTime(String cycleExeTime) {
        this.cycleExeTime = cycleExeTime;
        return this;
    }

    public CyclePlanJobBuilder withExeOnce(Boolean exeOnce) {
        this.exeOnce = exeOnce;
        return this;
    }

    public CyclePlanJobBuilder withStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public CyclePlanJobBuilder withEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

}
