package org.jeecg.modules.kol.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName TiktokTagsModel.java
 * @Description TODO
 * @createTime 2024年01月22日 17:45:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KolTagsModel extends KolTags {
    // tag状态
    private String tagNames;
    private String column;
    private String order;
    private String dataListStr;
    private String productIds;
    private String productData;
    private List<TypeData> dataList;
    // 类目及子级列表
    private List<String> categoryIdList;

    private String subCategoryIds;
    /**
     * 达人类型
     */
    @Excel(name = "达人类型1", width = 15)
    private String influencerType;
    @Excel(name = "达人类型2", width = 15)
    private String influencerType2;
    @Excel(name = "达人类型3", width = 15)
    private String influencerType3;
    @Excel(name = "达人类型4", width = 15)
    private String influencerType4;
    @Excel(name = "达人类型5", width = 15)
    private String influencerType5;

    /**
     * 人群类型
     */
    @Excel(name = "人群类型1", width = 15)
    private String audience;
    @Excel(name = "人群类型2", width = 15)
    private String audience2;
    @Excel(name = "人群类型3", width = 15)
    private String audience3;

    /**
     * 物理空间
     */
    @Excel(name = "物理空间1", width = 15)
    private String usageScenarios;
    @Excel(name = "物理空间2", width = 15)
    private String usageScenarios2;
    @Excel(name = "物理空间3", width = 15)
    private String usageScenarios3;

    private String recordId;
    // 根据产品ID查询
    private String productId;
    /**
     * 通过飞书导入，名称格式为：品牌-产品-型号
     */
    private String productImportName;
    /**
     * 是否同步
     */
    @Excel(name = "是否同步", width = 15)
    private String isUpdate;
    @Excel(name = "行号", width = 15)
    private Integer rowNum;

}
