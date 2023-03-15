package com.xxl.job.admin.extend.util;

import java.time.format.DateTimeFormatter;

/**
 * @author LiJingTang
 * @date 2023-03-09 17:12
 */
public class ExtendUtils {

    private ExtendUtils() {}


    public static final String PLAN_JOB_FLAG = "PLAN-JOB";
    public static final String PLAN_JOB_AUTH = "API接口创建";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");


}
