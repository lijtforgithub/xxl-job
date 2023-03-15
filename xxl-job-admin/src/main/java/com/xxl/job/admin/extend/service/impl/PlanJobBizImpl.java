package com.xxl.job.admin.extend.service.impl;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.extend.dao.PlanJobDao;
import com.xxl.job.admin.extend.dto.CancelJobParamDTO;
import com.xxl.job.admin.extend.dto.PlanJobParamDTO;
import com.xxl.job.admin.extend.enums.PlanJobStatusEnum;
import com.xxl.job.admin.extend.event.PlanJobEndEvent;
import com.xxl.job.admin.extend.helper.PlanHelper;
import com.xxl.job.admin.extend.helper.PlanService;
import com.xxl.job.admin.extend.model.PlanJob;
import com.xxl.job.admin.extend.properties.MonitorProperties;
import com.xxl.job.admin.extend.service.PlanJobService;
import com.xxl.job.admin.extend.util.ExtendUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.extend.biz.PlanJobBiz;
import com.xxl.job.extend.biz.enums.PlanEnum;
import com.xxl.job.extend.biz.model.CancelJobParam;
import com.xxl.job.extend.biz.model.Option;
import com.xxl.job.extend.biz.model.PlanJobParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.xxl.job.admin.core.thread.JobScheduleHelper.PRE_READ_MS;

/**
 * @author LiJingTang
 * @date 2023-03-09 14:46
 */
@Slf4j
@Service
public class PlanJobBizImpl implements PlanJobBiz, PlanJobService {

    @Value("${xxl.job.extend.alarmEmail:}")
    private String alarmEmail;
    @Autowired
    private MonitorProperties monitorProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PlanJobDao planJobDao;
    @Autowired
    private XxlJobGroupDao xxlJobGroupDao;
    @Autowired
    private XxlJobInfoDao xxlJobInfoDao;
    @Autowired
    private XxlJobLogDao xxlJobLogDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> addPlan(PlanJobParam param) {
        PlanJobParamDTO paramDTO = (PlanJobParamDTO) param;
        if (StringUtils.hasLength(paramDTO.getJobKey())) {
            Long id = planJobDao.selectByKey(paramDTO.getAppName(), paramDTO.getJobHandler(), paramDTO.getJobKey());
            Assert.isNull(id, String.format("计划任务【%s:%s】已存在", paramDTO.getJobHandler(), paramDTO.getJobKey()));
        } else {
            paramDTO.setJobKey(UUID.randomUUID().toString());
        }

        PlanService planService = PlanHelper.checkAndGetPlanService(paramDTO.getPlanType().getValue());
        Integer jobGroup = xxlJobGroupDao.getIdByAppName(paramDTO.getAppName());
        Assert.notNull(jobGroup, "执行器【" + paramDTO.getAppName() + "】不存在");

        PlanJob planJob = assemblePlanJob(paramDTO, jobGroup);
        // 校验参数和设置一些默认值 很重要 顺序不能调整
        planService.checkAndInitPlan(planJob);
        Date nextFireTime = Boolean.TRUE.equals(planJob.getExeOnce()) ? new Date(System.currentTimeMillis() + PRE_READ_MS) : planService.getNextFireTime(planJob);
        Integer jobId = null;

        if (Objects.isNull(nextFireTime)) {
            log.warn("计划任务【{}:{}:{}】已结束", paramDTO.getAppName(), paramDTO.getJobHandler(), paramDTO.getJobKey());
        } else {
            planJob.setNextFireTime(nextFireTime);
            jobId = saveJobInfo(planJob);
            planJob.setStatus(PlanJobStatusEnum.SYNC_JOB.getValue());
        }

        planJob.setJobId(jobId);
        planJobDao.insert(planJob);

        log.info("保存一条计划任务：{}", GsonTool.toJson(planJob));

        return new ReturnT<>(planJob.getId().toString());
    }

