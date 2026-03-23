package org.jeecg.modules.kol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.model.KolProductModel;

import java.util.List;

/**
 * @Description: 品牌与产品关联表
 * @Author: nqr
 * @Date:   2025-05-24
 * @Version: V1.0
 */
public interface KolProductMapper extends BaseMapper<KolProduct> {
    /**
     * 分页查询品牌与产品关联表
     *
     * @param page
     * @param kolProductModel
     * @return
     */
    IPage<KolProductModel> pageList(Page<KolProductModel> page, @Param("kolProductModel") KolProductModel kolProductModel);

    IPage<KolProductModel> pageListNew(Page<KolProductModel> page, @Param("kolProductModel") KolProductModel kolProductModel);

    List<KolProductModel> getProductListAll(@Param("kolProductModel") KolProductModel kolProductModel);

    List<KolProduct> getProductListByBrandId(@Param("brandId") String brandId);
}
