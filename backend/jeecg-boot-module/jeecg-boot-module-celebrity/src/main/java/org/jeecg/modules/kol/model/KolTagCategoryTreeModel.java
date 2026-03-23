package org.jeecg.modules.kol.model;

import lombok.Data;
import org.jeecg.common.system.vo.PersonalTagsModel;
import org.jeecg.modules.kol.entity.KolTagCategory;

import java.util.List;

/**
 * @Description: 网红标签分类类目树
 * @Author: nqr
 * @Date: 2025年4月27日17:19:13
 * @Version: V1.0
 */
@Data
public class KolTagCategoryTreeModel extends KolTagCategory {
    List<KolTagCategoryTreeModel> children;
    private Integer size;
    private List<PersonalTagsModel> personalTags;
    private String attributeCode;
    private String attributeName;
    private String attributeEnName;
    private String attributeCodePath;
    private String attributeNamePath;
    private String attributeEnNamePath;
    private String attributeTypeId;
    private String attributeTypeName;
}
