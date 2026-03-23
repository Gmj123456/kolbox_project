package org.jeecg.modules.instagram.storecelebrity.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityModel;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPriceModel;

import java.util.List;

/**
 * @Description: 网红档案
 * @Author: nqr
 * @Date: 2020-04-20
 * @Version: V1.0
 */
public interface StoreCelebrityMapper extends BaseMapper<StoreCelebrity> {

    IPage<StoreCelebrityPriceModel> getStoreList(Page<StoreCelebrity> page, @Param("storeCelebrityModel") StoreCelebrityModel storeCelebrityModel);

    IPage<StoreCelebrity> parentList(Page<StoreCelebrity> page, @Param("storeCelebrityModel") StoreCelebrityModel storeCelebrityModel);

    IPage<StoreCelebrity> followingList(Page<StoreCelebrity> page, @Param("storeCelebrityModel") StoreCelebrityModel storeCelebrityModel);

    IPage<StoreCelebrity> seedFollowingList(Page<StoreCelebrity> page, @Param("storeCelebrityModel") StoreCelebrityModel storeCelebrityModel);

    IPage<StoreCelebrity> celebrityValidation(Page<StoreCelebrity> page, @Param("storeCelebrityModel") StoreCelebrityModel storeCelebrityModel);


    List<StoreCelebrity> showList();

    List<StoreCelebrity> getOldList();

    /**
     * 根据邮箱查询网红
     */
    List<StoreCelebrity> queryByEmail(@Param("email") String email);

    /**
     * 根据userName查询网红
     */
    List<StoreCelebrity> queryByUserName(@Param("userName") String userName);

    /**
     * 功能描述:查询热门网红条数
     *
     * @return java.lang.Integer
     * @Date 2023-11-03 09:23:03
     */
    Integer getStoreListCount(@Param("storeCelebrityModel") StoreCelebrityModel storeCelebrityModel);
}
