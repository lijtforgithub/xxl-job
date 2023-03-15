package com.xxl.job.extend.biz.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiJingTang
 * @date 2023-03-09 16:00
 */
@Data
public class CancelJobParam implements Serializable {

    private String jobKey;
    private String jobHandler;

}
