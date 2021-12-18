package com.zhangjiashuai.yyetshistory.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.setting.yaml.YamlUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.*;

@Slf4j
class NativeYamlUtil {

    private static final Map<String, Object> PROPERTIES = new HashMap<>();
    private static final int DEFAULT_PORT = 8080;


    static Object getProperty(String key) {
        if(SpringUtil.getApplicationContext() != null) {
            return SpringUtil.getProperty(key);
        }
        return PROPERTIES.get(key);
    }

    static Object getPropertyOrDefault(String key, Object defaultValue) {
        Object v;
        return (((v = getProperty(key)) != null) || PROPERTIES.containsKey(key))
                ? v : defaultValue;
    }

    static void loadYamlConfig() {
        PROPERTIES.clear();
        loadConfigYaml(null);
        Object configValue = PROPERTIES.get("spring.profiles.active");
        if(configValue == null) {
            loadSystemProperties();
            return;
        }
        Collection<String> activeProfiles;
        if (configValue instanceof Collection) {
            activeProfiles = (Collection<String>) configValue;
        } else {
            activeProfiles = Arrays.stream(configValue.toString().split(COMMA)).collect(Collectors.toList());
        }
        for (String profile : activeProfiles) {
            if(StrUtil.isBlank(profile)) {
                continue;
            }
            try {
                loadConfigYaml(profile);
            } catch (Exception e) {
                log.debug("yaml加载异常, profile: {}", profile, e);
            }
        }
        loadSystemProperties();
    }

    static void loadCommandLineArgs(String[] args) {
        if(ArrayUtil.isEmpty(args)) {
            return;
        }
        String prefix = "--";
        String separator = "=";
        for (String arg : args) {
            if(StrUtil.isBlank(arg) || !arg.startsWith(prefix)) {
                continue;
            }
            String subs = arg.substring(prefix.length());
            String[] array = subs.split(separator);
            if(array.length != 2 || StrUtil.isBlank(array[0])) {
                continue;
            }
            PROPERTIES.put(array[0].trim(), StrUtil.trim(array[1]));
        }
    }

    static String getHost() {
        return getPropertyOrDefault("yyets-history.host", "localhost").toString();
    }

    static int getPort() {
        Object value = getProperty("server.port");
        if(value == null) {
            return DEFAULT_PORT;
        }
        if(value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return DEFAULT_PORT;
        }
    }

    /**
     * 尝试获取系统变量与jvm参数
     */
    private static void loadSystemProperties() {
        for (Map.Entry<String, Object> entry : PROPERTIES.entrySet()) {
            String key = entry.getKey();
            String newValue;
            if((newValue = System.getenv(key)) != null) {
                if(newValue.contains(UNDERLINE)) {
                    newValue = Arrays.stream(newValue.split(UNDERLINE)).map(String::toLowerCase)
                            .collect(Collectors.joining(DOT));
                } else {
                    newValue = newValue.toLowerCase();
                }
            } else {
                newValue = System.getProperty(key);
            }
            if(newValue != null) {
                entry.setValue(newValue.trim());
            }
        }
    }

    private static void loadConfigYaml(String profile) {
        String filename;
        if(StrUtil.isBlank(profile)) {
            filename = "application.yml";
        } else {
            filename = String.format("application-%s.yml", profile.trim());
        }
        Dict dict = YamlUtil.loadByPath(filename);
        readYaml(PROPERTIES, null, dict);
    }

    private static void readYaml(Map<String, Object> properties, String key, Object value) {
        if(value instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String childKey = entry.getKey();
                Object childValue = entry.getValue();
                String keyPart = Optional.ofNullable(key).map(s -> (s + DOT + childKey)).orElse(childKey);
                readYaml(properties, keyPart, childValue);
            }
        } else {
            properties.put(key, value instanceof String ? ((String) value).trim() : value);
        }
    }
}
