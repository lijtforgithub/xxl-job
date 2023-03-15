package com.xxl.job.admin.extend.model;

import com.xxl.job.core.util.GsonTool;
import lombok.Data;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2023-03-09 14:27
 */
@Data
public class PlanJob {

    private Long id;

    private String appName;

    private Integer jobGroup;

    private Integer jobId;

    private Date createTime;

    private Date updateTime;

    private Date lastFireTime;

    private Date nextFireTime;

    private Integer status;

    private String cancelReason;


    /**
     * 任务唯一编号（最长50） 处理器+编号 用来确定唯一性默认使用UUID
     */
    private String jobKey;
    /**
     * 任务处理器 必填
     */
    private String jobHandler;
    /**
     * 任务描述 非必填
     */
    private String jobDesc;
    /**
     * 任务处理参数 非必填
     */
    private String handlerParam;

    /**
     * 计划类型 必填
     */
    private Integer planType;
    /**
     * 计划类型选项 WEEK/MONTH 必填
     */
    private String planOption;
    private transient List<Integer> planOptionList;


    /**
     * 指定日期时间 DATE 必填
     */
    private String assignDateTime;
    private transient List<String> assignDateTimeList;
    /**
     * 周期间隔 DAILY/WEEK/MONTH 必填
     */
    private Integer cycleInterval;
    /**
     * 周期执行时间 DAILY/WEEK/MONTH 必填
     */
    private LocalTime cycleExeTime;
    /**
     * 创建后是否立即执行一次
     */
    private Boolean exeOnce;


    /**
     * 计划任务开始时间 非必填
     */
    private Date startDateTime;
    /**
     * 计划任务结束时间 非必填
     */
    private Date endDateTime;

    public void setAssignDateTimeList(List<String> assignDateTimeList) {
        this.assignDateTimeList = assignDateTimeList;
        if (Objects.nonNull(this.assignDateTimeList)) {
            Collections.sort(this.assignDateTimeList);
            this.assignDateTime = GsonTool.toJson(this.assignDateTimeList);
        }
    }

    public void setAssignDateTime(String assignDateTime) {
        this.assignDateTime = assignDateTime;
        if (Objects.nonNull(this.assignDateTime)) {
            this.assignDateTimeList = GsonTool.fromJsonList(this.assignDateTime, String.class);
            Collections.sort(this.assignDateTimeList);
        }
    }

    public void setPlanOption(String planOption) {
        this.planOption = planOption;
        if (Objects.nonNull(this.planOption)) {
            this.planOptionList = GsonTool.fromJsonList(this.planOption, Double.class).stream().map(Double::intValue).sorted().collect(Collectors.toList());
        }
    }

    public void setPlanOptionList(List<Integer> planOptionList) {
        this.planOptionList = planOptionList;
        if (Objects.nonNull(this.planOptionList)) {
            Collections.sort(this.planOptionList);
            this.planOption = GsonTool.toJson(this.planOptionList);
        }
    }

}
