package com.xxl.job.admin.extend.helper.strategy;

import com.xxl.job.admin.core.thread.JobScheduleHelper;
import com.xxl.job.admin.extend.enums.PlanJobStatusEnum;
import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.core.util.DateUtil;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-08 14:45
 */
@Slf4j
@Component
class AssignPlanStrategy extends AbstractPlanStrategy {

    private static final long WIN_TIME = JobScheduleHelper.PRE_READ_MS;

    @Override
    public PlanEnum getType() {
        return PlanEnum.ASSIGN;
    }

    @Override
    public void checkAndInitPlan(PlanJob plan) {
        super.checkAndInitPlan(plan);

        plan.setCycleInterval(0);

        List<String> list = plan.getAssignDateTimeList();
        for (String str : list) {
            Assert.isTrue(DateUtil.parseDateTime(str).getTime() > System.currentTimeMillis() + WIN_TIME, "日期时间【" + str + "】不合法");
        }
    }

    @Override
    protected Date nextFireTime(PlanJob plan) {
        List<String> list = plan.getAssignDateTimeList();
        if (Objects.isNull(plan.getLastFireTime())) {
            plan.setLastFireTime(new Date());
        }

        for (String str : list) {
            Date date = DateUtil.parseDateTime(str);
            if (date.compareTo(plan.getLastFireTime()) > 0) {
                return date;
            }
        }

        return null;
    }

    @Override
    public boolean isEnd(PlanJob plan) {
        if (PlanJobStatusEnum.isEnd(plan.getStatus())) {
            return true;
        }
        List<String> list = plan.getAssignDateTimeList();
        String last = list.get(list.size() - 1);
        return (Objects.nonNull(plan.getLastFireTime()) && last.equals(DateUtil.formatDateTime(plan.getLastFireTime())))
                || DateUtil.parseDateTime(last).getTime() < System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS;
    }

}
