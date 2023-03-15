/**
 * @author LiJingTang
 * @date 2023-03-09 16:42
 */
package com.xxl.job.admin.extend;

/**
 *
 * PlanJobMapper.xml
 * com.xxl.job.admin.dao.XxlJobGroupDao#getIdByAppName(java.lang.String)
 *
 * com.xxl.job.admin.controller.JobInfoController#nextTriggerTime(java.lang.Integer, java.lang.String)
 *
 * com.xxl.job.admin.core.thread.JobScheduleHelper#refreshNextValidTime(com.xxl.job.admin.core.model.XxlJobInfo, java.util.Date)
 *
 *
 * jobinfo.index.1.js -> nextTriggerTime -> id
 *
 * # 计划任务参数
 * #xxl.job.extend.monitor.openClear=false
 * xxl.job.extend.monitor.endJobRetentionDays=30
 * xxl.job.extend.monitor.statusCheckDays=1
 * #xxl.job.extend.alarmEmail=lijingtang@youmatech.com
 *
 *      <dependency>
 * 			<groupId>com.xuxueli</groupId>
 * 			<artifactId>xxl-job-extend</artifactId>
 * 			<version>${project.parent.version}</version>
 * 		</dependency>
 * 		<dependency>
 * 			<groupId>org.quartz-scheduler</groupId>
 * 			<artifactId>quartz</artifactId>
 * 			<version>2.3.2</version>
 * 		</dependency>
 * 		<dependency>
 * 			<groupId>org.projectlombok</groupId>
 * 			<artifactId>lombok</artifactId>
 * 			<version>1.18.22</version>
 * 			<scope>compile</scope>
 * 		</dependency>
 *
 */