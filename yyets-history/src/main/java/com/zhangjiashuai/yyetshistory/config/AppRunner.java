package com.zhangjiashuai.yyetshistory.config;

import com.zhangjiashuai.yyetshistory.util.NativeOperationUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        NativeOperationUtils.onStartFinish();
    }

}
