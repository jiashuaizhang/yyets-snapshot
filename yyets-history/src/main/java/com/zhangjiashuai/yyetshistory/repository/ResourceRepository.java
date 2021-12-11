package com.zhangjiashuai.yyetshistory.repository;

import cn.hutool.db.PageResult;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;

import java.util.List;

public interface ResourceRepository {

    ResourceDO findById(long id);

    List<ResourceDO> findByName(String name);

    ResourceDO findOneByName(String name);

    List<ResourceDO> findAll();

    PageResult<ResourceDO> selectPage(String name, int pageNo);

    PageResult<ResourceDO> selectPage(String name, int pageNo, int pageSize);

    long countByNameLike(String name);
}
