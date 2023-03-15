package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.extend.model.PlanJob;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author LiJingTang
 * @date 2023-03-15 12:35
 */
abstract class AbstractMultiDateStrategy extends AbstractPlanStrategy {

    @Override
    protected Date nextFireTime(PlanJob plan) {
        List<Date> dateList = getDateList(plan);
        Collections.sort(dateList);

        if (!dateList.isEmpty()) {
            return dateList.get(0);
        }

        return null;
    }

    protected abstract List<Date> getDateList(PlanJob plan);

}
