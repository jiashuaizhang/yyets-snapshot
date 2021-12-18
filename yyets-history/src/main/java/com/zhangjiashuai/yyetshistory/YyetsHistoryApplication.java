package com.zhangjiashuai.yyetshistory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.zhangjiashuai.yyetshistory.util.NativeOperationUtils.onStartPrepare;

@SpringBootApplication
public class YyetsHistoryApplication {

    public static void main(String[] args) {
        onStartPrepare(args);
        SpringApplication.run(YyetsHistoryApplication.class, args);
    }

}
