package org.jeecg.modules.youtube.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTagsCounselor;
import org.jeecg.modules.youtube.mapper.YtCelebrityTagsCounselorMapper;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsCounselorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: YT标签网红顾问分配接口实现层
 * @Author: xyc
 * @Date:   2024-08-28
 * @Version: V1.1
 * @History: V1.1 - [2024-12-26] - [新增getTagAllottedList 查询分配历史明细] - [xyc]
 */
@Service
public class YtCelebrityTagsCounselorServiceImpl extends ServiceImpl<YtCelebrityTagsCounselorMapper, YoutubeCelebrityTagsCounselor> implements IYtCelebrityTagsCounselorService {

    @Override
    public List<KolBrand> getBrandListByCounselorId(String counselorId) {
        return baseMapper.getBrandListByCounselorId(counselorId);
    }

    /**
     * 根据counselorId获取分配顾问明细列表
     *
     * @param counselorId
     * @return
     */
    @Override
    public List<YoutubeCelebrityTagsCounselor> getListByCounselorId(String counselorId) {
        LambdaQueryWrapper<YoutubeCelebrityTagsCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(YoutubeCelebrityTagsCounselor::getCounselorId, counselorId);
        return this.list(queryWrapper);
    }

    /**
     * 获取网红分配历史明细视图模型数据列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    @Override
    public IPage<KolTagAllotModel> getTagAllottedList(IPage<KolTagAllotModel> page, KolSearchModel searchModel) {
        return baseMapper.getTagAllottedList(page, searchModel);
    }

    /**
     * 获取网红分配历史明细视图模型数据列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    @Override
    public IPage<KolTagAllotModel> getKolTagAllottedList(IPage<KolTagAllotModel> page, KolSearchModel searchModel) {
        return baseMapper.getTagAllottedList(page, searchModel);
    }

    @Override
    public int getKolTagAllottedListCount(KolSearchModel searchModel) {
        return baseMapper.getTagAllottedListCount(searchModel);
    }

    @Override
    public List<YoutubeCelebrityTagsCounselor> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName) {
        return baseMapper.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
    }
}
