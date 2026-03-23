package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityPrivateMapper;
import org.jeecg.modules.instagram.storecelebrity.model.*;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateProductService;

import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storetags.entity.StoreCelebrityPrivatePersonalTags;
import org.jeecg.modules.instagram.storetags.entity.StorePersonalTags;
import org.jeecg.modules.instagram.storetags.service.IStoreCelebrityPrivatePersonalTagsService;
import org.jeecg.modules.instagram.storetags.service.IStorePersonalTagsService;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.feishu.config.FeishuAppConfig;
import org.jeecg.modules.feishu.model.PrivateProductFields;
import org.jeecg.modules.feishu.model.PrivateProductRecord;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.model.TypeData;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.service.ISysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
@Service
public class StoreCelebrityPrivateServiceImpl extends ServiceImpl<StoreCelebrityPrivateMapper, StoreCelebrityPrivate> implements IStoreCelebrityPrivateService {

    @Autowired
    StoreCelebrityPrivateMapper storeCelebrityPrivateMapper;
    @Autowired
    private ISysDictService sysDictService;
    @Autowired
    private IStorePersonalTagsService storePersonalTagsService;
    @Autowired
    private IStoreCelebrityPrivateProductService privateProductService;
    @Autowired
    @Lazy
    private IKolProductService productService;
    @Autowired
    @Lazy
    private IFeishuService feishuService;
    @Autowired
    @Lazy
    private FeishuAppConfig feishuAppConfig;
    @Resource
    private IStoreCelebrityPrivatePersonalTagsService privatePersonalTagsService;

    @Override
    public IPage<StoreCelebrityPrivateModel> getPageList(Page<StoreCelebrityPrivateModel> page, StoreCelebrityPrivateModel storeCelebrityPrivateModel) {
        return storeCelebrityPrivateMapper.getPageList(page, storeCelebrityPrivateModel);
    }

    @Override
    public List<StoreSellDetailModel> getDetailById(StoreSellDetailModel storeSellDetailModel) {
        return storeCelebrityPrivateMapper.getDetailById(storeSellDetailModel);
    }

    /**
     * 私有网红列表查询
     *
     * @Author: zhoushen
     */
    @Override
    public IPage<StoreCelebrityPrivateModel> getPrivatePageList(Page<StoreCelebrityPrivateModel> page, StoreCelebrityPrivateModel storeCelebrityPrivateModel, String sql) {
        return storeCelebrityPrivateMapper.getPrivatePageList(page, storeCelebrityPrivateModel, sql);
    }

    @Override
    public IPage<StoreCelebrityPrivateModel> getCelebrityPrivateList(Page<StoreCelebrityPrivateModel> page, StoreCelebrityPrivateModel storeCelebrityPrivateModel, String sql) {
        try {
            IPage<StoreCelebrityPrivateModel> pageList = storeCelebrityPrivateMapper.getCelebrityPrivateList(page, storeCelebrityPrivateModel, sql);
            return pageList;
        } catch (Exception e) {
            log.error("执行getCelebrityPrivateList时发生错误: ", e);
            throw new RuntimeException("查询私有网红列表失败: " + e.getMessage(), e);
        }
    }

