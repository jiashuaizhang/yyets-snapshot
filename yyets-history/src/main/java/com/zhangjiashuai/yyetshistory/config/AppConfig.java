package com.zhangjiashuai.yyetshistory.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.extra.compress.extractor.SevenZExtractor;
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
import java.nio.file.StandardCopyOption;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    @ConfigurationProperties(prefix = "yyets-history")
    public YyetsHistoryProperties yyetsHistoryConfig() {
        return new YyetsHistoryProperties();
    }

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        extractDb7zFile();
        return DataSourceBuilder.create().driverClassName(properties.getDriverClassName())
                .url(properties.getUrl()).build();
    }

    private void extractDb7zFile() {
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
        String dbFilePath = staticResourcePath + "yyets.db";
        File dbFile = new File(dbFilePath);
        if (dbFile.exists()) {
            throw new IllegalStateException("请重新编译项目");
        }
        String db7zFilePath = staticResourcePath + "yyets.7z";
        File db7zFile = new File(db7zFilePath);
        if (!db7zFile.exists()) {
            throw new IORuntimeException("数据源文件缺失并且无法找到压缩包: " + db7zFilePath);
        }
        log.info("开始解压数据源文件: {}", db7zFilePath);
        try (SevenZExtractor sevenZExtractor = new SevenZExtractor(db7zFile)) {
            sevenZExtractor.extract(new File(staticResourcePath));
            if (dbFile.exists()) {
                log.info("数据源文件自动解压完成: {}", dbFilePath);
                String classPath = getClass().getResource("/static").getPath();
                classPath = URLDecoder.decode(classPath, StandardCharsets.UTF_8);
                FileUtil.copyFile(dbFile, new File(classPath), StandardCopyOption.REPLACE_EXISTING);
                log.info("复制数据源文件至classpath: {}", classPath);
            } else {
                throw new IORuntimeException("未知的解压异常: " + db7zFilePath);
            }
        } catch (Exception e) {
            String msg = "数据源文件自动解压失败，请手动解压文件: resources/static/yyets.7z";
            throw new IORuntimeException(msg, e);
        }
    }
}
