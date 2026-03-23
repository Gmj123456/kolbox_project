package org.jeecg.modules.kol.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.*;
import org.jeecg.modules.feishu.config.FeishuAppConfig;
import org.jeecg.modules.feishu.exception.AsycToFeishuException;
import org.jeecg.modules.kol.mapper.*;
import org.jeecg.modules.kol.model.*;
import org.jeecg.modules.kol.service.IKolBrandService;
import org.jeecg.modules.kol.service.IKolTagBrandService;
import org.jeecg.modules.kol.service.IKolTagProductService;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jeecg.common.constant.enums.TagType.HASHTAG;
import static org.jeecg.common.constant.enums.TagType.ROOT_VIDEO;

/**
 * @Description: tag表
 * @Author: dongruyang
 * @Date: 2023-10-10
 * @Version: V1.0
 */
@Service
public class KolTagsServiceImpl extends ServiceImpl<KolTagsMapper, KolTags> implements IKolTagsService {

    @Autowired
    private IKolTagBrandService kolTagBrandService;
    @Autowired
    private KolTagCategoryMapper categoryMapper;
    @Autowired
    private KolTagAttributeRelationMapper tagAttributeRelationMapper;
    @Autowired
    private IKolTagProductService tagProductService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FeishuAppConfig feishuAppConfig;
    @Autowired
    private IKolTagBrandService kolTagsBrandService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private ITiktokCelebrityTagsService tkTagsService;
    @Autowired
    private IKolBrandService kolBrandService;
    @Autowired
    private KolAttributeMapper attributeMapper;
    @Autowired
    private KolProductMapper kolProductMapper;

    @Override
    public IPage<KolTags> queryPageList(Page<KolTags> page, KolTagsModel tiktokTagsModel) {
        return this.baseMapper.queryPageList(page, tiktokTagsModel);
    }

    /**
     * 功能描述:根据标签名查询
     *
     * @return java.util.List<org.jeecg.modules.kol.entity.TiktokTags>
     * @Date 2023-11-03 15:04:02
     */
    @Override
    public List<KolTags> getByTagNames(List<String> tagNames, Integer platformType) {
        LambdaQueryWrapper<KolTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(KolTags::getTagName, tagNames);
        queryWrapper.eq(KolTags::getPlatformType, platformType);
        return this.list(queryWrapper);
    }

    @Override
    public List<KolTags> queryTagsListByName(KolTagsModel tiktokTagsModel) {
        return this.baseMapper.queryTagsListByName(tiktokTagsModel);
    }

    @Override
    public List<KolTags> listByNames(List<String> tagNames) {
        LambdaQueryWrapper<KolTags> queryWrapper = new LambdaQueryWrapper<>();
        if (tagNames != null) {
            queryWrapper.in(KolTags::getTagName, tagNames);
        }
        return this.list(queryWrapper);
    }


    /**
     * 根据 categoryId 查询标签，包括子类目的标签
     *
     * @param categoryId 类目 ID
     * @return 标签列表
     */
    @Override
    public List<KolTags> searchTagsByCategoryId(String categoryId) {
        // 1. 获取所有子类目 ID
        List<String> categoryIds = getAllCategoryIds(categoryId);

        // 2. 查询匹配的标签
        LambdaQueryWrapper<KolTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(KolTags::getCategoryId, categoryIds);
        return this.list(queryWrapper);
    }

    /**
     * 获取指定类目及其子类目的所有 ID
     *
     * @param categoryId 类目 ID
     * @return 类目 ID 列表
     */
    @Override
    public List<String> getAllCategoryIds(String categoryId) {
        List<KolTagCategory> subCategories = categoryMapper.findCategoryWithSubNodes(categoryId);
        return subCategories.stream().map(KolTagCategory::getId).collect(Collectors.toList());
    }

