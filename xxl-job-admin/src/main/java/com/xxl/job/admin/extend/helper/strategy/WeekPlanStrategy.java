package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.TimeOfDay;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2023-03-08 11:43
 */
@Slf4j
@Component
class WeekPlanStrategy extends AbstractPlanStrategy {

    @Override
    public PlanEnum getType() {
        return PlanEnum.WEEK;
    }

    @Override
    public void checkAndInitPlan(PlanJob plan) {
        super.checkAndInitPlan(plan);

        Assert.isTrue(plan.getCycleInterval() == 1 || plan.getPlanOptionList().size() == 1, "间隔多周目前只支持周几一个选项");
    }

    @Override
    protected ScheduleBuilder<?> buildSchedule(PlanJob plan) {
        LocalTime cycleExeTime = plan.getCycleExeTime();
        List<Integer> daysOfWeek = plan.getPlanOptionList().stream().map(o -> (o % 7) + 1).collect(Collectors.toList());

        if (plan.getCycleInterval() > 1) {
            int intervalHours = 24 * 7 * plan.getCycleInterval();
            return DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
                    .withIntervalInHours(intervalHours).onDaysOfTheWeek(daysOfWeek.get(0))
                    .startingDailyAt(new TimeOfDay(cycleExeTime.getHour(), cycleExeTime.getMinute(), cycleExeTime.getSecond()));
        } else {
            String week = daysOfWeek.stream().map(Objects::toString).collect(Collectors.joining(DELIMITER));
            String cron = String.format("%d %d %d ? * %s", cycleExeTime.getSecond(), cycleExeTime.getMinute(), cycleExeTime.getHour(), week);

            try {
                return CronScheduleBuilder.cronSchedule(cron);
            } catch (RuntimeException e) {
                log.error("Cron表达式错误：" + cron, e);
                return null;
            }
        }
    }

}
