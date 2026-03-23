package org.jeecg.common.constant.enums;

/**
 * 飞书在线表格类型枚举
 * @Author: jeecg-boot
 * @Date: 2025-09-15
 * @Version: V1.0
 */
public enum FeishuSpreadSheetType {
    PRIVATE_CELEBRITY(0, "PrivateCelebrity", "私有网红"),
    PERSONAL_TAGS(1, "PersonalTags", "个人标签"),
    COUNTRY(2, "Country", "国家"),
    CONTACT_EMAIL(3, "ContactEmail", "联系邮箱"),
    PRODUCT(4, "Product", "产品");

    private final int code;
    private final String value;
    private final String info;

    FeishuSpreadSheetType(int code, String value, String info) {
        this.code = code;
        this.value = value;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getInfo() {
        return info;
    }

    /**
     * 根据值获取枚举
     * @param value 值
     * @return 枚举
     */
    public static FeishuSpreadSheetType getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (FeishuSpreadSheetType type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据代码获取枚举
     * @param code 代码
     * @return 枚举
     */
    public static FeishuSpreadSheetType getByCode(int code) {
        for (FeishuSpreadSheetType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}