package com.zhangjiashuai.yyetshistory.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@Slf4j
@Configuration
@EnableSpringUtil
public class AppConfig {

    @Bean
    @ConfigurationProperties(prefix = "yyets-history")
    public YyetsHistoryProperties yyetsHistoryConfig() {
        return new YyetsHistoryProperties();
    }

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        uncompressDbFile();
        return DataSourceBuilder.create().driverClassName(properties.getDriverClassName())
                .url(properties.getUrl()).build();
    }

    private void uncompressDbFile() {
        URL dbResource = getClass().getResource("/static/yyets.db");
        if (dbResource != null) {
            log.info("检测到数据源文件: {}", URLDecoder.decode(dbResource.getPath(), StandardCharsets.UTF_8));
            return;
        }
        final String moduleName = "/yyets-history";
        String projectPath = System.getProperty("user.dir");
        projectPath = projectPath.replaceAll("\\\\", "/");
        if (!projectPath.endsWith(moduleName)) {
            projectPath = projectPath + moduleName;
        }
        String staticResourcePath = projectPath + "/src/main/resources/static/";
        File dbFile = new File(staticResourcePath + "yyets.db");
        if (dbFile.exists()) {
            throw new IllegalStateException("请重新编译项目");
        }
        String zipFilePath = staticResourcePath + "yyets.zip";
        File zipFile = new File(staticResourcePath + "yyets.zip");
        if (!zipFile.exists()) {
            throw new IORuntimeException("数据源文件缺失并且无法找到压缩包: " + zipFilePath);
        }
        log.info("开始解压数据源文件: {}", zipFilePath);
        try {
            ZipUtil.unzip(zipFile, new File(staticResourcePath));
            if (dbFile.exists()) {
                log.info("数据源文件自动解压完成: {}", dbFile.getPath());
                String classPath = getClass().getResource("/static").getPath();
                classPath = URLDecoder.decode(classPath, StandardCharsets.UTF_8);
                FileUtil.copyFile(dbFile, new File(classPath));
                log.info("复制数据源文件至classpath: {}", classPath);
            } else {
                throw new IORuntimeException("未知的解压异常: " + zipFilePath);
            }
        } catch (Exception e) {
            String msg = "数据源文件自动解压失败，请手动解压文件: " + zipFilePath;
            throw new IORuntimeException(msg, e);
        }
    }
}
