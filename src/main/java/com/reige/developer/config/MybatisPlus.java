package com.reige.developer.config;

import com.reige.developer.common.mybatis.PerformanceInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenrui reige3@gmail.com
 * @date 2018/12/2.
 */
@Configuration
public class MybatisPlus {
    /**
     * 性能分析拦截器，不建议生产使用
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor(){
        return new PerformanceInterceptor();
    }

}
