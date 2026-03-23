package org.jeecg.modules.instagram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.entity.IgCelebrityTags;
import org.jeecg.modules.instagram.model.IgCelebrityTagsModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagCountModel;
import org.jeecg.modules.kol.model.KolTagUpdateDTO;
import org.jeecg.modules.kol.model.UserTagAllotModel;
import org.jeecg.modules.tiktok.model.TiktokTagsNumModel;

import java.util.List;

/**
 * @Description: ig_celebrity_tags
 * @Author: xyc
 * @Date: 2024年12月7日 17:38:16
 * @Version: V1.0
 */
public interface IgCelebrityTagsMapper extends BaseMapper<IgCelebrityTags> {

    /**
     * 查询未分配网红标签的数量
     *
     * @param searchModel
     * @return
     */
    Long getUnallottedTagCount(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 获取网红标签未分配列表
     *
     * @param searchModel
     * @return
     */
    List<UserTagAllotModel> getUnallottedTagList(@Param("searchModel") KolSearchModel searchModel);


    /**
     * 获取网红标签未分配数量分类汇总
     *
     * @param searchModel
     * @return
     */
    IPage<KolTagCountModel> getUnallottedTagCountSummary(Page<KolTagCountModel> page, @Param("searchModel") KolSearchModel searchModel);

    /**
     * 方法描述: 导出标签数据
     *
     * @author nqr
     * @date 2025/01/03 18:23
     **/
    List<TiktokTagsNumModel> exportTagsExcel(@Param("searchModel") KolSearchModel searchModel);

    List<IgCelebrityTagsModel> getUnAlloteTagList(@Param("searchModel") KolSearchModel searchModel);

    List<KolTagUpdateDTO> getNotAllotTagsList(@Param("allotMaxDays") int allotMaxDays, @Param("sql") String sql);

    List<IgCelebrityTags> getCelebrityTagsList(@Param("searchModel") KolSearchModel searchModel, @Param("uniqueIdList") List<String> uniqueIdList, @Param("tempTableName") String tempTableName);

    IPage<IgCelebrityTagsModel> getUnAlloteTagPageList(Page<IgCelebrityTagsModel> page, @Param("searchModel") KolSearchModel searchModel);

    int getNotAllotCount(@Param("searchModel") KolSearchModel searchModel);
}
