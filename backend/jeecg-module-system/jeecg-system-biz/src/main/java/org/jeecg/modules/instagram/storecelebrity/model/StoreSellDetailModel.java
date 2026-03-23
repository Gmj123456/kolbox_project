package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreSellDetail;



@Data
public class StoreSellDetailModel extends StoreSellDetail {

    /**
     * 商家ID
     */
    private String sellerId;
    /**
     * 商家昵称
     */
    private String sellerName;
    /**
     * 商品标题
     */
    private String goodsTitle;
    /**
     * 商品图片
     */
    private String goodsPicUrl;
    /**
     * 商品链接
     */
    private String goodsUrl;

    /**
     * 网红签约产品单价
     */
    private java.math.BigDecimal goodsPrice;

    /**
     * 商品品牌
     */
    private String goodsBrand;
    /**
     * 商品名称
     * */
    private String sellerGoodsName;
    /**
     * 私有网红账号
     * */
    private String privateAccount;

}
