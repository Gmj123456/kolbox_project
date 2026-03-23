package org.jeecg.modules.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagCountModel;
import org.jeecg.modules.kol.model.KolTagUpdateDTO;
import org.jeecg.modules.kol.model.UserTagAllotModel;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.jeecg.modules.tiktok.model.TiktokCelebrityTagsModel;
import org.jeecg.modules.tiktok.model.TiktokTagsNumModel;

import java.util.List;

/**
 * @Description: 网红tag表
 * @Author: xyc
 * @Date: 2023-10-10
 * @Version: V1.1
 * @History: V1.1 - [2024-12-25] - [根据实际业务，新增方法替换原有方法，测试通过后 将删除历史不规范的命名方法] - [xyc]
 */
public interface TiktokCelebrityTagsMapper extends BaseMapper<TiktokCelebrityTags> {

    /**
     * 查询未分配网红标签的数量
     *
     * @param searchModel
     * @return
     */
    Long getUnallottedTagCount(@Param("searchModel") KolSearchModel searchModel);

    /**
     * 查询未分配网红标签的数量  改进版本
     * 2025年4月8日11:33:29
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
     * 获取网红标签未分配列表 改进版本
     * 2025年4月8日14:12:37
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
     * 获取导出Tiktok未分配标签对应的网红列表
     *
     * @param searchModel
     * @return
     */
    List<TiktokTagsNumModel> exportTagsExcel(@Param("searchModel") KolSearchModel searchModel);

    int getUnallottedTagCountSummaryCount(@Param("searchModel") KolSearchModel searchModel);

    List<TiktokCelebrityTagsModel> getUnAlloteTagList(@Param("searchModel") KolSearchModel searchModel);

    void batchUpdateTags(@Param("list") List<TiktokCelebrityTags> tkTagsUpdateList);

    List<KolTagUpdateDTO> getNotAllotTagsList(@Param("allotMaxDays") int allotMaxDays, @Param("sql") String sql);

    List<TiktokCelebrityTags> getCelebrityTagsList(@Param("searchModel") KolSearchModel searchModel, @Param("uniqueIds") List<String> uniqueIds, @Param("tempTableName") String tempTableName);

    IPage<TiktokCelebrityTagsModel> getUnAlloteTagPageList(Page<KolTagCountModel> page, @Param("searchModel") KolSearchModel searchModel);

    int getNotAllotCount(@Param("searchModel") KolSearchModel searchModel);
}
