package org.jeecg.modules.instagram.storecelebrity.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityProduct;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class StoreCelebrityPrivateModel extends StoreCelebrityPrivate {

    /**
     * 粉丝开始数量
     */
    private Integer followersStartNum;

    /**
     * 粉丝结束数量
     */
    private Integer followersEndNum;

    /**
     * 帖子数开始数量
     */
    private Integer postsStartNum;

    /**
     * 帖子数结束数量
     */
    private Integer postsEndNum;

    /**
     * 合作时间开始时间
     */
    private String startCooperationTime;

    /**
     * 合作时间结束时间
     */
    private String endCooperationTime;

    /**
     * 签约时间开始时间
     */
    private String startContractTime;

    /**
     * 签约时间结束时间
     */
    private String endContractTime;

    /**
     * 货币符号
     */
    private String symbol;

    /**
     * 推广ID
     */
    private String promId;

    /**
     * 物流单号
     */
    private String goodsWaybill;

    /**
     * 运输商
     */
    private String goodsCarrier;


    /**
     * 推广视频图片列表（JSON格式存取）
     */
    private String mediaImgJson;


    /**
     * 推广视频链接列表
     */
    private String mediaUploads;

    /**
     * 推广状态
     */
    private Integer celebrityPromStatus;
    /**
     * 追加金额
     */
    private BigDecimal appendGoodsAmount;
    /**
     * 商品ID
     */
    private String goodsId;

    private BigDecimal paymentAmount;

    /**
     * 最小网红签约费用
     */
    private BigDecimal mixContractAmount;

    /**
     * 最大网红签约费用
     */
    private BigDecimal maxContractAmount;
    /**
     * 二维码
     */
    private String qrCode;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 商家名称
     */
    private String sellerName;
    /**
     * 货币符号
     */
    private String currencySymbol;
    /**
     * 品牌
     */
    private String productBrand;
    /**
     * 电话
     */
    private String phone;
    /**
     * 播放量
     */
    private Integer playAvgStartNum;
    /**
     * 播放量
     */
    private Integer playAvgEndNum;
    /**
     * 排序列
     */
    private String column;
    /**
     * 排序
     */
    private String order;
    /**
     * 网红顾问
     */
    List<StoreCelebrityPrivateCounselor> privateCounselorList;

    /**
     * 均播放量（K）
     */
    private BigDecimal playAvgNumDecimal;

    /**
     * 历史选中
     */
    private String productSelectIds;
    private String productSelectInfo;
    /**
     * 历史合作
     */
    private String productCooperateIds;
    private String productCooperateInfo;
    /**
     * 关系类型（0:产品推广, 1:品牌宣传, 2:地推）
     */
    private Integer relationType;

    private List<TypeData> dataList;
    private String dataListStr;


    /**
     * 社媒属性ID列表（逗号分隔的字符串，例如 "attr1,attr2,attr3"）
     */
    private String attributeIds;

    /**
     * 行业标签ID列表（逗号分隔的字符串，例如 "24000000000,25000000000"）
     */
    private String categoryCode;

    /**
     * 历史选中产品ID列表（逗号分隔的字符串，例如 "prod1,prod2"）
     */
    private String selectedProductIds;

    /**
     * 历史合作产品ID列表（逗号分隔的字符串，例如 "prod3,prod4"）
     */
    private String cooperatedProductIds;
    /**
     * 可确认社媒属性标识（0：不可确认，1：可确认）
     */
    private Integer isAttributeUpdate;

    private String userId;
    /**
     * 视频内容费用
     */
    private List<PrivateContractInfo> privateContractInfoList;

    private String productId;
    private String promBrandtId;

    private String personalTagIds;
    private String personalTagNames;

    private String categoryIds;
    private String platFormTypeStr;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "视频最近发布日期")
    private Date videoCreateTimeLast;
    private String videoCreateTimeLastStart;
    private String videoCreateTimeLastEnd;

    private String selectedIds;

    private List<TiktokCelebrityProduct> tiktokCelebrityProductList;

    private BigDecimal videoGmvValueStart;
    private BigDecimal videoGmvValueEnd;

    /**
     * 是否置顶 0：否 1：是
     */
    private Integer isTop;


    private String kolTypes;
    /**
     * 网红ID
     */
    private String kolId;
    /**
     * 商务人员Id
     */
    @Excel(name = " 商务人员Id", width = 15)
    @Schema(title =  " 商务人员Id")
    private String counselorUserId;
    /**
     * 商务人员名称
     */
    @Excel(name = " 商务人员名称", width = 15)
    @Schema(title =  " 商务人员名称")
    private String counselorUserName;

    private List<StoreCelebrityPrivateProduct> privateProducts;
}