    private List<StoreCelebrityPrivateModel> getCategoryRelationModels(List<StoreCelebrityPrivateModel> list) {
        ObjectMapper mapper = new ObjectMapper();
        list.forEach(item -> {
            String dataListStr = item.getDataListStr();
            if (dataListStr == null) {
                return;
            }
            try {
                // 直接反序列化为 List<TypeData>
                List<TypeData> dataList = mapper.readValue(dataListStr, new TypeReference<List<TypeData>>() {
                });
                dataList.forEach(typeData -> typeData.getList().forEach(categoryItem -> categoryItem.setIsSel(true)));
                item.setDataListStr(null);
                item.setDataList(dataList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    @Override
    public List<StoreCelebrityPrivateModel> getKolData(String goodsId) {
        return storeCelebrityPrivateMapper.getKolData(goodsId);
    }

    /**
     * 功能描述:根据推广id获取网红信息
     *
     * @param promId
     * @return java.util.List<org.jeecg.modules.instagram.storecelebrityprivate.model.StoreCelebrityPrivateModel>
     * @Date 2021-06-24 16:01:02
     */
    @Override
    public List<StoreCelebrityPrivateModel> getCelebrityData(String promId) {
        return storeCelebrityPrivateMapper.getCelebrityData(promId);
    }

    /**
     * 功能描述:根据账号查询mcn网红
     *
     * @return java.util.List<org.jeecg.modules.instagram.storecelebrityprivate.entity.StoreCelebrityPrivate>
     * @Date 2023-09-16 08:47:42
     */
    @Override
    public List<StoreCelebrityPrivate> getMcnByAccounts(List<String> accounts, Integer isMcn, Integer platformType) {
        LambdaQueryWrapper<StoreCelebrityPrivate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StoreCelebrityPrivate::getAccount, accounts);
        if (oConvertUtils.isNotEmpty(platformType)) {
            queryWrapper.eq(StoreCelebrityPrivate::getPlatformType, platformType);
        }
        queryWrapper.eq(StoreCelebrityPrivate::getIsMcn, isMcn);
        return this.list(queryWrapper);
    }

    /**
     * 功能描述:
     *
     * @return int
     * @Date 2023-09-18 17:37:12
     */
    @Override
    public int getCelebrityPrivateListCount(StoreCelebrityPrivateModel storeCelebrityPrivateModel, String sql) {
        return this.baseMapper.getCelebrityPrivateListCount(storeCelebrityPrivateModel, sql);
    }

    @Override
    public StoreCelebrityPrivate checkCelebrityPrivate(String account, Integer platformType) {
        LambdaQueryWrapper<StoreCelebrityPrivate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCelebrityPrivate::getAccount, account);
        lambdaQueryWrapper.eq(StoreCelebrityPrivate::getPlatformType, platformType);
        return list(lambdaQueryWrapper).stream().findFirst().orElse(null);
    }


    /**
     * 功能描述：更新私有网红建联邮箱对应的网红顾问
     *
     * @param privateCounselorModel
     * @Param:
     * @Author: fengLiu
     * @Date: 2024-01-16 10:12
     */
    @Override
    public void updateCounselorByEmails(StoreCelebrityPrivateCounselorModel privateCounselorModel) {
        this.baseMapper.updateCounselorByEmails(privateCounselorModel);
    }

    /**
     * 获取所有符合条件的私有网红列表。
     *
     * @param storeCelebrityPrivateModel 用于过滤私有网红列表模型。
     * @return 匹配指定条件的私有网红列表。
     */
    @Override
    public List<StoreCelebrityPrivateModel> getAllCelebrityPrivates(StoreCelebrityPrivateModel storeCelebrityPrivateModel) {
        return storeCelebrityPrivateMapper.getAllCelebrityPrivates(storeCelebrityPrivateModel);
    }

    @Override
    public List<StoreCelebrityPrivate> listByAccounts(List<String> accounts) {
        return lambdaQuery().in(StoreCelebrityPrivate::getAccount, accounts).list();
    }

    /**
     * 功能描述：获取网红各平台网红分布
     *
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 9:04
     */
    @Override
    public List<PlatformDistributionModel> getPlatformDistribution() {
        return this.baseMapper.getPlatformDistribution();
    }

    /**
     * 功能描述：获取签约网红统计数据
     *
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 10:05
     */

    @Override
    public CelebritySignedModel getCelebritySignedStats() {
        return this.baseMapper.getSignedCelebrityStats();
    }

    /*
     * 功能描述：获取网红各平台粉丝数区间汇总
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 15:04
     */
    @Override
    public List<CelebrityFlowersStatsModel> getCelebrityFollowersStats(Integer platformType) {
        List<SysDictItem> dictItems = sysDictService.getItemList(CommonConstant.CELEBRITY_SEGMENT);
        List<HashMap<String, Object>> rawStats = this.baseMapper.getCelebrityFollowersStats(dictItems, platformType);
        // 转换为 CelebrityFlowersStatsModel 对象
        List<CelebrityFlowersStatsModel> result = new ArrayList<>();
        for (HashMap<String, Object> row : rawStats) {
            CelebrityFlowersStatsModel vo = new CelebrityFlowersStatsModel();
            vo.setPlatformName((String) row.get("platform_name"));
            vo.setFollowerRange((String) row.get("follower_range"));
            vo.setTotalCount(((Number) row.get("total_count")).longValue());

            List<CelebrityStarModel> starList = new ArrayList<>();
            for (SysDictItem item : dictItems) {
                String starName = item.getItemText();
                if (row.containsKey(starName)) {
                    CelebrityStarModel starModel = new CelebrityStarModel();
                    starModel.setStarName(starName);
                    starModel.setStarValue(((Number) row.get(starName)).longValue());
                    starModel.setStarColor(item.getDescription()); // 假设颜色根据 itemValue 确定
                    starList.add(starModel);
                }
            }
            vo.setCelebrityStarModelList(starList);
            result.add(vo);
        }
        return result;
    }

    /**
     * 功能描述：获取各平台网红签约费用区间汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 17:54
     */
    @Override
    public List<CelebrityCostStatsModel> getCelebrityCostStats(Integer platformType) {
        List<SysDictItem> dictItems = sysDictService.getItemList(CommonConstant.CELEBRITY_SEGMENT);
        List<HashMap<String, Object>> rawStats = this.baseMapper.getCelebrityCostStats(dictItems, platformType);
        // 转换为 CelebrityCostStatsModel
        List<CelebrityCostStatsModel> result = new ArrayList<>();
        for (HashMap<String, Object> row : rawStats) {
            CelebrityCostStatsModel statsModel = new CelebrityCostStatsModel();
            statsModel.setPlatformName((String) row.get("platform_name"));
            statsModel.setCostRange((String) row.get("cost_range"));
            statsModel.setTotalCount(((Number) row.get("total_count")).longValue());

            List<CelebrityStarModel> starList = new ArrayList<>();
            for (SysDictItem item : dictItems) {
                String starName = item.getItemText();
                if (row.containsKey(starName)) {
                    CelebrityStarModel starModel = new CelebrityStarModel();
                    starModel.setStarName(starName);
                    starModel.setStarValue(((Number) row.get(starName)).longValue());
                    starModel.setStarColor(item.getDescription());
                    starList.add(starModel);
                }
            }
            statsModel.setCelebrityStarModelList(starList);
            result.add(statsModel);
        }
        return result;
    }

    /**
     * 功能描述：获取网红顾问签约数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 18:20
     */
    @Override
    public Map<String, Object> getCelebrityCounselorStats(Integer platformType, String contractStartTime, String contractEndTime) {
        List<SysDictItem> dictItems = sysDictService.getItemList(CommonConstant.CELEBRITY_SEGMENT);
        Map<String, Object> params = new HashMap<>();
        params.put("dictItems", dictItems);
        params.put("platformType", platformType);
        params.put("contractStartTime", contractStartTime);
        params.put("contractEndTime", contractEndTime);

        // 调用 Mapper 获取原始数据
        List<HashMap<String, Object>> rawStats = this.baseMapper.getCelebrityCounselorStats(params);

        // 构建动态列名列表
        List<Map<String, String>> columns = new ArrayList<>();
        for (SysDictItem item : dictItems) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put("column", item.getRemark()); // 添加动态星级列名
            columnMap.put("name", item.getItemText()); // 添加动态星级列名
            columns.add(columnMap);
        }
        // 转换为 CelebrityCounselorStatsModel
        List<CelebrityCounselorStatsModel> celebrityCounselorStatsModels = new ArrayList<>();
        for (HashMap<String, Object> row : rawStats) {
            CelebrityCounselorStatsModel statsModel = new CelebrityCounselorStatsModel();
            statsModel.setPlatformName((String) row.get("platformName"));
            statsModel.setCounselorName((String) row.get("counselorName"));
            statsModel.setTotalSignedCount(((Number) row.get("totalSignedCount")).longValue());
            statsModel.setCounselorId((String) row.get("counselorId"));

            List<CelebrityStarModel> starList = new ArrayList<>();
            for (SysDictItem item : dictItems) {
                String starName = item.getRemark();
                if (row.containsKey(starName)) {
                    CelebrityStarModel starModel = new CelebrityStarModel();
                    starModel.setStarName(starName);
                    starModel.setStarValue(((Number) row.get(starName)).longValue());
                    starModel.setStarColor(item.getDescription());
                    starList.add(starModel);
                }
            }
            statsModel.setCelebrityStarModelList(starList);
            celebrityCounselorStatsModels.add(statsModel);
        }
        // 返回包含数据和列名的 Map
        Map<String, Object> result = new HashMap<>();
        result.put("data", rawStats);
        result.put("dataStats", celebrityCounselorStatsModels);
        result.put("columns", columns);
        return result;
    }

    /**
     * 功能描述：获取各平台网红类目数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-17 18:52
     */
    @Override
    public Map<String, Object> getCelebrityCategoryStats(Integer platformType) {
        // 获取数据字典项
        List<SysDictItem> dictItems = sysDictService.getItemList(CommonConstant.CELEBRITY_SEGMENT);

        // 调用 Mapper 获取原始数据
        List<HashMap<String, Object>> rawStats = this.baseMapper.getCelebrityCategoryStats(dictItems, platformType);
        // 调用获取网红顾问各个平台、各个类目数量汇总
        List<CounselorCategoryStatsModel> counselorCategoryStats = this.baseMapper.getCounselorCategoryStats(dictItems, platformType);
        // 构建动态列名列表
        List<Map<String, String>> columns = new ArrayList<>();
        for (SysDictItem item : dictItems) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put("column", item.getRemark()); // 添加动态星级列名
            columnMap.put("name", item.getItemText()); // 添加动态星级列名
            columns.add(columnMap);
        }
        // 转换为包含所有字段的 Map 结构，包括“其他”分类
        List<Map<String, Object>> data = new ArrayList<>();
        for (HashMap<String, Object> row : rawStats) {
            Map<String, Object> statsMap = new HashMap<>();
            String platformName = (String) row.get("platformName");
            String categoryName = (String) row.get("categoryName");
            statsMap.put("platformName", platformName);
            statsMap.put("categoryName", categoryName);
            statsMap.put("totalCelebrityCount", ((Number) row.get("totalCelebrityCount")).longValue());

            // 添加动态星级字段
            long totalKnownStars = 0;
            for (SysDictItem item : dictItems) {
                String starName = item.getRemark();
                if (row.containsKey(starName)) {
                    long starValue = ((Number) row.get(starName)).longValue();
                    statsMap.put(starName, starValue);
                    totalKnownStars += starValue;
                } else {
                    statsMap.put(starName, 0L); // 未出现的星级设为 0
                }
            }
            // 过滤出匹配当前平台和类目的 counselor 数据
            List<CounselorCategoryStatsModel> filteredCounselorStats = counselorCategoryStats.stream()
                    .filter(stats -> platformName.equals(stats.getPlatformName())
                            && categoryName.equals(stats.getCategoryName()))
                    .collect(Collectors.toList());

            // 将过滤后的 counselor 数据添加到 statsMap
            statsMap.put("counselorStats", filteredCounselorStats);
            data.add(statsMap);
        }

        // 返回包含数据和列名的 Map
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("columns", columns);
        return result;
    }

    /**
     * 功能描述：获取网红顾问各个平台、各个类目数量汇总
     *
     * @param platformType
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-03-18 11:45
     */
    @Override
    public List<CounselorCategoryStatsModel> getCounselorCategoryStats(Integer platformType) {
        // 获取数据字典项
        List<SysDictItem> dictItems = sysDictService.getItemList(CommonConstant.CELEBRITY_SEGMENT);
        // Call Mapper to get raw data
        List<CounselorCategoryStatsModel> result = this.baseMapper.getCounselorCategoryStats(dictItems, platformType);

        return result;
    }

    /**
     * 验证网红信息
     *
     * @param storeCelebrityPrivate 网红信息
     * @return 处理结果
     */
    @Override
    public boolean verifyCelebrityInfo(StoreCelebrityPrivate storeCelebrityPrivate) {
        if (storeCelebrityPrivate == null || storeCelebrityPrivate.getId() == null) {
            return false;
        }
        storeCelebrityPrivate.setCelebrityCounselorId(null);
        storeCelebrityPrivate.setCelebrityCounselorName(null);
        storeCelebrityPrivate.setContractTime(null);
        String personalTags = storeCelebrityPrivate.getPersonalTags();
        // 编辑个性化标签
        personalTags = updatePersonalTags(personalTags);
        storeCelebrityPrivate.setPersonalTags(personalTags);
        // 更新修改时间
        storeCelebrityPrivate.setUpdateTime(new Date());
        // 执行更新
        return this.updateById(storeCelebrityPrivate);
    }


    /**
     * 功能描述:编辑个性化标签
     *
     * @return void
     * @Date 2023-07-11 17:27:32
     */
    @Override
    public String updatePersonalTags(String personalTags) {
        personalTags = StringUtils.lowerCase(personalTags).replaceAll("，", ",").trim();
        List<String> tagList = Arrays.asList(personalTags.split(","));
        tagList = tagList.stream().map(String::trim).filter(oConvertUtils::isNotEmpty).collect(Collectors.toList());
        if (tagList.isEmpty()) {
            return "";
        }
        personalTags = String.join(",", tagList);
        LambdaQueryWrapper<StorePersonalTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StorePersonalTags::getTagName, tagList);
        List<StorePersonalTags> personalTagsList = storePersonalTagsService.list(queryWrapper);
        if (!personalTagsList.isEmpty()) {
            List<String> collect = personalTagsList.stream().map(StorePersonalTags::getTagName).collect(Collectors.toList());
            List<String> tags = tagList.stream().filter(x -> !collect.contains(x)).collect(Collectors.toList());
            if (!tags.isEmpty()) {
                // savePersonalTags(tags);
            }
        } else {
            // savePersonalTags(tagList);
        }
        return personalTags;
    }

