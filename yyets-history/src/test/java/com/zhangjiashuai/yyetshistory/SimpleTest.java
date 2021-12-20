package com.zhangjiashuai.yyetshistory;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.ZipUtil;
import com.zhangjiashuai.yyetshistory.util.NativeOperationUtils;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.DASHED;
import static cn.hutool.core.text.StrPool.UNDERLINE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

public class SimpleTest {

    @Test
    public void testLinkedHashSet() {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("电驴");
        linkedHashSet.add("磁力");
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 100; i++) {
            final int index = i;
            executorService.submit(() -> {
                boolean contains = linkedHashSet.contains("磁力");
                if(!contains) {
                    System.out.println(index + " 读取错误");
                }
                if(index % 10 == 0) {
                    System.out.println(index);
                    linkedHashSet.forEach(System.out::println);
                    System.out.println();
                }
            });
        }
        executorService.shutdown();
    }

    @Test
    public void testExtractZip() {
        String projectPath = System.getProperty("user.dir");
        String staticResourcePath = projectPath + "/src/main/resources/static/";
        String dbZipFilename = "yyets.zip";
        File dbZipFile = new File(staticResourcePath + dbZipFilename);
        Assert.isTrue(dbZipFile.exists());
        File dbFile = new File(staticResourcePath + "yyets.db");
        if(!dbFile.exists()) {
            long start = System.currentTimeMillis();
            ZipUtil.unzip(dbZipFile, new File(staticResourcePath));
            System.out.println("解压完成，耗时: " + (System.currentTimeMillis() - start));
        }
        Assert.isTrue(dbFile.exists());
    }

    @Test
    public void testUriDecode() {
        String path = "/E:/%e4%bb%a3%e7%a0%81/%e9%9b%b6%e7%a2%8e/yyets-snapshot/yyets-history/target/classes/static";
        String decode = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decode);
        String decodeAgain = URLDecoder.decode(decode, StandardCharsets.UTF_8);
        Assert.isTrue(decode.equals(decodeAgain));
        File file = new File(decode);
        System.out.println(file.isDirectory());
        System.out.println(file.getAbsolutePath());
    }

    @Test
    public void testDesktopBrowse() {
        DesktopUtil.browse("http://localhost:9000");
        Assert.isTrue(true);
    }

    @Test
    public void testMsg() {
        String url = "http://localhost:9000";
        ClipboardUtil.setStr(url);
        URL icon = getClass().getResource("/static/icon_msg.png");
        JOptionPane.showInputDialog(null, "打开浏览器访问以下地址:",
                "启动完成", INFORMATION_MESSAGE, new ImageIcon(icon), null, url);
        Assert.isTrue(url.equals(ClipboardUtil.getStr()));
    }

    @Test
    public void testMsgError() {
        String uri = "http://localhost:9000/";
        NativeOperationUtils.openErrorDialog ("端口冲突","当前地址: " + uri + " 已被占用");
        System.exit(0);
    }

    @Test
    public void testSystemEnv() {
        String key = "yyets-history.start-prepare-event";
        String envKey = Arrays.stream(key.split("\\.")).map(str -> str.replaceAll(DASHED, UNDERLINE).toUpperCase())
                .collect(Collectors.joining(UNDERLINE));
        System.out.println(envKey);
        String value = System.getenv(envKey);
        System.out.println(value);
        Assert.notNull(value);
    }

}
