package com.zhangjiashuai.yyetshistory;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.extra.compress.extractor.SevenZExtractor;
import com.zhangjiashuai.yyetshistory.util.NativeOperationUtils;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
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
    public void testExtract7z() {
        String projectPath = System.getProperty("user.dir");
        String staticResourcePath = projectPath + "/src/main/resources/static/";
        String db7zFilename = "yyets.7z";
        File db7zFile = new File(staticResourcePath + db7zFilename);
        System.out.println(db7zFile.exists());
        try (SevenZExtractor sevenZExtractor = new SevenZExtractor(db7zFile)) {
            sevenZExtractor.extract(new File(staticResourcePath));
        }
        File dbFile = new File(staticResourcePath + "yyets.db");
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
    public void testTelnet() {
        boolean open = NetUtil.isOpen(NetUtil.buildInetSocketAddress("localhost", 9000), 1000);
        Assert.isTrue(open);
        open = NetUtil.isOpen(NetUtil.buildInetSocketAddress("localhost", 9001), 10);
        Assert.isFalse(open);
    }

    @Test
    public void testMsgError() {
        String uri = "http://localhost:9000/";
        NativeOperationUtils.openErrorDialog ("端口冲突","当前地址: " + uri + " 已被占用");
        System.exit(0);
    }
}
