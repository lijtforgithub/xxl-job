package com.xxl.job.admin.extend.helper;

import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.extend.biz.enums.PlanEnum;

import java.util.Date;

/**
 * @author LiJingTang
 * @date 2023-03-08 11:09
 */
public interface PlanService {

    PlanEnum getType();

    void checkAndInitPlan(PlanJob plan);

    Date getNextFireTime(PlanJob plan);

    boolean isEnd(PlanJob plan);

}
