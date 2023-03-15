package com.xxl.job.extend.biz.model.builder;

import com.xxl.job.extend.biz.model.PlanJobParam;
import lombok.Getter;

/**
 * @author LiJingTang
 * @date 2023-03-10 12:59
 */
@Getter
public abstract class PlanJobBuilder<T extends PlanJobParam, R extends PlanJobBuilder> {

    /**
     * 任务唯一编号（最长50） 处理器+编号 用来确定唯一性默认使用UUID
     */
    private String jobKey;
    /**
     * 任务处理器 必填
     */
    private String jobHandler;
    /**
     * 任务描述 非必填
     */
    private String jobDesc;
    /**
     * 任务处理参数 非必填
     */
    private String handlerParam;


    public abstract T build();


    protected void init(T t) {
        t.setJobKey(jobKey);
        t.setJobHandler(jobHandler);
        t.setJobDesc(jobDesc);
        t.setHandlerParam(handlerParam);
    }


    public R withJobKey(String jobKey) {
        this.jobKey = jobKey;
        return (R) this;
    }

    public R withJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
        return (R) this;
    }

    public R withJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
        return (R) this;
    }

    public R withHandlerParam(String handlerParam) {
        this.handlerParam = handlerParam;
        return (R) this;
    }


    public static AssignPlanJobBuilder assignPlanJobBuilder() {
        return new AssignPlanJobBuilder();
    }

    public static CyclePlanJobBuilder cyclePlanJobBuilder() {
        return new CyclePlanJobBuilder();
    }

}
