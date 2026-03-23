package org.jeecg.modules.instagram.utils;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiCharUtils {
    private static Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    private final static Pattern emailer = Pattern.compile("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");

    /**
     * 转换emoji表情特殊字符的方法
     *
     * @param chars
     * @return
     */
    public static String transform(String chars) {
        if (StringUtils.isEmpty(chars)) {
            return chars;
        }
        StringBuilder emojiChars = new StringBuilder();
        int l = chars.length();
        for (int i = 0; i < l; i++) {
            char _s = chars.charAt(i);
            if (isEmojiCharacter(_s)) {
                emojiChars.append(_s);
            }
        }
        return emojiChars.toString();
    }

    public static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    public static String replaceEmoji(String source) {
        return source.replaceAll("[\\p{C}\\p{So}\uFE00-\uFE0F\\x{E0100}-\\x{E01EF}]+", "");
    }

    public static String getEmail(String str) {
        Matcher matchr = emailer.matcher(str);
        String email = "";
        while (matchr.find()) {
            email = matchr.group();
        }
        return email;
    }
}