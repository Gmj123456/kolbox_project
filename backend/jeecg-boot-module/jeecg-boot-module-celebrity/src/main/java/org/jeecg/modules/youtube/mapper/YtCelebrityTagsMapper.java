package org.jeecg.modules.youtube.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagCountModel;
import org.jeecg.modules.kol.model.KolTagUpdateDTO;
import org.jeecg.modules.kol.model.UserTagAllotModel;
import org.jeecg.modules.tiktok.model.TiktokTagsNumModel;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTags;
import org.jeecg.modules.youtube.model.YoutubeCelebrityTagsModel;

import java.util.List;

/**
 * @Description: YT网红标签Mapper
 * @Author: xyc
 * @Date: 2024-12-26 20:02:04
 * @Version: V1.0
 */
public interface YtCelebrityTagsMapper extends BaseMapper<YoutubeCelebrityTags> {
    /**
     * 查询未分配网红标签的数量
     *
     * @param searchModel
     * @return
     */
    Long getUnallottedTagCount(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 查询未分配网红标签的数量 升级版
     * 2025年4月8日16:18:05
     * 刘峰
     *
     * @param searchModel
     * @return
     */
    Long getUnallottedTagCountPlus(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 获取网红标签未分配列表
     *
     * @param searchModel
     * @return
     */
    List<UserTagAllotModel> getUnallottedTagList(@Param("searchModel") KolSearchModel searchModel);

    /**
     * 获取网红标签未分配列表 升级版
     * 2025年4月8日17:15:00
     * 刘峰
     *
     * @param searchModel
     * @return
     */
    List<UserTagAllotModel> getUnallottedTagListPlus(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 获取网红标签未分配数量分类汇总
     *
     * @param searchModel
     * @return
     */
    IPage<KolTagCountModel> getUnallottedTagCountSummary(Page<KolTagCountModel> page, @Param("searchModel") KolSearchModel searchModel);

    /**
     * 方法描述: 导出tiktok标签数据
     *
     * @author nqr
     * @date 2025/01/03 18:21
     **/
    List<TiktokTagsNumModel> exportTagsExcel(@Param("searchModel") KolSearchModel searchModel);


    List<YoutubeCelebrityTagsModel> getUnAlloteTagList(@Param("searchModel") KolSearchModel searchModel);

    List<KolTagUpdateDTO> getNotAllotTagsList(@Param("allotMaxDays") int allotMaxDays, String sql);

    List<YoutubeCelebrityTags> getCelebrityTagsList(@Param("searchModel") KolSearchModel searchModel, @Param("uniqueIdList") List<String> uniqueIdList, @Param("tempTableName") String tempTableName);

    IPage<YoutubeCelebrityTagsModel> getUnAlloteTagPageList(Page<YoutubeCelebrityTagsModel> page, @Param("searchModel") KolSearchModel searchModel);

    int getNotAllotCount(@Param("searchModel") KolSearchModel searchModel);
}
