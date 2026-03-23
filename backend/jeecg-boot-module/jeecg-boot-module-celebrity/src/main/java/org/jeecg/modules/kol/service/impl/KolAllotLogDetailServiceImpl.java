package org.jeecg.modules.kol.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.modules.kol.entity.KolAllotLogDetail;
import org.jeecg.modules.kol.mapper.KolAllotLogDetailMapper;
import org.jeecg.modules.kol.service.IKolAllotLogDetailService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: kol_allot_log_detail
 * @Author: jeecg-boot
 * @Date:   2024-12-11
 * @Version: V1.0
 */
@Service
public class KolAllotLogDetailServiceImpl extends ServiceImpl<KolAllotLogDetailMapper, KolAllotLogDetail> implements IKolAllotLogDetailService {

  @Override
  public <T> void updateAllotLog(String counselorId, String counselorName, String logDetailId, String tagName, String logId, Class<T> celebrityTagsClass, T celebrityTagsNew) {
    // 获取现有的 LogDetail
    KolAllotLogDetail allotLogDetailOld = this.getById(logDetailId);

    // 生成新的 allotContent
    String allotContent = getAllotContent(allotLogDetailOld, tagName, true);
    KolAllotLogDetail logDetailOld = new KolAllotLogDetail();
    logDetailOld.setId(logDetailId);
    logDetailOld.setAllotContent(allotContent);

    // 查询相关的 allotLogDetail
    List<KolAllotLogDetail> logDetailList = this.lambdaQuery().eq(KolAllotLogDetail::getAllotId, logId).list();
    Optional<KolAllotLogDetail> optional = logDetailList.stream().filter(x -> x.getCounselorId().equals(counselorId)).findFirst();
    KolAllotLogDetail allotLogDetailNew = new KolAllotLogDetail();

    if (optional.isPresent()) {
      KolAllotLogDetail allotLogDetail = optional.get();
      allotLogDetailNew.setId(allotLogDetail.getId());
      String content = getAllotContent(allotLogDetail, tagName, false);
      allotLogDetailNew.setAllotContent(content);

      // 使用反射设置 allotLogDetailId
      setAllotLogDetailId(celebrityTagsClass, celebrityTagsNew, allotLogDetail.getId());
    } else {
      // 如果没有找到对应的 LogDetail，创建新的一条
      String detailId = IdWorker.get32UUID();
      allotLogDetailNew.setId(detailId);
      allotLogDetailNew.setAllotId(logId);
      allotLogDetailNew.setCounselorId(counselorId);
      allotLogDetailNew.setCounselorName(counselorName);
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(tagName, 1);
      allotLogDetailNew.setAllotContent(jsonObject.toJSONString());
      allotLogDetailNew.setAllotTime(new Date());

      // 使用反射设置 allotLogDetailId
      setAllotLogDetailId(celebrityTagsClass, celebrityTagsNew, detailId);
    }

    // 更新 logDetail 和 allotLogDetail
    this.updateById(logDetailOld);
    this.saveOrUpdate(allotLogDetailNew);
  }


  // 通过反射设置 allotLogDetailId
  private <T> void setAllotLogDetailId(Class<T> celebrityTagsClass, T celebrityTagsNew, String logDetailId) {
    try {
      // 使用反射获取 celebrityTagsClass 的 `setAllotLogDetailId` 方法
      Method method = celebrityTagsClass.getMethod("setAllotLogDetailId", String.class);
      // 调用该方法设置 allotLogDetailId
      method.invoke(celebrityTagsNew, logDetailId);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("设置 allotLogDetailId 失败：" + e);
    }
  }


  private String getAllotContent(KolAllotLogDetail allotLogDetail, String tagName, boolean flag) {
    if (allotLogDetail == null) {
      return "";
    }
    JSONObject jsonObject = JSON.parseObject(allotLogDetail.getAllotContent());
    if (!jsonObject.containsKey(tagName)) {
      jsonObject.put(tagName, 1);
      return jsonObject.toJSONString();
    }
    Integer integer = jsonObject.getInteger(tagName);
    int max = Math.max(flag ? integer - 1 : integer + 1, 0);
    if (max > 0) {
      jsonObject.put(tagName, max);
    } else {
      jsonObject.remove(tagName);
    }
    return jsonObject.toJSONString();
  }
}
