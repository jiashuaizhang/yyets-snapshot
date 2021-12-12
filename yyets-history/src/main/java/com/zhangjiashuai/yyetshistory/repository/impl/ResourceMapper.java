package com.zhangjiashuai.yyetshistory.repository.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;
import com.zhangjiashuai.yyetshistory.repository.ResourceRepository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceMapper extends BaseMapper<ResourceDO>, ResourceRepository {

    static LambdaQueryWrapper<ResourceDO> queryWrapperByNameLike(String name) {
        return Wrappers.<ResourceDO>lambdaQuery()
                .like(ResourceDO::getName, name).orderByAsc(ResourceDO::getName);
    }

    default int countByNameLike(String name) {
        return selectCount(queryWrapperByNameLike(name)).intValue();
    }

    default List<ResourceDO> findByNameLike(String name) {
        return selectList(queryWrapperByNameLike(name));
    }

    default ResourceDO findById(long id) {
        return selectById(id);
    }

    default ResourceDO findOneByName(String name) {
        List<ResourceDO> list = selectList(Wrappers.<ResourceDO>lambdaQuery()
                .eq(ResourceDO::getName, name));
        if(CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    default List<ResourceDO> findAll() {
        return selectList(Wrappers.emptyWrapper());
    }
}
