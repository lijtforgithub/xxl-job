package com.xxl.job.admin.extend.service;

import java.util.Date;

/**
 * @author LiJingTang
 * @date 2023-03-10 15:02
 */
public interface PlanJobService {

    Date getTriggerNextTime(Integer jobId, Date fromTime, boolean isSchedule);

    void endPlan(Long planId);

    void monitor();

}
