package org.jeecg.modules.instagram.storepromotion.service;

import com.alibaba.fastjson.JSONObject;

public interface IBasePromotionService {
    /**
     * 功能描述:根据推广id查询推广信息，推广产品信息，推广网红信息

    * @return com.alibaba.fastjson.JSONObject
    * @Date 2023-08-04 09:57:50
    */
    JSONObject getPromotionData(String promId);
}
