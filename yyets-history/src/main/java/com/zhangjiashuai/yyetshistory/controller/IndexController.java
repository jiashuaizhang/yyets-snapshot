package com.zhangjiashuai.yyetshistory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.zhangjiashuai.yyetshistory.config.YyetsHistoryProperties.APPLICATION_INFO;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/info")
    @ResponseBody
    public String info() {
        return APPLICATION_INFO;
    }
}
