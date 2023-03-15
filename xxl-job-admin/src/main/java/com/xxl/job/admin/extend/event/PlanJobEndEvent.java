package com.xxl.job.admin.extend.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author LiJingTang
 * @date 2023-03-10 15:14
 */
public class PlanJobEndEvent extends ApplicationEvent {

    private final Long planId;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public PlanJobEndEvent(Object source) {
        super(source);
        planId = 0L;
    }

    public PlanJobEndEvent(Long planId) {
        super("计划任务结束");
        this.planId = planId;
    }

    public Long getPlanId() {
        return planId;
    }

}
