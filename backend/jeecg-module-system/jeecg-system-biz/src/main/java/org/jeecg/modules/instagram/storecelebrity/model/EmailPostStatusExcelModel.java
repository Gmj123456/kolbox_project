package org.jeecg.modules.instagram.storecelebrity.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;


/**
 * 阿里导入excel模板类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmailPostStatusExcelModel {

    @Excel(name = "创建时间", width = 15)
    private Date createTime;

    @Excel(name = "发信地址", width = 15)
    private String sendAddress;

    @Excel(name = "收信地址", width = 15)
    private String reciveAddress;

    @Excel(name = "投递状态", width = 15)
    private String postStatus;

    @Excel(name = "详情", width = 15)
    private String details;
}
