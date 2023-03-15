package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.helper.AbstractPlanService;
import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.extend.biz.enums.PlanEnum;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.TimeOfDay;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2023-03-08 11:43
 */
@Component
class WeekPlanStrategy extends AbstractPlanService {

    @Override
    public PlanEnum getType() {
        return PlanEnum.WEEK;
    }

    @Override
    protected ScheduleBuilder<?> buildSchedule(PlanJob plan) {
        LocalTime cycleExeTime = plan.getCycleExeTime();
        int intervalHours = 24 * 7 * plan.getCycleInterval();
        Set<Integer> daysOfWeek = plan.getPlanOptionList().stream().map(o -> (o % 7) + 1).collect(Collectors.toSet());

        return DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
                .withIntervalInHours(intervalHours).onDaysOfTheWeek(daysOfWeek)
                .startingDailyAt(new TimeOfDay(cycleExeTime.getHour(), cycleExeTime.getMinute(), cycleExeTime.getSecond()));
    }

}
