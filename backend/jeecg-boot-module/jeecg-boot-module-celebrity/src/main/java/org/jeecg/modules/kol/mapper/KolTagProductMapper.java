package org.jeecg.modules.kol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolTagProduct;

import java.util.List;

/**
 * @Description: 标签产品管理表
 * @Author: nqr
 * @Date:   2025-06-28
 * @Version: V1.0
 */
public interface KolTagProductMapper extends BaseMapper<KolTagProduct> {

    List<String> setBrandProductTags(@Param("productIds") List<String> productIds);

    List<KolTagProduct> getTagProductDetails(@Param("tagIds") List<String> tagIds);
}
