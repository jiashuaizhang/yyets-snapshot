package com.zhangjiashuai.yyetshistory.service;

import cn.hutool.db.PageResult;
import com.zhangjiashuai.yyetshistory.entity.Resource;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;

import java.util.List;
import java.util.stream.Collectors;

public interface ResourceService {

    ResourceDO findById(long id);

    List<ResourceDO> findByName(String name);

    ResourceDO findOneByName(String name);

    List<ResourceDO> findAll();

    Resource parseResource(ResourceDO resourceDO);

    Resource getResourceByName(String name);

    Resource getResourceById(long id);

    List<Resource> getResourcesByName(String name);

    List<Resource> getAllResources();

    PageResult<ResourceDO> selectDOPage(String name, int pageNo);

    default PageResult<Resource> selectPage(String name, int pageNo) {
        PageResult<ResourceDO> doPageResult = selectDOPage(name, pageNo);
        PageResult<Resource> pageResult = new PageResult<>(doPageResult.getPage(), doPageResult.getPageSize(), doPageResult.getTotal());
        doPageResult.stream().map(this::parseResource).forEach(pageResult::add);
        return pageResult;
    }

}
