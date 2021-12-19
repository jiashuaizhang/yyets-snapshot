package com.zhangjiashuai.yyetshistory;

import cn.hutool.core.lang.Assert;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangjiashuai.yyetshistory.entity.Resource;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;
import com.zhangjiashuai.yyetshistory.repository.ResourceMapper;
import com.zhangjiashuai.yyetshistory.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@SpringBootTest
class YyetsHistoryApplicationTests {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ResourceMapper resourceMapper;

    @Test
    public void mapperPageTest() {
        String name = "律师";
        List<ResourceDO> listAll = resourceMapper.selectByNameLike(name);
        log.info("查询数量: {}",listAll.size());
        Page<ResourceDO> page1 = PageHelper.<ResourceDO>startPage(1, 10).doSelectPage(() -> resourceMapper.selectByNameLike(name));
        page1.getResult().forEach(resourceDO -> resourceDO.setData(null));
        log.info("==========第1页===============");
        log.info(page1.toString());
        log.info("==========第1页===============");
        Page<ResourceDO> page2 = PageHelper.<ResourceDO>startPage(2, 10).doSelectPage(() -> resourceMapper.selectByNameLike(name));
        page2.getResult().forEach(resourceDO -> resourceDO.setData(null));
        log.info("==========第2页===============");
        log.info(page2.toString());
        log.info("==========第2页===============");
        Assert.isFalse(page1.equals(page2));
    }

    @Test
    public void servicePageTest() {
        String name = "律师";
        PageInfo<Resource> page1 = resourceService.selectPage(name, 1);
        log.info("==========第1页===============");
        log.info(page1.toString());
        log.info("==========第1页===============");
        PageInfo<Resource> page2 = resourceService.selectPage(name, 2);
        log.info("==========第2页===============");
        log.info(page2.toString());
        log.info("==========第2页===============");
        Assert.isFalse(page1.equals(page2));
    }

    @Test
    public void allWaysTest() {
        PageInfo<Resource> page1 = resourceService.selectPage(null, 1, 100);
        Set<String> allWays = new HashSet<>();
        for (Resource resource : page1.getList()) {
            List<Resource.Season> seasons = resource.getSeasons();
            if(seasons == null)
                continue;
            for (Resource.Season season : seasons) {
                List<Resource.Group> groups = season.getGroups();
                if(groups == null)
                    continue;
                for (Resource.Group group : groups) {
                    List<Resource.Item> items = group.getItems();
                    if(items == null)
                        continue;
                    for (Resource.Item item : items) {
                        List<Resource.Link> links = item.getLinks();
                        if(links == null)
                            continue;
                        for (Resource.Link link : links) {
                            allWays.add(link.getWay());
                        }
                    }
                }
            }
        }
        System.out.println(allWays);
        Assert.isTrue(allWays.size() > 2);
    }
}
