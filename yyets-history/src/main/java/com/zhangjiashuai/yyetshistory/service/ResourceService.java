package com.zhangjiashuai.yyetshistory.service;

import com.github.pagehelper.PageInfo;
import com.zhangjiashuai.yyetshistory.entity.Resource;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;

import java.util.List;

import static com.zhangjiashuai.yyetshistory.config.YyetsHistoryProperties.DEFAULT_PAGE_SIZE;

public interface ResourceService {

    List<ResourceDO> findByNameLike(String name);

    long countByNameLike(String name);

    Resource parseResource(ResourceDO resourceDO);

    PageInfo<Resource> selectPage(String name, int pageNo, int pageSize);

    default PageInfo<Resource> selectPage(String name, int pageNo) {
        return selectPage(name, pageNo, DEFAULT_PAGE_SIZE);
    }

}
