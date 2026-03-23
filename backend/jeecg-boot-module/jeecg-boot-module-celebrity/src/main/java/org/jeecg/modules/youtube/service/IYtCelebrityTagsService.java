package org.jeecg.modules.youtube.service;

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
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTags;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTagsCounselor;
import org.jeecg.modules.youtube.model.YoutubeCelebrityTagsModel;

import java.util.List;

/**
 * @Description: YT网红标签
 * @Author: xyc
 * @Date: 2024-12-26 20:04:51
 * @Version: V1.0
 */
public interface IYtCelebrityTagsService extends IService<YoutubeCelebrityTags>, IKolTagCelebrityService {
    /**
     * 查询未分配网红标签的数量
     *
     * @param searchModel
     * @return
     */
    Long getUnallottedTagCount(KolSearchModel searchModel);

    /**
     * 查询未分配网红标签的数量 升级版
     * 2025年4月8日16:18:05
     * 刘峰
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
     * 获取网红标签未分配列表 升级版
     *2025年4月8日17:15:00
     * 刘峰
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
     * @param tagsUpdateList
     * @param tagsCounselorAddList
     * @param tagsCounselorUpdateList
     * @param kolAllotLogDetailEntity
     */
    void saveAllotTags(List<YoutubeCelebrityTags> tagsUpdateList, List<YoutubeCelebrityTagsCounselor> tagsCounselorAddList,
                       List<YoutubeCelebrityTagsCounselor> tagsCounselorUpdateList, KolAllotLogDetail kolAllotLogDetailEntity);

    List<YoutubeCelebrityTagsModel> getUnAlloteTagList(KolSearchModel searchModel);

    void saveAllotTagsNew(List<YoutubeCelebrityTags> ytTagsUpdateList, List<YoutubeCelebrityTagsCounselor> ytTagsCounselorAddList, List<KolAllotLogDetail> allotLogDetailList, List<KolTags> kolTagsUpdateList);

    /**
     * @description: 获取未分配的标签列表
     * @author: nqr
     * @date: 2025/7/22 15:00
     **/
    List<KolTagUpdateDTO> getNotAllotTagsList(int allotMaxDays, String sql);

    List<YoutubeCelebrityTags> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName);

    IPage<YoutubeCelebrityTagsModel> getUnAlloteTagPageList(Page<YoutubeCelebrityTagsModel> page, KolSearchModel searchModel);

    int getNotAllotCount(KolSearchModel searchModel);
}

