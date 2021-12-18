package com.zhangjiashuai.yyetshistory.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.system.SystemUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static com.zhangjiashuai.yyetshistory.config.YyetsHistoryProperties.*;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/info")
    @ResponseBody
    public Map<String, Object> info() {
        long pid;
        try {
            pid = SystemUtil.getCurrentPID();
        } catch (Exception e) {
            pid = UNKNOWN_PID;
        }
        return MapUtil.<String, Object>builder().put(INFO_KEY, APPLICATION_INFO).put(PID_KEY, pid).build();
    }
}
