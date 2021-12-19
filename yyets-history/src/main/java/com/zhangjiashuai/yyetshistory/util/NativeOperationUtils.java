package com.zhangjiashuai.yyetshistory.util;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.net.URL;

import static cn.hutool.core.text.CharSequenceUtil.EMPTY;
import static com.zhangjiashuai.yyetshistory.config.YyetsHistoryProperties.*;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

/**
 * 本地指令操作工具
 */
@Slf4j
public class NativeOperationUtils {

    static {
        System.setProperty("java.awt.headless", "false");
    }

    /**
     * 项目访问路径
     */
    private static volatile String uri;

    private NativeOperationUtils() {}

    private static void buildUri(String host, int port) {
        uri = String.format("http://%s:%d/", host, port);
        log.info("项目访问路径初始化完成: {}", uri);
    }

    /**
     * 准备启动时触发事件
     * @param args 命令行参数
     */
    public static void onStartPrepare(String[] args) {
        String classLoader = NativeOperationUtils.class.getClassLoader().getClass().getSimpleName();
        if(classLoader.endsWith("RestartClassLoader")) {
            return;
        }
        if(!NativeConfigUtil.isStartPrepareEvent(args)) {
            return;
        }
        NativeConfigUtil.loadYamlConfig();
        NativeConfigUtil.loadCommandLineArgs(args);
        String host = NativeConfigUtil.getHost();
        int port = NativeConfigUtil.getPort();
        if(!telnet(host, port)) {
            return;
        }
        log.info("探测到项目访问路径上有服务运行");
        buildUri(host, port);
        Pair<String, Long> processInfo = getRunningProcessInfo();
        if (APPLICATION_INFO.equals(processInfo.getKey())) {
            log.info("服务已在另一进程运行,pid: {}", processInfo.getValue());
            onStartFinish();
        } else {
            openErrorDialog ("端口冲突","当前地址: " + uri + " 已被占用");
        }
        System.exit(0);
    }

    /**
     * 启动成功触发事件
     */
    public static void onStartFinish() {
        if(!NativeConfigUtil.isStartFinishEvent()) {
            return;
        }
        if(uri == null) {
            buildUri(NativeConfigUtil.getHost(), NativeConfigUtil.getPort());
        }
        log.info("程序启动完成，访问地址: {}", uri);
        try {
            webBrowse(uri);
        } catch (Exception e1) {
            log.debug("打开桌面浏览器失败", e1);
            try {
                copy2Clipboard(uri);
            } catch (Exception e2) {
                log.debug("复制链接至剪贴板失败", e2);
            }
            try {
                openSuccessDialog(uri);
            } catch (Exception e3) {
                log.debug("弹出对话框失败", e3);
            }
        }
    }

    private static Pair<String, Long> getRunningProcessInfo() {
        String url = uri + "info";
        try {
            HttpResponse response = HttpUtil.createGet(url).timeout(100).execute();
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return Pair.of(EMPTY, UNKNOWN_PID);
            }
            String body = response.body();
            JSONObject jsonBody = JSON.parseObject(body);
            return Pair.of(jsonBody.getString(INFO_KEY), jsonBody.getLong(PID_KEY));
        } catch (Exception e) {
            return Pair.of(EMPTY, UNKNOWN_PID);
        }
    }
    /**
     * 浏览器访问
     * @param url
     */
    public static void webBrowse(String url) {
        DesktopUtil.browse(url);
    }

    /**
     * 复制到剪贴板
     * @param str
     */
    public static void copy2Clipboard(String str) {
        ClipboardUtil.setStr(str);
    }

    public static String openSuccessDialog(String url) {
        URL icon = NativeOperationUtils.class.getResource("/static/icon_msg.png");
        return openInputDialog("启动完成", "打开浏览器访问以下地址:", url, new ImageIcon(icon));
    }
    /**
     * 弹出对话框
     * @param title 标题
     * @param label 文本框名称
     * @param text 文本内容
     * @param icon 图标
     * @return
     */
    public static String openInputDialog(String title, String label, String text, Icon icon) {
        return (String) JOptionPane.showInputDialog(null, label,
                title, INFORMATION_MESSAGE, icon, null, text);
    }

    /**
     * 弹出错误提示框
     * @param title 标题
     * @param text 内容
     */
    public static void openErrorDialog(String title, String text) {
        JOptionPane.showMessageDialog(null, text, title, ERROR_MESSAGE);
    }

    /**
     * 检查端口是否开启
     * @param host
     * @param port
     * @return
     */
    public static boolean telnet(String host, int port) {
        return NetUtil.isOpen(NetUtil.buildInetSocketAddress(host, port), 100);
    }

}
