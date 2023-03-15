package com.xxl.job.extend.biz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author LiJingTang
 * @date 2023-03-09 11:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Option implements Serializable {

    private int value;
    private String desc;

}
