package com.zhangjiashuai.yyetshistory.config;

import lombok.Data;

import java.util.LinkedHashSet;

@Data
public class YyetsHistoryProperties {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String APPLICATION_INFO = "yyets-history";

    /**
     * 保留的链接类型
     */
    private LinkedHashSet<String> linkWayFilter;
    /**
     * 默认每页大小
     */
    private int defaultPageSize;
    /**
     * 域名
     */
    private String host;

    public int getDefaultPageSize() {
        return defaultPageSize < 1 ? DEFAULT_PAGE_SIZE : defaultPageSize;
    }
}
