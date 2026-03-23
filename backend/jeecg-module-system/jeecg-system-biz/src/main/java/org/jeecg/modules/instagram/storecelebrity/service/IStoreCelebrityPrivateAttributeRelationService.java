package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateAttributeRelation;
import org.jeecg.modules.kol.model.TypeData;

import java.util.List;
import java.util.Map;

/**
 * @Description: 私有网红社媒属性关联表
 * @Author: jeecg-boot
 * @Date: 2025-07-24
 * @Version: V1.0
 */
public interface IStoreCelebrityPrivateAttributeRelationService extends IService<StoreCelebrityPrivateAttributeRelation> {
    /**
     * @description: 创建私有网红社媒关联属性
     * @author: nqr
     * @date: 2025/7/24 15:47
     **/
    List<StoreCelebrityPrivateAttributeRelation> createAttributeRelation(StoreCelebrityPrivate celebrityPrivate, List<TypeData> dataList);

    List<Map<String, String>> selectAttributeStructureBatch(List<String> celebrityPrivateIds);
    List<TypeData> getCelebrityAttributes(List<String> celebrityPrivateIds);
}
