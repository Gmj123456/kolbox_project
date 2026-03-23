package org.jeecg.modules.kol.wechatexcel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Author: nqr
 * @Date: 2025/8/25 14:05
 * @Description: 微信记录数据模型
 **/
@Slf4j
@Data
public class Record {
    @JsonProperty("record_id")
    private String recordId;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("update_time")
    private String updateTime;

    private Map<String, Object> values;

    @JsonProperty("creator_name")
    private String creatorName;

    @JsonProperty("updater_name")
    private String updaterName;

    /**
     * 将当前 record 转换为结构化 SmartSheetData
     */
    public SmartSheetData toSmartSheetData() {
        SmartSheetData data = new SmartSheetData();

        // 提取文本字段
        extractText(values, "产品名称中英文", data::setProductName);
        extractText(values, "品牌名", data::setBrandName);
        extractText(values, "商务顾问", data::setCelebrityCounselorName);
        extractText(values, "平台", data::setPlatformType);
        extractText(values, "类目", data::setCategory);
        extractText(values, "编码", data::setExtensionCode);
        extractText(values, "网红顾问", data::setCounselorName);
        extractText(values, "选中网红", data::setUniqueId);

        // 提取日期字段
        extractDate(values, "日期", data::setDate);

        return data;
    }

    /**
     * 通用方法：提取 text 类型字段
     */
    private void extractText(Map<String, Object> values, String key, Consumer<String> setter) {
        if (values == null || key == null || setter == null) {
            return;
        }

        try {
            Object value = values.get(key);
            if (value == null) {
                return;
            }

            String textValue = null;

            if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (!list.isEmpty()) {
                    Object item = list.get(0);
                    if (item instanceof Map) {
                        Object text = ((Map<?, ?>) item).get("text");
                        if (text != null) {
                            textValue = text.toString();
                        }
                    }
                }
            }
            // 处理直接字符串值
            else if (value instanceof String) {
                textValue = (String) value;
            }
            // 处理其他可能的数据类型
            else {
                textValue = value.toString();
            }

            if (textValue != null) {
                setter.accept(textValue);
            }
        } catch (Exception e) {
            log.warn("提取文本字段失败 key: {}, error: {}", key, e.getMessage());
        }
    }

    /**
     * 提取日期字段（支持多种格式）
     */
    private void extractDate(Map<String, Object> values, String key, Consumer<Date> setter) {
        if (values == null || key == null || setter == null) {
            return;
        }

        try {
            Object value = values.get(key);
            if (value == null) {
                return;
            }

            Long timestamp = null;

            // 处理列表格式的日期值
            if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (!list.isEmpty()) {
                    Object first = list.get(0);
                    if (first instanceof Map) {
                        Object text = ((Map<?, ?>) first).get("text");
                        if (text != null) {
                            timestamp = parseTimestamp(text.toString());
                        }
                    }
                }
            }
            // 处理直接字符串格式的日期值
            else if (value instanceof String) {
                timestamp = parseTimestamp((String) value);
            }

            if (timestamp != null) {
                setter.accept(new Date(timestamp));
            }
        } catch (Exception e) {
            log.warn("提取日期字段失败 key: {}, error: {}", key, e.getMessage());
        }
    }

    /**
     * 解析时间戳字符串
     */
    private Long parseTimestamp(String timestampStr) {
        try {
            // 处理可能的时间戳格式（毫秒或秒级时间戳）
            long timestamp = Long.parseLong(timestampStr);

            // 如果是秒级时间戳（10位），转换为毫秒
            if (timestampStr.length() == 10) {
                timestamp *= 1000;
            }

            return timestamp;
        } catch (NumberFormatException e) {
            log.warn("时间戳解析失败: {}", timestampStr);
            return null;
        }
    }
}