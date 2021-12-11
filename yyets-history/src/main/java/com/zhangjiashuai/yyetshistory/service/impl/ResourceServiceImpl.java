package com.zhangjiashuai.yyetshistory.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.PageResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhangjiashuai.yyetshistory.entity.Resource;
import com.zhangjiashuai.yyetshistory.entity.ResourceDO;
import com.zhangjiashuai.yyetshistory.repository.ResourceRepository;
import com.zhangjiashuai.yyetshistory.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

    private ResourceRepository  resourceRepository;

    public ResourceServiceImpl(ResourceRepository  resourceRepository) {
        this.resourceRepository = resourceRepository;
    }
    @Override
    public ResourceDO findById(long id) {
        return resourceRepository.findById(id);
    }

    @Override
    public List<ResourceDO> findByName(String name) {
        return resourceRepository.findByName(name);
    }

    @Override
    public ResourceDO findOneByName(String name) {
        return resourceRepository.findOneByName(name);
    }

    @Override
    public List<ResourceDO> findAll() {
        return resourceRepository.findAll();
    }

    @Override
    public Resource parseResource(ResourceDO resourceDO) {
        Assert.notNull(resourceDO);
        String data = resourceDO.getData();
        JSONObject parseObject = JSON.parseObject(data);
        JSONObject dataObject = parseObject.getJSONObject("data");
        if(MapUtil.isEmpty(dataObject)) {
            throw new NullPointerException("data is null");
        }
        JSONObject infoJson = dataObject.getJSONObject("info");
        if(MapUtil.isEmpty(infoJson)) {
            throw new NullPointerException("info is null");
        }
        Resource resource = new Resource();
        resource.setName(infoJson.getString("cnname"));
        resource.setNameEN(infoJson.getString("enname"));
        resource.setChannel(infoJson.getString("channel_cn"));
        resource.setArea(infoJson.getString("area"));
        resource.setId(infoJson.getLongValue("id"));
        JSONArray list = dataObject.getJSONArray("list");
        if(CollectionUtil.isEmpty(list)) {
            throw new NullPointerException("list is null");
        }
        List<Resource.Season> seasons = new ArrayList<>(list.size());
        resource.setSeasons(seasons);
        for (int i = 0; i < list.size(); i++) {
            JSONObject seasonJson = list.getJSONObject(i);
            Resource.Season season = new Resource.Season();
            season.setName(seasonJson.getString("season_cn"));
            season.setNumber(seasonJson.getIntValue("season_num"));
            seasons.add(season);
            JSONObject itemsJson = seasonJson.getJSONObject("items");
            if(CollectionUtil.isEmpty(itemsJson)) {
                continue;
            }
            List<Resource.Group> groups = new ArrayList<>(itemsJson.size());
            season.setGroups(groups);
            for (Map.Entry<String, Object> entry : itemsJson.entrySet()) {
                String groupName = entry.getKey();
                Resource.Group group = new Resource.Group();
                groups.add(group);
                group.setName(groupName);
                JSONArray itemArray = (JSONArray) entry.getValue();
                List<Resource.Item> items = new ArrayList<>(itemArray.size());
                group.setItems(items);
                for (int j = 0; j < itemArray.size(); j++) {
                    JSONObject itemJson = itemArray.getJSONObject(j);
                    Resource.Item item = new Resource.Item();
                    items.add(item);
                    item.setEpisode(itemJson.getIntValue("episode"));
                    item.setName(itemJson.getString("name"));
                    item.setSize(itemJson.getString("size"));
                    JSONArray filesArray = itemJson.getJSONArray("files");
                    if(CollectionUtil.isEmpty(filesArray)) {
                        continue;
                    }
                    List<Resource.Link> links = new ArrayList<>();
                    item.setLinks(links);
                    for (int k = 0; k < filesArray.size(); k++) {
                        JSONObject fileJson = filesArray.getJSONObject(k);
                        String way = fileJson.getString("way_cn");
                        if(!"电驴".equals(way) && !"磁力".equals(way)) {
                            continue;
                        }
                        String address = fileJson.getString("address");
                        if(StrUtil.isEmpty(address)) {
                            continue;
                        }
                        Resource.Link link = new Resource.Link();
                        links.add(link);
                        link.setAddress(address);
                        link.setWay(way);
                    }
                    Collections.sort(links);
                }
                Collections.sort(items);
            }
            Collections.sort(groups);
        }
        Collections.sort(seasons);
        return resource;
    }

    @Override
    public Resource getResourceByName(String name) {
        ResourceDO resourceDO = findOneByName(name);
        if(resourceDO == null) {
            return null;
        }
        return parseResource(resourceDO);
    }

    @Override
    public Resource getResourceById(long id) {
        ResourceDO resourceDO = findById(id);
        if(resourceDO == null) {
            return null;
        }
        return parseResource(resourceDO);
    }

    @Override
    public List<Resource> getResourcesByName(String name) {
        List<ResourceDO> resourceDOList = findByName(name);
        if (CollectionUtil.isEmpty(resourceDOList)) {
            return Collections.emptyList();
        }
        return resourceDOList.stream().map(this::parseResource).collect(Collectors.toList());
    }

    @Override
    public List<Resource> getAllResources() {
        List<ResourceDO> resourceDOList = findAll();
        if (CollectionUtil.isEmpty(resourceDOList)) {
            return Collections.emptyList();
        }
        return resourceDOList.stream().map(this::parseResource).collect(Collectors.toList());
    }

    @Override
    public PageResult<ResourceDO> selectDOPage(String name, int pageNo) {
        return resourceRepository.selectPage(name, pageNo);
    }
}