    /**
     * @description: 保存标签
     * @param: [tags]
     * @author: nqr
     * @date: 2025/4/15 14:36
     **/

    private void savePersonalTags(List<String> tags) {
        List<StorePersonalTags> storePersonalTagsList = new ArrayList<>();
        tags.forEach(x -> {
            StorePersonalTags storePersonalTags = new StorePersonalTags();
            storePersonalTags.setTagName(x);
            storePersonalTagsList.add(storePersonalTags);
        });
        storePersonalTagsService.saveBatch(storePersonalTagsList);
    }

    @Override
    public Map<String, Object> batchUpdateAbnormalStatus(List<String> celebrityIds, Integer isAbnormalAccount) {
        Map<String, Object> result = new HashMap<>();

        if (celebrityIds == null || celebrityIds.isEmpty() || isAbnormalAccount == null) {
            result.put("success", false);
            result.put("message", "参数无效");
            return result;
        }

        // 验证isAbnormalAccount值是否合法
        if (!isAbnormalAccount.equals(0) && !isAbnormalAccount.equals(2)) {
            result.put("success", false);
            result.put("message", "异常状态值必须为0或2");
            return result;
        }

        // 查询存在的ID
//        List<StoreCelebrityPrivate> existingRecords = this.listByIds(celebrityIds);
        Collection<StoreCelebrityPrivate> existingRecords = this.listByIds(celebrityIds);
        Set<String> existingIds = existingRecords.stream()
                .map(StoreCelebrityPrivate::getId)
                .collect(Collectors.toSet());

        // 找出不存在的ID
        List<String> notExistingIds = celebrityIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());

