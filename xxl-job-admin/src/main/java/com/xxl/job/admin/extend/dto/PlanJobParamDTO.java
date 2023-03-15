package com.xxl.job.admin.extend.dto;

import com.xxl.job.extend.biz.model.PlanJobParam;
import lombok.Data;

import java.util.List;

/**
 * @author LiJingTang
 * @date 2023-03-09 14:50
 */
@Data
public class PlanJobParamDTO extends PlanJobParam {

    private String appName;


    /**
     * 指定日期时间 （yyyy-MM-dd HH:mm:ss） 必填
     */
    private List<String> assignDateTimeList;


    /**
     * 计划类型选项 WEEK/MONTH 必填
     */
    private List<Integer> planOption;
    /**
     * 周期间隔 DAILY/WEEK/MONTH 必填
     */
    private Integer cycleInterval;
    /**
     * 周期执行时间（HH:mm:ss） DAILY/WEEK/MONTH 必填
     */
    private String cycleExeTime;
    /**
     * 创建后是否立即执行一次
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

}
