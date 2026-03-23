package org.jeecg.modules.instagram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.entity.IgCelebrityTagsCounselor;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;

import java.util.List;

/**
 * @Description: IG网红标签分配顾问明细
 * @Author: xyc
 * @Date:   2024-12-02
 * @Version: V1.0
 */
public interface IgCelebrityTagsCounselorMapper extends BaseMapper<IgCelebrityTagsCounselor> {

    List<KolBrand> getBrandListByCounselorId(@Param("counselorId") String counselorId);

    /**
     * 获取IG网红分配历史明细视图模型数据列表
     * @param page
     * @param searchModel
     * @return
     */
    IPage<KolTagAllotModel> getIgTagAllottedList(IPage<KolTagAllotModel> page,  @Param("searchModel") KolSearchModel searchModel );

    /**
     * 获取IG网红分配历史明细视图模型数据列表总数
     *
     * @param searchModel
     * @return
     */
    int getKolTagAllottedListCount(@Param("searchModel") KolSearchModel searchModel);

    List<IgCelebrityTagsCounselor> getCelebrityTagsList(@Param("searchModel") KolSearchModel searchModel, @Param("uniqueIdList") List<String> uniqueIdList, @Param("tempTableName") String tempTableName);
}
