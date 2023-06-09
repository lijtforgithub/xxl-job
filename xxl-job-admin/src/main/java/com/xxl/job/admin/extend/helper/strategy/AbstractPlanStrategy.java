package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.enums.PlanJobStatusEnum;
import com.xxl.job.admin.extend.helper.PlanService;
import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.extern.slf4j.Slf4j;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-08 11:59
 */
@Slf4j
public abstract class AbstractPlanStrategy implements PlanService {

    protected static final String LAST_DAY_OF_MONTH = "L";
    protected static final String DELIMITER = ",";

    @Override
    public void checkAndInitPlan(PlanJob plan) {
        if (Objects.nonNull(plan.getStartDateTime()) && Objects.nonNull(plan.getEndDateTime())) {
            Assert.isTrue(plan.getEndDateTime().compareTo(plan.getStartDateTime()) > 0, "结束日期要大于开始日期");
        }

        if (Objects.isNull(plan.getCycleInterval()) && PlanEnum.ASSIGN != getType()) {
            plan.setCycleInterval(1);
        }

        if (Objects.isNull(plan.getExeOnce())) {
            plan.setExeOnce(Boolean.FALSE);
        }

        if (Objects.isNull(plan.getStartDateTime()) && PlanEnum.ASSIGN != getType()) {
            plan.setStartDateTime(plan.getCreateTime());
        }
    }

    @Override
    public Date getNextFireTime(PlanJob plan) {
        if (isEnd(plan)) {
            return null;
        }

        return nextFireTime(plan);
    }

    protected Date nextFireTime(PlanJob plan) {
        return nextFireTime(plan, buildSchedule(plan));
    }

    protected Date nextFireTime(PlanJob plan, ScheduleBuilder<?> scheduleBuilder) {
        if (Objects.isNull(scheduleBuilder)) {
            log.warn("ScheduleBuilder 为空");
            return null;
        }

        // CalendarIntervalScheduleBuilder 的startAt的时分秒就是每周期执行的时间
        TriggerBuilder<?> triggerBuilder = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder)
                .startAt(plan.getStartDateTime());

        if (Objects.nonNull(plan.getEndDateTime())) {
            triggerBuilder.endAt(plan.getEndDateTime());
        }

        Trigger trigger = triggerBuilder.build();
        return trigger.getFireTimeAfter(plan.getLastFireTime());
    }

    protected ScheduleBuilder<?> buildSchedule(PlanJob plan) {
        return null;
    }

    @Override
    public boolean isEnd(PlanJob plan) {
        return PlanJobStatusEnum.isEnd(plan.getStatus()) || Objects.nonNull(plan.getEndDateTime())
                && System.currentTimeMillis() > plan.getEndDateTime().getTime();
    }

}
