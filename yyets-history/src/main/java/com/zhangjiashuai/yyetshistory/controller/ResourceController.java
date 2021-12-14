package com.zhangjiashuai.yyetshistory.controller;

import com.github.pagehelper.PageInfo;
import com.zhangjiashuai.yyetshistory.entity.Resource;
import com.zhangjiashuai.yyetshistory.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/page")
    public PageInfo<Resource> selectPage(String name, int pageNo) {
        PageInfo<Resource> resources = resourceService.selectPage(name, pageNo);
        log.info("查询成功, name: {}, pageNo: {}, pageSize: {}, total: {}",
                name, pageNo, resources.getPageSize(), resources.getTotal());
        return resources;
    }
}
