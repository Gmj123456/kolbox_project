package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.IKolCelebrityApi;
import org.jeecg.common.system.vo.CelebrityPrivateModel;
import org.jeecg.common.system.vo.KolProductVo;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityPrivateMapper;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateProductService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
@Service
public class KolCelebrityApiServiceImpl extends ServiceImpl<StoreCelebrityPrivateMapper, StoreCelebrityPrivate> implements IKolCelebrityApi {
    @Autowired
    private IStoreCelebrityPrivateProductService privateProductMapperService;
    @Autowired(required = false)
    private IStorePromotionGoodsService storePromotionGoodsService;
    @Autowired
    private IStoreCelebrityPrivateService privateService;

    @Override
    public Integer getByProductId(String id) {
        return Math.toIntExact(privateProductMapperService.lambdaQuery().eq(StoreCelebrityPrivateProduct::getProductId, id).count());
    }

    @Override
    public void updatePromotionGoodsInfo(KolProductVo productVo) {
        storePromotionGoodsService.updatePromotionGoodsInfo(productVo);
    }

    @Override
    public boolean getPromGoods(String id) {
        return storePromotionGoodsService.getPromGoods(id);
    }

    @Override
    public List<CelebrityPrivateModel> getPrivateCelebrityList(List<String> celebrityNames) {
        if (celebrityNames == null || celebrityNames.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // 根据账号精确匹配查询
            LambdaQueryWrapper<StoreCelebrityPrivate> accountWrapper = new LambdaQueryWrapper<>();
            accountWrapper.in(StoreCelebrityPrivate::getAccount, celebrityNames);
            List<StoreCelebrityPrivate> accountResult = privateService.list(accountWrapper);

            // 转换为Model并返回
            return accountResult.stream()
                    .map(c -> {
                        CelebrityPrivateModel model = new CelebrityPrivateModel();
                        // 复制所有属性
                        org.springframework.beans.BeanUtils.copyProperties(c, model);
                        return model;
                    })
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("获取私有网红列表失败", e);
            return Collections.emptyList();
        }
    }

}
