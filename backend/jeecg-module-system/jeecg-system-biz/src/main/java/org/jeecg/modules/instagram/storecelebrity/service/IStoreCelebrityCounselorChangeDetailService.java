package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeDetail;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeLog;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;

import java.util.List;
import java.util.Map;

/**
 * @Description: 网红顾问变更日志明细
 * @Author: jeecg-boot
 * @Date:   2025-10-30
 * @Version: V1.0
 */
public interface IStoreCelebrityCounselorChangeDetailService extends IService<StoreCelebrityCounselorChangeDetail> {

    void createCounselorChangeDetail(StoreCelebrityCounselorChangeLog counselorChangeLog, List<StoreCelebrityPrivateCounselor> counselorSaveList, List<StoreCelebrityPrivateCounselor> counselorDelList, List<StoreCelebrityCounselorChangeDetail> counselorChangeDetails);

    void cancelAdjustmentCounselor(String logId, List<StoreCelebrityPrivateCounselor> privateCounselorsSave, List<String> privateCounselorsIdsDel);

    List<Map<String,Object>> queryList(StoreCelebrityCounselorChangeDetail changeDetail);
}
