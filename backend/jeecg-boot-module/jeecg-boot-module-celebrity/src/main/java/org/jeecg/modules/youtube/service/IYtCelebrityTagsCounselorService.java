package org.jeecg.modules.youtube.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.jeecg.modules.kol.service.IKolTagAllotService;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTagsCounselor;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: YT标签网红顾问分配接口层
 * @Author: xyc
 * @Date:   2024-08-28
 * @Version: V1.0
 */
public interface IYtCelebrityTagsCounselorService extends IService<YoutubeCelebrityTagsCounselor>, IKolTagAllotService<KolTagAllotModel> {


    /**
     * 根据counselorId获取分配顾问明细列表
     *
     * @param counselorId
     * @return
     */
    List<YoutubeCelebrityTagsCounselor> getListByCounselorId(String counselorId);

    /**
     * 获取网红分配历史明细视图模型数据列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<KolTagAllotModel> getTagAllottedList(IPage<KolTagAllotModel> page, KolSearchModel searchModel);

    List<YoutubeCelebrityTagsCounselor> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName);
}
