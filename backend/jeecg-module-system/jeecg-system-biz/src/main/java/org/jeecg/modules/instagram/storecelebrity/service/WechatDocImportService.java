package org.jeecg.modules.instagram.storecelebrity.service;

import org.jeecg.common.system.vo.StoreCelebrityPrivateExcelModel;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateProductModel;

import java.util.List;

public interface WechatDocImportService {
    /**
     * @description: 导入私有网红数据
     * @author: nqr
     * @date: 2025/8/29 17:35
     **/
    void importStoreCelebrityPrivate(List<StoreCelebrityPrivateExcelModel> list, String docid, String sheetId);

    /**
    * 功能描述：导入私有网红合作产品
    * @Param: 
     * @param privateProductModels
     * @param templateDocId
     * @param privateProductSheetId 
    * @Author: fengLiu
    * @Date: 2025-09-09 8:45
    */
    void importPrivateCelebrityProducts(List<StoreCelebrityPrivateProductModel> privateProductModels, String templateDocId, String privateProductSheetId);
}
