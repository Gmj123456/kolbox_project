package org.jeecg.modules.kol.wechatexcel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: nqr
 * @Date: 2025/8/25 14:15
 * @Description:
 **/
@Data
public class SmartSheetData {
    /**
     * 产品名称中英文
     */
    private String productName;
    /**
     * 产品id
     */
    private String productId;
    /**
     * 品牌名
     */
    private String brandName;
    /**
     * 品牌id
     */
    private String brandId;
    /**
     * 商务顾问
     */
    private String celebrityCounselorName;
    /**
     * 商务顾问id
     */
    private String celebrityCounselorId;
    /**
     * 平台类型
     */
    private String platformType;
    /**
     * 推广日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;               // 日期
    /**
     * 类目
     */
    private String category;
    /**
     * 类目id
     */
    private String categoryId;
    /**
     * 推广单号
     */
    private String extensionCode;
    /**
     * 网红顾问id
     */
    private String counselorId;
    /**
     * 网红顾问姓名
     */
    private String counselorName;
    /**
     * 选中网红
     */
    private String uniqueId;
    private String recordId;
}
