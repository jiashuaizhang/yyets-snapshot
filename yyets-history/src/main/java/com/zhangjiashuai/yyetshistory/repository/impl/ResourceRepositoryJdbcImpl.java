package com.zhangjiashuai.yyetshistory.repository.impl;

import cn.hutool.db.PageResult;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;
import com.zhangjiashuai.yyetshistory.repository.ResourceRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class ResourceRepositoryJdbcImpl implements ResourceRepository {

    private static final String BASE_SQL = "SELECT ID, URL, NAME, EXPIRE, EXPIRE_CST, DATA FROM resource";
    private static final String FIND_BY_ID_SQL = BASE_SQL + " WHERE ID = ?";
    private static final String FIND_BY_NAME_SQL = BASE_SQL + " WHERE NAME like ?";
    private static final String FIND_ONE_BY_NAME_SQL = BASE_SQL + " WHERE NAME = ?";

    private JdbcTemplate jdbcTemplate;

    public ResourceRepositoryJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ResourceDO findById(long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, new BeanPropertyRowMapper<>(ResourceDO.class), id);
    }

    @Override
    public List<ResourceDO> findByName(String name) {
        String nameFilter = "%" + name + "%";
        return jdbcTemplate.query(FIND_BY_NAME_SQL, new BeanPropertyRowMapper<>(ResourceDO.class), nameFilter);
    }

    @Override
    public ResourceDO findOneByName(String name) {
        List<ResourceDO> list = jdbcTemplate.query(FIND_ONE_BY_NAME_SQL, new BeanPropertyRowMapper<>(ResourceDO.class), name);
        if(list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<ResourceDO> findAll() {
        return jdbcTemplate.query(BASE_SQL, new BeanPropertyRowMapper<>(ResourceDO.class));
    }

    @Override
    public PageResult<ResourceDO> selectPage(String name, int pageNo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PageResult<ResourceDO> selectPage(String name, int pageNo, int pageSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countByNameLike(String name) {
        throw new UnsupportedOperationException();
    }
}
