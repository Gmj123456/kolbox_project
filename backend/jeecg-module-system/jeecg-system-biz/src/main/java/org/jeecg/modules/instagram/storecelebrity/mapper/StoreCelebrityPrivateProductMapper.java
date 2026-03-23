package org.jeecg.modules.instagram.storecelebrity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateProductModel;
import org.jeecg.modules.kol.entity.KolProduct;

import java.util.List;

/**
 * @Description: 私有网红品牌产品关联表
 * @Author: nqr
 * @Date:   2025-06-03
 * @Version: V1.0
 */
public interface StoreCelebrityPrivateProductMapper extends BaseMapper<StoreCelebrityPrivateProduct> {

    /**
     * 通过历史合作、历史选中状态查询产品列表
     *
     * @param relationStatus
     * @return
     */
    List<KolProduct> queryProducts(@Param("relationStatus") Integer relationStatus);

    List<StoreCelebrityPrivateProductModel> getProductList(@Param("celebrityPrivateIds") List<String> celebrityPrivateIds);
}
