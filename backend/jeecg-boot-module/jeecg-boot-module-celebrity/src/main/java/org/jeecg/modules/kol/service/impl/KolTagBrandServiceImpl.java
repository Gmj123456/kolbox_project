package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolTagBrand;
import org.jeecg.modules.kol.mapper.KolTagBrandMapper;
import org.jeecg.modules.kol.model.KolTagBrandExportModel;
import org.jeecg.modules.kol.service.IKolTagBrandService;
import org.springframework.stereotype.Service;

/**
 * @Description: 标签品牌表
 * @Author: dongruyang
 * @Date: 2024-07-18
 * @Version: V1.0
 */
@Service
public class KolTagBrandServiceImpl extends ServiceImpl<KolTagBrandMapper, KolTagBrand> implements IKolTagBrandService {

  @Override
  public IPage<KolTagBrand> pageList(Page<KolTagBrand> page, KolTagBrand kolTagBrand) {
    return this.baseMapper.pageList(page, kolTagBrand);
  }

  @Override
  public List<KolTagBrandExportModel> queryList(KolTagBrand kolTagBrand) {
    return this.baseMapper.queryList(kolTagBrand);
  }

  @Override
  public KolTagBrand checkTagsBrand(String tagId, String brandName, Integer platformType) {
    LambdaQueryWrapper<KolTagBrand> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(KolTagBrand::getTagId, tagId);
    if (oConvertUtils.isNotEmpty(brandName)) {
      queryWrapper.eq(KolTagBrand::getBrandName, brandName);
    }
    queryWrapper.eq(KolTagBrand::getPlatformType, platformType);
    queryWrapper.last("limit 1");
    return this.getOne(queryWrapper);
  }

  @Override
  public void saveBatchData(List<KolTagBrand> tkTagsBrands) {
    this.baseMapper.saveBatchData(tkTagsBrands);
  }

  @Override
  public void removeByTagsId(String id) {
    LambdaUpdateWrapper<KolTagBrand> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(KolTagBrand::getTagId, id);
    this.remove(updateWrapper);
  }

  @Override
  public String getByTagsId(String id) {
    LambdaQueryWrapper<KolTagBrand> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(KolTagBrand::getTagId, id);
    List<KolTagBrand> list = this.list(queryWrapper);
    if (list != null && !list.isEmpty()) {
      List<String> collect = list.stream().map(KolTagBrand::getBrandName).distinct().collect(Collectors.toList());
      return String.join(",", collect);
    }
    return "";
  }
}
