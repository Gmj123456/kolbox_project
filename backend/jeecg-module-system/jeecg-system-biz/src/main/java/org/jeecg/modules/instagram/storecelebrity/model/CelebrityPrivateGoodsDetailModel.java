package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreSellDetail;

@Data
public class CelebrityPrivateGoodsDetailModel extends StoreSellDetail {
    /**
     * 		私有网红昵称
     * */
    private String nickName;
    /**
     * 		私有网红账号
     * */
    private String account;

    /**
     * 图片url
     * */
    private String goodsPicUrl;
    /**
     * 商品标题
     * */
    private String goodsTitle;
    /**
     * 接收 月份字段
     * */
    private String monthNew;
    /**
     * 商家名称
     * */
    private String sellerName;


}
