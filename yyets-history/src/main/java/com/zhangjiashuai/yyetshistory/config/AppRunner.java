package com.zhangjiashuai.yyetshistory.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import static com.zhangjiashuai.yyetshistory.util.NativeOperationUtils.onStartFinish;

@Configuration
public class AppRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        onStartFinish();
    }
}
