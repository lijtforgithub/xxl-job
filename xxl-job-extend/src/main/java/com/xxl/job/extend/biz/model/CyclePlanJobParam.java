package com.xxl.job.extend.biz.model;

import com.xxl.job.core.util.DateUtil;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2023-03-10 13:08
 */
@Data
public class CyclePlanJobParam extends PlanJobParam {

    /**
     * 计划类型选项 WEEK/MONTH/SEASON 必填
     */
    private List<Integer> planOption;
    /**
     * 周期间隔 非必填 默认值为1
     */
    private Integer cycleInterval;
    /**
     * 周期执行时间（HH:mm:ss） 必填
     */
    private String cycleExeTime;
    /**
     * 创建后是否立即执行一次 默认false
     */
    private Boolean exeOnce;

    /**
     * 计划任务开始时间（yyyy-MM-dd HH:mm:ss） 非必填
     */
    private String startDateTime;
    /**
     * 计划任务结束时间（yyyy-MM-dd HH:mm:ss） 非必填
     */
    private String endDateTime;


    public CyclePlanJobParam(PlanEnum planType) {
        super(planType);
        Assert.isTrue(PlanEnum.ASSIGN != planType, "计划类型不匹配");
    }



    @Override
    public void verify() {
        super.verify();

        switch (getPlanType()) {
            case DAILY: {
                planOption = null;
                break;
            }

            case WEEK:
            case MONTH:
            case SEASON: {
                Assert.notEmpty(planOption, "计划任务类型选项为空");
                planOption = planOption.stream().distinct().collect(Collectors.toList());
                Set<Integer> options = getPlanType().getOptions().stream().mapToInt(Option::getValue).boxed().collect(Collectors.toSet());
                for (Integer option : planOption) {
                    Assert.isTrue(options.contains(option), "任务类型选项不合法" + getPlanType().getDesc() + "-" + planOption);
                }

                if (PlanEnum.SEASON == getPlanType()) {
                    Assert.isTrue(planOption.stream().anyMatch(o -> o < PlanEnum.TWO_STAGE_OPTION), "每季度请指定具体执行日期");
                }

                break;
            }
            default: break;
        }


        Assert.isTrue(StringUtils.hasText(cycleExeTime), "周期执行时间为空");
        Assert.notNull(DateUtil.parse(cycleExeTime, "HH:mm:ss"), "周期执行时间格式不合法");

        Assert.isTrue(StringUtils.isEmpty(startDateTime) || Objects.nonNull(DateUtil.parseDateTime(startDateTime)), "计划任务开始时间格式不合法");
        Assert.isTrue(StringUtils.isEmpty(endDateTime) || Objects.nonNull(DateUtil.parseDateTime(endDateTime)), "计划任务结束时间格式不合法");
    }

}
