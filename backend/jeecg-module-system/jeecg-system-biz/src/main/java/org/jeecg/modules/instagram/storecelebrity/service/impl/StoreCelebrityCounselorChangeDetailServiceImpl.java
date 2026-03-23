package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeDetail;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeLog;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityCounselorChangeDetailMapper;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityCounselorChangeLogMapper;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityPrivateCounselorMapper;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityCounselorChangeDetailService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Description: 网红顾问变更日志明细
 * @Author: jeecg-boot
 * @Date: 2025-10-30
 * @Version: V1.0
 */
@Service
public class StoreCelebrityCounselorChangeDetailServiceImpl extends ServiceImpl<StoreCelebrityCounselorChangeDetailMapper, StoreCelebrityCounselorChangeDetail> implements IStoreCelebrityCounselorChangeDetailService {
    @Resource
    private StoreCelebrityCounselorChangeLogMapper changeLogMapper;
    @Resource
    private StoreCelebrityPrivateCounselorMapper counselorMapper;

    @Override
    public void createCounselorChangeDetail(StoreCelebrityCounselorChangeLog counselorChangeLog, List<StoreCelebrityPrivateCounselor> counselorSaveList,
                                            List<StoreCelebrityPrivateCounselor> counselorDelList, List<StoreCelebrityCounselorChangeDetail> counselorChangeDetails) {
        if (!counselorSaveList.isEmpty()) {
            counselorSaveList.forEach(counselor -> {
                counselorChangeDetails.add(createDetail(counselorChangeLog, counselor, 1));
            });
        }
        if (!counselorDelList.isEmpty()) {
            counselorDelList.forEach(counselor -> {
                counselorChangeDetails.add(createDetail(counselorChangeLog, counselor, 0));
            });
        }
    }

    public StoreCelebrityCounselorChangeDetail createDetail(StoreCelebrityCounselorChangeLog counselorChangeLog, StoreCelebrityPrivateCounselor counselor, int operationType) {
        StoreCelebrityCounselorChangeDetail detail = new StoreCelebrityCounselorChangeDetail();
        detail.setId(IdWorker.get32UUID());
        detail.setChangeLogId(counselorChangeLog.getId());
        detail.setCelebrityPrivateId(counselor.getCelebrityPrivateId());
        detail.setSourceRecordId(counselor.getId());
        detail.setOperationType(operationType);
        detail.setCelebrityCounselorId(counselor.getCelebrityCounselorId());
        detail.setCelebrityCounselorName(counselor.getCelebrityCounselorName());
        detail.setEmail(counselor.getEmail());
        detail.setCelebrityContactEmail(counselor.getCelebrityContactEmail());
        detail.setContractAmount(counselor.getContractAmount());
        detail.setContractTime(counselor.getContractTime());
        detail.setContractInfo(counselor.getContractInfo());
        detail.setSort(counselor.getSort());
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAdjustmentCounselor(String logId, List<StoreCelebrityPrivateCounselor> privateCounselorsSave, List<String> privateCounselorsIdsDel) {
        // 1. 取消调整日志
        StoreCelebrityCounselorChangeLog log = new StoreCelebrityCounselorChangeLog();
        log.setId(logId);
        log.setChangeStatus(Integer.valueOf(CommonConstant.STATUS__2));
        changeLogMapper.updateById(log);
        // 2. 还原顾问
        if (!privateCounselorsSave.isEmpty()) {
            privateCounselorsSave.forEach(counselor -> {
                counselorMapper.insert(counselor);
            });
        }
        if (!privateCounselorsIdsDel.isEmpty()) {
            counselorMapper.deleteBatchIds(privateCounselorsIdsDel);
        }
    }

    @Override
    public List<Map<String,Object>> queryList(StoreCelebrityCounselorChangeDetail changeDetail) {
        return this.baseMapper.queryList(changeDetail);
    }
}
