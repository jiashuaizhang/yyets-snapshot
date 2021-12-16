package com.zhangjiashuai.yyetshistory;

import com.zhangjiashuai.yyetshistory.util.NativeOperationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YyetsHistoryApplication {

    public static void main(String[] args) {
        if(NativeOperationUtils.onStartPrepare(args)) {
            SpringApplication.run(YyetsHistoryApplication.class, args);
        }
    }

}
