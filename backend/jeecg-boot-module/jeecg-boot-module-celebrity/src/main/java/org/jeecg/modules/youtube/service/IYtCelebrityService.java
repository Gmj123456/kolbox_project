package org.jeecg.modules.youtube.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.IKolAllotService;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecg.modules.youtube.model.YtCelebrityModel;

import java.util.Map;

/**
 * @Description: YT网红接口层
 * @Author: xyc
 * @Date: 2024-12-26 17:25:27
 * @Version: V1.0
 */
public interface IYtCelebrityService extends IService<YoutubeCelebrity>, IKolAllotService<KolBaseModel> {

  /**
   * yt网红总数
   *
   * @param searchModel
   * @return
   */
  Integer getCelebrityCount(KolSearchModel searchModel);


  /**
   * 获取tk网红列表
   *
   * @param page
   * @param searchModel
   * @return
   */
  IPage<YtCelebrityModel> getCelebrityModelList(Page<YtCelebrityModel> page, KolSearchModel searchModel);


  /**
   * 查询已分配网红总数
   *
   * @param searchModel
   * @return
   */
  Integer getAllottedKolCount(KolSearchModel searchModel);


  /**
   * 查询已分配网红列表
   *
   * @param page
   * @param searchModel
   * @return
   */
  IPage<YtCelebrityModel> getYtAllottedKolList(Page<YtCelebrityModel> page, KolSearchModel searchModel);

  /**
   * 查询已分配网红列表
   *
   * @param page
   * @param searchModel
   * @return 返回通用分页结果
   */
  IPage<KolBaseModel> getAllottedKolList(Page<KolBaseModel> page, KolSearchModel searchModel);

  /**
   * 方法描述: 查询未分配完的标签的网红数量
   *
   * @author nqr
   * @date 2025/01/06 15:02
   **/
  IPage<Map<String, Object>> getNotAllotTagsList(Page<YtCelebrityModel> page, KolSearchModel kolSearchModel);

  /**
   * 方法描述: 根据账号查询
   *
   * @author nqr
   * @date 2025/01/14 16:21
   **/
  YoutubeCelebrity getByAccount(String uniqueId);

  int getNotAllotTagsListCount(KolSearchModel kolSearchModel);
}
