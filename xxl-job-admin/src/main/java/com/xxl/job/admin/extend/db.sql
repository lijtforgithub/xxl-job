CREATE TABLE `xxl_plan_job` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `app_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客户端应用名',
    `job_group` int(11) NOT NULL DEFAULT '0' COMMENT '执行器主键ID',
    `job_id` int(11) DEFAULT NULL COMMENT '任务ID（负数表示已结束的计划清除了定时任务和调度日志）',
    `last_fire_time` datetime DEFAULT NULL COMMENT '上次执行时间',
    `next_fire_time` datetime DEFAULT NULL COMMENT '下次执行时间',
    `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
    `cancel_reason` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '取消原因',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `job_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '任务业务编号',
    `job_handler` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '执行器任务handler',
    `job_desc` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务描述',
    `handler_param` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '执行任务参数',
    `plan_type` int(11) NOT NULL DEFAULT '0' COMMENT '计划类型',
    `plan_option` json DEFAULT NULL COMMENT '计划选项',
    `assign_date_time` json DEFAULT NULL COMMENT '指定日期时间（yyyy-MM-dd HH:mm:ss）',
    `cycle_interval` int(11) NOT NULL DEFAULT '0' COMMENT '周期间隔（每2周/每3天）',
    `cycle_exe_time` time DEFAULT NULL COMMENT '周期执行时间',
    `exe_once` tinyint(4) DEFAULT '0' COMMENT '创建后是否立即执行一次 1-是 0-否',
    `start_date_time` datetime DEFAULT NULL COMMENT '计划任务开始时间',
    `end_date_time` datetime DEFAULT NULL COMMENT '计划任务结束时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_key` (`app_name`,`job_handler`,`job_key`),
    UNIQUE KEY `uniq_job_id` (`job_id`) USING BTREE,
    KEY `index_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


ALTER TABLE `xxl_job`.`xxl_job_group`
ADD UNIQUE INDEX `idx_app_name`(`app_name`);

INSERT INTO xxl_job_lock VALUES ("plan_monitor_lock");