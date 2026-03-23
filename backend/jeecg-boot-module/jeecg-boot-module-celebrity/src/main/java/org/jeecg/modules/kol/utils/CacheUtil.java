package org.jeecg.modules.kol.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeecg.modules.kol.model.KolSearchModel;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: nqr
 * @Date: 2025/9/13 17:43
 * @Description:
 **/
public class CacheUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成缓存键 - 将对象中有值的字段转为JSON字符串
     *
     * @param obj 要处理的对象
     * @return JSON字符串格式的缓存键
     */
    public static String generateCacheKey(Object obj) {
        if (obj == null) {
            return "null";
        }

        Map<String, Object> fieldsWithValue = new LinkedHashMap<>();

        // 使用反射获取所有字段
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                // 判断字段是否有值
                if (hasValue(value)) {
                    if (value instanceof String) {
                        fieldsWithValue.put(field.getName(), ((String) value).trim());
                    } else {
                        fieldsWithValue.put(field.getName(), value);
                    }
                }
            } catch (IllegalAccessException e) {
                // 忽略无法访问的字段
            }
        }

        try {
            // 转换为JSON字符串
            String jsonStr = objectMapper.writeValueAsString(fieldsWithValue);

            // 生成MD5 hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(jsonStr.getBytes(StandardCharsets.UTF_8));

            // 转换为16进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            // 如果处理失败，返回对象的hashCode
            return String.valueOf(fieldsWithValue.hashCode());
        }
    }

    /**
     * 判断值是否有效（不为空）
     */
    private static boolean hasValue(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String) {
            return !((String) value).trim().isEmpty();
        }
        if (value instanceof Collection) {
            return !((Collection<?>) value).isEmpty();
        }
        return true;
    }

    /**
     * 专门为KolSearchModel生成缓存键的方法
     */
    public static String generateCacheKey(KolSearchModel searchModel) {
        return generateCacheKey((Object) searchModel);
    }
}