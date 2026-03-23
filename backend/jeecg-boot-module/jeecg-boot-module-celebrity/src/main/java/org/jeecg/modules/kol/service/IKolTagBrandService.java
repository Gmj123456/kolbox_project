package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.kol.entity.KolTagBrand;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.model.KolTagBrandExportModel;

import java.util.List;

/**
 * @Description: 标签品牌表
 * @Author: dongruyang
 * @Date: 2024-07-18
 * @Version: V1.0
 */
public interface IKolTagBrandService extends IService<KolTagBrand> {

    IPage<KolTagBrand> pageList(Page<KolTagBrand> page, KolTagBrand kolTagBrand);

    List<KolTagBrandExportModel> queryList(KolTagBrand kolTagBrand);

    KolTagBrand checkTagsBrand(String tagId, String brandName, Integer platformType);

    void saveBatchData(List<KolTagBrand> tkTagsBrands);

    void removeByTagsId(String id);

    String getByTagsId(String id);
}
