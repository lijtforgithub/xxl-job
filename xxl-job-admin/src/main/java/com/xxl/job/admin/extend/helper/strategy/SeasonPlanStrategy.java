package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.extern.slf4j.Slf4j;
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
class SeasonPlanStrategy extends AbstractMultiCronStrategy {

    private static final Map<Integer, List<Integer>> MONTH_MAP = new HashMap<Integer, List<Integer>>() {{
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
    protected List<String> getCronList(PlanJob plan) {
        List<Integer> monthOptions = plan.getPlanOptionList().stream().filter(o -> o > PlanEnum.TWO_STAGE_OPTION).collect(Collectors.toList());
        String monthCron = monthOptions.stream().map(MONTH_MAP::get).flatMap(List::stream).sorted().map(Objects::toString).collect(Collectors.joining(DELIMITER));
        List<String> dayCronList = getDayCronList(plan);
        List<String> cronList = new ArrayList<>(dayCronList.size());
        LocalTime cycleExeTime = plan.getCycleExeTime();

        for (String dayCron : dayCronList) {
            cronList.add(String.format("%d %d %d %s %s ?", cycleExeTime.getSecond(), cycleExeTime.getMinute(), cycleExeTime.getHour(), dayCron, monthCron));
        }

        return cronList;
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
