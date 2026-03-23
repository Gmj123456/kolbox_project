package org.jeecg.modules.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;
import org.jeecg.modules.tiktok.mapper.TkCelebrityTagsCounselorMapper;
import org.jeecg.modules.tiktok.service.ITkCelebrityTagsCounselorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: tk标签网红顾问分配表
 * @Author: xyc
 * @Date: 2024-12-26 18:09:04
 * @Version: V1.0
 */
@Service
public class TkCelebrityTagsCounselorServiceImpl extends ServiceImpl<TkCelebrityTagsCounselorMapper, TiktokCelebrityTagsCounselor>
        implements ITkCelebrityTagsCounselorService{

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
    public List<TiktokCelebrityTagsCounselor> getListByCounselorId(String counselorId) {
        LambdaQueryWrapper<TiktokCelebrityTagsCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TiktokCelebrityTagsCounselor::getCounselorId, counselorId);
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
        return baseMapper.getKolTagAllottedListCount(searchModel);
    }

    @Override
    public List<TiktokCelebrityTagsCounselor> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName) {
        return baseMapper.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
    }
}
