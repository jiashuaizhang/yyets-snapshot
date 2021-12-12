package com.zhangjiashuai.yyetshistory.config;

import com.zhangjiashuai.yyetshistory.repository.ResourceRepository;
import com.zhangjiashuai.yyetshistory.repository.impl.ResourceRepositoryJdbcImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AppConfig {

    @Bean
    @ConditionalOnMissingClass("org.apache.ibatis.annotations.Mapper")
    public ResourceRepository resourceRepository(JdbcTemplate jdbcTemplate) {
        return new ResourceRepositoryJdbcImpl(jdbcTemplate);
    }

    @Bean
    @ConfigurationProperties(prefix = "yyets-history")
    public YyetsHistoryProperties yyetsHistoryConfig() {
        return new YyetsHistoryProperties();
    }
}
