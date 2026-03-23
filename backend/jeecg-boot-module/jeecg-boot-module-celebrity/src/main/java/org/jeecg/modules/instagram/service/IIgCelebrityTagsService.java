package org.jeecg.modules.instagram.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.entity.IgCelebrityTags;
import org.jeecg.modules.instagram.entity.IgCelebrityTagsCounselor;
import org.jeecg.modules.instagram.model.IgCelebrityTagsModel;
import org.jeecg.modules.kol.entity.KolAllotLogDetail;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagCountModel;
import org.jeecg.modules.kol.model.KolTagUpdateDTO;
import org.jeecg.modules.kol.model.UserTagAllotModel;
import org.jeecg.modules.kol.service.IKolTagCelebrityService;

import java.util.List;

/**
 * @Description: ig_celebrity_tags
 * @Author: xyc
 * @Date: 2024年12月7日 17:55:03
 * @Version: V1.0
 */
public interface IIgCelebrityTagsService extends IService<IgCelebrityTags>, IKolTagCelebrityService {

    /**
     * 查询Instagram网红标签未分配的数量
     *
     * @param igSearchModel
     * @return
     */
    Long getUnallottedTagCount(KolSearchModel igSearchModel);


    /**
     * 获取Instagram顾问对应的标签网红未分配列表
     *
     * @param igSearchModel
     * @return
     */
    List<UserTagAllotModel> getUnallottedTagList(KolSearchModel igSearchModel);


    /**
     * 保存分配网红标签关联的数据
     *
     * @param igTagsUpdateList
     * @param igTagsCounselorAddList
     * @param igTagsCounselorUpdateList
     * @param kolAllotLogDetailEntity
     */
    void saveAllotTags(List<IgCelebrityTags> igTagsUpdateList,
                       List<IgCelebrityTagsCounselor> igTagsCounselorAddList,
                       List<IgCelebrityTagsCounselor> igTagsCounselorUpdateList,
                       KolAllotLogDetail kolAllotLogDetailEntity
    );


    /**
     * 获取Instagram标签未分配数量分类汇总
     *
     * @param igSearchModel
     * @return
     */
    IPage<KolTagCountModel> getUnallottedTagCountSummary(Page<KolTagCountModel> page, KolSearchModel igSearchModel);

    List<IgCelebrityTagsModel> getUnAlloteTagList(KolSearchModel searchModel);

    /**
     * @description:保存分配网红标签关联的数据
     * @author: nqr
     * @date: 2025/7/24 8:11
     **/
    void saveAllotTagsNew(List<IgCelebrityTags> igTagsUpdateList, List<IgCelebrityTagsCounselor> igTagsCounselorAddList, List<KolAllotLogDetail> allotLogDetailList, List<KolTags> kolTagsUpdateList);

    /**
     * @description: 获取未分配的标签列表
     * @author: nqr
     * @date: 2025/7/22 15:00
     **/
    List<KolTagUpdateDTO> getNotAllotTagsList(int allotMaxDays, String sql);

    List<IgCelebrityTags> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName);

    IPage<IgCelebrityTagsModel> getUnAlloteTagPageList(Page<IgCelebrityTagsModel> page, KolSearchModel searchModel);

    int getNotAllotCount(KolSearchModel searchModel);
}
