package org.jeecg.modules.tiktok.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.jeecg.modules.kol.service.IKolTagAllotService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: tk标签网红顾问表
 * @Author: xyc
 * @Date:   2024-12-26 18:07:30
 * @Version: V1.0
 */
public interface ITkCelebrityTagsCounselorService extends IService<TiktokCelebrityTagsCounselor>, IKolTagAllotService<KolTagAllotModel> {


    /**
     * 根据counselorId获取分配顾问明细列表
     *
     * @param counselorId
     * @return
     */
    List<TiktokCelebrityTagsCounselor> getListByCounselorId(String counselorId);

    /**
     * 获取网红分配历史明细视图模型数据列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<KolTagAllotModel> getTagAllottedList(IPage<KolTagAllotModel> page, KolSearchModel searchModel);

    List<TiktokCelebrityTagsCounselor> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName);
}
