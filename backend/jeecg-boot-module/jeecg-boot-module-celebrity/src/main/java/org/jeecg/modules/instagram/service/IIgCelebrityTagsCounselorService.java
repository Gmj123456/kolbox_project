package org.jeecg.modules.instagram.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.entity.IgCelebrityTagsCounselor;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolTagAllotModel;
import org.jeecg.modules.kol.service.IKolTagAllotService;

import java.util.List;

/**
 * @Description: 网红标签顾问分配明细表
 * @Author: xyc
 * @Date: 2024年12月11日 15:20:01
 * @Version: V1.1
 */
public interface IIgCelebrityTagsCounselorService extends IService<IgCelebrityTagsCounselor>, IKolTagAllotService<KolTagAllotModel> {


    /**
     * 根据counselorId获取ig_celebrity_tags_counselor列表
     *
     * @param counselorId
     * @return
     */
    List<IgCelebrityTagsCounselor> getListByCounselorId(String counselorId);


    /**
     * 获取IG网红分配历史明细视图模型数据列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<KolTagAllotModel> getIgTagAllottedList(IPage<KolTagAllotModel> page, KolSearchModel searchModel);

    List<IgCelebrityTagsCounselor> getCelebrityTagsList(KolSearchModel searchModel, List<String> uniqueIdList, String tempTableName);
}
