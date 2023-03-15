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

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2023-03-14 13:41
 */
@Slf4j
@Component
public class SeasonPlanStrategy extends AbstractPlanService {

    private static final Map<Integer, List<Integer>> MONTH_MAP = new HashMap<Integer, List<Integer>>(){{
        put(PlanEnum.TWO_STAGE_OPTION + 1, new ArrayList<Integer>() {{
            add(1);
            add(4);
            add(7);
            add(10);
        }});
        put(PlanEnum.TWO_STAGE_OPTION + 2, new ArrayList<Integer>() {{
            add(2);
            add(5);
            add(8);
            add(11);
        }});
        put(PlanEnum.TWO_STAGE_OPTION + 3, new ArrayList<Integer>() {{
            add(3);
            add(6);
            add(9);
            add(12);
        }});
    }};

    @Override
    public PlanEnum getType() {
        return PlanEnum.SEASON;
    }

    @Override
    public void checkAndInitPlan(PlanJob plan) {
        super.checkAndInitPlan(plan);

        plan.setCycleInterval(1);

        if (plan.getPlanOptionList().stream().noneMatch(o -> o > PlanEnum.TWO_STAGE_OPTION)) {
            plan.getPlanOptionList().add(PlanEnum.TWO_STAGE_OPTION + 1);
        }
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
        String monthCron = getMonthCron(plan);
        List<String> dayCronList = getDayCronList(plan);
        List<Date> dateList = new ArrayList<>(dayCronList.size());

        Date start = new Date();
        if (Objects.nonNull(plan.getStartDateTime()) && plan.getStartDateTime().compareTo(start) > 0) {
            start = plan.getStartDateTime();
        }

        LocalTime cycleExeTime = plan.getCycleExeTime();

        for (String dayCron : dayCronList) {
            String cron = null;
            try {
                cron = String.format("%d %d %d %s %s ?", cycleExeTime.getSecond(), cycleExeTime.getMinute(), cycleExeTime.getHour(), dayCron, monthCron);
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

    private String getMonthCron(PlanJob plan) {
        List<Integer> monthOptions = plan.getPlanOptionList().stream().filter(o -> o > PlanEnum.TWO_STAGE_OPTION).collect(Collectors.toList());
        return monthOptions.stream().map(MONTH_MAP::get).flatMap(List::stream).sorted().map(Objects::toString).collect(Collectors.joining(DELIMITER));
    }

    private List<String> getDayCronList(PlanJob plan) {
        List<Integer> dayOptions = plan.getPlanOptionList().stream().filter(o -> o < PlanEnum.TWO_STAGE_OPTION).collect(Collectors.toList());

        List<String> cronList = new ArrayList<>(2);
        Integer lastDay = null;
        List<Integer> days = new ArrayList<>();

        for (Integer o : dayOptions) {
            if (o > 0) {
                days.add(o);
            } else {
                lastDay = o;
            }
        }

        if (!days.isEmpty()) {
            String str = days.stream().map(Object::toString).collect(Collectors.joining(DELIMITER));
            cronList.add(str);
        }

        if (Objects.nonNull(lastDay)) {
            cronList.add(LAST_DAY_OF_MONTH);
        }

        return cronList;
    }

}
