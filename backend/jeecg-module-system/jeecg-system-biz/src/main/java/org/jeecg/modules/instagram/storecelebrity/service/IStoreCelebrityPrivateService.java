package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.instagram.storecelebrity.model.*;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
public interface IStoreCelebrityPrivateService extends IService<StoreCelebrityPrivate> {
    IPage<StoreCelebrityPrivateModel> getPageList(Page<StoreCelebrityPrivateModel> page, StoreCelebrityPrivateModel storeCelebrityPrivateModel);

    List<StoreSellDetailModel> getDetailById(StoreSellDetailModel storeSellDetailModel);

    /**
     * 私有网红列表查询
     *
     * @Author: zhoushen
     */
    IPage<StoreCelebrityPrivateModel> getPrivatePageList(Page<StoreCelebrityPrivateModel> page, StoreCelebrityPrivateModel storeCelebrityPrivateModel, String sql);

    IPage<StoreCelebrityPrivateModel> getCelebrityPrivateList(Page<StoreCelebrityPrivateModel> page, StoreCelebrityPrivateModel storeCelebrityPrivateModel, String sql);

    /**
     * 功能描述: 获取网红信息
     *
     * @Date 2021-02-08 11:11:37
     */
    List<StoreCelebrityPrivateModel> getKolData(String goodsId);

    /**
     * 功能描述:根据推广id获取网红信息
     *
     * @param promId
     * @return java.util.List<org.jeecg.modules.instagram.storecelebrityprivate.model.StoreCelebrityPrivateModel>
     * @Date 2021-06-24 16:01:02
     */
    List<StoreCelebrityPrivateModel> getCelebrityData(String promId);

    /**
     * 功能描述:根据账号查询mcn网红
     *
     * @return java.util.List<org.jeecg.modules.instagram.storecelebrityprivate.entity.StoreCelebrityPrivate>
     * @Date 2023-09-16 08:47:14
     */
    List<StoreCelebrityPrivate> getMcnByAccounts(List<String> accounts, Integer isMcn, Integer platformType);

    int getCelebrityPrivateListCount(StoreCelebrityPrivateModel storeCelebrityPrivateModel, String sql);

    /**
     * 功能描述:判断私有网红是否存在
     *
     * @return org.jeecg.modules.instagram.storecelebrityprivate.entity.StoreCelebrityPrivate
     * @Date 2023-09-21 16:42:20
     */
    StoreCelebrityPrivate checkCelebrityPrivate(String account, Integer platformType);

    /**
     * 功能描述：更新私有网红建联邮箱对应的网红顾问
     *
     * @param privateCounselorModel
     * @Param:
     * @Author: fengLiu
     * @Date: 2024-01-16 10:12
     */
    void updateCounselorByEmails(StoreCelebrityPrivateCounselorModel privateCounselorModel);

    /**
     * 获取所有符合条件的私有网红列表。
     *
     * @param storeCelebrityPrivateModel 用于过滤私有网红列表模型。
     * @return 匹配指定条件的私有网红列表。
     */
    List<StoreCelebrityPrivateModel> getAllCelebrityPrivates(StoreCelebrityPrivateModel storeCelebrityPrivateModel);

    List<StoreCelebrityPrivate> listByAccounts(List<String> accounts);

    /**
     * 功能描述：获取网红各平台网红分布
     *
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 9:03
     */
    List<PlatformDistributionModel> getPlatformDistribution();

    /**
     * 功能描述：获取签约网红统计数据
     *
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 10:05
     */
    CelebritySignedModel getCelebritySignedStats();


    /*
     * 功能描述：获取网红各平台粉丝数区间汇总
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 15:04
     */
    List<CelebrityFlowersStatsModel> getCelebrityFollowersStats(Integer platformType);

    /**
     * 功能描述：获取各平台网红签约费用区间汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 17:54
     */
    List<CelebrityCostStatsModel> getCelebrityCostStats(Integer platformType);

    /**
     * 功能描述：获取网红顾问签约数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 18:20
     */
    Map<String, Object> getCelebrityCounselorStats(Integer platformType, String contractStartTime, String contractEndTime);

    /**
     * 功能描述：获取各平台网红类目数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 18:52
     */
    Map<String, Object> getCelebrityCategoryStats(Integer platformType);

    /**
     * 功能描述：获取网红顾问各个平台、各个类目数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-18 11:45
     */
    List<CounselorCategoryStatsModel> getCounselorCategoryStats(Integer platformType);

    /**
     * 验证网红信息
     *
     * @param storeCelebrityPrivate 网红信息
     * @return 处理结果
     */
    boolean verifyCelebrityInfo(StoreCelebrityPrivate storeCelebrityPrivate);

    /**
     * 功能描述:编辑个性化标签
     *
     * @return void
     * @Date 2023-07-11 17:27:32
     */
    String updatePersonalTags(String personalTags);

    /**
     * 批量更新网红异常状态
     *
     * @param celebrityIds      网红ID列表
     * @param isAbnormalAccount 异常状态（0:正常; 1:异常）
     * @return Map 包含处理结果和不存在的ID列表
     */
    Map<String, Object> batchUpdateAbnormalStatus(List<String> celebrityIds, Integer isAbnormalAccount);

    /**
     * @description:私有网红导出
     * @author: nqr
     * @date: 2025/6/23 10:30
     **/

    List<CelebrityPrivateExportModel> getCelebrityPrivateExportList(StoreCelebrityPrivateModel storeCelebrityPrivateModel);

    /**
     * @description: 新增私有网红
     * @author: nqr
     * @date: 2025/6/30 15:04
     **/
    void addData(StoreCelebrityPrivate storeCelebrityPrivate, List<StoreCelebrityPrivateProduct> privateProductListSave, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList);

    /**
     * @description: 编辑私有网红
     * @author: nqr
     * @date: 2025/6/30 15:07
     **/

    void editData(StoreCelebrityPrivate storeCelebrityPrivate, List<StoreCelebrityPrivateProduct> privateProductListSave,List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList);

    Result<?> extractedPrivateProducts(String data) throws IOException;

    void saveImportData(ImportPrivateProductResult importResult);

    List<StoreCelebrityPrivateModel> getCelebrityPrivateListNew(StoreCelebrityPrivateModel storeCelebrityPrivateModel);

    ImportPrivateProductResult importPrivateProducts(List<StoreCelebrityPrivateProductExcelModel> list, String userId);

    /**
     * @description: 处理个性化标签，并创建关联关系
     * @author: nqr
     * @date: 2025/8/29 11:48
     **/
    String processingPersonalizedLabels(List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList,String privateId, String personalTags);

    void processingPersonalizedTags(List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList, String id, String personalTagIds);
    /**
     * @description: 确认个性化标签
     * @author: nqr
     * @date: 2025/10/23 9:27
     **/
    void confirmedPersonalTags(String celebrityPrivateId,List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList);

    void processingPersonalizedTagsNew(List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList, String privateId, String personalTagIds);

}