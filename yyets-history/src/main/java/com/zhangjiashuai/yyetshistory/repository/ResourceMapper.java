package com.zhangjiashuai.yyetshistory.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceMapper extends BaseMapper<ResourceDO> {

    LambdaQueryWrapper<ResourceDO> ORDER_BY_NAME_WRAPPER = Wrappers.<ResourceDO>lambdaQuery()
            .orderByAsc(ResourceDO::getName);

    static LambdaQueryWrapper<ResourceDO> queryWrapperByNameLike(String name) {
        return Wrappers.<ResourceDO>lambdaQuery()
                .like(ResourceDO::getName, name).orderByAsc(ResourceDO::getName);
    }

    default long countByNameLike(String name) {
        if (StrUtil.isEmpty(name)) {
            return selectCount(ORDER_BY_NAME_WRAPPER);
        }
        return selectCount(queryWrapperByNameLike(name));
    }

    default List<ResourceDO> selectByNameLike(String name) {
        if (StrUtil.isEmpty(name)) {
            return selectList(ORDER_BY_NAME_WRAPPER);
        }
        return selectList(queryWrapperByNameLike(name));
    }

    default ResourceDO selectOneByName(String name) {
        List<ResourceDO> list = selectList(Wrappers.<ResourceDO>lambdaQuery()
                .eq(ResourceDO::getName, name));
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    default List<ResourceDO> selectAll() {
        return selectList(Wrappers.emptyWrapper());
    }

}
