package com.zhangjiashuai.yyetshistory.controller;

import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import com.zhangjiashuai.yyetshistory.entity.Resource;
import com.zhangjiashuai.yyetshistory.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/resource")
public class ResourceController {

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    private ResourceService resourceService;

    @GetMapping("/page/{pageNo}/{name}")
    public PageResult<Resource> selectPage(@PathVariable("name") String name,
                                           @PathVariable("pageNo") int pageNo) {
        PageResult<Resource> resources = resourceService.selectPage(name, pageNo);
        log.info("查询成功, name: {}, pageNo: {}, pageSize: {}, total: {}",
                name, pageNo, resources.getPageSize(), resources.getTotal());
        return resources;
    }
}
