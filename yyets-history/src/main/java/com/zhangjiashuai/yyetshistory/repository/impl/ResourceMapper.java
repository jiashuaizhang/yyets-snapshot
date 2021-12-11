package com.zhangjiashuai.yyetshistory.repository.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;
import com.zhangjiashuai.yyetshistory.repository.ResourceRepository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceMapper extends BaseMapper<ResourceDO>, ResourceRepository {

    int DEFAULT_PAGE_SIZE = 20;

    default PageResult<ResourceDO> selectPage(String name, int pageNo) {
        return selectPage(name, pageNo, DEFAULT_PAGE_SIZE);
    }

    default PageResult<ResourceDO> selectPage(String name, int pageNo, int pageSize) {
        long count = countByNameLike(name);
        PageResult<ResourceDO> pageResult = new PageResult<>(pageNo, pageSize, (int)count);
        if(count == 0) {
            return pageResult;
        }
        List<ResourceDO> list = selectByNameLike(name);
        List<ResourceDO> pageList = ListUtil.page(pageNo - 1, pageSize, list);
        pageResult.addAll(pageList);
        return pageResult;
    }

    default long countByNameLike(String name) {
        return selectCount(queryWrapperByNameLike(name));
    }

    default List<ResourceDO> selectByNameLike(String name) {
        return selectList(queryWrapperByNameLike(name));
    }

    default LambdaQueryWrapper<ResourceDO> queryWrapperByNameLike(String name) {
        return Wrappers.<ResourceDO>lambdaQuery()
                .like(ResourceDO::getName, name).orderByAsc(ResourceDO::getName);
    }

    default ResourceDO findById(long id) {
        return selectById(id);
    }

    default List<ResourceDO> findByName(String name) {
        return selectList(Wrappers.<ResourceDO>lambdaQuery()
                .eq(ResourceDO::getName, name));
    }

    default ResourceDO findOneByName(String name) {
        return selectOne(Wrappers.<ResourceDO>lambdaQuery()
                .eq(ResourceDO::getName, name));
    }

    default List<ResourceDO> findAll() {
        return selectList(Wrappers.emptyWrapper());
    }
}
