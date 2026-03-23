package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.entity.KolBrand;

import java.util.List;

/**
 * @Description: 品牌表
 * @Author: xyc
 * @Date: 2024-12-18 19:48:39
 * @Version: V1.0
 */
public interface IKolBrandService extends IService<KolBrand> {

  KolBrand getBrandName(String brandName);

  List<KolBrand> listByNames(List<String> brandNames);

  List<KolBrand> queryListAll(String brandName);

    void saveOrUpdateImportDataBatch(List<KolBrand> brandListSave, List<KolBrand> brandListUpdate);

    void editBrandData(KolBrand kolBrand);
}
