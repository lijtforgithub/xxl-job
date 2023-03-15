package com.xxl.job.extend.biz.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiJingTang
 * @date 2023-03-09 13:04
 */
@Data
public class AdminInfo implements Serializable {

    private String addressUrl ;
    private String accessToken;
    private int timeout = 3;

}
