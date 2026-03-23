package org.jeecg.modules.feishu.converter;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jeecg.modules.feishu.model.Record;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/7/16 15:37
 * @Description:JSON转换工具类
 **/
public class JsonConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 使用Jackson进行JSON转换（推荐）
     */
    public static List<Record> convertWithJackson(String jsonString) {
        try {
            TypeReference<List<Record>> typeRef = new TypeReference<List<Record>>() {
            };
            return objectMapper.readValue(jsonString, typeRef);
        } catch (Exception e) {
            System.err.println("Jackson转换失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用Gson进行JSON转换
     * 注意：如果使用Gson，需要在Fields类中使用@SerializedName注解替代@JsonProperty
     */
    public static List<Record> convertWithGson(String jsonString) {
        try {
            Type listType = new TypeToken<List<Record>>() {
            }.getType();
            return gson.fromJson(jsonString, listType);
        } catch (Exception e) {
            System.err.println("Gson转换失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将Java对象转换为JSON字符串（Jackson）
     */
    public static String toJsonWithJackson(List<Record> records) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        } catch (Exception e) {
            System.err.println("Jackson序列化失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将Java对象转换为JSON字符串（Gson）
     */
    public static String toJsonWithGson(List<Record> records) {
        try {
            return gson.toJson(records);
        } catch (Exception e) {
            System.err.println("Gson序列化失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
