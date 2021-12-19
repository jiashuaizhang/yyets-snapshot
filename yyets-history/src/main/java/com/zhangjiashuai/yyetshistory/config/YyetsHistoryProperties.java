package com.zhangjiashuai.yyetshistory.config;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Data
public class YyetsHistoryProperties {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String APPLICATION_INFO = "yyets-history";

    public static final String INFO_KEY = "info";
    public static final String PID_KEY = "pid";
    public static final long UNKNOWN_PID = -1L;

    public static final String DEFAULT_HOST = "localhost";
    public static final Set<String> DEFAULT_LINK_WAY_FILTER =
            Collections.unmodifiableSet(CollectionUtil.newHashSet("电驴", "磁力"));

    /**
     * 保留的链接类型
     */
    private Set<String> linkWayFilter = DEFAULT_LINK_WAY_FILTER;

    /**
     * 域名
     */
    private String host = DEFAULT_HOST;

    /**
     * 是否触发启动前置事件
     */
    private boolean startPrepareEvent = true;

    /**
     * 是否触发启动成功事件
     */
    private boolean startFinishEvent = true;

}
