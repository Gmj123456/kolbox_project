package org.jeecg.modules.instagram.storeseller.storeuser.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.SellerCollection;
import org.jeecg.modules.instagram.storeseller.storeuser.mapper.SellerCollectionMapper;
import org.jeecg.modules.instagram.storeseller.storeuser.model.SellerCollectionAndCelebrityModel;
import org.jeecg.modules.instagram.storeseller.storeuser.service.ISellerCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商家网红收藏表
 * @Author: jeecg-boot
 * @Date:   2020-10-27
 * @Version: V1.0
 */
@Service
public class SellerCollectionServiceImpl extends ServiceImpl<SellerCollectionMapper, SellerCollection> implements ISellerCollectionService {

    @Autowired
    SellerCollectionMapper sellerCollectionMapper;

    @Override
    public IPage<SellerCollectionAndCelebrityModel> parentList(Page<SellerCollectionAndCelebrityModel> page, SellerCollectionAndCelebrityModel sellerCollectionAndCelebrityModel) {
        return sellerCollectionMapper.parentList(page,sellerCollectionAndCelebrityModel);
    }
}
