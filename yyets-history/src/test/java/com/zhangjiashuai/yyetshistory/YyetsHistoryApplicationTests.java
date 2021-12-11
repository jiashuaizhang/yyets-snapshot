package com.zhangjiashuai.yyetshistory;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhangjiashuai.yyetshistory.entity.Resource;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;
import com.zhangjiashuai.yyetshistory.repository.ResourceRepository;
import com.zhangjiashuai.yyetshistory.repository.impl.ResourceMapper;
import com.zhangjiashuai.yyetshistory.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
@SpringBootTest
class YyetsHistoryApplicationTests {

    @Autowired(required = false)
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ResourceMapper resourceMapper;

    @Test
    public void jdbcTemplateTest() {
        List<ResourceDO> resources = resourceRepository.findByName("绝命律师");
        Assert.notEmpty(resources);
    }

    @Test
    public void ResourceServiceTest() {
        Resource resource = resourceService.getResourceById(33190);
        log.info("资源总览: {}", resource);
        Resource.Season season = resource.getSeasons().get(0);
        log.info("季度总览: {}", season);
        Resource.Group group = season.getGroups().stream().
                filter(g -> "HR-HDTV".equals(g.getName())).findFirst().orElse(null);
        if(group == null) {
            System.out.println(season);
            return;
        }
        log.info("分组总览: {}", group);
        List<Resource.Item> items = group.getItems();
        log.info("文件总览: {}", items);
        String tempFile = "F:\\temp\\decode\\" + resource.getName() + "." + season.getName() + ".txt";
        try (FileWriter writer = new FileWriter(tempFile);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            for (Resource.Item item : items) {
                List<Resource.Link> links = item.getLinks();
                Map<String, String> linkMap = links.stream().collect(toMap(Resource.Link::getWay, Resource.Link::getAddress));
                String theWay;
                if(linkMap.containsKey("磁力")) {
                    theWay = "磁力";
                } else if(linkMap.containsKey("电驴")) {
                    theWay = "电驴";
                } else {
                    theWay = linkMap.keySet().stream().findFirst().orElse(StrUtil.EMPTY);
                }
                String address = linkMap.get(theWay);
                log.info("数据写入, 名称: {}, 渠道: {}, 链接: {}", item.getName(), theWay, address);
                bufferedWriter.write(address);
                bufferedWriter.newLine();
            }
        } catch (Exception e) {
            log.error("写文件失败: {}", tempFile, e);
        }
    }

    @Test
    public void resourceMapperTest() {
        ResourceDO resourceDO = resourceMapper.selectById(33190);
        Assert.notNull(resourceDO);
        Assert.isTrue(StrUtil.contains(resourceDO.getName(), "绝命律师"));
        Long count = resourceMapper.selectCount(Wrappers.emptyWrapper());
        log.info("count: {}", count);
        Assert.isTrue(count > 0);
        List<ResourceDO> list = resourceMapper.selectPage("律师", 1);
        list.stream().map(ResourceDO::getName).forEach(System.out::println);
    }
}
