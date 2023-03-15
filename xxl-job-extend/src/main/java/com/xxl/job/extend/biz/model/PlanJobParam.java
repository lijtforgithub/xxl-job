package com.xxl.job.extend.biz.model;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.extend.biz.enums.PlanEnum;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-09 11:35
 */
@Data
public abstract class PlanJobParam implements Serializable {

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
    private final PlanEnum planType;


    protected PlanJobParam(PlanEnum planType) {
        this.planType = Objects.requireNonNull(planType, "计划类型不能为空");
    }

    protected PlanJobParam() {
        this.planType = null;
    }


    public void verify() {
        Objects.requireNonNull(jobHandler, "任务处理器【" + jobHandler + "】为空");
        Assert.notNull(XxlJobExecutor.loadJobHandler(jobHandler), "任务处理器【" + jobHandler + "】不存在");

        Objects.requireNonNull(planType, "计划任务类型为空");

        Assert.isTrue(!StringUtils.hasLength(handlerParam) || handlerParam.length() <= 512, "任务处理参数最大长度支持512");

        if (Objects.nonNull(jobDesc) && jobDesc.length() > 200) {
            jobDesc = jobDesc.substring(0, 200);
        }

        Assert.isTrue(Objects.isNull(jobKey) || jobKey.length() <= 50, "任务唯一编号最大长度支持50");
    }

}
