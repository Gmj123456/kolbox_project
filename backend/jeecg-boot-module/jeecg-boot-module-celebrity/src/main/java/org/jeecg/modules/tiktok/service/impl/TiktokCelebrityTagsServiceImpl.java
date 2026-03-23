package org.jeecg.modules.tiktok.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolAllotLogDetail;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.mapper.KolTagsMapper;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagCountModel;
import org.jeecg.modules.kol.model.KolTagUpdateDTO;
import org.jeecg.modules.kol.model.UserTagAllotModel;
import org.jeecg.modules.kol.service.IKolAllotLogDetailService;
import org.jeecg.modules.kol.service.IKolCelebrityService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;
import org.jeecg.modules.tiktok.mapper.TiktokCelebrityTagsMapper;
import org.jeecg.modules.tiktok.model.TiktokCelebrityTagsModel;
import org.jeecg.modules.tiktok.model.TiktokTagsNumModel;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityTagsService;
import org.jeecg.modules.tiktok.service.ITkCelebrityTagsCounselorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.jeecg.common.constant.CelebrityPromStatus.*;


/**
 * @Description: 网红tag表
 * @Author: dongruyang
 * @Date: 2023-10-10
 * @Version: V1.0
 */
@Service
public class TiktokCelebrityTagsServiceImpl extends ServiceImpl<TiktokCelebrityTagsMapper, TiktokCelebrityTags> implements ITiktokCelebrityTagsService {
    @Autowired
    private IKolAllotLogDetailService allotLogDetailService;
    @Autowired
    private ITkCelebrityTagsCounselorService tagsCounselorService;
    @Autowired
    private IKolAllotLogDetailService kolAllotLogDetailService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private KolTagsMapper kolTagsMapper;
    @Autowired
    private IKolCelebrityService kolCelebrityService;

    /**
     * 查询未分配网红标签的数量
     *
     * @param searchModel
     * @return
     */
    @Override
    public Long getUnallottedTagCount(KolSearchModel searchModel) {
        return baseMapper.getUnallottedTagCount(searchModel);
    }

    /**
     * 查询未分配网红标签的数量 改进版本
     * 2025年4月8日11:34:15
     * 刘峰
     *
     * @param searchModel
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long getUnallottedTagCountPlus(KolSearchModel searchModel) {
        Long countPlus = 0L;
        try {
            // kolCelebrityService.createTempTable(searchModel);
            countPlus = baseMapper.getUnallottedTagCountPlus(searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        return countPlus;
    }

    /**
     * 获取网红标签未分配列表
     *
     * @param searchModel
     * @return
     */
    @Override
    public List<UserTagAllotModel> getUnallottedTagList(KolSearchModel searchModel) {
        return baseMapper.getUnallottedTagList(searchModel);
    }

    /**
     * 获取网红标签未分配列表 升级版
     * 2025年4月8日14:15:51
     * 刘峰
     *
     * @param searchModel
     * @return
     */
    @Override
    public List<UserTagAllotModel> getUnallottedTagListPlus(KolSearchModel searchModel) {
        return baseMapper.getUnallottedTagListPlus(searchModel);
    }

    /**
     * 获取网红标签未分配数量分类汇总
     *
     * @param page
     * @param searchModel
     * @return
     */
    @Override
    public IPage<KolTagCountModel> getUnallottedTagCountSummary(Page<KolTagCountModel> page, KolSearchModel searchModel) {
        return baseMapper.getUnallottedTagCountSummary(page, searchModel);

    }

    /**
     * 保存分配网红标签关联的数据
     *
     * @param tkTagsUpdateList
     * @param tkTagsCounselorAddList
     * @param tkTagsCounselorUpdateList
     * @param kolAllotLogDetailEntity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAllotTags(List<TiktokCelebrityTags> tkTagsUpdateList, List<TiktokCelebrityTagsCounselor> tkTagsCounselorAddList,
                              List<TiktokCelebrityTagsCounselor> tkTagsCounselorUpdateList, KolAllotLogDetail kolAllotLogDetailEntity) {
        if (oConvertUtils.listIsNotEmpty(tkTagsUpdateList)) {
            this.updateBatchById(tkTagsUpdateList);
        }
        if (oConvertUtils.listIsNotEmpty(tkTagsCounselorAddList)) {
            tagsCounselorService.saveBatch(tkTagsCounselorAddList);
        }
        if (oConvertUtils.listIsNotEmpty(tkTagsCounselorUpdateList)) {
            tagsCounselorService.updateBatchById(tkTagsCounselorUpdateList);
        }
        kolAllotLogDetailService.save(kolAllotLogDetailEntity);
    }


    /**
     * 功能描述:修改网红顾问
     *
     * @return void
     * @Date 2023-11-03 14:54:14
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCounselor(KolSearchModel kolSearchModel) {
        String id = kolSearchModel.getId();
        TiktokCelebrityTagsCounselor tagsCounselor = tagsCounselorService.getById(id);
        if (tagsCounselor == null) {
            throw new JeecgBootException("未找到该网红顾问信息");
        }

        String tagsId = kolSearchModel.getTagsId();
        String counselorId = kolSearchModel.getCounselorId();
        String counselorName = kolSearchModel.getCounselorName();

        TiktokCelebrityTags celebrityTags = this.getById(tagsId);
        String logDetailId = celebrityTags.getAllotLogDetailId();
        String tagName = celebrityTags.getTagName();
        String logId = celebrityTags.getAllotLogId();

        TiktokCelebrityTags celebrityTagsNew = new TiktokCelebrityTags();
        celebrityTagsNew.setId(tagsId);
        celebrityTagsNew.setCelebrityCounselorId(counselorId);
        celebrityTagsNew.setCelebrityCounselorName(counselorName);

        TiktokCelebrityTagsCounselor tagsCounselorNew = new TiktokCelebrityTagsCounselor();
        tagsCounselorNew.setId(id);
        tagsCounselorNew.setCounselorName(kolSearchModel.getCounselorName());
        tagsCounselorNew.setCounselorId(kolSearchModel.getCounselorId());
        tagsCounselorService.updateById(tagsCounselorNew);

        if (oConvertUtils.isEmpty(logDetailId)) {
            this.updateById(celebrityTagsNew);
            return;
        }


        allotLogDetailService.updateAllotLog(counselorId, counselorName, logDetailId, tagName, logId, TiktokCelebrityTags.class, celebrityTagsNew);
        this.updateById(celebrityTagsNew);

    }


    /**
     * 方法描述:获取导出Tiktok未分配标签对应的网红列表
     *
     * @author nqr
     * @date 2025/01/04 09:13
     **/
    @Override
    public List<TiktokTagsNumModel> exportTagsExcel(KolSearchModel kolSearchModel) {
        return this.baseMapper.exportTagsExcel(kolSearchModel);
    }