    private Integer saveJobInfo(PlanJob planJob) {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setJobGroup(planJob.getJobGroup());
        jobInfo.setJobCron(ExtendUtils.PLAN_JOB_FLAG);
        jobInfo.setJobDesc(getJobDesc(planJob));
        jobInfo.setAddTime(planJob.getCreateTime());
        jobInfo.setAuthor(ExtendUtils.PLAN_JOB_AUTH);
        jobInfo.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.CONSISTENT_HASH.name());
        jobInfo.setExecutorHandler(planJob.getJobHandler());
        jobInfo.setExecutorParam(planJob.getHandlerParam());
        jobInfo.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name());
        jobInfo.setExecutorTimeout(0);
        jobInfo.setExecutorFailRetryCount(1);
        jobInfo.setGlueType(GlueTypeEnum.BEAN.name());
        jobInfo.setTriggerStatus(1);
        jobInfo.setTriggerLastTime(0);
        jobInfo.setTriggerNextTime(planJob.getNextFireTime().getTime());
        jobInfo.setGlueUpdatetime(planJob.getCreateTime());
        if (StringUtils.hasLength(alarmEmail)) {
            jobInfo.setAlarmEmail(alarmEmail);
        }
        xxlJobInfoDao.save(jobInfo);
        return jobInfo.getId();
    }

    private PlanJob assemblePlanJob(PlanJobParamDTO paramDTO, Integer jobGroup) {
        PlanJob planJob = new PlanJob();
        planJob.setAppName(paramDTO.getAppName());
        planJob.setJobGroup(jobGroup);
        planJob.setCreateTime(new Date());
        planJob.setUpdateTime(planJob.getCreateTime());
        planJob.setJobKey(paramDTO.getJobKey());
        planJob.setJobHandler(paramDTO.getJobHandler());
        planJob.setJobDesc(trimNull(paramDTO.getJobDesc()));
        planJob.setHandlerParam(trimNull(paramDTO.getHandlerParam()));
        planJob.setPlanType(paramDTO.getPlanType().getValue());
        planJob.setPlanOptionList(paramDTO.getPlanOption());
        planJob.setAssignDateTimeList(paramDTO.getAssignDateTimeList());
        planJob.setCycleInterval(paramDTO.getCycleInterval());
        planJob.setCycleExeTime(parseTime(paramDTO.getCycleExeTime()));
        planJob.setExeOnce(paramDTO.getExeOnce());
        planJob.setStartDateTime(parseDateTime(paramDTO.getStartDateTime()));
        planJob.setEndDateTime(parseDateTime(paramDTO.getEndDateTime()));
        return planJob;
    }

    private String getJobDesc(PlanJob planJob) {
        if (StringUtils.hasLength(planJob.getJobDesc())) {
            return planJob.getJobDesc();
        }

        PlanEnum planEnum = PlanEnum.match(planJob.getPlanType());
        if (PlanEnum.ASSIGN == planEnum) {
            return String.format("%s[%s]", planEnum.getDesc(), planJob.getJobKey());
        } else {
            String desc = planJob.getCycleInterval() > 1 ? planEnum.getDesc().replace("每", "每" + planJob.getCycleInterval()) : planEnum.getDesc();
            StringBuilder strBuilder = new StringBuilder("");
            if (Objects.nonNull(planJob.getPlanOptionList())) {
                Integer lastDay = null;
                List<Integer> twoOptions = new ArrayList<>();
                List<Integer> options = new ArrayList<>();

                for (Integer o : planJob.getPlanOptionList()) {
                    if (o < 0) {
                        lastDay = o;
                    } else if (o > PlanEnum.TWO_STAGE_OPTION) {
                        twoOptions.add(o);
                    } else {
                        options.add(o);
                    }
                }

                if (Objects.nonNull(lastDay)) {
                    options.add(lastDay);
                }

                if (!twoOptions.isEmpty()) {
                    strBuilder.append(twoOptions.stream().map(planEnum::matchOption).filter(Objects::nonNull).map(Option::getDesc).collect(Collectors.joining("/")));
                }

                if (!options.isEmpty()) {
                    strBuilder.append(options.stream().map(planEnum::matchOption).filter(Objects::nonNull).map(Option::getDesc).collect(Collectors.joining("/")));
                }
            }

            desc = String.format("%s%s%s[%s]", desc, strBuilder, planJob.getCycleExeTime(), planJob.getJobKey());
            if (desc.length() > 255) {
                desc = desc.substring(0, 255);
            }

            return desc;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> cancelPlanById(String planId) {
        PlanJob planJob = planJobDao.selectById(Long.valueOf(planId));
        Assert.notNull(planJob, String.format("计划任务【%s】不存在", planId));

        if (PlanJobStatusEnum.CANCEL.getValue() != planJob.getStatus()) {
            PlanJob temp = new PlanJob();
            temp.setId(planJob.getId());
            temp.setStatus(PlanJobStatusEnum.CANCEL.getValue());
            temp.setCancelReason("API接口取消");
            planJobDao.updateById(temp);

            if (Objects.nonNull(planJob.getJobId())) {
                xxlJobInfoDao.delete(planJob.getJobId());
                log.info("计划任务【{}】取消 删除定时任务【{}】", planId, planJob.getJobId());
            }
        }

        return new ReturnT<>(planJob.getJobKey());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> cancelPlanByJobKey(CancelJobParam param) {
        CancelJobParamDTO paramDTO = (CancelJobParamDTO) param;
        Long planId = planJobDao.selectByKey(paramDTO.getAppName(), paramDTO.getJobHandler(), paramDTO.getJobKey());
        Assert.notNull(planId, String.format("计划任务【%s:%s:%s】不存在", paramDTO.getAppName(), paramDTO.getJobHandler(), paramDTO.getJobKey()));

        String planIdStr = planId.toString();
        cancelPlanById(planIdStr);
        return new ReturnT<>(planIdStr);
    }

    private String trimNull(String str) {
        return Objects.isNull(str) ? "" : str;
    }

    private Date parseDateTime(String date) {
        return StringUtils.hasLength(date) ? DateUtil.parseDateTime(date) : null;
    }

    private LocalTime parseTime(String time) {
        return StringUtils.hasLength(time) ? LocalTime.parse(time, ExtendUtils.TIME_FORMATTER) : null;
    }


    @Override
    public Date getTriggerNextTime(Integer jobId, Date fromTime, boolean isSchedule) {
        PlanJob planJob = planJobDao.selectByJobId(jobId);
        if (Objects.isNull(planJob)) {
            return null;
        }

        PlanService planService = PlanHelper.planService(planJob.getPlanType());
        if (Objects.isNull(planService)) {
            log.warn("计划类型【{}】不合法", planJob.getPlanType());
            return null;
        }

        planJob.setLastFireTime(fromTime);

        Date nextFireTime = planService.getNextFireTime(planJob);
        if (isSchedule) {
            PlanJob temp = new PlanJob();
            temp.setId(planJob.getId());
            temp.setStatus(PlanJobStatusEnum.RUNNING.getValue());
            temp.setLastFireTime(planJob.getLastFireTime());
            temp.setNextFireTime(nextFireTime);
            planJobDao.updateById(temp);
        }

        if (Objects.isNull(nextFireTime) && !PlanJobStatusEnum.isEnd(planJob.getStatus())) {
            // 发送计划任务结束事件
            applicationContext.publishEvent(new PlanJobEndEvent(planJob.getId()));
        }

        log.info("计划任务【{}】获取下次执行时间【{}】isSchedule={}", planJob.getId(), nextFireTime, isSchedule);
        return nextFireTime;
    }

    @Override
    public void endPlan(Long planId) {
        PlanJob planJob = planJobDao.selectById(planId);
        if (Objects.isNull(planJob)) {
            return;
        }

        if (!PlanJobStatusEnum.isEnd(planJob.getStatus())
                && PlanHelper.checkAndGetPlanService(planJob.getPlanType()).isEnd(planJob)) {
            PlanJob temp = new PlanJob();
            temp.setId(planId);
            temp.setStatus(PlanJobStatusEnum.END.getValue());
            planJobDao.updateById(temp);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void monitor() {
        planJobDao.monitorLock();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxUpdateTime = now.minusDays(monitorProperties.getStatusCheckDays());

        LocalDateTime minUpdateTime = maxUpdateTime.minusDays(2).toLocalDate().atStartOfDay();
        List<PlanJob> planList = planJobDao.selectMonitorPage(minUpdateTime, maxUpdateTime, monitorProperties.getPageSize(), 1);

        for (PlanJob planJob : planList) {
            try {
                PlanJob temp = testPlan(planJob);
                if (Objects.nonNull(temp)) {
                    planJobDao.updateById(temp);
                }
            } catch (Exception e) {
                log.error("监控计划状态任务" + planJob.getId(), e);
            }
        }

        if (Boolean.TRUE.equals(monitorProperties.getOpenClear())) {
            maxUpdateTime = now.minusDays(monitorProperties.getEndJobRetentionDays());
            minUpdateTime = maxUpdateTime.minusDays(2).toLocalDate().atStartOfDay();
            planList = planJobDao.selectMonitorPage(minUpdateTime, maxUpdateTime, monitorProperties.getPageSize(), 2);

            try {
                for (PlanJob planJob : planList) {
                    xxlJobInfoDao.delete(planJob.getJobId());
                    xxlJobLogDao.delete(planJob.getJobId());
                    PlanJob temp = new PlanJob();
                    temp.setId(planJob.getId());
                    temp.setJobId(-planJob.getJobId());
                    planJobDao.updateById(temp);
                    log.info("计划任务【{}=>{}】删除定时任务和调度日志", planJob.getId(), planJob.getJobId());
                }
            } catch (Exception e) {
                log.error("监控计划清除结束任务", e);
            }
        }
    }

    private PlanJob testPlan(PlanJob planJob) {
        PlanJobStatusEnum statusEnum = PlanJobStatusEnum.match(planJob.getStatus());

        if (Objects.isNull(statusEnum)) {
            return null;
        }

        switch (statusEnum) {
            case CREATE:
            case SYNC_JOB:
            case RUNNING:
                return toEnd(planJob);
            case CANCEL:
                return cancel(planJob);
        }

        return null;
    }

    private PlanJob cancel(PlanJob planJob) {
        // 计划取消时 删除了定时任务 日志没有删
        if (Objects.nonNull(planJob.getJobId())) {
            PlanJob temp = new PlanJob();
            temp.setId(planJob.getId());
            temp.setJobId(0);
            xxlJobLogDao.delete(planJob.getJobId());
            log.info("计划任务【{}=>{}】删除调度日志", planJob.getId(), planJob.getJobId());
            return temp;
        }

        return null;
    }

    private static PlanJob toEnd(PlanJob planJob) {
        // 防止定时任务已停止 计划未结束情况
        boolean end = PlanHelper.checkAndGetPlanService(planJob.getPlanType()).isEnd(planJob);
        if (end) {
            PlanJob temp = new PlanJob();
            temp.setId(planJob.getId());
            temp.setStatus(PlanJobStatusEnum.END.getValue());
            log.info("计划任务【{}】满足结束条件", planJob.getId());
            return temp;
        }

        return null;
    }

}
