package org.jeecg.modules.instagram.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Base64 工具类（使用标准 java.util.Base64）
 */
public class Base64Util {

    private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("[\\r\\n]");

    /**
     * Base64 字符串解码为字节数组
     *
     * @param base64Str Base64 编码的字符串（可包含换行符）
     * @return 解码后的字节数组，失败返回 null
     */
    public static byte[] decode(String base64Str) {
        if (base64Str == null) {
            return null;
        }
        try {
            String cleanStr = removeLineBreaks(base64Str);
            return Base64.getDecoder().decode(cleanStr);
        } catch (Exception e) {
            // 建议记录日志而非 printStackTrace
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字节数组编码为 Base64 字符串（无换行）
     *
     * @param data 原始字节数组
     * @return Base64 字符串（已移除换行符）
     */
    public static String encode(byte[] data) {
        if (data == null) {
            return null;
        }
        return removeLineBreaks(Base64.getEncoder().encodeToString(data));
    }

    /**
     * 字符串编码为 Base64（UTF-8）
     *
     * @param text 原始字符串
     * @return Base64 字符串
     */
    public static String encode(String text) {
        if (text == null) {
            return null;
        }
        return encode(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * 从文件路径读取图片为字节数组
     *
     * @param path 文件路径
     * @return 文件字节数组，失败返回 null
     */
    public static byte[] imageToByte(String path) {
        if (path == null) {
            return null;
        }
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 移除字符串中的换行符（\r, \n）
     */
    private static String removeLineBreaks(String str) {
        if (str == null) {
            return null;
        }
        return LINE_BREAK_PATTERN.matcher(str).replaceAll("");
    }
}