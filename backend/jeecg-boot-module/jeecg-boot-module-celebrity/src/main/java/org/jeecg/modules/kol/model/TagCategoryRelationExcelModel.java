package org.jeecg.modules.kol.model;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class TagCategoryRelationExcelModel {

    /**
     * 标签名称
     */
    @Excel(name = "标签", width = 15)
    private String tagName;
    private String tagId;
    /**
     * 类目名称
     */
    @Excel(name = "类目", width = 15)
    private String categoryName;
    private String categoryId;
    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    private Integer platformType;
    /**
     * 标签类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount
     */
    private Integer tagType;

    @Excel(name = "视频链接", width = 15)
    private String videoUrl;

    @TableField(exist = false)
    List<KolTagCategory> tagCategoryList;

    private String categoryIds;

    /**
     * 是否恢复 0-未恢复 1-已恢复
     */
    private int isRecover;
    @Excel(name = "平台", width = 15)
    private String platformTypeStr;
    @Excel(name = "类型", width = 15)
    private String tagTypeStr;

    public Integer getTagType() {
        switch (tagTypeStr) {
            case "hashtag":
                return 0;
            case "keyword":
                return 1;
            case "rootVideo":
                return 2;
            case "rootAccount":
                return 3;
        }
        return null;
    }

    public Integer getPlatformType() {
        switch (platformTypeStr) {
            case "IG":
                return 0;
            case "YT":
                return 1;
            case "TK":
                return 2;
        }
        return null;
    }
}
