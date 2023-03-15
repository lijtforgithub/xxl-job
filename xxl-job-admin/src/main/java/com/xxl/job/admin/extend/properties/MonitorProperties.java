package com.xxl.job.admin.extend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author LiJingTang
 * @date 2023-03-13 11:28
 */
@Data
@ConfigurationProperties(prefix = "xxl.job.extend.monitor")
public class MonitorProperties {

    private Boolean openClear = Boolean.TRUE;
    private Integer endJobRetentionDays = 30;

    private Integer statusCheckDays = 1;

    private Integer pageSize = 200;

}
