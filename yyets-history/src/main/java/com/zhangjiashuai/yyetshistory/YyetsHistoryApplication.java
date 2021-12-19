package com.zhangjiashuai.yyetshistory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.zhangjiashuai.yyetshistory.util.NativeOperationUtils.onStartFinish;
import static com.zhangjiashuai.yyetshistory.util.NativeOperationUtils.onStartPrepare;

@SpringBootApplication
public class YyetsHistoryApplication {

    public static void main(String[] args) {
        onStartPrepare(args);
        SpringApplication.run(YyetsHistoryApplication.class, args);
        onStartFinish(args);
    }

}
