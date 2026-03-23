package org.jeecg.modules.instagram.storeseller.storeuser.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.SellerCollection;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storeseller.storeuser.model.SellerCollectionAndCelebrityModel;

/**
 * @Description: 商家网红收藏表
 * @Author: jeecg-boot
 * @Date:   2020-10-27
 * @Version: V1.0
 */
public interface ISellerCollectionService extends IService<SellerCollection> {
    IPage<SellerCollectionAndCelebrityModel> parentList(Page<SellerCollectionAndCelebrityModel> page, SellerCollectionAndCelebrityModel sellerCollectionAndCelebrityModel);

}
