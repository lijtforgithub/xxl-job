package com.xxl.job.extend.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author LiJingTang
 * @date 2023-03-09 13:38
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(XxlJobExtendRegistrar.class)
public @interface EnableXxlJobExtend {
}
