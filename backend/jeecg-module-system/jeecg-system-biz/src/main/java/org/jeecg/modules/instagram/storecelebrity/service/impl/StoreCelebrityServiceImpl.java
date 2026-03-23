package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.CelebrityConsignee;
import org.jeecg.modules.instagram.storecelebrity.mapper.CelebrityConsigneeMapper;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.jeecg.modules.instagram.storecelebrity.service.IMessagePushDetailService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityMapper;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityModel;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPriceModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrice;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityPriceMapper;
import org.jeecg.modules.instagram.storemerchant.entity.StoreEmailPackagePurchase;
import org.jeecg.modules.instagram.storemerchant.service.IStoreEmailPackagePurchaseService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 网红档案
 * @Author: nqr
 * @Date: 2020-04-20
 * @Version: V1.0
 */
@Service
public class StoreCelebrityServiceImpl extends ServiceImpl<StoreCelebrityMapper, StoreCelebrity> implements IStoreCelebrityService {

    @Autowired
    private StoreCelebrityMapper storeCelebrityMapper;

    @Autowired
    private StoreCelebrityPriceMapper storeCelebrityPriceMapper;

    @Autowired
    private IMessagePushDetailService messagePushDetailService;

    @Autowired
    private IStoreEmailPackagePurchaseService packagePurchaseService;

    @Autowired
    private CelebrityConsigneeMapper celebrityConsigneeMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public IPage<StoreCelebrity> seedFollowingList(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel) {
        return storeCelebrityMapper.seedFollowingList(page, storeCelebrityModel);
    }

    /**
     * 宣传页展示列表
     *
     * @return
     */
    @Override
    public List<StoreCelebrity> showList() {
        return storeCelebrityMapper.showList();
    }

    @Override
    public List<StoreCelebrity> getOldList() {
        return storeCelebrityMapper.getOldList();
    }

    @Override
    public IPage<StoreCelebrity> celebrityValidation(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel) {
        return storeCelebrityMapper.celebrityValidation(page, storeCelebrityModel);
    }

    /**
     * 修改增量包使用额度，增加邮件发送明细
     *
     * @param pushDetailList
     * @param packagePurchaseList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPushEmail(List<MessagePushDetail> pushDetailList, List<StoreEmailPackagePurchase> packagePurchaseList, SysUser sysUserNew) {
        long start = System.currentTimeMillis();
        if (oConvertUtils.isNotEmpty(pushDetailList)) {
            if (pushDetailList.size() > 0) {
                messagePushDetailService.saveBatch(pushDetailList);
            }
        }
        if (oConvertUtils.isNotEmpty(packagePurchaseList)) {
            if (packagePurchaseList.size() > 0) {
                packagePurchaseService.updateBatchById(packagePurchaseList);
            }
        }
        if (oConvertUtils.isNotEmpty(sysUserNew) && oConvertUtils.isNotEmpty(sysUserNew.getId())) {
            sysUserService.updateById(sysUserNew);
        }
        long end = System.currentTimeMillis();
        System.err.println("耗时：" + ((end - start) / 1000) + "秒");
    }

    /**
     * 事务更新网红数据与网红price数据
     *
     * @param storeCelebrity
     * @param storeCelebrityPrice
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void storeCelebrityEdit(StoreCelebrity storeCelebrity, StoreCelebrityPrice storeCelebrityPrice, CelebrityConsignee celebrityConsignee) {
        if (null != storeCelebrity && null != storeCelebrity.getId() && storeCelebrity.getId().length() > 0) {
            storeCelebrityMapper.updateById(storeCelebrity);
        }
        if (null != storeCelebrityPrice && null != storeCelebrityPrice.getId() && storeCelebrityPrice.getId().length() > 0) {
            storeCelebrityPriceMapper.updateById(storeCelebrityPrice);
        } else {
            storeCelebrityPriceMapper.insert(storeCelebrityPrice);
        }
        if (null != celebrityConsignee && null != celebrityConsignee.getId() && celebrityConsignee.getId().length() > 0) {
            celebrityConsigneeMapper.updateById(celebrityConsignee);
        } else {
            celebrityConsigneeMapper.insert(celebrityConsignee);
        }
    }

    @Override
    public IPage<StoreCelebrity> followingList(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel) {
        return storeCelebrityMapper.followingList(page, storeCelebrityModel);
    }

    @Override
    public IPage<StoreCelebrity> parentList(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel) {
        return storeCelebrityMapper.parentList(page, storeCelebrityModel);
    }

    @Override
    public IPage<StoreCelebrityPriceModel> getStoreList(Page<StoreCelebrity> page, StoreCelebrityModel storeCelebrityModel) {
        return storeCelebrityMapper.getStoreList(page, storeCelebrityModel);
    }

    /**
     * 根据邮箱获得网红对象
     *
     * @param email
     */
    @Override
    public List<StoreCelebrity> queryByEmail(String email) {
        return storeCelebrityMapper.queryByEmail(email);
    }

    /**
     * 根据userName查询网红对象
     *
     * @Param userName
     */
    @Override
    public List<StoreCelebrity> queryByUserName(String userName) {
        return storeCelebrityMapper.queryByUserName(userName);
    }

    /**
     * 功能描述:查询热门网红条数
     *
     * @return java.lang.Integer
     * @Date 2023-11-03 09:22:51
     */
    @Override
    public Integer getStoreListCount(StoreCelebrityModel storeCelebrityModel) {
        return storeCelebrityMapper.getStoreListCount(storeCelebrityModel);
    }
}
