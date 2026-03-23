package org.jeecg.modules.instagram.storetags.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 个性化标签
 * @Author: nqr
 * @Date: 2023-07-11
 * @Version: V1.0
 */
@Data
public class StorePersonalTagsModel {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "id")
    private String id;
    /**
     * 标签名称
     */
    @Excel(name = "标签", width = 15)
    @Schema(title =  "标签名称")
    private String tagName;

    @Excel(name = "类目", width = 15)
    private String categoryName;
    /**
     * 备注
     */
    @Schema(title =  "备注")
    private String remark;
    /**
     * 创建人
     */
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private java.util.Date createTime;
    /**
     * 修改人
     */
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private java.util.Date updateTime;
    private String categoryId;
    private String categoryIds;
    private String tagId;
    private String personalTagsCategoryId;

    /**
     * 是否恢复 0-未恢复 1-已恢复
     */
    private int isRecover;
    private int delFlag;
}
