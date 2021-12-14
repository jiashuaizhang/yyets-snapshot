package com.zhangjiashuai.yyetshistory.service;

import com.github.pagehelper.PageInfo;
import com.zhangjiashuai.yyetshistory.entity.Resource;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;

import java.util.List;

public interface ResourceService {

    List<ResourceDO> findByNameLike(String name);

    long countByNameLike(String name);

    Resource parseResource(ResourceDO resourceDO);

    PageInfo<Resource> selectPage(String name, int pageNo, int pageSize);

    PageInfo<Resource> selectPage(String name, int pageNo);

}
