package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.model.IgCelebrityModel;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.mapper.KolCelebrityMapper;
import org.jeecg.modules.kol.model.*;
import org.jeecg.modules.kol.service.IKolAttributeService;
import org.jeecg.modules.kol.service.IKolCelebrityService;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecg.modules.kol.service.IKolTagProductService;
import org.jeecg.modules.tiktok.model.TkCelebrityModel;
import org.jeecg.modules.youtube.model.YtCelebrityModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: nqr
 * @Date: 2025/7/18 11:20
 * @Description:
 **/
@Service
public class KolCelebrityServiceImpl extends ServiceImpl<KolCelebrityMapper, KolCelebrityModel> implements IKolCelebrityService {
    @Autowired
    private IKolAttributeService attributeService;
    @Lazy
    @Autowired
    private IKolTagProductService tagProductService;
    @Resource
    private IKolProductService productService;

    @Override
    public void checkParams(KolSearchModel searchModel) {
        String categoryIds = searchModel.getCategoryIds();
        String attributeIds = searchModel.getAttributeIds();

        List<String> tagNameList = searchModel.getTagNameList();
        if (tagNameList == null) {
            tagNameList = new ArrayList<>();
            searchModel.setTagNameList(tagNameList);
        }

        Set<String> allTagNames = new HashSet<>(tagNameList);

/*        // 一次性查询所有标签名称
        List<String> queryTagNames = getAllTagNamesByConditions(categoryIds, productId, attributeIds, searchModel.getPlatformType(), searchModel.getCountryCode());
        if (queryTagNames != null && !queryTagNames.isEmpty()) {
            allTagNames.addAll(queryTagNames);
        }*/

        // 处理类目参数
        if (oConvertUtils.isNotEmpty(categoryIds)) {
            // 将字符串转换为 List
            List<String> categoryIdList = Arrays.stream(categoryIds.split(","))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            List<String> categoryTagNames = this.baseMapper.getTagNameByCategoryIds(categoryIdList, searchModel.getPlatformType());
            if (categoryTagNames != null && !categoryTagNames.isEmpty()) {
                allTagNames.addAll(categoryTagNames);
            }
        }
        List<String> attributeTagNameAll = new ArrayList<>();
        // 处理社媒属性参数
        if (oConvertUtils.isNotEmpty(attributeIds)) {
            // 1、按照社媒属性查询标签
            List<String> attributeTagNames = this.baseMapper.getTagNameByAttributeIds(attributeIds, searchModel);
            if (attributeTagNames != null && !attributeTagNames.isEmpty()) {
                KolSearchModel model = new KolSearchModel();
                String tempTableName = "temp_" + UUID.randomUUID().toString().replaceAll("-", "");
                Set<String> tempTagNames = new HashSet<>(attributeTagNames);
                model.setTempTableName(tempTableName);
                model.setTagNameList(new ArrayList<>(tempTagNames));
                model.setPlatformType(searchModel.getPlatformType());
                model.setCountryCode(searchModel.getCountryCode());
                try {
                    createTempTable(model);
                    List<String> tagNames = this.baseMapper.getTagNameByConditions(model);
                    allTagNames.addAll(tagNames);
                    attributeTagNameAll.addAll(tagNames);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    dropTempTable(tempTableName);
                }
            }
        }

        // 根据产品ID添加产品标签
        if (oConvertUtils.isNotEmpty(searchModel.getProductId())) {
            List<String> productTags = setProductTags(searchModel);
            if (!CollectionUtils.isEmpty(productTags)) {
                searchModel.setProductTags(productTags);
                // 处理产品标签参数,判断如果同时选中社媒属性，取两者之间的交集
                if (oConvertUtils.isNotEmpty(attributeIds)) {
                    Set<String> set2 = new HashSet<>(productTags);
                    Set<String> tagsSet = attributeTagNameAll.stream()
                            .filter(set2::contains).collect(Collectors.toSet());
                 /*   if (!tagNameList.isEmpty()) {
                        allTagNames = tagNameList.stream().filter(tagsSet::contains).collect(Collectors.toSet());
                    } else {
                        allTagNames = allTagNames.stream().filter(tagsSet::contains).collect(Collectors.toSet());
                    }*/
                    allTagNames = allTagNames.stream().filter(tagsSet::contains).collect(Collectors.toSet());
                } else if (!allTagNames.isEmpty()) {
                    allTagNames = allTagNames.stream().filter(productTags::contains).collect(Collectors.toSet());
                } else {
                    allTagNames.addAll(productTags);
                }
            }
        }

        // 根据品牌ID添加品牌标签（仅当没有指定产品ID时）
        if (oConvertUtils.isNotEmpty(searchModel.getBrandId()) && oConvertUtils.isEmpty(searchModel.getProductId())) {
            List<String> tags = setBrandIdTags(searchModel);
            if (!CollectionUtils.isEmpty(tags)) {
                allTagNames.addAll(tags);
            }
        }

        // 过滤空值并去重，转换为List
        List<String> tags = allTagNames.stream()
                .filter(oConvertUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
        searchModel.setTagNameList(tags);
    }

    private List<String> setBrandIdTags(KolSearchModel searchModel) {
        // 查询品牌下所有产品
        List<KolProduct> products = productService.getProductListByBrandId(searchModel.getBrandId());
        if (products.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询产品对应的标签
        List<String> tagNames = tagProductService.setBrandProductTags(products.stream().map(KolProduct::getId).collect(Collectors.toList()));
        if (tagNames.isEmpty()) {
            return Collections.emptyList();
        }
        return tagNames;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> void setAttributes(IPage<T> pageList, KolSearchModel model) {
        List<T> records = pageList.getRecords();
        if (CollectionUtils.isEmpty(records)) return;

        // 1. 优化标签提取：使用Set去重，提取公共逻辑
        Set<String> allTagNames = extractAllTagNames(records);
        if (allTagNames.isEmpty()) return;

        String tempTableName = "temp_tag_name_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
        // 保存标签到临时表，防止 in 查询数据过多
        KolSearchModel searchModel = new KolSearchModel();
        BeanUtils.copyProperties(model, searchModel);
        searchModel.setTempTableName(tempTableName);
        searchModel.setPlatformType(model.getPlatformType());
        searchModel.setTagNameList(new ArrayList<>(allTagNames));
        List<KolAttributeModel> attributeList = new ArrayList<>();
        try {
            createTempTable(searchModel);
            attributeList = attributeService.getAttributeList(searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // 删除临时表
            dropTempTable(tempTableName);
        }
        if (CollectionUtils.isEmpty(attributeList)) return;

        // 4. 预构建索引映射，提高查询效率
        Map<String, List<String>> tagToAttributeIdsMap = buildTagToAttributeIdsMap(attributeList);
        Map<String, KolAttribute> attributeIdToAttributeMap = buildAttributeIdMap(attributeList);

        // 5. 并行处理每个记录
        records.parallelStream().forEach(record ->
                processRecord(record, tagToAttributeIdsMap, attributeIdToAttributeMap));
    }

    /**
     * 提取所有记录中的标签名称（优化：使用Set去重，减少重复处理）
     */
    private <T> Set<String> extractAllTagNames(List<T> records) {
        return records.stream()
                .map(this::getTagListFromRecord)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(KolAllotTagModel::getTagName)
                .filter(Objects::nonNull)
                .map(String::trim) // 增加去除前后空格
                .filter(tagName -> !tagName.isEmpty()) // 过滤掉空字符串
                .collect(Collectors.toSet()); // 使用Set自动去重
    }

    /**
     * 从记录中获取标签列表（提取公共逻辑）
     */
    private <T> List<? extends KolAllotTagModel> getTagListFromRecord(T record) {
        if (record instanceof KolCelebrityModel) {
            return ((KolCelebrityModel) record).getTagList();
        } else if (record instanceof KolBaseModel) {
            return ((KolBaseModel) record).getTagList();
        } else if (record instanceof TkCelebrityModel) {
            return ((TkCelebrityModel) record).getTagList();
        } else if (record instanceof YtCelebrityModel) {
            return ((YtCelebrityModel) record).getTagList();
        } else if (record instanceof IgCelebrityModel) {
            return ((IgCelebrityModel) record).getTagList();
        }
        return null;
    }

    /**
     * 构建标签名到属性ID列表的映射
     */
    private Map<String, List<String>> buildTagToAttributeIdsMap(List<KolAttributeModel> attributeList) {
        return attributeList.stream()
                .filter(relation -> Objects.nonNull(relation.getTagName()) && Objects.nonNull(relation.getAttributeId()))
                .collect(Collectors.groupingBy(
                        KolAttributeModel::getTagName,
                        Collectors.mapping(KolAttributeModel::getAttributeId, Collectors.toList())
                ));
    }

    /**
     * 构建属性ID到属性对象的映射
     */
    private Map<String, KolAttribute> buildAttributeIdMap(List<KolAttributeModel> attributeList) {
        return attributeList.stream()
                .filter(attr -> Objects.nonNull(attr.getId()))
                .collect(Collectors.toMap(
                        KolAttribute::getId,
                        Function.identity(),
                        (existing, replacement) -> existing // 处理可能的重复key
                ));
    }

    /**
     * 处理单个记录
     */
    private <T> void processRecord(T record,
                                   Map<String, List<String>> tagToAttributeIdsMap,
                                   Map<String, KolAttribute> attributeIdToAttributeMap) {
        // 获取当前记录的标签列表
        List<KolAllotTagModel> tags = (List<KolAllotTagModel>) getTagListFromRecord(record);
        if (CollectionUtils.isEmpty(tags)) return;

        // 获取标签名称集合
        Set<String> tagNames = tags.stream()
                .map(KolAllotTagModel::getTagName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 获取相关的属性ID集合（使用索引映射，避免嵌套循环）
        Set<String> relatedAttributeIds = tagNames.stream()
                .map(tagToAttributeIdsMap::get)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        if (relatedAttributeIds.isEmpty()) return;

        // 获取相关属性对象（使用索引映射，O(1)查找）
        List<KolAttribute> relatedAttributes = relatedAttributeIds.stream()
                .map(attributeIdToAttributeMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 构建TypeData列表
        List<TypeData> dataList = buildTypeDataList(relatedAttributes);

        // 设置数据到记录中
        setDataListToRecord(record, dataList);
    }

    /**
     * 构建TypeData列表（优化：使用流式处理）
     */
    private List<TypeData> buildTypeDataList(List<KolAttribute> attributes) {
        return attributes.stream()
                .collect(Collectors.groupingBy(KolAttribute::getAttributeTypeId))
                .entrySet().stream()
                .map(entry -> {
                    TypeData typeData = new TypeData();
                    typeData.setTypeId(entry.getKey());

                    List<TypeData.CategoryItem> categoryItemList = entry.getValue().stream()
                            .map(this::createCategoryItem)
                            .collect(Collectors.toList());

                    typeData.setList(categoryItemList);
                    return typeData;
                })
                .collect(Collectors.toList());
    }

    /**
     * 创建CategoryItem对象
     */
    private TypeData.CategoryItem createCategoryItem(KolAttribute attribute) {
        TypeData.CategoryItem categoryItem = new TypeData.CategoryItem();
        categoryItem.setAttributeId(attribute.getId());
        categoryItem.setAttributeName(attribute.getAttributeName());
        categoryItem.setAttributeEnName(attribute.getAttributeEnName());
        return categoryItem;
    }

    /**
     * 将数据列表设置到记录中
     */
    private <T> void setDataListToRecord(T record, List<TypeData> dataList) {
        if (record instanceof KolCelebrityModel) {
            ((KolCelebrityModel) record).setDataList(dataList);
        } else if (record instanceof KolBaseModel) {
            ((KolBaseModel) record).setDataList(dataList);
        } else if (record instanceof TkCelebrityModel) {
            ((TkCelebrityModel) record).setDataList(dataList);
        } else if (record instanceof YtCelebrityModel) {
            ((YtCelebrityModel) record).setDataList(dataList);
        } else if (record instanceof IgCelebrityModel) {
            ((IgCelebrityModel) record).setDataList(dataList);
        }
    }

    public List<String> setProductTags(KolSearchModel searchModel) {
        // if (searchModel.getIsContact() == null && oConvertUtils.isNotEmpty(searchModel.getProductId())) {
        //     return this.baseMapper.getProductTags(searchModel);
        // }
        // 2025年12月9日15:26:13
        if (oConvertUtils.isNotEmpty(searchModel.getProductId())) {
            return this.baseMapper.getProductTags(searchModel);
        }
        return Collections.emptyList();
    }

    @Override
    public void createTempTable(KolSearchModel searchModel) {
        if (oConvertUtils.isNotEmpty(searchModel.getTempTableName()) && !searchModel.getTagNameList().isEmpty()) {
            try {
                String tempTableName = searchModel.getTempTableName();
                // 创建临时表
                this.baseMapper.createTempUpdateTable(tempTableName);

                // 批量插入数据
                oConvertUtils.splitListIntoBatches(searchModel.getTagNameList(), 1000).forEach(batch -> batchInsertIntoTempTable(tempTableName, batch));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void batchInsertIntoUniqueIdTempTable(String tableName, List<String> batch) {
        this.baseMapper.batchInsertIntoUniqueIdTempTable(tableName, batch);
    }

    public void batchInsertIntoTempTable(String tableName, List<String> batch) {
        this.baseMapper.batchInsertIntoTempTable(tableName, batch);
    }

    @Override
    public void dropTempTable(String tableName) {
        if (oConvertUtils.isNotEmpty(tableName))
            this.baseMapper.dropTempTable(tableName);
    }

    @Override
    public void createUniqueIdTempTable(String tempTableName, List<String> uniqueIdList) {
        if (oConvertUtils.isNotEmpty(tempTableName) && !uniqueIdList.isEmpty()) {
            try {
                // 创建临时表
                this.baseMapper.createUniqueIdTempTable(tempTableName);

                // 批量插入数据
                oConvertUtils.splitListIntoBatches(uniqueIdList, 1000).forEach(batch -> batchInsertIntoUniqueIdTempTable(tempTableName, batch));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
