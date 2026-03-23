package org.jeecg.modules.instagram.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.entity.IgCelebrityTagsCounselor;
import org.jeecg.modules.instagram.mapper.IgCelebrityTagsCounselorMapper;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsCounselorService;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 网红标签顾问分配明细表
 * @Author: xyc
 * @Date:   2024-12-02
 * @Version: V1.0
 */
@Service
public class IgCelebrityTagsCounselorServiceImpl extends ServiceImpl<IgCelebrityTagsCounselorMapper, IgCelebrityTagsCounselor>
        implements IIgCelebrityTagsCounselorService {

    @Override
    public List<KolBrand> getBrandListByCounselorId(String counselorId) {
        return baseMapper.getBrandListByCounselorId(counselorId);
    }

    /**
     * 根据counselorId获取ig_celebrity_tags_counselor列表
     *
     * @param counselorId
     * @return
     */
    @Override
    public List<IgCelebrityTagsCounselor> getListByCounselorId(String counselorId) {
        LambdaQueryWrapper<IgCelebrityTagsCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IgCelebrityTagsCounselor::getCounselorId, counselorId);
        return this.list(queryWrapper);
    }

    /**
     * 获取IG网红分配历史明细视图模型数据列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    @Override
    public IPage<KolTagAllotModel> getIgTagAllottedList(IPage<KolTagAllotModel> page, KolSearchModel searchModel) {
        return baseMapper.getIgTagAllottedList(page, searchModel);
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
        return baseMapper.getIgTagAllottedList(page, searchModel);
    }

    @Override
    public int getKolTagAllottedListCount(KolSearchModel searchModel) {
        return baseMapper.getKolTagAllottedListCount(searchModel);
    }

    @Override
    public List<IgCelebrityTagsCounselor> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName) {
        return baseMapper.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
    }
}
