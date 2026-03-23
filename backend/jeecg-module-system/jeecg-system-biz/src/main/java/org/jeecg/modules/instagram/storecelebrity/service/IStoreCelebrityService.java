package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storecelebrity.entity.CelebrityConsignee;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityModel;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPriceModel;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrice;
import org.jeecg.modules.instagram.storemerchant.entity.StoreEmailPackagePurchase;
import org.jeecg.modules.system.entity.SysUser;

import java.util.List;

/**
 * @Description: 网红档案
 * @Author: nqr
 * @Date: 2020-04-20
 * @Version: V1.0
 */
public interface IStoreCelebrityService extends IService<StoreCelebrity> {

    IPage<StoreCelebrityPriceModel> getStoreList(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel);

    IPage<StoreCelebrity> parentList(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel);

    IPage<StoreCelebrity> followingList(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel);

    IPage<StoreCelebrity> seedFollowingList(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel);

    /**
     * 事务更新网红数据与网红price数据
     *
     * @param storeCelebrity
     * @param storeCelebrityPrice
     */
    void storeCelebrityEdit(StoreCelebrity storeCelebrity, StoreCelebrityPrice storeCelebrityPrice, CelebrityConsignee celebrityConsignee);

    /**
     * 事务添加邮箱，修改增量包余额
     *
     * @param pushDetailList
     * @param packagePurchaseList
     */
    void addPushEmail(List<MessagePushDetail> pushDetailList, List<StoreEmailPackagePurchase> packagePurchaseList, SysUser sysUserNew);

    /**
     * 宣传页展示列表
     *
     * @return
     */
    List<StoreCelebrity> showList();


    /**
     * 随机获取30条数据
     *
     * @return
     */
    List<StoreCelebrity> getOldList();

    /**
     * 网红验证列表
     */
    IPage<StoreCelebrity> celebrityValidation(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel);

    /**
     * 根据邮箱获得网红对象
     */
    List<StoreCelebrity> queryByEmail(String email);

    /**
     * 通过userName查询网红对象
     */
    List<StoreCelebrity> queryByUserName(String userName);

    /**
     * 功能描述:查询热门网红条数
     *
     * @return java.lang.Integer
     * @Date 2023-11-03 09:22:35
     */
    Integer getStoreListCount(StoreCelebrityModel storeCelebrityModel);
}
