package com.xxl.job.admin.extend.dto;

import com.xxl.job.extend.biz.model.CancelJobParam;
import lombok.Data;

/**
 * @author LiJingTang
 * @date 2023-03-09 16:01
 */
@Data
public class CancelJobParamDTO extends CancelJobParam  {

    private String appName;

}
