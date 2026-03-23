package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName PrivateContractInfo.java
 * @Description TODO
 * @createTime 2025年08月26日 11:18:00
 */
@Data
public class PrivateContractInfo {
    /**
     * 视频内容
     */
    private String videoTag;
    /**
     * 视频内容费用
     */
    private BigDecimal contractAmount;
}
