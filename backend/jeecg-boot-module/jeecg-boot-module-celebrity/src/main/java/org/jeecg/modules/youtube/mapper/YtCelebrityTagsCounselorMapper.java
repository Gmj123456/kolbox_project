package org.jeecg.modules.youtube.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTagsCounselor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: YT网红标签分配明细Mapper
 * @Author: xyc
 * @Date:   2024-12-26 18:00:49
 * @Version: V1.0
 */
@Mapper
public interface YtCelebrityTagsCounselorMapper extends BaseMapper<YoutubeCelebrityTagsCounselor> {

    List<KolBrand> getBrandListByCounselorId(@Param("counselorId") String counselorId);
    /**
     * 获取网红分配历史明细视图模型数据列表
     * @param page
     * @param searchModel
     * @return
     */
    IPage<KolTagAllotModel> getTagAllottedList(IPage<KolTagAllotModel> page, @Param("searchModel") KolSearchModel searchModel );

    int getTagAllottedListCount(@Param("searchModel") KolSearchModel searchModel);

    List<YoutubeCelebrityTagsCounselor> getCelebrityTagsList(@Param("searchModel") KolSearchModel searchModel, @Param("uniqueIdList") List<String> uniqueIdList, @Param("tempTableName") String tempTableName);
}
