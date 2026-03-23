package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName StoreCelebrityPrivateProductModel.java
 * @Description TODO
 * @createTime 2025年07月30日 18:12:00
 */
@Data
public class StoreCelebrityPrivateProductModel extends StoreCelebrityPrivateProduct {
    @Excel(name = "选中网红", width = 15)
    private String account;
    @Excel(name = "产品名称", width = 15)
    private String productName;
    private Integer platformType;
    @Excel(name = "平台", width = 15)
    private String platformTypeStr;
    @Excel(name = "品牌名称", width = 15)
    private String brandName;
    private String recordId;
    /**
     * 失败原因
     */
    @Excel(name = "异常原因", width = 15)
    private String failReason;

    /**
     * 是否同步
     */
    @Excel(name = "是否同步", width = 15)
    private String isUpdate;

    @Excel(name = "行号", width = 15)
    private Integer rowNum;

    /**
     * 合作开始时间
     */
    @Excel(name = "日期", width = 20)
    private String startDateStr;

    /**
     * 是否重复数据
     */
    private int isRepeat = 0;
}
