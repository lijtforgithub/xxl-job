package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.helper.AbstractPlanService;
import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.extend.biz.enums.PlanEnum;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.TimeOfDay;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * @author LiJingTang
 * @date 2023-03-08 11:13
 */
@Component
class DailyPlanStrategy extends AbstractPlanService {

    @Override
    public PlanEnum getType() {
        return PlanEnum.DAILY;
    }

    @Override
    protected ScheduleBuilder<?> buildSchedule(PlanJob plan) {
        int intervalHours = 24 * plan.getCycleInterval();
        LocalTime cycleExeTime = plan.getCycleExeTime();

        return DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
                .withIntervalInHours(intervalHours)
                .startingDailyAt(new TimeOfDay(cycleExeTime.getHour(), cycleExeTime.getMinute(), cycleExeTime.getSecond()));
    }

}
