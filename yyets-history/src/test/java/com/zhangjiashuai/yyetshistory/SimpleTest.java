package com.zhangjiashuai.yyetshistory;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.extra.compress.extractor.SevenZExtractor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
}
