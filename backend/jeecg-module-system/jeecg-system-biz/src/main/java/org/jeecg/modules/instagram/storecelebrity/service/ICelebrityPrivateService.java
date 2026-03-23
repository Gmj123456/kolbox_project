package org.jeecg.modules.instagram.storecelebrity.service;

import org.jeecg.common.system.vo.StoreCelebrityPrivateExcelModel;
import org.jeecg.modules.instagram.entity.IgCelebrity;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateAttributeRelation;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.tiktok.entity.TiktokCelebrity;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;

import java.util.List;
import java.util.Map;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
public interface ICelebrityPrivateService {

    void saveAll(List<StoreCelebrityPrivate> listNew, List<StoreCelebrityPrivateCounselor> privateCounselors, List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations, List<StoreCelebrityPrivatePersonalTags> personalTagsList);

    void saveBatchData(List<StoreCelebrityPrivate> processedCelebrities, List<StoreCelebrityPrivateCounselor> counselorRelations,
                       List<StoreCelebrityPrivateAttributeRelation> privateAttributeRelations, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList,
                       List<TiktokCelebrity> tiktokCelebrities, List<YoutubeCelebrity> youtubeCelebrities, List<IgCelebrity> igCelebrities);

    /**
     * @description: 更新私有网红信息并处理社媒属性（删除旧属性，创建新属性）
     * @author: lf
     * @date: 2025/8/18
     */
    void updateCelebrityWithAttributes(StoreCelebrityPrivate celebrityPrivate, List<StoreCelebrityPrivateAttributeRelation> attributeRelations);

    /**
     * @description: 更新私有网红信息并处理社媒属性（删除旧属性，创建新属性）
     * @author: lf
     * @date: 2025/8/18
     */
    void updateCelebritiesWithAttributes(List<StoreCelebrityPrivate> celebrities, List<StoreCelebrityPrivateAttributeRelation> allAttributeRelations);
    /**
     * @description: 导入私有网红数据
     * @author: nqr
     * @date: 2025/8/29 17:35
     **/
    Map<String,Object> importStoreCelebrityPrivate(List<StoreCelebrityPrivateExcelModel> list, String userId, String userName);

    Map<String, Object> importFeishuCelebrityPrivate(List<StoreCelebrityPrivateExcelModel> privateExcelModels, String userId, String username);
}