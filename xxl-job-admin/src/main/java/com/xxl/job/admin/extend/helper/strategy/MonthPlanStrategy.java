package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2023-03-08 13:48
 */
@Slf4j
@Component
class MonthPlanStrategy extends AbstractMultiCronStrategy {

    public PlanEnum getType() {
        return PlanEnum.MONTH;
    }

    @Override
    protected List<String> getCronList(PlanJob plan) {
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
            String str = days.stream().map(Object::toString).collect(Collectors.joining(DELIMITER));
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
            int startMonth = getReasonableStart(plan);
            return String.format("%d %d %d %s %d/%d ?", cycleExeTime.getSecond(), cycleExeTime.getMinute(), cycleExeTime.getHour(), day,
                    startMonth, plan.getCycleInterval());
        } else {
            return String.format("%d %d %d %s * ?", cycleExeTime.getSecond(), cycleExeTime.getMinute(), cycleExeTime.getHour(), day);
        }
    }

    /**
     * 计算合理的开始时间 如果开始月不符合选项 则顺延下月开始
     */
    private int getReasonableStart(PlanJob plan) {
        LocalDateTime startDateTime = LocalDateTime.ofInstant(plan.getStartDateTime().toInstant(), ZoneId.systemDefault());
        LocalDate startDate = startDateTime.toLocalDate();

        List<Integer> options = plan.getPlanOptionList().stream().sorted().collect(Collectors.toList());
        if (options.contains(-1)) {
            // 月底最后一天
            int lastDay = startDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
            options.add(lastDay);
        }

        int dayOfMonth = startDate.getDayOfMonth();
        Integer lastOption = options.get(options.size() - 1);
        if (dayOfMonth > lastOption || (dayOfMonth == lastOption && startDateTime.toLocalTime().compareTo(plan.getCycleExeTime()) > 0)) {
            return startDate.plusMonths(1).getMonthValue();
        }

        return startDate.getMonthValue();
    }

}