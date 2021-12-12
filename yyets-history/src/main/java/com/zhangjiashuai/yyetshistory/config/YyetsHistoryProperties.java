package com.zhangjiashuai.yyetshistory.config;

import lombok.Data;

import java.util.LinkedHashSet;

import static com.zhangjiashuai.yyetshistory.repository.impl.ResourceMapper.DEFAULT_PAGE_SIZE;

@Data
public class YyetsHistoryProperties {
    /**
     * 保留的链接类型
     */
    private LinkedHashSet<String> linkWayFilter;

    private int defaultPageSize;

    public int getDefaultPageSize() {
        return defaultPageSize < 1 ? DEFAULT_PAGE_SIZE : defaultPageSize;
    }
}
