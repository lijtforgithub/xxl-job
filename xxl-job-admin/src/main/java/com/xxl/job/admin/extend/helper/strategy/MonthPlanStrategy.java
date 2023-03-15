package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.helper.AbstractPlanService;
import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2023-03-08 13:48
 */
@Slf4j
@Component
class MonthPlanStrategy extends AbstractPlanService {

    public PlanEnum getType() {
        return PlanEnum.MONTH;
    }

    @Override
    protected Date nextFireTime(PlanJob plan) {
        List<Date> dateList = getDateList(plan);
        Collections.sort(dateList);

        if (!dateList.isEmpty()) {
            return dateList.get(0);
        }

        return null;
    }

    private List<Date> getDateList(PlanJob plan) {
        List<String> cronList = getCronList(plan);
        List<Date> dateList = new ArrayList<>(cronList.size());

        Date start = new Date();
        if (Objects.nonNull(plan.getStartDateTime()) && plan.getStartDateTime().compareTo(start) > 0) {
            start = plan.getStartDateTime();
        }

        for (String cron : cronList) {
            try {
                ScheduleBuilder<?> scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
                TriggerBuilder<?> triggerBuilder = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder);

                triggerBuilder.startAt(start);
                if (Objects.nonNull(plan.getEndDateTime())) {
                    triggerBuilder.endAt(plan.getEndDateTime());
                }

                Trigger trigger = triggerBuilder.build();
                dateList.add(trigger.getFireTimeAfter(plan.getLastFireTime()));
            } catch (RuntimeException e) {
                log.error("Cron表达式错误：" + cron, e);
            }
        }

        return dateList;
    }

    private List<String> getCronList(PlanJob plan) {
        List<String> cronList = new ArrayList<>(2);
        Integer lastDay = null;
        List<Integer> days = new ArrayList<>();

        for (Integer o : plan.getPlanOptionList()) {
            if (o > 0) {
                days.add(o);
            } else {
                lastDay = o;
            }
        }

        if (!days.isEmpty()) {
            String str = days.stream().map(Object::toString).collect(Collectors.joining(","));
            cronList.add(getCron(plan, str));
        }

        if (Objects.nonNull(lastDay)) {
            cronList.add(getCron(plan, LAST_DAY_OF_MONTH));
        }

        return cronList;
    }

    private String getCron(PlanJob plan, String day) {
        LocalTime cycleExeTime = plan.getCycleExeTime();

        if (plan.getCycleInterval() > 1) {
            Integer startMonth = LocalDateTime.ofInstant(plan.getStartDateTime().toInstant(), ZoneId.systemDefault()).getMonthValue();
            return String.format("%d %d %d %s %d/%d ?", cycleExeTime.getSecond(), cycleExeTime.getMinute(), cycleExeTime.getHour(), day,
                    startMonth, plan.getCycleInterval());
        } else {
            return String.format("%d %d %d %s * ?", cycleExeTime.getSecond(), cycleExeTime.getMinute(), cycleExeTime.getHour(), day);
        }
    }

}
