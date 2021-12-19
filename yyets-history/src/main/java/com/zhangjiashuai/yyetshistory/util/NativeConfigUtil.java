package com.zhangjiashuai.yyetshistory.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.setting.yaml.YamlUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.*;
import static com.zhangjiashuai.yyetshistory.config.YyetsHistoryProperties.DEFAULT_HOST;

@Slf4j
class NativeConfigUtil {

    private static final Map<String, Object> PROPERTIES = new HashMap<>();
    private static final int DEFAULT_PORT = 8080;
    private static final String COMMAND_LINE_ARG_PREFIX = "--";
    private static final String COMMAND_LINE_ARG_SEPARATOR = "=";

    /**
     * 获取配置属性
     * @param key
     * @return
     */
    static Object getProperty(String key) {
        if(SpringUtil.getApplicationContext() != null) {
            return SpringUtil.getProperty(key);
        }
        return PROPERTIES.get(key);
    }

    /**
     * 获取环境变量
     * @param key
     * @return
     */
    static String getSystemEnv(String key) {
        String envKey = Arrays.stream(key.split("\\.")).map(str -> str.replaceAll(DASHED, UNDERLINE).toUpperCase())
                .collect(Collectors.joining(UNDERLINE));
        String value = null;
        if((value = System.getenv(envKey)) == null) {
            value = System.getenv(key);
        }
        return StrUtil.trim(value);
    }

    /**
     * 获取jvm参数
     * @param key
     * @return
     */
    static String getJvmPropertity(String key) {
        String value = System.getProperty(key);
        return StrUtil.trim(value);
    }

    /**
     * 获取命令行参数
     * @param str
     * @return
     */
    static Pair<String, String> getCommandLineArg(String str) {
        if(StrUtil.isBlank(str) || !str.startsWith(COMMAND_LINE_ARG_PREFIX)) {
            return null;
        }
        String subs = str.substring(COMMAND_LINE_ARG_PREFIX.length());
        String[] array = subs.split(COMMAND_LINE_ARG_SEPARATOR);
        if(array.length != 2 || StrUtil.isBlank(array[0])) {
            return null;
        }
        return Pair.of(array[0].trim(), StrUtil.trim(array[1]));
    }

    static void loadYamlConfig() {
        PROPERTIES.clear();
        try {
            loadConfigYaml(null);
            Object configValue = PROPERTIES.get("spring.profiles.active");
            if(configValue == null) {
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
        } finally {
            loadSystemProperties();
        }
    }

    static void loadCommandLineArgs(String[] args) {
        if(ArrayUtil.isEmpty(args)) {
            return;
        }
        for (String arg : args) {
            Pair<String, String> pair = getCommandLineArg(arg);
            if(pair != null) {
                PROPERTIES.put(pair.getKey(), pair.getValue());
            }
        }
    }

    static String getHost() {
        String host = StrUtil.toStringOrNull(getProperty("yyets-history.host"));
        return StrUtil.isNotBlank(host) ? StrUtil.trim(host) : DEFAULT_HOST;
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

    static boolean isStartPrepareEvent(String[] args) {
        String key = "yyets-history.start-prepare-event";
        return eventCheck(key, args);
    }

    static boolean isStartFinishEvent(String[] args) {
        String key = "yyets-history.start-finish-event";
        Object configValue = getProperty(key);
        if(configValue != null) {
            return Boolean.parseBoolean(configValue.toString());
        }
        return eventCheck(key, args);
    }

    private static boolean eventCheck(String key, String[] args) {
        String strValue = null;
        if(ArrayUtil.isNotEmpty(args)) {
            for (String arg : args) {
                if(StrUtil.startWith(arg, COMMAND_LINE_ARG_PREFIX + key)) {
                    Pair<String, String> pair = getCommandLineArg(arg);
                    strValue = pair.getValue();
                    break;
                }
            }
        }
        if(strValue == null) {
            strValue = getJvmPropertity(key);
        }
        if(strValue == null) {
            strValue = getSystemEnv(key);
        }
        if (strValue == null) {
            // 默认开启事件
            return true;
        }
        return Boolean.parseBoolean(strValue);
    }

    /**
     * 尝试获取系统变量与jvm参数
     */
    private static void loadSystemProperties() {
        for (Map.Entry<String, Object> entry : PROPERTIES.entrySet()) {
            String key = entry.getKey();
            String newValue;
            if((newValue = getJvmPropertity(key)) == null) {
                newValue = getSystemEnv(key);
            }
            if(newValue != null) {
                entry.setValue(newValue);
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
                String childKey = StrUtil.trim(entry.getKey());
                Object childValue = entry.getValue();
                String keyPart = Optional.ofNullable(key).map(s -> (s + DOT + childKey)).orElse(childKey);
                readYaml(properties, keyPart, childValue);
            }
        } else {
            properties.put(key, value instanceof String ? ((String) value).trim() : value);
        }
    }
}
