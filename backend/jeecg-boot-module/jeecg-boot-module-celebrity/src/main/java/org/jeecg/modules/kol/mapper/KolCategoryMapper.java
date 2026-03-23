package org.jeecg.modules.kol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.model.TreeSelectModel;
import org.jeecg.modules.kol.entity.KolCategory;

import java.util.List;
import java.util.Map;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date:   2024年11月22日 18:08:42
 * @Version: V1.0
 */
public interface KolCategoryMapper extends BaseMapper<KolCategory> {
	
	/**
	  *  根据父级ID查询树节点数据
	 * @param parentId
	 * @return
	 */
	public List<TreeSelectModel> queryListByParentId(@Param("parentId")  String parentId, @Param("query") Map<String, String> query);

	/**
	 * 递归查询类目路径(指定类目及其子类)
	 * @param categoryId 类目 ID
	 * @return 类目路径列表
	 */
	List<KolCategory> findCategoryWithSubNodes(@Param("categoryId") String categoryId);

	List<String> queryAttributeParentIdsByCategoryId(@Param("categoryId") String categoryId);
}
