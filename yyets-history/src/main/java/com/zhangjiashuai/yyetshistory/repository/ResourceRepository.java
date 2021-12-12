package com.zhangjiashuai.yyetshistory.repository;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.db.PageResult;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;

import java.util.List;

public interface ResourceRepository {

    int DEFAULT_PAGE_SIZE = 20;

    ResourceDO findById(long id);

    List<ResourceDO> findByNameLike(String name);

    ResourceDO findOneByName(String name);

    List<ResourceDO> findAll();

    int countByNameLike(String name);

    default PageResult<ResourceDO> selectPage(String name, int pageNo) {
        return selectPage(name, pageNo, DEFAULT_PAGE_SIZE);
    }

    default PageResult<ResourceDO> selectPage(String name, int pageNo, int pageSize) {
        int count = countByNameLike(name);
        PageResult<ResourceDO> pageResult = new PageResult<>(pageNo, pageSize, count);
        if(count == 0) {
            return pageResult;
        }
        List<ResourceDO> list = findByNameLike(name);
        List<ResourceDO> pageList = ListUtil.page(pageNo - 1, pageSize, list);
        pageResult.addAll(pageList);
        return pageResult;
    }
}