        // 如果有不存在的ID，记录在结果中
        if (!notExistingIds.isEmpty()) {
            result.put("success", false);
            result.put("message", "存在无效的网红ID");
            result.put("notExistingIds", notExistingIds);
            return result;
        }

        // 执行批量更新
        UpdateWrapper<StoreCelebrityPrivate> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", celebrityIds)
                .set("is_abnormal_account", isAbnormalAccount)
                .set("update_time", new Date());

        boolean updateSuccess = this.update(updateWrapper);
        result.put("success", updateSuccess);
        result.put("message", updateSuccess ?
                (isAbnormalAccount == 0 ? "批量更新为正常状态成功" : "批量更新为异常状态成功") :
                "批量更新失败");

        return result;
    }


    @Override
    public List<CelebrityPrivateExportModel> getCelebrityPrivateExportList(StoreCelebrityPrivateModel storeCelebrityPrivateModel) {
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getFollowersEndNum())) {
            Integer followersEndNum = storeCelebrityPrivateModel.getFollowersEndNum() * 1000;
            storeCelebrityPrivateModel.setFollowersEndNum(followersEndNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getFollowersStartNum())) {
            Integer postsStartNum = storeCelebrityPrivateModel.getFollowersStartNum() * 1000;
            storeCelebrityPrivateModel.setFollowersStartNum(postsStartNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getPlayAvgStartNum())) {
            Integer playAvgStartNum = storeCelebrityPrivateModel.getPlayAvgStartNum() * 1000;
            storeCelebrityPrivateModel.setPlayAvgStartNum(playAvgStartNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getPlayAvgEndNum())) {
            Integer playAvgEndNum = storeCelebrityPrivateModel.getPlayAvgEndNum() * 1000;
            storeCelebrityPrivateModel.setPlayAvgEndNum(playAvgEndNum);
        }
        if (oConvertUtils.isNotEmpty(storeCelebrityPrivateModel.getColumn())) {
            storeCelebrityPrivateModel.setColumn(oConvertUtils.camelToUnderline(storeCelebrityPrivateModel.getColumn()));
        }
        return this.baseMapper.getCelebrityPrivateExportList(storeCelebrityPrivateModel);
    }

    @Override
    public void addData(StoreCelebrityPrivate storeCelebrityPrivate, List<StoreCelebrityPrivateProduct> privateProductListSave, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList) {
        saveOrUpdate(storeCelebrityPrivate);
        if (!privateProductListSave.isEmpty()) {
            privateProductService.lambdaUpdate().eq(StoreCelebrityPrivateProduct::getCelebrityId, storeCelebrityPrivate.getId()).remove();
            privateProductService.saveBatch(privateProductListSave);
        }
        // privatePersonalTagsService.lambdaUpdate().eq(StoreCelebrityPrivatePersonalTags::getCelebrityId, storeCelebrityPrivate.getId()).remove();
        if (!privatePersonalTagsList.isEmpty()) {
            privatePersonalTagsService.saveBatch(privatePersonalTagsList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editData(StoreCelebrityPrivate storeCelebrityPrivate, List<StoreCelebrityPrivateProduct> privateProductListSave, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList) {
        updateById(storeCelebrityPrivate);
        privateProductService.lambdaUpdate().eq(StoreCelebrityPrivateProduct::getCelebrityId, storeCelebrityPrivate.getId()).remove();
        if (!privateProductListSave.isEmpty()) {
            privateProductService.lambdaUpdate().eq(StoreCelebrityPrivateProduct::getCelebrityId, storeCelebrityPrivate.getId()).remove();
            privateProductService.saveBatch(privateProductListSave);
        }
        privatePersonalTagsService.lambdaUpdate().eq(StoreCelebrityPrivatePersonalTags::getCelebrityId, storeCelebrityPrivate.getId()).remove();
        if (!privatePersonalTagsList.isEmpty()) {
            privatePersonalTagsService.saveBatch(privatePersonalTagsList);
        }
    }

    @Override
    public Result<?> extractedPrivateProducts(String data) throws IOException {
        // 使用 Jackson 解析 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        List<PrivateProductRecord> records = objectMapper.readValue(data, objectMapper.getTypeFactory().constructCollectionType(List.class, PrivateProductRecord.class));
        List<String> recordIds = records.stream().map(PrivateProductRecord::getRecordId).collect(Collectors.toList());
        // 过滤掉 fields 为空的记录并打印结果
        records.stream().filter(record -> record.getFields() != null && !oConvertUtils.isAllFieldNull(record.getFields())).forEach(record -> {
            PrivateProductFields fields = record.getFields();
            System.out.println("网红名称: " + fields.getAccount());
            System.out.println("产品名称: " + fields.getProductName());
            System.out.println("合作状态: " + fields.getRelationStatusStr());
            System.out.println("平台: " + fields.getPlatformTypeStr());
            System.out.println("品牌: " + fields.getBrandName());
            System.out.println("---");
        });

        List<StoreCelebrityPrivateProductModel> privateProductModels = records.stream().filter(record -> record.getFields() != null && !oConvertUtils.isAllFieldNull(record.getFields())).map(record -> {
            PrivateProductFields fields = record.getFields();
            StoreCelebrityPrivateProductModel privateProductModel = new StoreCelebrityPrivateProductModel();
            privateProductModel.setAccount(fields.getAccount());
            privateProductModel.setProductName(fields.getProductName());
            privateProductModel.setRelationStatus(getRelationStatus(fields.getRelationStatusStr()));
            privateProductModel.setPlatformType(getPlatformType(fields.getPlatformTypeStr()));
            privateProductModel.setPlatformTypeStr(fields.getPlatformTypeStr());
            privateProductModel.setBrandName(fields.getBrandName());
            privateProductModel.setRecordId(record.getRecordId());
            return privateProductModel;
        }).collect(Collectors.toList());
        if (privateProductModels.isEmpty()) {
            return Result.ok("没有需要同步的标签数据");
        }

        ImportPrivateProductResult importPrivateProductResult = importPrivateProduct(privateProductModels);
        if (!importPrivateProductResult.getErrorMap().isEmpty()) {
            // 更新飞书多维表格中的“异常信息”列
            for (Map.Entry<String, String> entry : importPrivateProductResult.getErrorMap().entrySet()) {
                String recordId = entry.getKey();
                String errorMessage = entry.getValue();
                HashMap<String, Object> updateFields = new HashMap<>();
                updateFields.put("异常信息", errorMessage);
                updateFields.put("是否同步", "是");
                updateFields.put("同步状态", "同步异常");

                AppTableRecord[] recordArr = new AppTableRecord[1];
                recordArr[0] = AppTableRecord.newBuilder()
                        .recordId(recordId)
                        .fields(updateFields)
                        .build();

                try {
                    feishuService.batchUpdateRecords(feishuAppConfig.getPrivateProductAppToken(), feishuAppConfig.getPrivateProductTableId(), recordArr);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            // 将 errorMap 的值转换为 List<String> 作为错误列表
            List<String> errorList = new ArrayList<>(importPrivateProductResult.getErrorMap().values());
            return Result.error(500, "飞书文档导入失败!", errorList);
        }
        saveImportData(importPrivateProductResult);
        HashMap<String, Object> map = new HashMap<>();
        map.put("是否同步", "是");
        map.put("同步状态", "已完成");

        AppTableRecord[] recordArr = new AppTableRecord[recordIds.size()];
        for (int i = 0; i < recordIds.size(); i++) {
            recordArr[i] = AppTableRecord.newBuilder()
                    .recordId(recordIds.get(i))
                    .fields(map)
                    .build();
        }

        feishuService.batchUpdateRecords(feishuAppConfig.getPrivateProductAppToken(), feishuAppConfig.getPrivateProductTableId(), recordArr);
        return Result.ok("飞书文档导入成功");
    }

    private ImportPrivateProductResult importPrivateProduct(List<StoreCelebrityPrivateProductModel> privateProductModels) {
        ImportPrivateProductResult importPrivateProductResult = new ImportPrivateProductResult();
        Map<String, String> errorMap = new HashMap<>();
        List<StoreCelebrityPrivateProduct> insertPrivateProducts = new ArrayList<>();
        List<StoreCelebrityPrivateProduct> updatePrivateProducts = new ArrayList<>();
        for (StoreCelebrityPrivateProductModel privateProductModel : privateProductModels) {
            // 验证是否存在私有网红
            StoreCelebrityPrivateProduct storeCelebrityPrivateProduct = new StoreCelebrityPrivateProduct();
            LambdaQueryWrapper<StoreCelebrityPrivate> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StoreCelebrityPrivate::getAccount, privateProductModel.getAccount());
            queryWrapper.eq(StoreCelebrityPrivate::getPlatformType, privateProductModel.getPlatformType());
            List<StoreCelebrityPrivate> existingCelebrityPrivate = this.list(queryWrapper);

            // 验证是否存在产品信息
            LambdaQueryWrapper<KolProduct> productLambdaQueryWrapper = new LambdaQueryWrapper<>();
            productLambdaQueryWrapper.eq(KolProduct::getProductName, privateProductModel.getProductName());
            productLambdaQueryWrapper.eq(KolProduct::getBrandName, privateProductModel.getBrandName());
            List<KolProduct> existingProduct = productService.list(productLambdaQueryWrapper);

            // 处理私有网红和产品都不存在的情况
            if (existingCelebrityPrivate.isEmpty() && existingProduct.isEmpty()) {
                errorMap.put(privateProductModel.getRecordId(),
                        String.format("私有网红：%s, 平台：%s 不存在; 产品：%s, 品牌：%s 不存在",
                                privateProductModel.getAccount(), privateProductModel.getPlatformTypeStr(),
                                privateProductModel.getProductName(), privateProductModel.getBrandName()));
                continue;
            }

            // 处理私有网红不存在的情况
            if (existingCelebrityPrivate.isEmpty()) {
                errorMap.put(privateProductModel.getRecordId(),
                        String.format("私有网红：%s, 平台：%s 不存在",
                                privateProductModel.getAccount(), privateProductModel.getPlatformTypeStr()));
                continue;
            }

            // 处理产品不存在的情况
            if (existingProduct.isEmpty()) {
                errorMap.put(privateProductModel.getRecordId(),
                        String.format("产品：%s, 品牌：%s 不存在",
                                privateProductModel.getProductName(), privateProductModel.getBrandName()));
                continue;
            }

            Optional<StoreCelebrityPrivate> celebrityPrivateOptional = existingCelebrityPrivate.stream().findFirst();
            if (celebrityPrivateOptional.isPresent()) {
                storeCelebrityPrivateProduct.setCelebrityId(celebrityPrivateOptional.get().getId());
            }
            Optional<KolProduct> productOptional = existingProduct.stream().findFirst();
            if (productOptional.isPresent()) {
                storeCelebrityPrivateProduct.setProductId(productOptional.get().getId());
                storeCelebrityPrivateProduct.setBrandId(productOptional.get().getBrandId());
            }

            // 验证是否存在私有网红与产品关联信息
            LambdaQueryWrapper<StoreCelebrityPrivateProduct> privateProductLambdaQueryWrapper = new LambdaQueryWrapper<>();
            privateProductLambdaQueryWrapper.eq(StoreCelebrityPrivateProduct::getCelebrityId, storeCelebrityPrivateProduct.getCelebrityId());
            privateProductLambdaQueryWrapper.eq(StoreCelebrityPrivateProduct::getBrandId, storeCelebrityPrivateProduct.getBrandId());
            privateProductLambdaQueryWrapper.eq(StoreCelebrityPrivateProduct::getProductId, storeCelebrityPrivateProduct.getProductId());
            List<StoreCelebrityPrivateProduct> existingPrivateProduct = privateProductService.list(privateProductLambdaQueryWrapper);
            if (existingPrivateProduct.isEmpty()) {
                storeCelebrityPrivateProduct.setRelationStatus(privateProductModel.getRelationStatus());
                insertPrivateProducts.add(storeCelebrityPrivateProduct);
            } else {
                Optional<StoreCelebrityPrivateProduct> privateProductOptional = existingPrivateProduct.stream().findFirst();
                if (privateProductOptional.isPresent()) {
                    // 验证导入的合作状态是否大于数据库中的合作状态
                    if (privateProductModel.getRelationStatus() > privateProductOptional.get().getRelationStatus()) {
                        storeCelebrityPrivateProduct.setRelationStatus(privateProductModel.getRelationStatus());
                        updatePrivateProducts.add(storeCelebrityPrivateProduct);
                    }
                }
            }
        }
        importPrivateProductResult.setErrorMap(errorMap);
        importPrivateProductResult.setInsertPrivateProducts(insertPrivateProducts);
        importPrivateProductResult.setUpdatePrivateProducts(updatePrivateProducts);
        return importPrivateProductResult;

    }

    private Integer getRelationStatus(String relationStatusStr) {
        Integer relationStatus = null;
        if (oConvertUtils.isEmpty(relationStatusStr)) {
            return null;
        }
        switch (relationStatusStr) {
            case "合作":
                relationStatus = 1;
                break;
            case "选中":
                relationStatus = 0;
                break;
            default:
                break;
        }
        return relationStatus;
    }

    private Integer getPlatformType(String platformTypeStr) {
        Integer platformType = null;
        if (oConvertUtils.isEmpty(platformTypeStr)) {
            return null;
        }
        switch (platformTypeStr) {
            case "TK":
                platformType = 2;
                break;
            case "YT":
                platformType = 1;
                break;
            case "IG":
                platformType = 0;
                break;
            default:
                break;
        }
        return platformType;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveImportData(ImportPrivateProductResult importResult) {
        if (!importResult.getInsertPrivateProducts().isEmpty()) {
            privateProductService.saveBatch(importResult.getInsertPrivateProducts());
        }
        if (!importResult.getUpdatePrivateProducts().isEmpty()) {
            privateProductService.updateBatchById(importResult.getUpdatePrivateProducts());
        }
    }

    @Override
    public List<StoreCelebrityPrivateModel> getCelebrityPrivateListNew(StoreCelebrityPrivateModel storeCelebrityPrivateModel) {
        return this.baseMapper.getCelebrityPrivateListNew(storeCelebrityPrivateModel);
    }

    /**
     * 导入私有网红产品数据
     *
     * @param models Excel 模型列表
     * @param userId 用户ID
     * @return 导入结果
     */
    @Override
    public ImportPrivateProductResult importPrivateProducts(List<StoreCelebrityPrivateProductExcelModel> models, String userId) {
        ImportPrivateProductResult result = new ImportPrivateProductResult();
        Map<String, String> errorMap = new HashMap<>();
        List<StoreCelebrityPrivateProduct> insertPrivateProducts = new ArrayList<>();
        List<StoreCelebrityPrivateProduct> updatePrivateProducts = new ArrayList<>();

        for (int i = 0; i < models.size(); i++) {
            StoreCelebrityPrivateProductExcelModel model = models.get(i);
            String rowKey = "第" + (i + 2) + "行"; // Excel 行号（从第2行开始）

            // 转换平台类型
            Integer platformType = getPlatformType(model.getPlatformTypeStr());
            if (platformType == null) {
                errorMap.put(rowKey, oConvertUtils.isEmpty(model.getPlatformTypeStr()) ? "平台类型无效" : String.format("平台类型无效：%s", model.getPlatformTypeStr()));
                continue;
            }
            model.setPlatformType(platformType);
            // Integer relationStatus = getRelationStatus(model.getRelationStatusStr());
            // 2025年8月27日10:00:06 只保留历史合作
            Integer relationStatus = getRelationStatus("合作");
            if (relationStatus == null) {
                errorMap.put(rowKey, String.format("合作状态无效：%s", model.getRelationStatusStr()));
                continue;
            }
            model.setRelationStatus(relationStatus);

            // 验证网红是否存在
            LambdaQueryWrapper<StoreCelebrityPrivate> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StoreCelebrityPrivate::getAccount, model.getAccount())
                    .eq(StoreCelebrityPrivate::getPlatformType, platformType);
            List<StoreCelebrityPrivate> existingCelebrities = this.list(queryWrapper);

            if (existingCelebrities.isEmpty()) {
                errorMap.put(rowKey, String.format("网红账号：%s，平台：%s 不存在", model.getAccount(), model.getPlatformTypeStr()));
                continue;
            }

            // 解析产品信息（品牌-产品名称-产品型号）
            KolProduct product = parseProductInfo(model.getProductInfo());
            if (product == null) {
                errorMap.put(rowKey, oConvertUtils.isEmpty(model.getProductInfo()) ? "产品信息格式错误" : String.format("产品信息格式错误：%s", model.getProductInfo()));
                continue;
            }

            // 验证产品是否存在
            LambdaQueryWrapper<KolProduct> productQuery = new LambdaQueryWrapper<>();
            productQuery.eq(KolProduct::getBrandName, product.getBrandName())
                    .eq(KolProduct::getProductName, product.getProductName());
            List<KolProduct> existingProducts = productService.list(productQuery);

            if (existingProducts.isEmpty()) {
                errorMap.put(rowKey, String.format("产品：%s，品牌：%s 不存在", product.getProductName(), product.getBrandName()));
                continue;
            }

            // 获取网红和产品信息
            StoreCelebrityPrivateProduct privateProduct = new StoreCelebrityPrivateProduct();
            Optional<StoreCelebrityPrivate> celebrityOptional = existingCelebrities.stream().findFirst();
            Optional<KolProduct> productOptional = existingProducts.stream().findFirst();

            if (celebrityOptional.isPresent()) {
                privateProduct.setCelebrityId(celebrityOptional.get().getId());
            }
            if (productOptional.isPresent()) {
                privateProduct.setProductId(productOptional.get().getId());
                privateProduct.setBrandId(productOptional.get().getBrandId());
            }

            // 验证网红与产品关联是否存在
            LambdaQueryWrapper<StoreCelebrityPrivateProduct> privateProductQuery = new LambdaQueryWrapper<>();
            privateProductQuery.eq(StoreCelebrityPrivateProduct::getCelebrityId, privateProduct.getCelebrityId())
                    .eq(StoreCelebrityPrivateProduct::getBrandId, privateProduct.getBrandId())
                    .eq(StoreCelebrityPrivateProduct::getProductId, privateProduct.getProductId());
            List<StoreCelebrityPrivateProduct> existingPrivateProducts = privateProductService.list(privateProductQuery);

            privateProduct.setRelationStatus(getRelationStatus(model.getRelationStatusStr()));
            if (existingPrivateProducts.isEmpty()) {
                insertPrivateProducts.add(privateProduct);
                privateProduct.setSelectionTime(new Date());
            } else {
                Optional<StoreCelebrityPrivateProduct> existingPrivateProduct = existingPrivateProducts.stream().findFirst();
                if (existingPrivateProduct.isPresent() && model.getRelationStatus() != null &&
                        model.getRelationStatus() > existingPrivateProduct.get().getRelationStatus()) {
                    privateProduct.setId(existingPrivateProduct.get().getId());
                    updatePrivateProducts.add(privateProduct);
                }
            }
        }

        result.setErrorMap(errorMap);
        result.setInsertPrivateProducts(insertPrivateProducts);
        result.setUpdatePrivateProducts(updatePrivateProducts);
        return result;
    }

    /**
     * 解析产品信息（格式：品牌-产品名称-产品型号）
     *
     * @param productInfo 产品信息字符串
     * @return 解析后的产品信息对象
     */
    private KolProduct parseProductInfo(String productInfo) {
        if (StringUtils.isBlank(productInfo)) {
            return null;
        }
        String[] parts = productInfo.split("-");
        if (parts.length < 2) {
            return null;
        }
        KolProduct info = new KolProduct();
        info.setBrandName(parts[0].trim());
        info.setProductName(parts[1].trim());
        return info;
    }

    @Override
    public String processingPersonalizedLabels(List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList, String privateId, String personalTags) {
        if (oConvertUtils.isEmpty(personalTags)) {
            return "";
        }
        personalTags = StringUtils.lowerCase(personalTags).replaceAll("，", ",").trim();
        List<String> tagList = Arrays.asList(personalTags.split(","));
        tagList = tagList.stream().map(String::trim).filter(oConvertUtils::isNotEmpty).collect(Collectors.toList());
        if (tagList.isEmpty()) {
            return "";
        }

        StringBuilder tagsBuilder = new StringBuilder();

        LambdaQueryWrapper<StorePersonalTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StorePersonalTags::getTagName, tagList);
        List<StorePersonalTags> personalTagsList = storePersonalTagsService.list(queryWrapper);
        if (!personalTagsList.isEmpty()) {
            List<String> collect = personalTagsList.stream().map(StorePersonalTags::getTagName).collect(Collectors.toList());
            List<String> tags = tagList.stream().filter(x -> collect.stream().noneMatch(y -> y.equalsIgnoreCase(x))).collect(Collectors.toList());
            if (!tags.isEmpty()) {
                tagsBuilder.append("个性化标签：").append(String.join(",", tags)).append(" 不存在，请先添加！");
                return tagsBuilder.toString();
            }
        } else {
            tagsBuilder.append("个性化标签：").append(String.join(",", tagList)).append(" 不存在，请先添加！");
            return tagsBuilder.toString();
        }
        List<StoreCelebrityPrivatePersonalTags> celebrityPrivatePersonalTags = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getCelebrityId, privateId).list();
        // 创建关联关系
        for (StorePersonalTags tags : personalTagsList) {
            boolean match = celebrityPrivatePersonalTags.stream().anyMatch(x -> x.getTagId().equals(tags.getId()));
            if (match) {
                continue;
            }
            StoreCelebrityPrivatePersonalTags privatePersonalTags = new StoreCelebrityPrivatePersonalTags();
            privatePersonalTags.setId(IdWorker.get32UUID());
            privatePersonalTags.setCelebrityId(privateId);
            privatePersonalTags.setTagId(tags.getId());
            privatePersonalTags.setIsDel(0);
            privatePersonalTagsList.add(privatePersonalTags);
        }
        return "";
    }

    @Override
    public void processingPersonalizedTags(List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList, String privateId, String personalTagIds) {
        if (oConvertUtils.isEmpty(personalTagIds)) {
            return;
        }
        // List<StoreCelebrityPrivatePersonalTags> personalTags = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getCelebrityId, privateId).list();
        // 创建关联关系
        for (String tagId : personalTagIds.split(",")) {
         /*   boolean match = personalTags.stream().anyMatch(x -> x.getTagId().equals(tagId));
            if (match) {
                continue;
            }*/
            StoreCelebrityPrivatePersonalTags privatePersonalTags = new StoreCelebrityPrivatePersonalTags();
            privatePersonalTags.setId(IdWorker.get32UUID());
            privatePersonalTags.setCelebrityId(privateId);
            privatePersonalTags.setTagId(tagId);
            privatePersonalTags.setIsDel(0);
            privatePersonalTagsList.add(privatePersonalTags);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmedPersonalTags(String celebrityPrivateId, List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList) {
        LoginUser logUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
        storeCelebrityPrivate.setId(celebrityPrivateId);
        storeCelebrityPrivate.setIsPersonalTagsConfirmed(1);
        storeCelebrityPrivate.setPersonalTagsConfirmedUserId(logUser.getId());
        storeCelebrityPrivate.setPersonalTagsConfirmedUsername(logUser.getUsername());
        storeCelebrityPrivate.setPersonalTagsConfirmedTime(new Date());
        updateById(storeCelebrityPrivate);
        privatePersonalTagsService.lambdaUpdate().eq(StoreCelebrityPrivatePersonalTags::getCelebrityId, celebrityPrivateId).remove();
        if (!privatePersonalTagsList.isEmpty()) {
            privatePersonalTagsService.saveBatch(privatePersonalTagsList);
        }
    }

    @Override
    public void processingPersonalizedTagsNew(List<StoreCelebrityPrivatePersonalTags> privatePersonalTagsList, String privateId, String personalTagIds) {
        if (oConvertUtils.isEmpty(personalTagIds)) {
            return;
        }
        List<StoreCelebrityPrivatePersonalTags> personalTags = privatePersonalTagsService.lambdaQuery().eq(StoreCelebrityPrivatePersonalTags::getCelebrityId, privateId).list();
        List<String> personalTagIdList = Arrays.asList(personalTagIds.split(","));
        List<StorePersonalTags> storePersonalTags = storePersonalTagsService.lambdaQuery().in(StorePersonalTags::getId, personalTagIdList).list();
        // 创建关联关系
        for (String tagId : personalTagIdList) {
            boolean match = personalTags.stream().anyMatch(x -> x.getTagId().equals(tagId));
            if (match) {
                continue;
            }
            if(!storePersonalTags.isEmpty()){
                boolean anyMatch = storePersonalTags.stream().anyMatch(x -> "无".equals(x.getTagName()));
                if(anyMatch){
                    continue;
                }
            }
            StoreCelebrityPrivatePersonalTags privatePersonalTags = new StoreCelebrityPrivatePersonalTags();
            privatePersonalTags.setId(IdWorker.get32UUID());
            privatePersonalTags.setCelebrityId(privateId);
            privatePersonalTags.setTagId(tagId);
            privatePersonalTags.setIsDel(0);
            privatePersonalTagsList.add(privatePersonalTags);
        }
    }


}

