package org.jeecg.modules.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;

import java.util.List;

/**
 * @Description: tk网红标签顾问分配明细mapper
 * @Author: xyc
 * @Date: 2024-12-26 18:06:32
 * @Version: V1.0
 */
public interface TkCelebrityTagsCounselorMapper extends BaseMapper<TiktokCelebrityTagsCounselor> {

    List<KolBrand> getBrandListByCounselorId(@Param("counselorId") String counselorId);

    /**
     * 获取网红分配历史明细视图模型数据列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<KolTagAllotModel> getTagAllottedList(IPage<KolTagAllotModel> page,
            @Param("searchModel") KolSearchModel searchModel);

    /**
     * 获取网红分配历史明细视图模型数据列表总数
     *
     * @param searchModel
     * @return
     */
    int getKolTagAllottedListCount(@Param("searchModel") KolSearchModel searchModel);

    List<TiktokCelebrityTagsCounselor> getCelebrityTagsList(@Param("searchModel") KolSearchModel searchModel,
            @Param("uniqueIdList") List<String> uniqueIdList, @Param("tempTableName") String tempTableName);
}
