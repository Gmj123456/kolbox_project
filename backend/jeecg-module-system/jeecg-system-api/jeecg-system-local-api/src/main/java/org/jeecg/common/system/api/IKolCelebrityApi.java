package org.jeecg.common.system.api;

import org.jeecg.common.system.vo.CelebrityPrivateModel;
import org.jeecg.common.system.vo.KolProductVo;

import java.util.List;

/**
 * 功能描述:网红相关共用模块，提供给其他模块独立调用
 *
 * @Author: nqr
 * @return
 * @Date 2023-10-18 08:08:25
 */
public interface IKolCelebrityApi {
    Integer getByProductId(String id);
    void updatePromotionGoodsInfo(KolProductVo productVo);
    boolean getPromGoods(String id);
    List<CelebrityPrivateModel> getPrivateCelebrityList(List<String> celebrityNames);
}
