package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.kol.model.TreeSelectModel;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.model.KolAttributeModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date:   2024年11月22日 18:12:13
 * @Version: V1.0
 */
public interface IKolAttributeService extends IService<KolAttribute> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**
	 * 新增类目
	 * @param KolAttribute
	 */
	void addKolAttribute(KolAttribute KolAttribute) throws Exception;

	/**
	 * 修改类目-需要考虑业务中冗余的名称同步更新
	 * @param KolAttribute
	 */
	void updateKolAttribute(KolAttribute KolAttribute);
	

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
	public List<KolAttribute> getTreeRootNodeList(Wrapper<KolAttribute> queryWrapper);

	/**
	 * 级联删除节点，根据id
	 */
	@Transactional(rollbackFor = Exception.class)
	void deleteByIds(String ids) throws JeecgBootException;

	List<KolAttributeModel> getAttributeList(KolSearchModel searchModel);

	List<String> queryAttributeIdsByProductId(String productId,Integer platformType);

	/**
	 * 功能描述：获取社媒属性叶子节点
	 * @Param:
	 * @param type
	 * @param isParent
	 * @Author: fengLiu
	 * @Date: 2025-07-28 14:05
	 */
	List<Map<String, Object>> getKolAttributeLeafNodes(Integer type,String typeCode,Integer isParent);

	/**
	* 功能描述：获取产品对照的社媒属性（叶子节点）
	* @Param:
	 * @param productId
	* @Author: fengLiu
	* @Date: 2025-07-29 19:32
	*/
	List<String> queryProductAttributes(String productId);
}
