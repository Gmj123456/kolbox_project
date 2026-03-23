package org.jeecg.modules.instagram.storecelebrity.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.model.*;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.system.entity.SysDictItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
public interface StoreCelebrityPrivateMapper extends BaseMapper<StoreCelebrityPrivate> {

    IPage<StoreCelebrityPrivateModel> getPageList(Page<StoreCelebrityPrivateModel> page, @Param("storeCelebrityPrivateModel") StoreCelebrityPrivateModel storeCelebrityPrivateModel);

    List<StoreSellDetailModel> getDetailById(@Param("storeSellDetailModel") StoreSellDetailModel storeSellDetailModel);

    /**
     * 私有网红列表查询
     *
     * @Author: zhoushen
     */
    IPage<StoreCelebrityPrivateModel> getPrivatePageList(Page<StoreCelebrityPrivateModel> page, @Param("storeCelebrityPrivateModel") StoreCelebrityPrivateModel storeCelebrityPrivateModel, @Param("permissionSql") String permissionSql);

    /**
     * 功能描述: 获取网红信息
     *
     * @Author: nqr
     * @Date 2021-02-08 11:12:05
     */
    List<StoreCelebrityPrivateModel> getKolData(@Param("goodsId") String goodsId);

    /**
     * 功能描述:根据推广id获取网红信息
     *
     * @param promId
     * @return java.util.List<org.jeecg.modules.instagram.storecelebrityprivate.model.StoreCelebrityPrivateModel>
     * @Date 2021-06-24 16:01:02
     */
    List<StoreCelebrityPrivateModel> getCelebrityData(@Param("promId") String promId);
    @InterceptorIgnore(tenantLine = "true")
    IPage<StoreCelebrityPrivateModel> getCelebrityPrivateList(Page<StoreCelebrityPrivateModel> page, @Param("storeCelebrityPrivateModel") StoreCelebrityPrivateModel storeCelebrityPrivateModel, @Param("permissionSql") String permissionSql);

    Integer getCelebrityPrivateListCount(@Param("storeCelebrityPrivateModel") StoreCelebrityPrivateModel storeCelebrityPrivateModel, @Param("permissionSql") String permissionSql);



    /**
    * 功能描述：更新私有网红建联邮箱对应的网红顾问
    * @Param:
     * @param counselorModel
    * @Author: fengLiu
    * @Date: 2024-01-16 10:13
    */
    void updateCounselorByEmails(@Param("counselorModel") StoreCelebrityPrivateCounselorModel counselorModel);

    /**
     * 获取所有符合条件的私有网红列表。
     *
     * @param storeCelebrityPrivateModel 用于过滤私有网红列表模型。
     * @return 匹配指定条件的私有网红列表。
     */
    List<StoreCelebrityPrivateModel> getAllCelebrityPrivates(@Param("storeCelebrityPrivateModel")StoreCelebrityPrivateModel storeCelebrityPrivateModel);

    /**
     * 功能描述：获取网红各平台网红分布
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 9:04
     */
    List<PlatformDistributionModel> getPlatformDistribution();

    /**
     * 功能描述：获取签约网红统计数据
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 10:05
     */
    CelebritySignedModel getSignedCelebrityStats();

    /**
     * 功能描述：获取网红粉丝统计数据
     *
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 10:05
     */
    List<HashMap<String, Object>> getCelebrityFollowersStats(@Param("dictItems") List<SysDictItem> dictItems,
                                                             @Param("platformType") Integer platformType);

    /**
     * 功能描述：获取各平台网红签约费用区间汇总
     * @Param:
     * @param platformType
     * @Author: fengLiu
     * @Date: 2025-03-17 17:54
     */
    List<HashMap<String, Object>> getCelebrityCostStats(@Param("dictItems") List<SysDictItem> dictItems, @Param("platformType") Integer platformType);

    /**
     * 功能描述：获取网红顾问签约数量汇总
     * @Param:
     * @param params 包含dictItems, platformType, contractStartTime, contractEndTime的Map
     * @Author: fengLiu
     * @Date: 2025-03-17 18:52
     */
    List<HashMap<String, Object>> getCelebrityCounselorStats(@Param("params") Map<String, Object> params);

    /**
     * 功能描述：获取各平台网红类目数量汇总
     * @Param:
     * @param platformType
     * @Author: fengLiu
     * @Date: 2025-03-17 18:52
     */
    List<HashMap<String, Object>> getCelebrityCategoryStats(@Param("dictItems") List<SysDictItem> dictItems, @Param("platformType") Integer platformType);

    /**
     * 功能描述：获取网红顾问各个平台、各个类目数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-18 11:45
     */
    List<CounselorCategoryStatsModel> getCounselorCategoryStats(@Param("dictItems") List<SysDictItem> dictItems, @Param("platformType") Integer platformType);

    List<CelebrityPrivateExportModel> getCelebrityPrivateExportList(@Param("storeCelebrityPrivateModel") StoreCelebrityPrivateModel storeCelebrityPrivateModel);

    List<StoreCelebrityPrivateModel> getCelebrityPrivateListNew(@Param("storeCelebrityPrivateModel") StoreCelebrityPrivateModel storeCelebrityPrivateModel);
}
