package com.zhangjiashuai.yyetshistory;

import org.junit.jupiter.api.Test;

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
}
