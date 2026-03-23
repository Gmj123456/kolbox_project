package org.jeecg.modules.kol.model;

import org.jeecg.modules.kol.entity.KolCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 网红标签分类类目树形结构模型
 * @Author: jeecg-boot
 * @Date: 2024-12-10
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "网红标签分类类目树形结构模型")
public class KolCategoryTreeModel extends KolCategory {

    /**
     * 子节点列表
     */
    @Schema(description = "子节点列表")
    private List<KolCategoryTreeModel> children = new ArrayList<>();

    /**
     * 添加子节点
     *
     * @param child 子节点
     */
    public void addChild(KolCategoryTreeModel child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

    public KolCategoryTreeModel() {
    }

    public KolCategoryTreeModel(KolCategory category) {
        this.setId(category.getId());
        this.setParentId(category.getParentId());
        this.setCategoryCode(category.getCategoryCode());
        this.setCategoryName(category.getCategoryName());
        this.setCategoryEnName(category.getCategoryEnName());
        this.setCategoryCodePath(category.getCategoryCodePath());
        this.setCategoryNamePath(category.getCategoryNamePath());
        this.setCategoryEnNamePath(category.getCategoryEnNamePath());
        this.setIsHasChild(category.getIsHasChild());
        this.setSortCode(category.getSortCode());
        this.setCreateBy(category.getCreateBy());
        this.setCreateTime(category.getCreateTime());
        this.setUpdateBy(category.getUpdateBy());
        this.setUpdateTime(category.getUpdateTime());
        this.setIsDelete(category.getIsDelete());
    }
}