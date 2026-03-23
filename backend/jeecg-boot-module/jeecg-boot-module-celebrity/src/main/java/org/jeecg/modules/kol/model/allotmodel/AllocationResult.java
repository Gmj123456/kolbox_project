package org.jeecg.modules.kol.model.allotmodel;

import lombok.Data;
import org.jeecg.modules.instagram.entity.IgCelebrityTags;
import org.jeecg.modules.instagram.entity.IgCelebrityTagsCounselor;
import org.jeecg.modules.kol.entity.KolAllotLogDetail;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolTagsResultModel;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTags;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTagsCounselor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/7/23 19:18
 * @Description:分配结果容器
 **/
@Data
public class AllocationResult {
    /**
     * 本次新分配的网红标签列表 - 通过分配算法新分配给顾问的网红标签
     */
    private List<TiktokCelebrityTags> tkAllocatedTags;
    private List<YoutubeCelebrityTags> ytAllocatedTags;
    private List<IgCelebrityTags> igAllocatedTags;

    /**
     * 分配结果统计列表 - 每个顾问的分配统计信息，用于前端展示
     */
    private List<KolTagsResultModel> resultList;

    /**
     * 所有需更新的网红标签列表 - 包含新分配和私有网红的所有标签更新记录
     */
    private List<TiktokCelebrityTags> tkTagsUpdateList;
    private List<YoutubeCelebrityTags> ytTagsUpdateList;
    private List<IgCelebrityTags> igTagsUpdateList;

    /**
     * 顾问标签关联列表 - 所有新增的顾问与网红标签关联关系记录
     */
    private List<TiktokCelebrityTagsCounselor> tkTagsCounselorAddList;
    private List<YoutubeCelebrityTagsCounselor> ytTagsCounselorAddList;
    private List<IgCelebrityTagsCounselor> igTagsCounselorAddList;

    /**
     * 分配日志详情列表 - 完整的分配操作日志记录，用于审计和追溯
     */
    private List<KolAllotLogDetail> allotLogDetailList = new ArrayList<>();

    private List<KolTags> tagsList;
}
