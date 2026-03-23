package org.jeecg.modules.instagram.storeseller.storeuser.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.SellerCollection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.instagram.storeseller.storeuser.model.SellerCollectionAndCelebrityModel;

/**
 * @Description: 商家网红收藏表
 * @Author: jeecg-boot
 * @Date:   2020-10-27
 * @Version: V1.0
 */
public interface SellerCollectionMapper extends BaseMapper<SellerCollection> {
    IPage<SellerCollectionAndCelebrityModel> parentList(Page<SellerCollectionAndCelebrityModel> page, @Param("sellerCollectionAndCelebrityModel") SellerCollectionAndCelebrityModel sellerCollectionAndCelebrityModel);


}
