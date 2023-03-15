package com.xxl.job.admin.extend.dao;

import com.xxl.job.admin.extend.model.PlanJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LiJingTang
 * @date 2023-03-09 16:05
 */
@Mapper
public interface PlanJobDao {

    int insert(PlanJob planJob);

    int updateById(PlanJob planJob);

    PlanJob selectById(Long id);

    PlanJob selectByJobId(Integer jobId);

    Long selectByKey(@Param("appName") String appName, @Param("jobHandler") String jobHandler, @Param("jobKey") String jobKey);

    List<PlanJob> selectMonitorPage(@Param("minUpdateTime") LocalDateTime minUpdateTime, @Param("maxUpdateTime") LocalDateTime maxUpdateTime,
                                    @Param("pageSize") int pageSize, @Param("type") int type);

    String monitorLock();

}
