package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeDetail;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeLog;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityCounselorChangeLogMapper;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityPrivateCounselorMapper;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateCounselorModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityCounselorChangeDetailService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityCounselorChangeLogService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateCounselorService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;
import org.jeecg.modules.instagram.storeseller.storeuser.mapper.StoreUserContactEmailMapper;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserContactEmailService;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 私有网红网红顾问签约表
 * @Author: nqr
 * @Date: 2023-09-05
 * @Version: V1.0
 */
@Service
public class StoreCelebrityPrivateCounselorServiceImpl extends ServiceImpl<StoreCelebrityPrivateCounselorMapper, StoreCelebrityPrivateCounselor> implements IStoreCelebrityPrivateCounselorService {

    @Autowired
    private IStoreCelebrityPrivateService celebrityPrivateService;
    @Resource
    private IStoreCelebrityCounselorChangeLogService changeLogService;
    @Resource
    private IStoreCelebrityCounselorChangeDetailService changeDetailService;
    @Resource
    private StoreUserContactEmailMapper contactEmailMapper;

    /**
     * 功能描述:创建并保存网红顾问签约数据
     *
     * @return void
     * @Date 2023-09-05 10:31:30
     */
    @Override
    public void createPrivateCounselor(StoreCelebrityPrivate storeCelebrityPrivate, String privateCounselorId) {
        StoreCelebrityPrivateCounselor privateCounselor = new StoreCelebrityPrivateCounselor();
        privateCounselor.setId(privateCounselorId)
                .setCelebrityPrivateId(storeCelebrityPrivate.getId())
                .setCelebrityCounselorId(storeCelebrityPrivate.getCelebrityCounselorId())
                .setCelebrityCounselorName(storeCelebrityPrivate.getCelebrityCounselorName())
                .setContractAmount(storeCelebrityPrivate.getContractAmount())
                .setContractTime(storeCelebrityPrivate.getContractTime())
                .setEmail(storeCelebrityPrivate.getEmail())
                .setCelebrityContactEmail(storeCelebrityPrivate.getCelebrityContactEmail())
                .setContractInfo(storeCelebrityPrivate.getContractInfo())
                .setSort(1);
        this.saveOrUpdate(privateCounselor);
    }

    @Override
    public StoreCelebrityPrivateCounselor getByCelebrityCounselorId(String celebrityCounselorId, String privateId) {
        LambdaQueryWrapper<StoreCelebrityPrivateCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreCelebrityPrivateCounselor::getCelebrityCounselorId, celebrityCounselorId);
        queryWrapper.eq(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, privateId);
        return this.list(queryWrapper).stream().findFirst().orElse(null);
    }

    @Override
    public List<StoreCelebrityPrivateCounselorModel> queryByCelebrityPrivateId(String id) {
        return this.baseMapper.queryByCelebrityPrivateId(id);
    }

    /**
     * 功能描述：更新建联邮箱对应网红顾问
     *
     * @param counselorModel
     * @Param:
     * @Author: fengLiu
     * @Date: 2024-01-15 18:02
     */
    @Override
    public void updateCounselorByEmails(StoreCelebrityPrivateCounselorModel counselorModel) {
        this.baseMapper.updateCounselorByEmails(counselorModel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCounselorUser(List<StoreCelebrityPrivate> celebrityPrivateList, List<StoreCelebrityPrivateCounselor> counselorSaveList,
                                    List<StoreCelebrityPrivateCounselor> counselorDelList, List<StoreUserContactEmail> contactEmailNewList,
                                    StoreCelebrityCounselorChangeLog counselorChangeLog, List<StoreCelebrityCounselorChangeDetail> counselorChangeDetails) {
        if (!celebrityPrivateList.isEmpty()) {
            celebrityPrivateService.updateBatchById(celebrityPrivateList);
        }
        if (counselorSaveList != null && !counselorSaveList.isEmpty()) {
            this.saveBatch(counselorSaveList);
        }
        if (counselorDelList != null && !counselorDelList.isEmpty()) {
            List<String> delPrivateCounselorIds = counselorDelList.stream().map(StoreCelebrityPrivateCounselor::getId).collect(Collectors.toList());
            this.baseMapper.deleteBatchIds(delPrivateCounselorIds);
        }
        if (!contactEmailNewList.isEmpty()) {
            contactEmailNewList.forEach(contactEmail -> contactEmailMapper.insert(contactEmail));
        }
        if (counselorChangeLog != null) {
            changeLogService.save(counselorChangeLog);
        }
        if (counselorChangeDetails != null && !counselorChangeDetails.isEmpty()) {
            changeDetailService.saveBatch(counselorChangeDetails);
        }
    }


    @Override
    public List<StoreCelebrityPrivateCounselor> getByCelebrityIds(List<String> celebrityIds) {
        return lambdaQuery().in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, celebrityIds)
                .isNotNull(StoreCelebrityPrivateCounselor::getCelebrityCounselorId)
                .isNotNull(StoreCelebrityPrivateCounselor::getCelebrityPrivateId)
                .list();
    }
}
