package org.jeecg.modules.instagram.storeseller.category.model;

import lombok.Data;
import org.jeecg.modules.instagram.storeseller.category.entity.StoreGoodsCategory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Data
public class GoodsCategoryTree implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;
	private String key;
	private String title;
	/**
	 * 父id
	 */
	private String parentId;
	/**
	 * 类目名称
	 */
	private String categoryName;

	/**
	 * 类目类型
	 */
	private Integer categoryType;
	/**
	 * 类目编码
	 */
	private String categoryCode;
	/**
	 * 类目排序
	 */
	private int sortCode;
	/**
	 * 是否叶子节点: 1:是 0:不是
	 */
	private boolean isLeaf;

    /**按钮权限状态(0无效1有效)*/
	private int status;

	private boolean disabled;

	private List<GoodsCategoryTree> children;

	private String value;

	public GoodsCategoryTree() {
	}

	public GoodsCategoryTree(StoreGoodsCategory storeGoodsCategory) {
		this.key = storeGoodsCategory.getId();
		this.id = storeGoodsCategory.getId();
		this.categoryName = storeGoodsCategory.getCategoryName();
		this.parentId = storeGoodsCategory.getParentId();
		this.isLeaf = storeGoodsCategory.isLeaf();
		this.categoryType=storeGoodsCategory.getCategoryType();
		this.categoryCode=storeGoodsCategory.getCategoryCode();
		this.sortCode = storeGoodsCategory.getSortCode();
		this.value=storeGoodsCategory.getId();

		/*update_end author:wuxianquan date:20190908 for:赋值 */
		this.title= storeGoodsCategory.getCategoryName();
		if (!storeGoodsCategory.isLeaf()) {
			this.children = new ArrayList<GoodsCategoryTree>();
		}
		this.status = storeGoodsCategory.getStatus();
	}

	public GoodsCategoryTree(StoreGoodsCategory storeGoodsCategory,Boolean leaf) {
		this.key = storeGoodsCategory.getId();
		this.id = storeGoodsCategory.getId();
		this.categoryName = storeGoodsCategory.getCategoryName();
		this.parentId = storeGoodsCategory.getParentId();
		this.isLeaf = storeGoodsCategory.isLeaf();
		this.categoryType=storeGoodsCategory.getCategoryType();
		this.categoryCode=storeGoodsCategory.getCategoryCode();
		this.sortCode = storeGoodsCategory.getSortCode();
		this.value=storeGoodsCategory.getId();

		/*update_end author:wuxianquan date:20190908 for:赋值 */
		this.title= storeGoodsCategory.getCategoryName();
		if (!storeGoodsCategory.isLeaf()) {
			this.children = new ArrayList<GoodsCategoryTree>();
			if(leaf){
				this.disabled=true;
			}
		}
		this.status = storeGoodsCategory.getStatus();
	}


}
