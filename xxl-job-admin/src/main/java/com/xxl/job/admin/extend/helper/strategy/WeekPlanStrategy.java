package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.TimeOfDay;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Date;
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
    protected Date nextFireTime(PlanJob plan) {
        LocalTime cycleExeTime = plan.getCycleExeTime();
        List<Integer> daysOfWeek = plan.getPlanOptionList().stream().map(o -> (o % 7) + 1).collect(Collectors.toList());

        if (plan.getCycleInterval() > 1) {
            if (daysOfWeek.size() > 1) {
                return getDate(plan);
            } else {
                int intervalHours = 24 * 7 * plan.getCycleInterval();
                DailyTimeIntervalScheduleBuilder scheduleBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
                        .withIntervalInHours(intervalHours).onDaysOfTheWeek(daysOfWeek.get(0))
                        .startingDailyAt(new TimeOfDay(cycleExeTime.getHour(), cycleExeTime.getMinute(), cycleExeTime.getSecond()));
                return nextFireTime(plan, scheduleBuilder);
            }
        } else {
            String week = daysOfWeek.stream().map(Objects::toString).collect(Collectors.joining(DELIMITER));
            String cron = String.format("%d %d %d ? * %s", cycleExeTime.getSecond(), cycleExeTime.getMinute(), cycleExeTime.getHour(), week);

            try {
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
                return nextFireTime(plan, scheduleBuilder);
            } catch (RuntimeException e) {
                log.error("Cron表达式错误：" + cron, e);
                return null;
            }
        }
    }

    /**
     * 间隔多周 多个选项 自己计算时间
     */
    private Date getDate(PlanJob plan) {
        // 起始周的周一
        LocalDate weekFirstDate;
        // 上次执行的日期
        LocalDate lastFireDate;
        LocalDate lastWeekFirstDate;

        if (Objects.isNull(plan.getLastFireTime())) {
            lastFireDate = LocalDate.now();
            lastWeekFirstDate = lastFireDate.with(WeekFields.ISO.dayOfWeek(), 1);
            weekFirstDate = getReasonableStart(plan).with(WeekFields.ISO.dayOfWeek(), 1);
        } else {
            lastFireDate = LocalDateTime.ofInstant(plan.getLastFireTime().toInstant(), ZoneId.systemDefault()).toLocalDate();
            lastWeekFirstDate = lastFireDate.with(WeekFields.ISO.dayOfWeek(), 1);
            weekFirstDate = lastWeekFirstDate;
        }

        List<Integer> options = plan.getPlanOptionList().stream().sorted().collect(Collectors.toList());
        while (true) {
            int compare = weekFirstDate.compareTo(lastWeekFirstDate);
            boolean thisWeek = false;
            // 判断当前周是否执行完毕
            if (0 == compare) {
                int dayOfWeek = lastFireDate.getDayOfWeek().getValue();
                thisWeek = options.get(options.size() - 1) > dayOfWeek;
                if (thisWeek) {
                    options.removeIf(o -> o <= dayOfWeek);
                }
            }
            if (compare > 0 || thisWeek) {
                break;
            }
            // 下次执行的星期
            weekFirstDate = weekFirstDate.plusWeeks(plan.getCycleInterval());
        }

        return Date.from(weekFirstDate.plusDays((long) options.get(0) - 1).atTime(plan.getCycleExeTime()).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 计算合理的开始时间 如果开始周不符合选项 则顺延下周开始
     */
    private LocalDate getReasonableStart(PlanJob plan) {
        LocalDateTime startDateTime = LocalDateTime.ofInstant(plan.getStartDateTime().toInstant(), ZoneId.systemDefault());
        LocalDate startDate = startDateTime.toLocalDate();
        int dayOfWeek = startDate.getDayOfWeek().getValue();
        List<Integer> options = plan.getPlanOptionList().stream().sorted().collect(Collectors.toList());
        Integer lastOption = options.get(options.size() - 1);
        if (dayOfWeek > lastOption || (dayOfWeek == lastOption && startDateTime.toLocalTime().compareTo(plan.getCycleExeTime()) > 0)) {
            return startDate.plusWeeks(1).with(WeekFields.ISO.dayOfWeek(), 1);
        }

        return startDate;
    }

}