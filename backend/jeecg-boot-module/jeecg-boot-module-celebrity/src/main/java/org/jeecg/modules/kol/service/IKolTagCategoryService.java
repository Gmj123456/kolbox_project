package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.kol.model.TreeSelectModel;
import org.jeecg.modules.kol.entity.KolTagCategory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date:   2024年11月22日 18:12:13
 * @Version: V1.0
 */
public interface IKolTagCategoryService extends IService<KolTagCategory> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**
	 * 新增类目
	 * @param kolTagCategory
	 */
	void addKolTagCategory(KolTagCategory kolTagCategory);

	/**
	 * 修改类目-需要考虑业务中冗余的名称同步更新
	 * @param kolTagCategory
	 */
	void updateKolTagCategory(KolTagCategory kolTagCategory);
	

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
	public List<KolTagCategory> getTreeRootNodeList(Wrapper<KolTagCategory> queryWrapper);

	/**
	 * 级联删除节点，根据id
	 */
	@Transactional(rollbackFor = Exception.class)
	void deleteByIds(String ids) throws JeecgBootException;
	
}
