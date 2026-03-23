package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.kol.model.TreeSelectModel;
import org.jeecg.modules.kol.entity.KolCategory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date:   2024年11月22日 18:12:13
 * @Version: V1.0
 */
public interface IKolCategoryService extends IService<KolCategory> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**
	 * 新增类目
	 * @param KolCategory
	 */
	void addKolCategory(KolCategory KolCategory);

	/**
	 * 修改类目-需要考虑业务中冗余的名称同步更新
	 * @param KolCategory
	 */
	void updateKolCategory(KolCategory KolCategory);
	

	/**
	  * 根据pid查询子节点集合
	 * @param pid
	 * @return
	 */
	public List<TreeSelectModel> queryListByParentId(String pid);

	/**
	 * 根据pid查询子节点集合,支持查询条件
	 * @param pid
	 * @param condition
	 * @return
	 */
	public List<TreeSelectModel> queryListByParentId(String pid, Map<String,String> condition);


	/**
	 * 查询所有数据，无分页
	 *
	 * @param queryWrapper
	 */
	public List<KolCategory> getTreeRootNodeList(Wrapper<KolCategory> queryWrapper);

	/**
	 * 级联删除节点，根据id
	 */
	@Transactional(rollbackFor = Exception.class)
	void deleteByIds(String ids) throws JeecgBootException;

    List<String> queryAttributeParentIdsByCategoryId(String categoryId);
}
