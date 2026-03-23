package org.jeecg.modules.kol.model.allotmodel;

import lombok.Data;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.instagram.model.IgCelebrityTagsModel;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityRule;
import org.jeecg.modules.tiktok.model.TiktokCelebrityTagsModel;
import org.jeecg.modules.youtube.model.YoutubeCelebrityTagsModel;

import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/7/23 19:17
 * @Description: 初始化数据容器
 **/
@Data
public class InitializationData {
    /**
     * 未分配的网红标签列表 - 从数据库查询得到的待分配标签数据
     */
    private List<TiktokCelebrityTagsModel> tkUnallottedTagList;
    private List<YoutubeCelebrityTagsModel> ytUnallottedTagList;
    private List<IgCelebrityTagsModel> igUnallottedTagList;

    /**
     * 顾问用户列表 - 参与分配的网红顾问信息
     */
    private List<LoginUser> counselorList;

    /**
     * 分配规则列表 - 根据粉丝数等级制定的分配规则
     */
    private List<TiktokCelebrityRule> rules;

    /**
     * 最小粉丝数阈值 - 参与分配的网红最低粉丝数要求
     */
    private Integer minFollowerThreshold;

    /**
     * 错误结果 - 初始化过程中出现的错误信息
     */
    private Result<?> errorResult;

    /**
     * 检查初始化过程是否出现错误
     *
     * @return true表示有错误，false表示无错误
     */
    public boolean hasError() {
        return errorResult != null;
    }
}