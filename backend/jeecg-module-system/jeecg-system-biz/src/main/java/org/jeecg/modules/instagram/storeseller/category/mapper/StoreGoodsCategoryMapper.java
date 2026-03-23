package org.jeecg.modules.instagram.storeseller.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.instagram.storeseller.category.entity.StoreGoodsCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 商家商品类目表
 * @Author: jeecg-boot
 * @Date:   2020-10-12
 * @Version: V1.0
 */
public interface StoreGoodsCategoryMapper extends BaseMapper<StoreGoodsCategory> {

    /**
     *   修改菜单状态字段： 是否子节点
     */
    @Update("update store_goods_category set is_leaf=#{leaf} where id = #{id}")
    public int setCategoryLeaf(@Param("id") String id,@Param("leaf") int leaf);

    /**
     *   查询类目字段
     */
    @Select("SELECT id,parent_id as parentId,category_name as categoryName FROM `store_goods_category` WHERE parent_id =#{value} ORDER BY sort_code")
    List<StoreGoodsCategory> queryGoodsCategoryList(String parentId);
}