    /**
     * 保存标签及其品牌信息
     *
     * @param kolTags
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveKolTags(KolTags kolTags, List<KolTagBrand> kolTagBrands, List<String> kolTagBrandIds) {
        this.saveOrUpdate(kolTags);
        tagAttributeRelationMapper.delete(new LambdaQueryWrapper<KolTagAttributeRelation>().eq(KolTagAttributeRelation::getTagId, kolTags.getId()));
        if (!kolTagBrandIds.isEmpty()) {
            kolTagBrandService.removeByIds(kolTagBrandIds);
        }
        if (!kolTagBrands.isEmpty()) {
            kolTagBrandService.saveBatch(kolTagBrands);
        }
    }

    /**
     * @description: 保存标签及其品牌信息
     * @author: nqr
     * @date: 2025/6/28 11:30
     **/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveKolTagsNew(KolTags kolTags, List<KolTagProduct> tagProductList, List<KolTagAttributeRelation> attributeRelations) {
        this.saveOrUpdate(kolTags);
        tagProductService.remove(new LambdaQueryWrapper<KolTagProduct>().eq(KolTagProduct::getTagId, kolTags.getId()).in(KolTagProduct::getProductId, tagProductList.stream().map(KolTagProduct::getProductId).collect(Collectors.toList())));
        List<Map<String, Object>> paramList = attributeRelations.stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("tagId", r.getTagId());
                    map.put("productId", r.getProductId());
                    return map;
                })
                .collect(Collectors.toList());
        this.baseMapper.deleteByTagIdAndProductIdBatch(paramList);
        if (!attributeRelations.isEmpty()) {
            tagAttributeRelationMapper.insertBatch(attributeRelations);
        }
        if (!tagProductList.isEmpty()) {
            tagProductService.saveBatch(tagProductList);
        }
    }

    /**
     * @description:分页列表查询
     * @author: nqr
     * @date: 2025/6/28 9:40
     **/
    @Override
    public IPage<KolTagsModel> queryPageListNew(Page<KolTagsModel> page, KolTagsModel tiktokTagsModel) {
        /*IPage<KolTagsModel> pageList = this.baseMapper.queryPageListNew(page, tiktokTagsModel);
        List<KolTagsModel> records = pageList.getRecords();
        List<KolTagsModel> modelList = getCategoryRelationModels(records);
        pageList.setRecords(modelList);
        return pageList;*/
        return this.baseMapper.queryPageListNew(page, tiktokTagsModel);
    }

    private static List<KolTagsModel> getCategoryRelationModels(List<KolTagsModel> list) {
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
    public Integer countBySearch(KolTagsModel kolTagsModel) {
        return this.baseMapper.countBySearch(kolTagsModel);
    }

    @Override
    public String synchronizeData(String tableId) {
        OkHttpClient client = new OkHttpClient();
        try {
            String accessToken = null;
            try {
                accessToken = getAccessToken(feishuAppConfig);
            } catch (IOException e) {
                throw new AsycToFeishuException(e.getMessage());
            }
            if (oConvertUtils.isEmpty(accessToken)) {
                return null;
            }
            boolean hasMore = true;
            String pageToken = "";
            JSONArray jsonArray = new JSONArray();
            while (hasMore) {
                String url = "https://open.feishu.cn/open-apis/bitable/v1/apps/" + feishuAppConfig.getAppToken() + "/tables/" + tableId + "/records/search?page_size=500&page_token=";
                if (oConvertUtils.isNotEmpty(pageToken)) {
                    url += pageToken;
                }
                Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"), "{}")).addHeader("Authorization", "Bearer " + accessToken).build();
                JSONObject jsonResponse;
                try (Response response = client.newCall(request).execute()) {
                    jsonResponse = JSON.parseObject(response.body().string());
                } catch (Exception e) {
                    throw new AsycToFeishuException("分页获取飞书表格数据失败");
                }
                JSONObject data = jsonResponse.getJSONObject("data");
                hasMore = data.getBooleanValue("has_more");
                if (data.containsKey("page_token")) {
                    pageToken = data.getString("page_token");
                }
                JSONArray items = data.getJSONArray("items");
                if (items == null || items.isEmpty()) {
                    break;
                }
                jsonArray.addAll(items);
            }
            return jsonArray.toJSONString();
        } catch (Exception e) {
            log.error("同步飞书表格数据失败：---------------------------------------------" + e);
            throw new AsycToFeishuException("同步飞书表格数据失败");
        }
    }

    public String getAccessToken(FeishuAppConfig feishuAppConfig) throws IOException {
        long now = System.currentTimeMillis();
        // 缓存有效则直接返回
        if (redisUtil.hasKey("feishu_access_token")) {
            return (String) redisUtil.get("feishu_access_token");
        }
        OkHttpClient client = new OkHttpClient();
        String json = String.format("{\"app_id\":\"%s\",\"app_secret\":\"%s\"}", feishuAppConfig.getAppId(), feishuAppConfig.getAppSecret());

        Request request = new Request.Builder().url("https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal/").post(RequestBody.create(MediaType.parse("application/json"), json)).build();
        JSONObject jsonResponse;
        try (Response response = client.newCall(request).execute()) {
            jsonResponse = JSON.parseObject(response.body().string());
        } catch (Exception e) {
            log.error("获取飞书访问令牌失败,错误信息：" + e.getMessage());
            return null;
        }
        String tenantAccessToken = jsonResponse.getString("tenant_access_token");
        redisUtil.set("feishu_access_token", tenantAccessToken, jsonResponse.getIntValue("expire"));
        return tenantAccessToken;
    }

    /**
     * @param importType 0: 飞书导入 1: excel导入 2: 企微导入
     * @return
     */
    @Override
    public Result<?> importTags(List<KolTagsModel> kolTagsList, Integer platformType, String productIds, Integer importType) {
        List<String> resultMsg = new ArrayList<>();
        Result<List<String>> resultNew = new Result<>();
        // 判断是否是标签导入
        if (importType == 1 || importType == 2) {
            List<DictModel> tagTypeList = sysBaseAPI.queryDictItemsByCode("tag_type");
            // 标签导入需要处理多种类型
            for (KolTagsModel model : kolTagsList) {
                Set<String> influencerTypeSet = Stream.of(model.getInfluencerType(), model.getInfluencerType2(), model.getInfluencerType3(), model.getInfluencerType4(), model.getInfluencerType5()).filter(Objects::nonNull).filter(oConvertUtils::isNotEmpty).collect(Collectors.toSet());
                model.setInfluencerType(String.join("|", influencerTypeSet));
                if (oConvertUtils.isEmpty(model.getTagTypeStr()) && platformType != 2) {
                    if (importType == 1) {
                        resultMsg.add(String.format("标签：'%s'，缺少标签类型", model.getTagName()));
                    } else {
                        // resultMsg.add(String.format("标签：'%s'，缺少标签类型|%s", model.getTagName(), model.getRecordId()));
                        resultMsg.add(String.format("缺少标签类型|%s", model.getRowNum()));
                    }
                    continue;
                }
                // 判断如果是TK平台默认类型为标签
                if (CommonConstant.TK == platformType) {
                    model.setTagType(0);
                } else {
                    String tagTypeText = tagTypeList.stream().filter(x -> x.getText().equals(model.getTagTypeStr())).findFirst().get().getValue();
                    model.setTagType(Integer.parseInt(tagTypeText));
                }

            }
        }
        // 过滤和去重标签
        List<KolTagsModel> filteredList = filterAndDeduplicateTags(kolTagsList, platformType, resultMsg, importType);
        if (filteredList.isEmpty()) {
            return Result.error("没有有效的标签数据");
        }
        if (!resultMsg.isEmpty()) {
            resultNew.setCode(500);
            resultNew.setResult(resultMsg);
            return resultNew;
        }
        List<KolTagsModel> collect = filteredList.stream().filter(x -> oConvertUtils.isEmpty(x.getInfluencerType())).collect(Collectors.toList());

        // 过滤掉没有达人类型的数据，达人类型必填校验 2025年8月4日10:13:04 nqr
        if (!collect.isEmpty()) {
            resultNew.setCode(500);
            if (importType == 1) {
                collect.forEach(x -> resultMsg.add(String.format("标签：'%s'，缺少达人类型", x.getTagName())));
            } else {
                // collect.forEach(x -> resultMsg.add(String.format("标签：'%s'，缺少达人类型|%s", x.getTagName(), x.getRecordId())));
                collect.forEach(x -> resultMsg.add(String.format("缺少达人类型|%s", x.getRowNum())));

            }
            resultNew.setResult(resultMsg);
            return resultNew;
        }
        // 查询主类目
        // List<KolTagCategory> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<>());
        List<KolTagCategory> categoryList = new ArrayList<>();

        // 处理标签和品牌关系
        ImportResult importResult = processTags(filteredList, categoryList, platformType, productIds, importType);
        if (!importResult.getErrorList().isEmpty()) {
            return Result.error(500, "文件导入失败!", importResult.getErrorList());
        }
        // 批量保存数据
        saveImportData(importResult);

        // 返回导入结果
        return buildImportResult(importResult);
    }
    private List<KolTagsModel> filterAndDeduplicateTags(List<KolTagsModel> kolTagsList, Integer platformType, List<String> errorList, Integer importType) {
        // 过滤空标签名
        List<KolTagsModel> filteredList = kolTagsList.stream().filter(x -> oConvertUtils.isNotEmpty(x.getTagName())).collect(Collectors.toList());

        // 标签名大小写不敏感去重
        Set<String> seen = new HashSet<>();
        filteredList.removeIf(item -> {
            String key = (item.getBrandName() != null ? item.getBrandName() : "") + "|*|" +
                    (item.getProductName() != null ? item.getProductName() : "") + "|*|" +
                    (item.getTagName() != null ? item.getTagName().toLowerCase(Locale.ROOT) : "") + "|*|" +
                    (item.getTagType() != null ? item.getTagType() : "");
            return !seen.add(key);
        });

        // 设置默认值和平台类型
        for (KolTagsModel tagsModel : filteredList) {
            if (oConvertUtils.isEmpty(tagsModel.getTagType())) {
                if (importType == 1) {
                    errorList.add(String.format("标签 '%s'，标签类型不能为空", tagsModel.getTagName()));
                } else {
                    // errorList.add(String.format("标签 '%s'，标签类型不能为空|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                    errorList.add(String.format("标签类型不能为空|%s", tagsModel.getRowNum()));
                }
                continue;
            }
            // 只有标签去除空格，其他类型只去除前后空格
            if (tagsModel.getTagType() == 0) {
                // 去除空格 2025年6月18日18:12:31 增加去除特殊字符,全部转为小写
                tagsModel.setTagName(StringUtils.lowerCase(tagsModel.getTagName().replaceAll("[\\p{P}\\p{S}\\p{C}]", "")));
            } else {
                tagsModel.setTagName(StringUtils.lowerCase(tagsModel.getTagName().trim()));
            }
            // tagsModel.setTagName(StringUtils.lowerCase(tagsModel.getTagName().replaceAll("[\\p{P}\\p{S}\\p{C}]", "").trim()));
            tagsModel.setSortCode(oConvertUtils.isEmpty(tagsModel.getSortCode()) ? 0 : tagsModel.getSortCode());
            tagsModel.setPlatformType(platformType);
            // nqr 2025年6月6日09:21:27 新增导入时间字段
            tagsModel.setImportTime(new Date());
        }

        // 根据标签、类型、平台去重
        return new ArrayList<>(new HashSet<>(filteredList));
    }

    private ImportResult processTags(List<KolTagsModel> filteredList, List<KolTagCategory> categoryList, Integer platformType, String productId, Integer importType) {
        ImportResult result = new ImportResult();
        // 查询标签类型是否符合平台要求
        List<DictModel> importTagPlatform = sysBaseAPI.queryDictItemsByCode("import_tag_platform");
        List<DictModel> platformTypeList = sysBaseAPI.queryDictItemsByCode("platform_type");
        // List<DictModel> tagTypeList = sysBaseAPI.queryDictItemsByCode("tag_type");

        DictModel dictModel = importTagPlatform.stream().filter(x -> x.getText().equals(platformType.toString())).findFirst().get();
        List<KolProduct> productList = kolProductMapper.selectList(null);
        List<KolTagProduct> tagProductList = tagProductService.lambdaQuery().isNotNull(KolTagProduct::getProductId).list();
        List<KolAttribute> attributeList = attributeMapper.selectList(null);

        List<String> collect = filteredList.stream().map(KolTags::getTagName).distinct().collect(Collectors.toList());
        List<KolTags> kolTagsList = this.lambdaQuery().in(KolTags::getTagName, collect).list();

        for (KolTagsModel tagsModel : filteredList) {
            try {
                Integer tagType = tagsModel.getTagType();
       /*     if (oConvertUtils.isNotEmpty(productId)) {
                tagsModel.setProductId(productId);
            }*/
                tagsModel.setIsDelete(0);
                if (tagsModel.getTagName().contains("|")) {
                    if (importType == 1) {
                        result.getErrorList().add("'|'字符为系统保留字，不允许添加");
                    } else {
                        // result.getErrorList().add("'|'字符为系统保留字，不允许添加," + tagsModel.getRecordId());
                        result.getErrorList().add("'|'字符为系统保留字，不允许添加," + tagsModel.getRowNum());
                    }
                    continue;
                }
                if (oConvertUtils.isEmpty(tagType)) {
                    if (importType == 1) {
                        result.getErrorList().add(String.format("标签 '%s'，标签类型不能为空", tagsModel.getTagName()));
                    } else {
                        // result.getErrorList().add(String.format("标签 '%s'，标签类型不能为空|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                        result.getErrorList().add(String.format("标签类型不能为空|%s", tagsModel.getRowNum()));
                    }
                    continue;
                }
                if (!dictModel.getValue().contains(String.valueOf(tagType))) {
                    String platformTypeText = platformTypeList.stream().filter(x -> x.getValue().equals(platformType.toString())).findFirst().get().getText();
                    if (importType == 1) {
                        result.getErrorList().add(String.format("标签 '%s'，%s平台不支持的%s类型", tagsModel.getTagName(), platformTypeText, tagsModel.getTagTypeStr()));
                    } else {
                        // result.getErrorList().add(String.format("标签 '%s'，%s平台 不支持的%s类型|%s", tagsModel.getTagName(), platformTypeText, tagsModel.getTagTypeStr(), tagsModel.getRecordId()));
                        result.getErrorList().add(String.format("%s平台 不支持的%s类型|%s", platformTypeText, tagsModel.getTagTypeStr(), tagsModel.getRowNum()));
                    }
                    continue;
                }

                // 查询品牌产品是否存在
                if (oConvertUtils.isNotEmpty(productId)) {
                    String brandName = tagsModel.getBrandName();
                    String productName = tagsModel.getProductName();
                    if (oConvertUtils.isEmpty(brandName) || oConvertUtils.isEmpty(productName)) {
                        if (importType == 1) {
                            result.getErrorList().add(String.format("标签 '%s'，产品或品牌不存在", tagsModel.getTagName()));
                        } else {
                            // result.getErrorList().add(String.format("标签 '%s'，产品或品牌不存在|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                            result.getErrorList().add(String.format("产品或品牌不存在|%s", tagsModel.getRowNum()));
                        }
                        continue;
                    }
                    Optional<KolProduct> productOptional = productList.stream().filter(x -> x.getBrandName().equals(brandName) && x.getProductName().equals(productName)).findFirst();
                    if (!productOptional.isPresent()) {
                        if (importType == 1) {
                            result.getErrorList().add(String.format("标签 '%s'，产品或品牌不存在", tagsModel.getTagName()));
                        } else {
                            // result.getErrorList().add(String.format("标签 '%s'，产品或品牌不存在|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                            result.getErrorList().add(String.format("产品或品牌不存在|%s", tagsModel.getRowNum()));
                        }
                        continue;
                    }
                    KolProduct kolProduct = productOptional.get();
                    tagsModel.setProductId(kolProduct.getId());
                    tagsModel.setProductName(kolProduct.getProductName());
                    tagsModel.setBrandId(kolProduct.getBrandId());
                    tagsModel.setBrandName(kolProduct.getBrandName());
                }

                // 验证和设置标签数据
                if (!validateAndSetupTag(tagsModel, categoryList, result, importType)) {
                    continue;
                }

                // 处理标签存储
                processTagStorage(tagsModel, platformType, result, kolTagsList);

                // 处理品牌关联
                // processBrandRelation(tagsModel, platformType, result);

                // 处理标签和社媒属性关系
                processTagAttributeRelation(tagsModel, platformType, result, attributeList);


                // 查询产品和标签是否存在关联关系
                // Integer count = tagProductService.lambdaQuery().eq(KolTagProduct::getTagId, tagsModel.getId()).eq(KolTagProduct::getProductId, tagsModel.getProductId()).count();
                boolean match = tagProductList.stream().anyMatch(x -> x.getTagId().equals(tagsModel.getId()) && x.getProductId().equals(tagsModel.getProductId()));
                if (!match) {
                    // 创建标签和产品关联关系
                    // createTagProductRelation(tagsModel, productList, result);
                    createTagProductNew(tagsModel, result);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        return result;
    }


    private boolean setProductInfo(Integer importType, KolTagsModel tagsModel, ImportResult result) {
        String brandName = tagsModel.getBrandName();
        String productName = tagsModel.getProductName();
        if (oConvertUtils.isEmpty(brandName) || oConvertUtils.isEmpty(productName)) {
            if (importType == 1) {
                result.getErrorList().add(String.format("标签 '%s'，产品或品牌不存在", tagsModel.getTagName()));
            } else {
                // result.getErrorList().add(String.format("标签 '%s'，产品或品牌不存在|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                result.getErrorList().add(String.format("产品或品牌不存在|%s", tagsModel.getRowNum()));
            }
            return true;
        }
        LambdaQueryWrapper<KolProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KolProduct::getBrandName, brandName);
        queryWrapper.eq(KolProduct::getProductName, productName);
        KolProduct kolProduct = kolProductMapper.selectOne(queryWrapper);
        if (kolProduct == null) {
            if (importType == 1) {
                result.getErrorList().add(String.format("标签 '%s'，产品或品牌不存在", tagsModel.getTagName()));
            } else {
                // result.getErrorList().add(String.format("标签 '%s'，产品或品牌不存在|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                result.getErrorList().add(String.format("产品或品牌不存在|%s", tagsModel.getRowNum()));
            }
            return true;
        }
        tagsModel.setProductId(kolProduct.getId());
        tagsModel.setProductName(kolProduct.getProductName());
        tagsModel.setBrandId(kolProduct.getBrandId());
        tagsModel.setBrandName(kolProduct.getBrandName());
        return false;
    }

    /**
     * @description: 创建标签和产品关联关系
     * @author: nqr
     * @date: 2025/7/18 17:54
     **/
    private void createTagProductRelation(KolTagsModel tag, List<KolProduct> productList, ImportResult result) {
        /*if (productList.isEmpty()) {
            // 飞书导入,获取产品信息
            String productImportName = tag.getProductImportName();
            if (oConvertUtils.isEmpty(productImportName)) {
                return;
            }
            String[] split = productImportName.split("-");
            String brandName = split[0];
            String productName = split[1];
            LambdaQueryWrapper<KolProduct> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(KolProduct::getBrandName, brandName);
            queryWrapper.eq(KolProduct::getProductName, productName);
            KolProduct kolProduct = kolProductMapper.selectOne(queryWrapper);
            createTagProduct(tag, result, kolProduct);
            return;
        }
        productList.forEach(kolProduct -> createTagProduct(tag, result, kolProduct));*/
    }

    private static void createTagProduct(KolTagsModel tag, ImportResult result, KolProduct kolProduct) {
        KolTagProduct tagProduct = new KolTagProduct();
        tagProduct.setId(IdWorker.get32UUID());
        tagProduct.setTagId(tag.getId());
        tagProduct.setTagName(tag.getTagName());
        tagProduct.setBrandId(kolProduct.getBrandId());
        tagProduct.setBrandName(kolProduct.getBrandName());
        tagProduct.setProductId(kolProduct.getId());
        tagProduct.setProductName(kolProduct.getProductName());
        tagProduct.setPlatformType(tag.getPlatformType());
        tagProduct.setIsDelete(0);
        result.getTagProductListSave().add(tagProduct);
    }

    private static void createTagProductNew(KolTagsModel tag, ImportResult result) {
        KolTagProduct tagProduct = new KolTagProduct();
        tagProduct.setId(IdWorker.get32UUID());
        tagProduct.setTagId(tag.getId());
        tagProduct.setTagName(tag.getTagName());
        tagProduct.setBrandId(tag.getBrandId());
        tagProduct.setBrandName(tag.getBrandName());
        tagProduct.setProductId(tag.getProductId());
        tagProduct.setProductName(tag.getProductName());
        tagProduct.setPlatformType(tag.getPlatformType());
        tagProduct.setIsDelete(0);
        try {
            result.getTagProductListSave().add(tagProduct);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description: 处理标签和社媒属性关系
     * @author: nqr
     * @date: 2025/7/16 17:26
     **/
    private void processTagAttributeRelation(KolTagsModel tag, Integer platformType, ImportResult result, List<KolAttribute> attributes) {
        String usageScenarios = tag.getUsageScenarios();
        String influencerType = tag.getInfluencerType();
        String audienceType = tag.getAudience();
        String productId = tag.getProductId();
        List<String> attributeNameList = new ArrayList<>();
        if (oConvertUtils.isNotEmpty(usageScenarios)) {
            String[] usageScenariosArr = usageScenarios.split("\\|");
            attributeNameList.addAll(Arrays.asList(usageScenariosArr));
        }
        if (oConvertUtils.isNotEmpty(influencerType)) {
            String[] influencerTypeArr = influencerType.split("\\|");
            attributeNameList.addAll(Arrays.asList(influencerTypeArr));
        }
        if (oConvertUtils.isNotEmpty(audienceType)) {
            String[] audienceTypeArr = audienceType.split("\\|");
            attributeNameList.addAll(Arrays.asList(audienceTypeArr));
        }
        if (attributeNameList.isEmpty()) {
            return;
        }
        // List<KolAttribute> attributeList = attributeMapper.selectList(new LambdaQueryWrapper<KolAttribute>().in(KolAttribute::getAttributeName, attributeNameList));
        List<KolAttribute> attributeList = attributes.stream().filter(x -> attributeNameList.contains(x.getAttributeName())).collect(Collectors.toList());
        for (KolAttribute kolAttribute : attributeList) {
            result.getTagAttributeRelations().add(createTagAttributeRelation(tag, kolAttribute, platformType, productId));
        }

    }

    private KolTagAttributeRelation createTagAttributeRelation(KolTagsModel tag, KolAttribute kolAttribute, Integer platformType, String productId) {
        KolTagAttributeRelation tagAttributeRelation = new KolTagAttributeRelation();
        tagAttributeRelation.setId(IdWorker.get32UUID());
        tagAttributeRelation.setTagId(tag.getId());
        tagAttributeRelation.setTagName(tag.getTagName());
        tagAttributeRelation.setPlatformType(platformType);
        tagAttributeRelation.setTagType(tag.getTagType());
        tagAttributeRelation.setVideoUrl(tag.getVideoUrl());
        tagAttributeRelation.setAttributeId(kolAttribute.getId());
        tagAttributeRelation.setAttributeName(kolAttribute.getAttributeName());
        tagAttributeRelation.setAttributeTypeId(kolAttribute.getAttributeTypeId());
        tagAttributeRelation.setAttributeTypeName(kolAttribute.getAttributeTypeName());
        tagAttributeRelation.setIsDelete(0);
        tagAttributeRelation.setLevel(1);
        tagAttributeRelation.setProductId(productId);
        return tagAttributeRelation;
    }

    /**
     * 方法描述: 验证和设置标签数据
     *
     * @author nqr
     * @date 2025/01/20 08:36
     **/
    /**
     * 方法描述: 验证和设置标签数据
     *
     * @author nqr
     * @date 2025/01/20 08:36
     **/
    private boolean validateAndSetupTag(KolTagsModel tagsModel, List<KolTagCategory> categoryList, ImportResult result, Integer importType) {
        tagsModel.setTagName(tagsModel.getTagName().trim());
        // 验证种子视频类型
        if (oConvertUtils.isEmpty(tagsModel.getTagType())) {
            if (importType == 1) {
                result.getErrorList().add(String.format("标签 '%s'，标签类型不能为空", tagsModel.getTagName()));
            } else {
                // result.getErrorList().add(String.format("标签 '%s'，标签类型不能为空|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                result.getErrorList().add(String.format("标签类型不能为空|%s", tagsModel.getRowNum()));
            }
            return false;
        }
        String videoUrl = tagsModel.getVideoUrl();
        if (tagsModel.getTagType() == ROOT_VIDEO.getCode() && oConvertUtils.isEmpty(videoUrl)) {
            if (importType == 1) {
                result.getErrorList().add(String.format("标签 '%s'，标签类型为种子视频，必须填写视频链接", tagsModel.getTagName()));
            } else {
                // result.getErrorList().add(String.format("标签 '%s'，标签类型为种子视频，必须填写视频链接|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                result.getErrorList().add(String.format("标签类型为种子视频，必须填写视频链接|%s", tagsModel.getRowNum()));
            }
            return false;
        }
        // 校验视频链接格式是否正确
        if (tagsModel.getTagType() == ROOT_VIDEO.getCode() && oConvertUtils.isNotEmpty(videoUrl)) {
            String regex = "^(http|https)://[a-zA-Z0-9\\-\\.]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\-~!@#$%^&*+?:_/=.]*)?$";
            if (!videoUrl.matches(regex)) {
                if (importType == 1) {
                    result.getErrorList().add(String.format("标签 '%s'，视频链接格式不正确", tagsModel.getTagName()));
                } else {
                    // result.getErrorList().add(String.format("标签 '%s'，视频链接格式不正确|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                    result.getErrorList().add(String.format("视频链接格式不正确|%s", tagsModel.getRowNum()));
                }
                return false;
            }
            tagsModel.setVideoUrl(videoUrl.trim());
        }
        if (tagsModel.getTagType() == HASHTAG.getCode()) {
            if (tagsModel.getTagName().contains(" ")) {
                if (importType == 1) {
                    result.getErrorList().add(String.format("标签 '%s'，不能包含空格", tagsModel.getTagName()));
                } else {
                    // result.getErrorList().add(String.format("标签 '%s'，不能包含空格|%s", tagsModel.getTagName(), tagsModel.getRecordId()));
                    result.getErrorList().add(String.format("不能包含空格|%s", tagsModel.getRowNum()));
                }
                return false;
            }
        }

       /* 2025年7月16日17:23:31 取消类目设置
       // 设置主类目和类目ID
        Optional<KolTagCategory> tagCategoryOptional = categoryList.stream().filter(x -> x.getCategoryName().equals(tagsModel.getCategoryName())).findFirst();

        if (tagCategoryOptional.isPresent()) {
            KolTagCategory tagCategory = tagCategoryOptional.get();
            Optional<KolTagCategory> kolTagCategory = categoryList.stream().filter(x -> x.getId().equals(tagCategory.getParentId())).findFirst();

            if (kolTagCategory.isPresent()) {
                tagsModel.setStoreTags(kolTagCategory.get().getCategoryName());
                tagsModel.setCategoryId(tagCategory.getId());
            } else {
                result.getErrorList().add(String.format("标签 '%s'，'%s'上级类目不存在", tagsModel.getTagName(), tagsModel.getCategoryName()));
                return false;
            }
        } else {
            String msg = String.format("标签 '%s'，'%s'标签类目不存在", tagsModel.getTagName(), tagsModel.getCategoryName());
            if (oConvertUtils.isEmpty(tagsModel.getCategoryName())) {
                msg = String.format("标签 '%s'，标签类目不能为空", tagsModel.getTagName());
            }
            result.getErrorList().add(msg);
            return false;
        }*/
        return true;
    }

    /**
     * 方法描述: 处理标签存储
     *
     * @author nqr
     * @date 2025/01/20 08:43
     **/
    private void processTagStorage(KolTagsModel tag, Integer platformType, ImportResult result, List<KolTags> kolTagsList) {
        // 验证标签是否已存在
        // List<KolTags> existingTags = getByTagNames(Collections.singletonList(tag.getTagName()), tag.getTagType(), platformType);
        List<KolTags> existingTags = kolTagsList.stream().filter(x -> x.getTagName().equalsIgnoreCase(tag.getTagName())&&
                x.getPlatformType().equals(platformType)&&x.getTagType().equals(tag.getTagType())).collect(Collectors.toList());

        tag.setIsActive(CommonConstant.IS_ACTIVE);
        if (existingTags.isEmpty()) {
            //已经存在于新增的tag标签中，不再添加到新增列表中 2026年1月20日09:19:50 刘峰修改
            Optional<KolTags> tagsOptional = result.getTagsToSave().stream().filter(x -> x.getTagName().equalsIgnoreCase(tag.getTagName()) && x.getPlatformType().equals(platformType) && x.getTagType().equals(tag.getTagType())).findFirst();
            if (tagsOptional.isPresent()) {
                tag.setId(tagsOptional.get().getId());
            }else{
                tag.setId(IdWorker.getIdStr());
                tag.setCreateTime(new Date());
                result.getTagsToSave().add(tag);
            }
        } else {
            Optional<KolTags> tagsOptional = existingTags.stream().filter(x -> x.getTagName().equalsIgnoreCase(tag.getTagName()) && x.getPlatformType().equals(platformType) && x.getTagType().equals(tag.getTagType())).findFirst();

            if (tagsOptional.isPresent()) {
                KolTags existingTag = tagsOptional.get();
                tag.setId(existingTag.getId());
                tag.setTagName(existingTag.getTagName());
                // 增加判断是否超过 30 天，如果选择更新标签增加判断是否超过30天，超过更新状态为未开始，未超过则不更新保留原标签状态
                int allotMaxDays = tkTagsService.getAllotExpireDays();
                Date expirationDate = DateUtils.getFetureDate(-allotMaxDays);
                String changeStatus = oConvertUtils.isEmpty(tag.getChangeStatus()) ? "自动" : tag.getChangeStatus();
                Date lastExecutionTime = existingTag.getLastExecutionTime();
                Integer tagStatus = existingTag.getTagStatus();
                boolean isUpdate = tagStatus == CommonConstant.TAG_STATUS_UN_STARTED;

                if (lastExecutionTime == null) {
                    isUpdate = true;
                } else {
                    // 超过30天
                    boolean isExpired = lastExecutionTime.before(expirationDate);
                    // "是" 或其他值：强制更新
                    if ("自动".equals(changeStatus)) {
                        // 自动：过期才更新
                        if (!isExpired) {
                            result.getErrorStatusList().add(oConvertUtils.isEmpty(tag.getRecordId()) ? tag.getTagName() : String.format("标签 '%s'，状态未更新|%s", tag.getTagName(), tag.getRecordId()));
                            isUpdate = false;
                        } else {
                            isUpdate = true;
                        }
                    } else isUpdate = !"否".equals(changeStatus);

                }
                if (isUpdate) {
                    tag.setAppStatus(CommonConstant.TAG_STATUS_UN_STARTED);
                    tag.setTagStatus(CommonConstant.TAG_STATUS_UN_STARTED);
                    // nqr 2025年6月17日11:50:47 新增finalStatus字段,如果更新状态同步更新finalStatus字段
                    tag.setFinalStatus(CommonConstant.TAG_STATUS_UN_STARTED);
                }
                result.getTagsToUpdate().add(tag);
            } else {
                tag.setId(IdWorker.getIdStr());
                tag.setCreateTime(new Date());
                result.getTagsToSave().add(tag);
            }
        }
    }


    /**
     * 方法描述: 处理品牌关联
     *
     * @author nqr
     * @date 2025/01/20 08:44
     **/
    private void processBrandRelation(KolTags tag, Integer platformType, ImportResult result) {
        String brandName = tag.getBrandName();
        if (oConvertUtils.isNotEmpty(brandName)) {
            KolBrand brand = kolBrandService.getBrandName(brandName);
            if (brand == null) {
                result.getErrorList().add(String.format("标签'%s'，'%s'品牌不存在", tag.getTagName(), brandName));
                return;
            }

            /*KolTagBrand kolTagBrand = kolTagsBrandService.checkTagsBrand(tag.getId(), brandName, platformType);
            if (kolTagBrand != null) {
                KolTagBrand tagBrandNew = createTagBrand(kolTagBrand.getId(), brand, tag);
                result.getTagsBrandsToUpdate().add(tagBrandNew);
            } else {
                KolTagBrand tagBrandNew = createTagBrand(null, brand, tag);
                tagBrandNew.setPlatformType(platformType);
                if (oConvertUtils.isNotEmpty(tag.getStoreTags())) {
                    tagBrandNew.setStoreTags(JSONArray.toJSONString(Collections.singletonList(tag.getStoreTags())));
                }
                result.getTagsBrandsToSave().add(tagBrandNew);
            }
            KolTagBrand tagBrandNew = createTagBrand(null, brand, tag);
            tagBrandNew.setPlatformType(platformType);
            if (oConvertUtils.isNotEmpty(tag.getStoreTags())) {
                tagBrandNew.setStoreTags(JSONArray.toJSONString(Collections.singletonList(tag.getStoreTags())));
            }
            result.getTagsBrandsToSave().add(tagBrandNew);*/
        }

    }

    private KolTagBrand createTagBrand(String id, KolBrand brand, KolTags tag) {
        KolTagBrand tagBrand = new KolTagBrand();
        tagBrand.setId(id);
        tagBrand.setBrandId(brand.getId());
        tagBrand.setBrandName(brand.getBrandName());
        tagBrand.setTagId(tag.getId());
        tagBrand.setTagName(tag.getTagName());
        return tagBrand;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveImportData(ImportResult importResult) {
        Set<KolTags> tagsToSave = importResult.getTagsToSave();
        if (!tagsToSave.isEmpty()) {
            saveBatch(tagsToSave);
        }
        Set<KolTags> tagsToUpdate = importResult.getTagsToUpdate();
        if (!tagsToUpdate.isEmpty()) {
            tagsToUpdate.forEach(x -> {
                x.setPlatformType(null);
                // x.setTagName(null);
            });
            updateBatchById(tagsToUpdate);
            kolTagsBrandService.lambdaUpdate().in(KolTagBrand::getTagId, tagsToUpdate.stream().map(KolTags::getId).collect(Collectors.toList())).remove();
        }
        if (importResult.getTagsBrandsToSave() != null && !importResult.getTagsBrandsToSave().isEmpty()) {
            kolTagsBrandService.saveBatch(importResult.getTagsBrandsToSave());
        }
        List<KolTagAttributeRelation> attributeRelations = importResult.getTagAttributeRelations();
        if (attributeRelations != null && !attributeRelations.isEmpty()) {
            List<Map<String, Object>> paramList = attributeRelations.stream()
                    .map(r -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("tagId", r.getTagId());
                        map.put("productId", r.getProductId());
                        return map;
                    })
                    .collect(Collectors.toList());
            this.baseMapper.deleteByTagIdAndProductIdBatch(paramList);
            tagAttributeRelationMapper.insertBatch(attributeRelations);
        }
        if (importResult.getTagProductListSave() != null && !importResult.getTagProductListSave().isEmpty()) {
            // tagProductService.remove(new LambdaQueryWrapper<KolTagProduct>().in(KolTagProduct::getTagId, importResult.getTagProductListSave().stream().map(KolTagProduct::getTagId).collect(Collectors.toList())));
            tagProductService.saveBatch(importResult.getTagProductListSave());
        }
    }
    private Result<?> buildImportResult(ImportResult importResult) {
        Set<String> successTags = importResult.getTagsToSave().stream().map(KolTags::getTagName).collect(Collectors.toSet());
        Set<String> updateTags = importResult.getTagsToUpdate().stream().map(KolTags::getTagName).collect(Collectors.toSet());

        Set<String> allTags = new HashSet<>(successTags);
        allTags.addAll(updateTags);
        if (allTags.isEmpty()) {
            return Result.error(500, "文件导入失败！没有成功导入的数据");
        }
        int successTagsCount = allTags.size();
        String msg = String.format("文件导入成功！成功行数：%d", successTagsCount);
        return importResult.getErrorStatusList().isEmpty() ? Result.ok(msg) : Result.OK(10001, msg, importResult.getErrorStatusList());
    }

    @Data
    private static class ImportResult {
        private Set<KolTags> tagsToSave = new HashSet<>();
        private Set<KolTags> tagsToUpdate = new HashSet<>();
        private Set<KolTagBrand> tagsBrandsToSave = new HashSet<>();
        private Set<KolTagBrand> tagsBrandsToUpdate = new HashSet<>();
        private List<String> errorList = new ArrayList<>();
        private List<String> errorStatusList = new ArrayList<>();
        private List<KolTagAttributeRelation> tagAttributeRelations = new ArrayList<>();
        private List<KolTagProduct> tagProductListSave = new ArrayList<>();
    }

    @Override
    public IPage<KolTagCountModel> getUnAllottedTagCount(Page<KolTagCountModel> page, KolSearchModel searchModel) {
        // 获取网红分配过期天数 默认15天
        int allotMaxDays = tkTagsService.getAllotExpireDays();
        searchModel.setAllotDays(allotMaxDays);
        return this.baseMapper.getUnAllottedTagCount(page, searchModel);
    }

    @Override
    public List<KolTags> getUpdateKolTags(KolSearchModel searchModel) {
        String targetCountry = searchModel.getCountryCode();
        List<KolTags> kolTags = lambdaQuery().in(KolTags::getTagName, searchModel.getTagNameList()).list();
        // 修改未分配的标签数量
        List<KolTags> kolTagsUpdateList = new ArrayList<>();
        for (KolTags kolTag : kolTags) {
            JSONArray countryDataArray = JSONArray.parseArray(kolTag.getCountryDataJson());
            if (countryDataArray == null) {
                continue;
            }
            int count = 0;
            JSONArray countryDataArrayNew = new JSONArray();
            for (int i = 0; i < countryDataArray.size(); i++) {
                JSONObject countryData = countryDataArray.getJSONObject(i);
                if (!targetCountry.equals(countryData.getString("country"))) {
                    countryDataArrayNew.add(countryData);
                    count += (int) countryData.get("unassignedCount");
                }
            }

            // 将修改后的 JSONArray 转换回字符串
            KolTags kolTagNew = new KolTags();
            kolTagNew.setId(kolTag.getId());
            kolTagNew.setUnassignedCount(count);
            kolTagNew.setCountryDataJson(countryDataArrayNew.toJSONString());
            kolTagsUpdateList.add(kolTagNew);
        }
        return kolTagsUpdateList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processWithTempTable(String tempTableName, List<KolTagUpdateDTO> dataList, Integer platformType) {
        try {
            // 创建临时表
            createTempUpdateTable(tempTableName);

            // 批量插入数据
            oConvertUtils.splitListIntoBatches(dataList, 2000).forEach(batch -> {
                batchInsertIntoTempTable(tempTableName, batch);
            });

            // 从临时表更新主表
            int updatedRows = updateKolTagsFromTempTable(tempTableName);

            // 清理临时表（可选，事务结束后临时表会自动销毁）
            dropTempTable(tempTableName);

            System.out.println(platformType + " 平台通过临时表成功更新 " + updatedRows + " 个标签的未分配数量。");

        } catch (Exception e) {
            // 确保清理临时表
            try {
                dropTempTable(tempTableName);
            } catch (Exception cleanupException) {
                // 忽略清理异常
            }
            throw e;
        }
    }

    public void createTempUpdateTable(String tableName) {
        this.baseMapper.createTempUpdateTable(tableName);
    }

    public void batchInsertIntoTempTable(String tableName, List<KolTagUpdateDTO> kolTagUpdateDTOS) {
        this.baseMapper.batchInsertIntoTempTable(tableName, kolTagUpdateDTOS);
    }

    public int updateKolTagsFromTempTable(String tableName) {
        return this.baseMapper.updateKolTagsFromTempTable(tableName);
    }

    public void dropTempTable(String tableName) {
        this.baseMapper.dropTempTable(tableName);
    }

}