    /**
     * 方法描述: 获取分配过期天数 默认30天
     *
     * @author nqr
     * @date 2025/01/04 09:45
     **/
    @Override
    public int getAllotExpireDays() {
        // 获取网红分配过期天数 默认30天
        return sysBaseAPI.queryDictItemsByCode(CommonConstant.ALLOT_DAYS).stream().
                filter(x -> KOL_ALLOT_MAX_DAYS.equals(x.getText())).findFirst().map(dict -> Integer.parseInt(dict.getValue())).orElse(KOL_ALLOT_MAX_DAYS_DEFAULT);
    }

    @Override
    public int getAllotTotal() {
        // 获取网红分配过期天数 默认30天
        return sysBaseAPI.queryDictItemsByCode(CommonConstant.ALLOT_DAYS).stream().filter(x -> TOTAL.equals(x.getText())).findFirst().map(dict -> Integer.parseInt(dict.getValue())).orElse(TOTAL_DEFAULT);
    }

    @Override
    public int getUnallottedTagCountSummaryCount(KolSearchModel searchModel) {
        return this.baseMapper.getUnallottedTagCountSummaryCount(searchModel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TiktokCelebrityTagsModel> getUnAlloteTagList(KolSearchModel searchModel) {
        List<TiktokCelebrityTagsModel> unAlloteTagList = new ArrayList<>();
        try {
            kolCelebrityService.createTempTable(searchModel);
            unAlloteTagList = this.baseMapper.getUnAlloteTagList(searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        return unAlloteTagList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAllotTagsNew(List<TiktokCelebrityTags> tkTagsUpdateList, List<TiktokCelebrityTagsCounselor> tkTagsCounselorAddList, List<KolAllotLogDetail> allotLogDetailList, List<KolTags> kolTagsUpdateList) {
        int batchSize = 500; // 每批处理 500 条
        if (oConvertUtils.listIsNotEmpty(tkTagsUpdateList)) {
            for (int i = 0; i < tkTagsUpdateList.size(); i += batchSize) {
                int end = Math.min(i + batchSize, tkTagsUpdateList.size());
                List<TiktokCelebrityTags> subList = tkTagsUpdateList.subList(i, end);
                this.baseMapper.batchUpdateTags(subList);
            }
        }
        if (!tkTagsCounselorAddList.isEmpty() && oConvertUtils.listIsNotEmpty(tkTagsCounselorAddList)) {
            tagsCounselorService.saveBatch(tkTagsCounselorAddList, 500);
        }
        if (!allotLogDetailList.isEmpty() && oConvertUtils.listIsNotEmpty(allotLogDetailList)) {
            kolAllotLogDetailService.saveBatch(allotLogDetailList, 500);
        }
        if (oConvertUtils.listIsNotEmpty(kolTagsUpdateList)) {
            for (int i = 0; i < kolTagsUpdateList.size(); i += batchSize) {
                int end = Math.min(i + batchSize, kolTagsUpdateList.size());
                List<KolTags> subList = kolTagsUpdateList.subList(i, end);
                kolTagsMapper.batchUpdateKolTags(subList);
            }
        }
    }

    @Override
    public List<KolTagUpdateDTO> getNotAllotTagsList(int allotMaxDays, String sql) {
        return this.baseMapper.getNotAllotTagsList(allotMaxDays, sql);
    }

    @Override
    public List<TiktokCelebrityTags> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName) {
        return this.baseMapper.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
    }

    @Override
    public IPage<TiktokCelebrityTagsModel> getUnAlloteTagPageList(Page<KolTagCountModel> page, KolSearchModel searchModel) {
        return this.baseMapper.getUnAlloteTagPageList(page, searchModel);
    }

    @Override
    public int getNotAllotCount(KolSearchModel searchModel) {
        int notAllotCount = 0;
        try {
            // kolCelebrityService.createTempTable(searchModel);
            notAllotCount = this.baseMapper.getNotAllotCount(searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        return notAllotCount;
    }
}
