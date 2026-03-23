package org.jeecg.modules.kol.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.kol.entity.KolTagCategory;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @Description: 标签与类目关联表
 * @Author: nqr
 * @Date: 2025-05-21
 * @Version: V1.0
 */
@Data
public class KolTagAttributeRelationModel {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 标签ID
     */
    @Excel(name = "标签ID", width = 15)
    @Schema(title =  "标签ID")
    private String tagId;
    /**
     * 标签名称
     */
    @Excel(name = "标签", width = 15)
    @Schema(title =  "标签名称")
    private String tagName;
    /**
     * 类目ID
     */
    @Excel(name = "类目ID", width = 15)
    @Schema(title =  "类目ID")
    private String categoryId;
    /**
     * 类目名称
     */
    @Excel(name = "类目", width = 15)
    @Schema(title =  "类目名称")
    private String categoryName;
    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    private Integer platformType;
    /**
     * 标签类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount
     */
    private Integer tagType;
    private String videoUrl;

    List<KolTagCategory> tagCategoryList;
    private String categoryIds;

    /**
     * 是否恢复 0-未恢复 1-已恢复
     */
    private int isRecover;

}
