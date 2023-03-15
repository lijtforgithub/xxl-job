package com.xxl.job.extend.biz.model;

import com.xxl.job.core.util.DateUtil;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2023-03-10 13:06
 */
@Data
public class AssignPlanJobParam extends PlanJobParam {

    /**
     * 指定日期时间 （yyyy-MM-dd HH:mm:ss） 必填
     */
    private List<String> assignDateTimeList;


    public AssignPlanJobParam() {
        super(PlanEnum.ASSIGN);
    }

    @Override
    public void verify() {
        super.verify();

        Assert.notEmpty(assignDateTimeList, "指定日期时间为空");
        assignDateTimeList = assignDateTimeList.stream().distinct().collect(Collectors.toList());
        for (String str : assignDateTimeList) {
            Assert.notNull(DateUtil.parseDateTime(str), "指定执行日期时间【" + str + "】格式不合法");
        }
    }

}
