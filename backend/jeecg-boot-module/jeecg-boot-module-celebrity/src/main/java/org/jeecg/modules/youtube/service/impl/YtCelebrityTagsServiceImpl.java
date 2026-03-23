package org.jeecg.modules.youtube.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
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
import org.jeecg.modules.tiktok.model.TiktokTagsNumModel;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTags;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTagsCounselor;
import org.jeecg.modules.youtube.mapper.YtCelebrityTagsMapper;
import org.jeecg.modules.youtube.model.YoutubeCelebrityTagsModel;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsCounselorService;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: YT网红标签
 * @Author: xyc
 * @Date: 2024-12-26 20:16:38
 * @Version: V1.0
 */
@Service
public class YtCelebrityTagsServiceImpl extends ServiceImpl<YtCelebrityTagsMapper, YoutubeCelebrityTags> implements IYtCelebrityTagsService {

    @Autowired
    private IYtCelebrityTagsCounselorService tagsCounselorService;

    @Autowired
    private IKolAllotLogDetailService kolAllotLogDetailService;
    @Autowired
    private IKolAllotLogDetailService allotLogDetailService;
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
     * 查询未分配网红标签的数量 升级版
     * 2025年4月8日16:18:05
     * 刘峰
     *
     * @param searchModel
     * @return
     */
    @Override
    public Long getUnallottedTagCountPlus(KolSearchModel searchModel) {
        Long countPlus = 0L;
        try {
            kolCelebrityService.createTempTable(searchModel);
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
     * 2025年4月8日17:15:00
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
     * @param tagsUpdateList
     * @param tagsCounselorAddList
     * @param tagsCounselorUpdateList
     * @param kolAllotLogDetailEntity
     */
    @Override
    public void saveAllotTags(List<YoutubeCelebrityTags> tagsUpdateList, List<YoutubeCelebrityTagsCounselor> tagsCounselorAddList,
                              List<YoutubeCelebrityTagsCounselor> tagsCounselorUpdateList, KolAllotLogDetail kolAllotLogDetailEntity) {
        if (oConvertUtils.listIsNotEmpty(tagsUpdateList)) {
            this.updateBatchById(tagsUpdateList);
        }
        if (oConvertUtils.listIsNotEmpty(tagsCounselorAddList)) {
            tagsCounselorService.saveBatch(tagsCounselorAddList);
        }
        if (oConvertUtils.listIsNotEmpty(tagsCounselorUpdateList)) {
            tagsCounselorService.updateBatchById(tagsCounselorUpdateList);
        }
        kolAllotLogDetailService.save(kolAllotLogDetailEntity);
    }

    /**
     * 方法描述: 导出tiktok标签数据
     *
     * @author nqr
     * @date 2025/01/03 18:21
     **/
    @Override
    public List<TiktokTagsNumModel> exportTagsExcel(KolSearchModel kolSearchModel) {
        return this.baseMapper.exportTagsExcel(kolSearchModel);
    }

    /**
     * 方法描述: 分配历史，修改网红顾问
     *
     * @author nqr
     * @date 2025/01/08 09:33
     **/
    @Override
    public void updateCounselor(KolSearchModel kolSearchModel) {
        String id = kolSearchModel.getId();
        YoutubeCelebrityTagsCounselor tagsCounselor = tagsCounselorService.getById(id);
        if (tagsCounselor == null) {
            throw new JeecgBootException("未找到该网红顾问信息");
        }

        String tagsId = tagsCounselor.getYoutubeCelebrityTagsId();
        String counselorId = kolSearchModel.getCounselorId();
        String counselorName = kolSearchModel.getCounselorName();

        YoutubeCelebrityTags celebrityTags = this.getById(tagsId);
        String logDetailId = celebrityTags.getAllotLogDetailId();
        String tagName = celebrityTags.getTagName();
        String logId = celebrityTags.getAllotLogId();

        YoutubeCelebrityTags celebrityTagsNew = new YoutubeCelebrityTags();
        celebrityTagsNew.setId(tagsId);
        celebrityTagsNew.setCounselorId(counselorId);
        celebrityTagsNew.setCounselorName(counselorName);

        YoutubeCelebrityTagsCounselor tagsCounselorNew = new YoutubeCelebrityTagsCounselor();
        tagsCounselorNew.setId(id);
        tagsCounselorNew.setCounselorName(kolSearchModel.getCounselorName());
        tagsCounselorNew.setCounselorId(kolSearchModel.getCounselorId());
        tagsCounselorService.updateById(tagsCounselorNew);

        if (oConvertUtils.isEmpty(logDetailId)) {
            this.updateById(celebrityTagsNew);
            return;
        }

        this.updateById(celebrityTagsNew);
        allotLogDetailService.updateAllotLog(counselorId, counselorName, logDetailId, tagName, logId, YoutubeCelebrityTags.class, celebrityTagsNew);
    }

    @Override
    public List<YoutubeCelebrityTagsModel> getUnAlloteTagList(KolSearchModel searchModel) {
        List<YoutubeCelebrityTagsModel> unAlloteTagList = new ArrayList<>();
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
    public void saveAllotTagsNew(List<YoutubeCelebrityTags> ytTagsUpdateList, List<YoutubeCelebrityTagsCounselor> ytTagsCounselorAddList, List<KolAllotLogDetail> allotLogDetailList, List<KolTags> kolTagsUpdateList) {
        if (oConvertUtils.listIsNotEmpty(ytTagsUpdateList)) {
            this.updateBatchById(ytTagsUpdateList);
        }
        if (oConvertUtils.listIsNotEmpty(ytTagsCounselorAddList)) {
            tagsCounselorService.saveBatch(ytTagsCounselorAddList);
        }
        if (oConvertUtils.listIsNotEmpty(allotLogDetailList)) {
            allotLogDetailService.saveBatch(allotLogDetailList);
        }
        if (oConvertUtils.listIsNotEmpty(kolTagsUpdateList)) {
            for (KolTags kolTags : kolTagsUpdateList) {
                kolTagsMapper.updateById(kolTags);
            }
        }
    }

    @Override
    public List<KolTagUpdateDTO> getNotAllotTagsList(int allotMaxDays, String sql) {
        return this.baseMapper.getNotAllotTagsList(allotMaxDays, sql);
    }

    @Override
    public List<YoutubeCelebrityTags> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName) {
        return this.baseMapper.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
    }

    @Override
    public IPage<YoutubeCelebrityTagsModel> getUnAlloteTagPageList(Page<YoutubeCelebrityTagsModel> page, KolSearchModel searchModel) {
        return this.baseMapper.getUnAlloteTagPageList(page, searchModel);
    }

    @Override
    public int getNotAllotCount(KolSearchModel searchModel) {
        int notAllotCount = 0;
        try {
            // kolCelebrityService.createTempTable(searchModel);
            notAllotCount = this.baseMapper.getNotAllotCount(searchModel);
        } catch (Exception e) {
            System.out.println("\n\n\n\n\n\n");
            System.out.println(e.getMessage());
            System.out.println("\n\n\n\n\n\n");
        } finally {
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        return notAllotCount;
    }
}
