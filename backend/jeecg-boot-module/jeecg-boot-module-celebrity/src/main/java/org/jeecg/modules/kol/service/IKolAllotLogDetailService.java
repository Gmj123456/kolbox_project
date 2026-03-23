package org.jeecg.modules.kol.service;

import org.apache.poi.ss.formula.functions.T;
import org.jeecg.modules.kol.entity.KolAllotLogDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;

/**
 * @Description: kol_allot_log_detail
 * @Author: jeecg-boot
 * @Date:   2024-12-11
 * @Version: V1.0
 */
public interface IKolAllotLogDetailService extends IService<KolAllotLogDetail> {
  /**
   * 更新分配日志
   * @param counselorId 网红顾问ID
   * @param counselorName 网红顾问名称
   * @param logDetailId 分配日志详情ID
   * @param tagName 分配的tag名称
   * @param logId 分配日志ID
   * @param celebrityTagsNew 标签实体类实例
   * @param <T> 标签实体类类型
   */
  <T> void updateAllotLog(String counselorId, String counselorName, String logDetailId, String tagName, String logId, Class<T> celebrityTagsClass, T celebrityTagsNew);
}
