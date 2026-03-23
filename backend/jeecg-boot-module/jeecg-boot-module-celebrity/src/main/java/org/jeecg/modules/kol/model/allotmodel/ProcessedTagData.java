package org.jeecg.modules.kol.model.allotmodel;

import lombok.Data;
import org.jeecg.modules.instagram.entity.IgCelebrityTags;
import org.jeecg.modules.instagram.entity.IgCelebrityTagsCounselor;
import org.jeecg.modules.instagram.model.IgCelebrityTagsModel;
import org.jeecg.modules.kol.entity.KolAllotLogDetail;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;
import org.jeecg.modules.tiktok.model.TiktokCelebrityTagsModel;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTags;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTagsCounselor;
import org.jeecg.modules.youtube.model.YoutubeCelebrityTagsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: nqr
 * @Date: 2025/7/23 19:18
 * @Description:处理后的标签数据容器
 **/
@Data
public class ProcessedTagData {
    /**
     * 符合分配条件的网红标签列表 - 过滤掉粉丝数不达标的网红后剩余的标签
     */
    private List<TiktokCelebrityTagsModel> tkEligibleTagList;
    private List<YoutubeCelebrityTagsModel> ytEligibleTagList;
    private List<IgCelebrityTagsModel> igEligibleTagList;

    /**
     * 被排除的网红ID列表 - 因粉丝数低于阈值而不参与分配的网红ID
     */
    private List<String> excludedCelebrityIds;

    /**
     * 现有私有网红标签映射 - 已有私有顾问的网红标签，按顾问分组
     */
    private Map<String, List<TiktokCelebrityTags>> tkExistingPrivateTagMap;
    private Map<String, List<YoutubeCelebrityTags>> ytExistingPrivateTagMap;
    private Map<String, List<IgCelebrityTags>> igExistingPrivateTagMap;

    /**
     * 待更新的网红标签列表 - 需要更新分配状态的网红标签记录
     */
    private List<TiktokCelebrityTags> tkTagsUpdateList;
    private List<YoutubeCelebrityTags> ytTagsUpdateList;
    private List<IgCelebrityTags> igTagsUpdateList;

    /**
     * 顾问标签关联列表 - 新增的顾问与网红标签的关联关系记录
     */
    private List<TiktokCelebrityTagsCounselor> tkTagsCounselorAddList;
    private List<YoutubeCelebrityTagsCounselor> ytTagsCounselorAddList;
    private List<IgCelebrityTagsCounselor> igTagsCounselorAddList;

    /**
     * 分配日志详情列表 - 记录本次分配操作的详细日志信息
     */
    private List<KolAllotLogDetail> allotLogDetailList;

    /**
     * 初始化各个列表为空列表，避免空指针异常
     */
    public void initializeLists() {
        this.tkTagsUpdateList = new ArrayList<>();
        this.ytTagsUpdateList = new ArrayList<>();
        this.igTagsUpdateList = new ArrayList<>();
        this.tkTagsCounselorAddList = new ArrayList<>();
        this.ytTagsCounselorAddList = new ArrayList<>();
        this.igTagsCounselorAddList = new ArrayList<>();
        this.allotLogDetailList = new ArrayList<>();
    }
}
