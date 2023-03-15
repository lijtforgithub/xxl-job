package com.xxl.job.extend.biz.model.builder;

import com.xxl.job.extend.biz.model.AssignPlanJobParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-10 13:11
 */
public class AssignPlanJobBuilder extends PlanJobBuilder<AssignPlanJobParam, AssignPlanJobBuilder> {

    private List<String> assignDateTimeList = new ArrayList<>();

    @Override
    public AssignPlanJobParam build() {
        AssignPlanJobParam planJob = new AssignPlanJobParam();
        super.init(planJob);
        planJob.setAssignDateTimeList(assignDateTimeList);
        return planJob;
    }


    @Override
    public AssignPlanJobBuilder withJobKey(String jobKey) {
        return super.withJobKey(jobKey);
    }

    @Override
    public AssignPlanJobBuilder withJobHandler(String jobHandler) {
        return super.withJobHandler(jobHandler);
    }

    @Override
    public AssignPlanJobBuilder withJobDesc(String jobDesc) {
        return super.withJobDesc(jobDesc);
    }

    @Override
    public AssignPlanJobBuilder withHandlerParam(String handlerParam) {
        return super.withHandlerParam(handlerParam);
    }


    public AssignPlanJobBuilder withDateTime(String dateTime) {
        assignDateTimeList.add(dateTime);
        return this;
    }

    public AssignPlanJobBuilder withDateTime(String... dateTime) {
        if (Objects.nonNull(dateTime)) {
            assignDateTimeList.addAll(Arrays.asList(dateTime));
        }
        return this;
    }

    public AssignPlanJobBuilder withDateTime(List<String> dateTimeList) {
        if (Objects.nonNull(dateTimeList)) {
            assignDateTimeList.addAll(dateTimeList);
        }
        return this;
    }

}
