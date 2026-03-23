package org.jeecg.modules.kol.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolTagBrand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.kol.model.KolTagBrandExportModel;

/**
 * @Description: 标签品牌表
 * @Author: dongruyang
 * @Date:   2024-07-18
 * @Version: V1.0
 */
public interface KolTagBrandMapper extends BaseMapper<KolTagBrand> {

    IPage<KolTagBrand> pageList(Page<KolTagBrand> page, @Param("kolTagBrand") KolTagBrand kolTagBrand);

    List<KolTagBrandExportModel> queryList(@Param("kolTagBrand") KolTagBrand kolTagBrand);

    void saveBatchData(@Param("tkTagsBrands") List<KolTagBrand> tkTagsBrands);
}
