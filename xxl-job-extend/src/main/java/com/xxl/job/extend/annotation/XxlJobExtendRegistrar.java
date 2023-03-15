package com.xxl.job.extend.annotation;

import com.xxl.job.extend.config.XxlJobExtendConfig;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author LiJingTang
 * @date 2023-03-09 13:38
 */
class XxlJobExtendRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition("xxl_job_extend_config", new RootBeanDefinition(XxlJobExtendConfig.class));
    }

}
