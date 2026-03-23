package org.jeecg.modules.instagram.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.entity.IgCelebrityTags;
import org.jeecg.modules.instagram.entity.IgCelebrityTagsCounselor;
import org.jeecg.modules.instagram.mapper.IgCelebrityTagsMapper;
import org.jeecg.modules.instagram.model.IgCelebrityTagsModel;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsCounselorService;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: IG网红标签
 * @Author: xyc
 * @Date: 2024年12月7日 17:55:22
 * @Version: V1.0
 */
@Service
public class IgCelebrityTagsServiceImpl extends ServiceImpl<IgCelebrityTagsMapper, IgCelebrityTags> implements IIgCelebrityTagsService {


    @Autowired
    private IKolAllotLogDetailService kolAllotLogDetailService;
    @Autowired
    private IIgCelebrityTagsCounselorService igTagsCounselorService;
    @Autowired
    private IKolAllotLogDetailService allotLogDetailService;
    @Autowired
    private KolTagsMapper kolTagsMapper;
    @Autowired
    private IKolCelebrityService kolCelebrityService;

    /**
     * 查询Instagram网红标签未分配的数量
     *
     * @param igSearchModel
     * @return
     */
    @Override
    public Long getUnallottedTagCount(KolSearchModel searchModel) {
        Long countPlus = 0L;
        try {
            kolCelebrityService.createTempTable(searchModel);
            countPlus = baseMapper.getUnallottedTagCount(searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        return countPlus;
    }

    /**
     * 获取标签网红未分配列表
     *
     * @param igSearchModel
     * @return
     */
    @Override
    public List<UserTagAllotModel> getUnallottedTagList(KolSearchModel igSearchModel) {
        return baseMapper.getUnallottedTagList(igSearchModel);
    }

    /**
     * 保存分配网红标签关联的数据
     *
     * @param igTagsUpdateList
     * @param igTagsCounselorAddList
     * @param igTagsCounselorUpdateList
     * @param kolAllotLogDetailEntity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAllotTags(List<IgCelebrityTags> igTagsUpdateList, List<IgCelebrityTagsCounselor> igTagsCounselorAddList,
                              List<IgCelebrityTagsCounselor> igTagsCounselorUpdateList, KolAllotLogDetail kolAllotLogDetailEntity) {
        if (oConvertUtils.listIsNotEmpty(igTagsUpdateList)) {
            this.updateBatchById(igTagsUpdateList);
        }
        if (oConvertUtils.listIsNotEmpty(igTagsCounselorAddList)) {
            igTagsCounselorService.saveBatch(igTagsCounselorAddList);
        }
        if (oConvertUtils.listIsNotEmpty(igTagsCounselorUpdateList)) {
            igTagsCounselorService.updateBatchById(igTagsCounselorUpdateList);
        }
        kolAllotLogDetailService.save(kolAllotLogDetailEntity);
    }

    /**
     * 获取Instagram标签未分配数量分类汇总
     *
     * @param igSearchModel
     * @return
     */
    @Override
    public IPage<KolTagCountModel> getUnallottedTagCountSummary(Page<KolTagCountModel> page, KolSearchModel igSearchModel) {
        return baseMapper.getUnallottedTagCountSummary(page, igSearchModel);
    }

    /**
     * 方法描述: 导出标签数据
     *
     * @author nqr
     * @date 2025/01/03 18:23
     **/
    @Override
    public List<TiktokTagsNumModel> exportTagsExcel(KolSearchModel kolSearchModel) {
        return baseMapper.exportTagsExcel(kolSearchModel);
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
        IgCelebrityTagsCounselor tagsCounselor = igTagsCounselorService.getById(id);
        if (tagsCounselor == null) {
            throw new JeecgBootException("未找到该网红顾问信息");
        }
        String tagsId = tagsCounselor.getIgCelebrityTagsId();
        String counselorId = kolSearchModel.getCounselorId();
        String counselorName = kolSearchModel.getCounselorName();
        IgCelebrityTags celebrityTags = this.getById(tagsId);
        if (celebrityTags == null) {
            throw new JeecgBootException("未找到该标签信息");
        }
        String logDetailId = celebrityTags.getAllotLogDetailId();
        String tagName = celebrityTags.getTagName();
        String logId = celebrityTags.getAllotLogId();

        IgCelebrityTags celebrityTagsNew = new IgCelebrityTags();
        celebrityTagsNew.setId(tagsId);
        celebrityTagsNew.setCounselorId(counselorId);
        celebrityTagsNew.setCounselorName(counselorName);

        IgCelebrityTagsCounselor tagsCounselorNew = new IgCelebrityTagsCounselor();
        tagsCounselorNew.setId(id);
        tagsCounselorNew.setCounselorName(kolSearchModel.getCounselorName());
        tagsCounselorNew.setCounselorId(kolSearchModel.getCounselorId());
        igTagsCounselorService.updateById(tagsCounselorNew);

        if (oConvertUtils.isEmpty(logDetailId)) {
            this.updateById(celebrityTagsNew);
            return;
        }


        this.updateById(celebrityTagsNew);
        allotLogDetailService.updateAllotLog(counselorId, counselorName, logDetailId, tagName, logId, IgCelebrityTags.class, celebrityTagsNew);
    }

    @Override
    public List<IgCelebrityTagsModel> getUnAlloteTagList(KolSearchModel searchModel) {
        List<IgCelebrityTagsModel> unAlloteTagList = new ArrayList<>();
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
    public void saveAllotTagsNew(List<IgCelebrityTags> igTagsUpdateList, List<IgCelebrityTagsCounselor> igTagsCounselorAddList, List<KolAllotLogDetail> allotLogDetailList, List<KolTags> kolTagsUpdateList) {
        if (oConvertUtils.listIsNotEmpty(igTagsUpdateList)) {
            igTagsUpdateList.forEach(tag -> {
                tag.setTagName(null);
                tag.setCounselorId(null);
                tag.setCounselorName(null);
                tag.setIgUsername(null);
                tag.setTagType(null);
            });
            this.updateBatchById(igTagsUpdateList,1000);
        }
        if (oConvertUtils.listIsNotEmpty(igTagsCounselorAddList)) {
            igTagsCounselorService.saveBatch(igTagsCounselorAddList);
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
    public List<IgCelebrityTags> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName) {
        return this.baseMapper.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
    }

    @Override
    public IPage<IgCelebrityTagsModel> getUnAlloteTagPageList(Page<IgCelebrityTagsModel> page, KolSearchModel searchModel) {
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
