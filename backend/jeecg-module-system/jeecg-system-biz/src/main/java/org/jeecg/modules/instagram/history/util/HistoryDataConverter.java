package org.jeecg.modules.instagram.history.util;

import org.jeecg.common.constant.enums.PlatformType;
import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrityDetail;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 历史提报数据转换工具类
 * @Author:
 * @Date: 2025-11-26
 * @Version: V1.0
 */
@Slf4j
public class HistoryDataConverter {

    /**
     * 将字符串平台类型转换为整数类型
     * 
     * @param platformTypeText 平台类型文本 (IG, YT, TK)
     * @return 平台类型整数值 (0, 1, 2)
     */
    public static Integer convertPlatformType(String platformTypeText) {
        if (!StringUtils.hasText(platformTypeText)) {
            log.warn("平台类型文本为空");
            return null;
        }

        try {
            switch (platformTypeText.trim().toUpperCase()) {
                case "IG":
                case "INSTAGRAM":
                    return PlatformType.INSTAGRAM.getCode();
                case "YT":
                case "YOUTUBE":
                    return PlatformType.YOUTUBE.getCode();
                case "TK":
                case "TIKTOK":
                    return PlatformType.TIKTOK.getCode();
                default:
                    log.warn("未知的平台类型: " + platformTypeText);
                    return null;
            }
        } catch (Exception e) {
            log.error("转换平台类型时发生错误，平台类型文本: " + platformTypeText, e);
            return null;
        }
    }

    /**
     * 将字符串性别转换为整数类型
     * 
     * @param genderText 性别文本 (男, 女)
     * @return 性别整数值 (0=男, 1=女, 2=未知)
     */
    public static Integer convertGender(String genderText) {
        if (!StringUtils.hasText(genderText)) {
            log.debug("性别文本为空，返回默认值未知");
            return 2; // 未知
        }

        try {
            switch (genderText.trim()) {
                case "男":
                    return 0;
                case "女":
                    return 1;
                default:
                    log.warn("未知的性别值: " + genderText + "，返回默认值未知");
                    return 2; // 未知
            }
        } catch (Exception e) {
            log.error("转换性别时发生错误，性别文本: " + genderText, e);
            return 2; // 未知
        }
    }

    /**
     * 处理从飞书获取的明细数据，转换其中的字符串字段为对应的整数类型
     * 
     * @param detail 明细数据
     */
    public static void processDetailData(KolHistoryCelebrityDetail detail) {
        // 处理平台类型转换
        // 注意：此方法主要用于说明目的，实际转换在控制器中完成
        if (detail == null) {
            log.warn("处理明细数据时输入参数为空");
            return;
        }

        log.debug("处理明细数据，网红名称: " + detail.getCelebrityName());
    }

    /**
     * 根据平台类型整数值获取平台名称
     * 
     * @param platformType 平台类型整数值
     * @return 平台名称
     */
    public static String getPlatformTypeName(Integer platformType) {
        if (platformType == null) {
            log.warn("平台类型为空");
            return "";
        }

        try {
            switch (platformType) {
                case 0:
                    return "IG";
                case 1:
                    return "YT";
                case 2:
                    return "TK";
                default:
                    log.warn("未知的平台类型值: " + platformType);
                    return "";
            }
        } catch (Exception e) {
            log.error("获取平台类型名称时发生错误，平台类型值: " + platformType, e);
            return "";
        }
    }

    /**
     * 根据性别整数值获取性别名称
     * 
     * @param gender 性别整数值
     * @return 性别名称
     */
    public static String getGenderName(Integer gender) {
        if (gender == null) {
            log.debug("性别值为空，返回默认值未知");
            return "未知";
        }

        try {
            switch (gender) {
                case 0:
                    return "男";
                case 1:
                    return "女";
                default:
                    log.debug("未知的性别值: " + gender + "，返回默认值未知");
                    return "未知";
            }
        } catch (Exception e) {
            log.error("获取性别名称时发生错误，性别值: " + gender, e);
            return "未知";
        }
    }
}