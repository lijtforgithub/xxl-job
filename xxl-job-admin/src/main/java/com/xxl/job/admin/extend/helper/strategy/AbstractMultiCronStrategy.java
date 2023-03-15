package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.model.PlanJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-15 11:45
 */
@Slf4j
abstract class AbstractMultiCronStrategy extends AbstractMultiDateStrategy {

    @Override
    protected List<Date> getDateList(PlanJob plan) {
        List<String> cronList = getCronList(plan);
        List<Date> dateList = new ArrayList<>(cronList.size());

        for (String cron : cronList) {
            try {
                ScheduleBuilder<?> scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
                TriggerBuilder<?> triggerBuilder = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder)
                        .startAt(plan.getStartDateTime());

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

    protected abstract List<String> getCronList(PlanJob plan);

}
