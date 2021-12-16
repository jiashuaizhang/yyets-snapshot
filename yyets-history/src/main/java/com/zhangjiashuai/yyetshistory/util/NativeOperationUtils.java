package com.zhangjiashuai.yyetshistory.util;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.net.URL;

import static com.zhangjiashuai.yyetshistory.config.YyetsHistoryProperties.APPLICATION_INFO;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

/**
 * 本地指令操作工具
 */
@Slf4j
public class NativeOperationUtils {

    static {
        System.setProperty("java.awt.headless", "false");
        NativeYamlUtil.loadYamlConfig();
    }

    /**
     * 项目范文路径
     */
    private static volatile String uri;

    private NativeOperationUtils() {}

    private static String buildUri(String host, int port) {
        uri = String.format("http://%s:%d/", host, port);
        log.info("项目访问路径初始化完成: {}", uri);
        return uri;
    }

    /**
     * 准备启动时触发事件
     * @param args 命令行参数
     * @return true 执行启动
     */
    public static boolean onStartPrepare(String[] args) {
        NativeYamlUtil.loadCommandLineArgs(args);
        String host = getHost();
        int port = getPort();
        String uri = buildUri(host, port);
        log.info("检查项目是否已经启动");
        if(telnet(host, port)) {
            log.info("探测到项目访问路径上有服务运行");
            if (startAlready()) {
                log.info("服务已在另一进程运行");
                onStartFinish();
            } else {
                openErrorDialog ("端口冲突","当前地址: " + uri + " 已被占用");
            }
            System.exit(0);
        }
        return true;
    }

    /**
     * 启动成功触发事件
     * @param uri 项目访问地址
     */
    public static void onStartFinish() {
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

    private static boolean startAlready() {
        String url = uri + "info";
        try {
            HttpResponse response = HttpUtil.createGet(url).timeout(100).execute();
            return response.getStatus() == HttpStatus.HTTP_OK && APPLICATION_INFO.equals(response.body());
        } catch (HttpException e) {
            return false;
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


    private static String getHost() {
        return NativeYamlUtil.getPropertyOrDefault("yyets-history.host", "localhost").toString();
    }

    private static int getPort() {
        Object value = NativeYamlUtil.getProperty("server.port");
        final int defaultPort = 8080;
        if(value == null) {
            return defaultPort;
        }
        if(value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultPort;
        }
    }
}
