package org.jeecg.modules.kol.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.kol.entity.KolAllotLog;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.mapper.KolAllotLogMapper;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagsResultModel;
import org.jeecg.modules.kol.service.IKolAllotLogService;
import org.jeecg.modules.kol.service.IKolAttributeService;
import org.jeecg.modules.kol.service.IKolProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: 分配日志表
 * @Author: dongruyang
 * @Date: 2025-09-17
 * @Version: V1.0
 */
@Service
public class KolAllotLogServiceImpl extends ServiceImpl<KolAllotLogMapper, KolAllotLog> implements IKolAllotLogService {
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IKolProductService productService;
    @Autowired
    private IKolAttributeService attributeService;

    @Override
    public void createKolAllotLog(KolSearchModel searchModel, String allotLogId) {
        List<String> counselorIds = searchModel.getCelebrityCounselorList();
        List<LoginUser> counselorList = sysBaseAPI.getUserByIds(counselorIds);
        String counselorNames = counselorList.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
        Integer allotType = Optional.ofNullable(searchModel.getAllotType()).orElse(1);
        KolAllotLog log = new KolAllotLog();
        log.setId(allotLogId);
        log.setPlatformType(searchModel.getPlatformType());
        log.setAllotCounselorNames(counselorNames);
        checkContent(searchModel, allotType, log);
        log.setAllotStartTime(new Date());
        // log.setAllotEndTime();
        log.setAllotType(allotType);
        log.setAllotStatus(CommonConstant.KOL_ALOT_LOG_STATUS_RUNNIN);
        log.setIsDelete(0);
        save(log);
    }

    private void checkContent(KolSearchModel searchModel, Integer allotType, KolAllotLog log) {
        switch (allotType) {
            case 1:
                log.setAllotContent(String.join(",", searchModel.getTagNameList()));
                break;
            case 2:
                String productId = searchModel.getProductId();
                KolProduct kolProduct = productService.getById(productId);
                log.setAllotContent(kolProduct.getBrandName() + "-" + kolProduct.getProductName());
                break;
            case 3:
                String attributeIds = searchModel.getAttributeIds();
                KolAttribute attribute = attributeService.getById(attributeIds);
                log.setAllotContent(attribute.getAttributeName());
                break;
        }
    }

    @Override
    public void updateKolAllotLog(String allotLogId, int allotStatus, String message) {
        if (allotStatus == CommonConstant.KOL_ALOT_LOG_STATUS_FAIL) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", message);
            message = jsonObject.toJSONString();
        }
        lambdaUpdate()
                .eq(KolAllotLog::getId, allotLogId)
                .set(KolAllotLog::getAllotStatus, allotStatus)
                .set(KolAllotLog::getAllotEndTime, new Date())
                .set(KolAllotLog::getAllotResult, message)
                .update();
    }

    @Override
    public String createResultJson(List<KolTagsResultModel> resultList) {
        List<JSONObject> jsonList = new ArrayList<>();
        for (KolTagsResultModel result : resultList) {
            if (result.getIsAllotNum() == null || result.getIsAllotNum() == 0) continue;
            jsonList.add(JSON.parseObject(JSON.toJSONString(result)));
        }
        return JSON.toJSONString(jsonList);
    }
}
