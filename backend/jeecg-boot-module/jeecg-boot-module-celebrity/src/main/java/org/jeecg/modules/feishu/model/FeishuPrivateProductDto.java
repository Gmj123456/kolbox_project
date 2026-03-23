package org.jeecg.modules.feishu.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

/**
 * 飞书私有网红产品数据传输对象
 * @Author: dongruyang
 * @Date: 2025-09-15
 * @Version: V1.0
 */
@Data
public class FeishuPrivateProductDto {
    
    /**
     * 日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy年M月d日")
    @DateTimeFormat(pattern = "yyyy年M月d日")
    private Date date;
    
    /**
     * 推广单号
     */
    private String promotionNumber;
    
    /**
     * 商务顾问
     */
    private String businessConsultant;
    
    /**
     * 网红顾问
     */
    private String celebrityConsultant;
    
    /**
     * 品牌名称
     */
    private String brandName;
    
    /**
     * 产品名称
     */
    private String productName;
    
    /**
     * 选中网红
     */
    private String selectedCelebrity;
    
    /**
     * 平台
     */
    private String platform;
    
    /**
     * 类目
     */
    private String category;
    
    /**
     * 网红报价
     */
    private Double celebrityQuote;
    
    /**
     * 网红成本$
     */
    private Double celebrityCost;
    
    /**
     * 备注
     */
    private String remarks;
    
    /**
     * 上线链接
     */
    private String onlineLink;
    
    /**
     * 是否同步
     */
    private String isSynchronized;
    
    /**
     * 异常原因
     */
    private String errorReason;
}