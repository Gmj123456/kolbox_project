package org.jeecg.modules.tiktok.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.entity.KolAllotLogDetail;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagCountModel;
import org.jeecg.modules.kol.model.KolTagUpdateDTO;
import org.jeecg.modules.kol.model.UserTagAllotModel;
import org.jeecg.modules.kol.service.IKolTagCelebrityService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;
import org.jeecg.modules.tiktok.model.TiktokCelebrityTagsModel;

import java.util.List;

/**
 * @Description: 网红tag表
 * @Author: xyc
 * @Date: 2023-10-10
 * @Version: V1.0
 */
public interface ITiktokCelebrityTagsService extends IService<TiktokCelebrityTags>, IKolTagCelebrityService {

    /**
     * 查询未分配网红标签的数量
     *
     * @param searchModel
     * @return
     */
    Long getUnallottedTagCount(KolSearchModel searchModel);

    /**
     * 查询未分配网红标签的数量 改进版本
     * 2025年4月8日11:34:15
     * 刘峰
     *
     * @param searchModel
     * @return
     */
    Long getUnallottedTagCountPlus(KolSearchModel searchModel);


    /**
     * 获取网红标签未分配列表
     *
     * @param searchModel
     * @return
     */
    List<UserTagAllotModel> getUnallottedTagList(KolSearchModel searchModel);

    /**
     * 获取网红标签未分配列表升级版
     * 2025年4月8日14:16:50
     * 刘峰
     *
     * @param searchModel
     * @return
     */
    List<UserTagAllotModel> getUnallottedTagListPlus(KolSearchModel searchModel);


    /**
     * 获取网红标签未分配数量分类汇总
     *
     * @param searchModel
     * @return
     */
    IPage<KolTagCountModel> getUnallottedTagCountSummary(Page<KolTagCountModel> page, KolSearchModel searchModel);


    /**
     * 保存分配网红标签关联的数据
     *
     * @param tkTagsUpdateList
     * @param tkTagsCounselorAddList
     * @param tkTagsCounselorUpdateList
     * @param kolAllotLogDetailEntity
     */
    void saveAllotTags(List<TiktokCelebrityTags> tkTagsUpdateList, List<TiktokCelebrityTagsCounselor> tkTagsCounselorAddList,
                       List<TiktokCelebrityTagsCounselor> tkTagsCounselorUpdateList, KolAllotLogDetail kolAllotLogDetailEntity);

    /**
     * 方法描述: 获取分配过期天数
     *
     * @author nqr
     * @date 2025/01/04 09:44
     **/
    int getAllotExpireDays();
    int getAllotTotal();

    int getUnallottedTagCountSummaryCount(KolSearchModel searchModel);

    List<TiktokCelebrityTagsModel> getUnAlloteTagList(KolSearchModel searchModel);

    void saveAllotTagsNew(List<TiktokCelebrityTags> tkTagsUpdateList, List<TiktokCelebrityTagsCounselor> tkTagsCounselorAddList,
                          List<KolAllotLogDetail> allotLogDetailList, List<KolTags> kolTagsUpdateList);

    List<KolTagUpdateDTO> getNotAllotTagsList(int allotMaxDays, String tkSql);

    List<TiktokCelebrityTags> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName);

    IPage<TiktokCelebrityTagsModel> getUnAlloteTagPageList(Page<KolTagCountModel> page, KolSearchModel searchModel);

    int getNotAllotCount(KolSearchModel searchModel);
}
