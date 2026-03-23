package org.jeecg.modules.kol.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.constant.CategoryType;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.kol.model.TreeSelectModel;
import org.jeecg.common.util.FillRuleUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolAttribute;
import org.jeecg.modules.kol.entity.KolAttributeType;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.feishu.config.FeishuAppConfig;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.mapper.KolAttributeMapper;
import org.jeecg.modules.kol.model.KolAttributeModel;
import org.jeecg.modules.kol.model.KolAttributeTreeModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.service.IKolAttributeService;
import org.jeecg.modules.kol.service.IKolAttributeTypeService;
import org.jeecg.modules.kol.service.IKolCategoryService;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.SystemConstants.QUERY_EMPTY;
import static org.jeecg.common.constant.SystemConstants.ROOT_PARENT_ID_VALUE;
import static org.jeecg.common.util.CommonUtils.split2List;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date: 2024年11月22日 18:17:35
 * @Version: V1.0
 */
@Service
public class KolAttributeServiceImpl extends ServiceImpl<KolAttributeMapper, KolAttribute> implements IKolAttributeService {

    @Autowired
    @Lazy
    private IKolTagsService tagsService;
    @Autowired
    private IFeishuService feishuService;
    @Autowired
    private IKolAttributeTypeService attributeTypeService;
    @Autowired
    private FeishuAppConfig feishuAppConfig;

    /**
     * 新增类目
     *
     * @param kolAttribute
     */
    @Override
    public void addKolAttribute(KolAttribute kolAttribute) throws Exception {
        String attributeCode = "";
        String categoryPid = IKolCategoryService.ROOT_PID_VALUE;
        String parentNamePath = null;
        String parentEnNamePath = null;
        String parentCodePath = null;
        if (oConvertUtils.isNotEmpty(kolAttribute.getParentId())) {
            categoryPid = kolAttribute.getParentId();

            // PID 不是根节点 说明需要设置父节点 hasChild 为1
            if (!IKolCategoryService.ROOT_PID_VALUE.equals(categoryPid)) {
                KolAttribute parent = baseMapper.selectById(categoryPid);
                if (parent != null) {
                    parentCodePath = parent.getAttributeCodePath();
                    parentEnNamePath = parent.getAttributeEnNamePath();
                    parentNamePath = parent.getAttributeNamePath();
                    if (parent.getIsHasChild() != 1) {
                        parent.setIsHasChild(1);
                        baseMapper.updateById(parent);
                    }
                }
            }
        }

        // update-begin--Author:baihailong  Date:20191209 for：分类字典编码规则生成器做成公用配置
        JSONObject formData = new JSONObject();
        formData.put("pid", categoryPid);
        attributeCode = (String) FillRuleUtil.executeRule("attribute_code_rule", formData);
        // update-end--Author:baihailong  Date:20191209 for：分类字典编码规则生成器做成公用配置
        kolAttribute.setAttributeCode(attributeCode);
        kolAttribute.setParentId(categoryPid);
        kolAttribute.setAttributeCodePath(parentCodePath == null ? attributeCode : parentCodePath + "|" + attributeCode);
        kolAttribute.setAttributeNamePath(parentNamePath == null ? kolAttribute.getAttributeName() : parentNamePath + "|" + kolAttribute.getAttributeName());
        kolAttribute.setAttributeEnNamePath(parentEnNamePath == null ? kolAttribute.getAttributeEnName() : parentEnNamePath + "|" + kolAttribute.getAttributeEnName());
        baseMapper.insert(kolAttribute);
        KolAttributeType attributeType = attributeTypeService.getById(kolAttribute.getAttributeTypeId());
        if (categoryPid.equals(IKolAttributeService.ROOT_PID_VALUE)) {
            String tableId = feishuAppConfig.getExpertsTableId();
            String appToken = feishuAppConfig.getAppToken();
            HashMap<String, Object> map = new HashMap<>();
            if (attributeType.getTypeCode().equals(CategoryType.SCENE_CODE)) {
                tableId = feishuAppConfig.getSceneTableId();
                map.put(CategoryType.SCENE_NAME, new String[]{kolAttribute.getAttributeName()});
            } else if (attributeType.getTypeCode().equals(CategoryType.AUDIENCE_CODE)) {
                tableId = feishuAppConfig.getAudienceTableId();
                map.put(CategoryType.AUDIENCE_NAME, new String[]{kolAttribute.getAttributeName()});
            } else {
                map.put(CategoryType.INFLUENCER_TYPE_NAME, new String[]{kolAttribute.getAttributeName()});
            }
            feishuService.insertRecord(appToken, tableId, map);
        }
    }


    /**
     * 修改类目-需要考虑业务中冗余的名称同步更新
     *
     * @param kolAttribute
     */
    @Override
    public void updateKolAttribute(KolAttribute kolAttribute) {

        KolAttribute entity = this.getById(kolAttribute.getId());
        if (entity == null) {
            throw new JeecgBootException(QUERY_EMPTY);
        }
        if (!entity.getAttributeName().equals(kolAttribute.getAttributeName())) {
            // 2025-5-12 16:38:30  增加判断类目名称是否发生改变
            // 判断类目名称是否存在
            KolAttribute attribute = this.lambdaQuery().eq(KolAttribute::getAttributeName, kolAttribute.getAttributeName()).last("limit 1").one();
            if (attribute != null && !attribute.getId().equals(entity.getId())) {
                throw new JeecgBootException("类目名称已存在，请重新输入！");
            }
        }
        if (oConvertUtils.isNotEmpty(entity.getAttributeNamePath())) {
            kolAttribute.setAttributeNamePath(entity.getAttributeNamePath().replace(entity.getAttributeName(), kolAttribute.getAttributeName()));
        }
        String oldPid = entity.getParentId();
        String new_pid = kolAttribute.getParentId();
        boolean isUpdateOld = false;
        boolean isUpdateBusinessAttributeName = false;
        // 如果父节点没有变化 则直接更新数据
        if (oldPid.equals(new_pid)) {
            if (!kolAttribute.getAttributeName().equals(entity.getAttributeName())) {
                // 2024年11月24日 16:35:59  增加判断类目名称是否发生改变，如果改变 则应同步更新 tiktok_tags 表中的对应的类目名称
                // 同步更新 tiktok_tags 表中的对应的类目名称
                isUpdateBusinessAttributeName = true;
            } else {
                this.updateById(kolAttribute);
                return;
            }

        } else {
            // 判断父id是否在子集内
            String categoryCodePath = entity.getAttributeCodePath();
            List<KolAttribute> tagCategories = this.lambdaQuery().like(KolAttribute::getAttributeName, categoryCodePath).list();
            if (!tagCategories.isEmpty()) {
                String finalNewPid = new_pid;
                tagCategories.stream().map(KolAttribute::getId).filter(x -> x.equals(finalNewPid)).findFirst().ifPresent(x -> {
                    throw new JeecgBootException("父类目错误，不能设置为子集！");
                });
            }
        }
        if (oConvertUtils.isEmpty(new_pid)) {
            new_pid = ROOT_PARENT_ID_VALUE;
            kolAttribute.setParentId(new_pid);
        }

        // 如果为根节点 则无需处理
        if (!isRootNode(oldPid)) {
            // 首先统计该父节点下的所有子节点
            int childCount = (int) this.count(new QueryWrapper<KolAttribute>().lambda().eq(KolAttribute::getParentId, oldPid));
            isUpdateOld = childCount <= 1;
        }
        updateWithTrans(kolAttribute, entity, oldPid, new_pid, isUpdateOld, isUpdateBusinessAttributeName);
       /* if (isRootNode(oldPid) && isUpdateBusinessAttributeName) {
            try {
                updateTableDataSynchronously(kolAttribute, entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    /**
     * 判断是否为根节点
     */
    private boolean isRootNode(String parentId) {
        return ROOT_PARENT_ID_VALUE.equals(parentId);
    }

    private void updateTableDataSynchronously(KolAttribute kolAttribute, KolAttribute entity) {
        // 获取当前社媒对应的recordId
        String attributeTypeId = entity.getAttributeTypeId();
        KolAttributeType attributeType = attributeTypeService.getById(attributeTypeId);
        // List<KolAttribute> attributeList = this.lambdaQuery().eq(KolAttribute::getAttributeTypeId, attributeTypeId).eq(KolAttribute::getParentId, ROOT_PARENT_ID_VALUE).list();
        // List<String> attributeNameList = attributeList.stream().map(KolAttribute::getAttributeName).collect(Collectors.toList());

        String tableId = feishuAppConfig.getExpertsTableId();
        String appToken = feishuAppConfig.getAppToken();
        String fieldName = "";
        HashMap<String, Object> map = new HashMap<>();
        if (attributeType.getTypeCode().equals(CategoryType.SCENE_CODE)) {
            tableId = feishuAppConfig.getSceneTableId();
            map.put(CategoryType.SCENE_NAME, new String[]{kolAttribute.getAttributeName()});
            fieldName = CategoryType.SCENE_NAME;
        } else if (attributeType.getTypeCode().equals(CategoryType.AUDIENCE_CODE)) {
            tableId = feishuAppConfig.getAudienceTableId();
            map.put(CategoryType.AUDIENCE_NAME, new String[]{kolAttribute.getAttributeName()});
            fieldName = CategoryType.AUDIENCE_NAME;
        } else {
            map.put(CategoryType.INFLUENCER_TYPE_NAME, new String[]{kolAttribute.getAttributeName()});
            fieldName = CategoryType.INFLUENCER_TYPE_NAME;
        }
        String recordIds = feishuService.getRecordIds(tableId, feishuAppConfig.getViewId(), fieldName, entity.getAttributeName());

        if (oConvertUtils.isNotEmpty(recordIds)) {
            String finalTableId = tableId;
            String[] recordIdsArr = recordIds.split(",");

            AppTableRecord[] records = new AppTableRecord[recordIdsArr.length];
            for (int i = 0; i < recordIdsArr.length; i++) {
                records[i] = AppTableRecord.newBuilder()
                        .recordId(recordIdsArr[i])
                        .fields(map)
                        .build();
            }
            try {
                CompletableFuture.runAsync(() -> {
                    feishuService.batchUpdateRecords(appToken, finalTableId, records);
                });
            } catch (Exception ignored) {
            }
        }
    }


    /**
     * 事务更新方法
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateWithTrans(KolAttribute attributeNew, KolAttribute attributeOld, String oldParentId, String newParentId, boolean isUpdateOld, boolean isUpdateBusinessAttributeName) {
        // 如果类目名称发生变化 则更新tags表对应的类目名称
        if (isUpdateBusinessAttributeName) {
            LambdaUpdateWrapper<KolTags> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(KolTags::getCategoryId, attributeNew.getId());
            wrapper.set(KolTags::getCategoryName, attributeNew.getAttributeName());
            tagsService.update(wrapper);
        }
        // 查询所有 attributeNamePath 包含旧名称的记录
        List<KolAttribute> attributes = this.lambdaQuery()
                .like(KolAttribute::getAttributeNamePath, attributeOld.getAttributeName())
                .ne(KolAttribute::getId, attributeNew.getId())
                .list();

        // 如果有匹配的记录，则进行名称替换并更新
        if (oConvertUtils.isNotEmpty(attributes)) {
            String oldName = attributeOld.getAttributeName();
            String newName = attributeNew.getAttributeName();

            for (KolAttribute attr : attributes) {
                // 替换 attributeNamePath 中的旧名称为新名称
                String updatedPath = attr.getAttributeNamePath().replace(oldName, newName);
                attr.setAttributeNamePath(updatedPath);
            }
            if (!attributes.isEmpty()) {
                // 批量更新到数据库
                this.updateBatchById(attributes);
            }
        }
        // 更新原父节点的hasChild状态 如果当前子节点为唯一子节点 则应更新
        if (isUpdateOld) {
            // 如果不存在或者仅有一个 则表示此次变更之后 该父节点就没有其他子节点了 将hasChild状态设置为否
            updateHasChild(oldParentId, YesNoStatus.NO.getCode());
        }
        // 处理新父节点 不判断 直接更新hasChild 为是
        if (!ROOT_PARENT_ID_VALUE.equals(newParentId)) {
            updateHasChild(newParentId, YesNoStatus.YES.getCode());
        }
        // 更新实体
        this.updateById(attributeNew);

    }

    /**
     * 更新hasChild状态
     *
     * @param keyId
     * @param hasChildStatus
     */
    private void updateHasChild(String keyId, Integer hasChildStatus) {
        LambdaUpdateWrapper<KolAttribute> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(KolAttribute::getId, keyId);
        wrapper.set(KolAttribute::getIsHasChild, hasChildStatus);
        this.update(wrapper);
    }

    @Override
    public List<TreeSelectModel> queryListByParentId(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByParentId(pid, null);
    }

    @Override
    public List<TreeSelectModel> queryListByParentId(String pid, Map<String, String> condition) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByParentId(pid, condition);
    }


    /**
     * 查询所有数据，无分页
     *
     * @param queryWrapper
     */
    @Override
    public List<KolAttribute> getTreeRootNodeList(Wrapper<KolAttribute> queryWrapper) {
        List<KolAttribute> nodeList = this.list(queryWrapper);
        List<KolAttribute> rootNodeList = new ArrayList<>();
        nodeList.forEach(x -> {
            String parentId = x.getParentId();
            // 如果不为根节点 则递归查找父类 直至根节点
            // 此处算法应该可以优化 例如有重复交叉的父节点 可能存在重复查找的问题
            // 是否可以记录所有中间父节点 循环查找同时 判断是否存在重复的父节点 如有则直接调至下次循环
            // 2023年7月13日17:59:46 xyc
            if (parentId != null && !ROOT_PARENT_ID_VALUE.equals(parentId)) {
                KolAttribute rootNode = this.getRootNode(parentId);
                if (rootNode != null && !rootNodeList.contains(rootNode)) {
                    rootNodeList.add(rootNode);
                }
            } else {
                if (!rootNodeList.contains(x)) {
                    rootNodeList.add(x);
                }
            }
        });
        return rootNodeList;
    }

    /**
     * 递归查询节点的根节点
     *
     * @param parentId
     * @return
     */
    private KolAttribute getRootNode(String parentId) {
        KolAttribute entity = this.getById(parentId);
        if (entity == null || entity.getParentId().equals(ROOT_PARENT_ID_VALUE)) return entity;
        return this.getRootNode(entity.getParentId());
    }

    /**
     * 删除节点（关联删除)
     *
     * @param ids 要删除的节点ids 逗号分隔
     * @throws JeecgBootException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(String ids) throws JeecgBootException {
        // 创建所有节点id列表 最终结果返回
        List<String> nodeIdList = split2List(ids);
        if (oConvertUtils.listIsEmpty(nodeIdList)) {
            throw new JeecgBootException(QUERY_EMPTY);
        }
        List<String> categoryIdList = getAllNodeIds(nodeIdList);
//        增加校验 如果标签业务表中存在引用 则禁止删除
        int tagCount = Math.toIntExact(tagsService.lambdaQuery().in(KolTags::getCategoryId, categoryIdList).count());
        if (tagCount > 0) {
            throw new JeecgBootException("标签业务表中存在引用，禁止删除");
        }
        List<String> allNodeIdList = getAllNodeIds(nodeIdList);
        List<String> updateNodeHasChildList = new ArrayList<>();
        List<KolAttribute> deleteFeishuRecords = new ArrayList<>();
        nodeIdList.forEach(x -> {
            KolAttribute examObjectiveTestCategory = this.getById(x);
            String parentId = examObjectiveTestCategory.getParentId();
            if (isRootNode(parentId)) {
                deleteFeishuRecords.add(examObjectiveTestCategory);
            }
            // 需要判断其父节点是否还有其他子节点 即兄弟节点 如有 则无需操作，若为独子或者其他兄弟节点也在删除列表 则应该更新其hasChild=0
            LambdaQueryWrapper<KolAttribute> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(KolAttribute::getParentId, parentId);
            queryWrapper.notIn(KolAttribute::getId, nodeIdList);
            int otherChildCount = (int) this.count(queryWrapper);
            // 如果没有其他子节点 则应更新hasChild=0
            if (otherChildCount == 0) {
                if (!updateNodeHasChildList.contains(parentId)) {
                    updateNodeHasChildList.add(parentId);
                }
            }
        });
        deleteNodeBatch(allNodeIdList, updateNodeHasChildList, deleteFeishuRecords);
    }

    /**
     * 事务删除操作
     *
     * @param allNodeIdList
     * @param updateNodeHasChildList
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNodeBatch(List<String> allNodeIdList, List<String> updateNodeHasChildList, List<KolAttribute> deleteFeishuRecords) {
        this.removeByIds(allNodeIdList);
        updateNodeHasChildList.forEach(x -> {
            updateHasChild(x, YesNoStatus.NO.getCode());
        });
        if (!deleteFeishuRecords.isEmpty()) {
            for (KolAttribute deleteFeishuRecord : deleteFeishuRecords) {
                String tableId = feishuAppConfig.getExpertsTableId();
                String appToken = feishuAppConfig.getAppToken();
                String fieldName = "";
                HashMap<String, Object> map = new HashMap<>();
                if (deleteFeishuRecord.getAttributeTypeId().equals(CategoryType.SCENE_ID)) {
                    tableId = feishuAppConfig.getSceneTableId();
                    map.put(CategoryType.SCENE_NAME, new String[]{deleteFeishuRecord.getAttributeName()});
                    fieldName = CategoryType.SCENE_NAME;
                } else if (deleteFeishuRecord.getAttributeTypeId().equals(CategoryType.AUDIENCE_ID)) {
                    tableId = feishuAppConfig.getAudienceTableId();
                    map.put(CategoryType.AUDIENCE_NAME, new String[]{deleteFeishuRecord.getAttributeName()});
                    fieldName = CategoryType.AUDIENCE_NAME;
                } else {
                    map.put(CategoryType.INFLUENCER_TYPE_NAME, new String[]{deleteFeishuRecord.getAttributeName()});
                    fieldName = CategoryType.INFLUENCER_TYPE_NAME;

                }
                String recordIds = feishuService.getRecordIds(tableId, feishuAppConfig.getViewId(), fieldName, deleteFeishuRecord.getAttributeName());
                if (oConvertUtils.isNotEmpty(recordIds)) {
                    String finalTableId = tableId;
                    feishuService.batchDeleteRecords(appToken, finalTableId, recordIds);

                }

            }
        }
    }

    /**
     * 递归获取所有节点id列表
     *
     * @param parentIdList
     * @return
     */
    private List<String> getAllNodeIds(List<String> parentIdList) {
        List<String> allNodeIdList = new ArrayList<>(parentIdList);
        parentIdList.forEach(x -> {
            getSubNodeIds(x, allNodeIdList);
        });
        return allNodeIdList;
    }

    /**
     * 根据父节点 递归查询对应的所有子级节点id
     *
     * @param parentId
     * @param allNodeIdList
     */
    private void getSubNodeIds(String parentId, List<String> allNodeIdList) {
        LambdaQueryWrapper<KolAttribute> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KolAttribute::getParentId, parentId);
        List<String> nodeIdList = this.list(queryWrapper).stream().map(KolAttribute::getId).collect(Collectors.toList());
        if (oConvertUtils.isNotEmpty(nodeIdList)) {
            allNodeIdList.addAll(nodeIdList);
            nodeIdList.forEach(x -> getSubNodeIds(x, allNodeIdList));
        }
    }

    @Override
    public List<KolAttributeModel> getAttributeList(KolSearchModel searchModel) {
        return this.baseMapper.getAttributeList(searchModel);
    }

    @Override
    public List<String> queryAttributeIdsByProductId(String productId,Integer platformType) {
        return this.baseMapper.queryAttributeIdsByProductId(productId,platformType);
    }

    /**
     * 功能描述：获取社媒属性叶子节点
     *
     * @param type
     * @Param:
     * @Author: fengLiu
     * @Date: 2025-07-28 14:05
     */
    @Override
    public List<Map<String, Object>> getKolAttributeLeafNodes(Integer type,String typeCode, Integer isParent) {
        // 查询属性类型，按 sort_code 升序排序
        LambdaQueryWrapper<KolAttributeType> typeQuery = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(type)) {
            typeQuery.eq(KolAttributeType::getType, type);
        }
        if (oConvertUtils.isNotEmpty(typeCode)) {
            typeQuery.eq(KolAttributeType::getTypeCode, typeCode);
        }
        typeQuery.orderByAsc(KolAttributeType::getSortCode);
        List<KolAttributeType> attributeTypes = attributeTypeService.list(typeQuery);
        List<String> typeIds = attributeTypes.stream().map(KolAttributeType::getId).collect(Collectors.toList());

        // 查询所有属性数据
        LambdaQueryWrapper<KolAttribute> query = new LambdaQueryWrapper<>();
        if (isParent != null) {
            query.eq(KolAttribute::getParentId, ROOT_PARENT_ID_VALUE);
        }
        query.in(!typeIds.isEmpty(), KolAttribute::getAttributeTypeId, typeIds)
                .orderByAsc(KolAttribute::getSortCode);
        List<KolAttribute> allAttributes = this.list(query);

        // 构建树形结构
        Map<String, List<KolAttribute>> childrenMap = allAttributes.stream()
                .filter(attr -> !attr.getParentId().equals(ROOT_PARENT_ID_VALUE))
                .collect(Collectors.groupingBy(KolAttribute::getParentId));
        List<KolAttributeTreeModel> treeNodes = new ArrayList<>();

        // 找到根节点（parent_id = 0）
        List<KolAttribute> rootAttributes = allAttributes.stream()
                .filter(attr -> attr.getParentId().equals(ROOT_PARENT_ID_VALUE))
                .sorted(Comparator.comparing(KolAttribute::getSortCode))
                .collect(Collectors.toList());

        // 递归构建树形结构
        for (KolAttribute rootAttr : rootAttributes) {
            KolAttributeTreeModel node = new KolAttributeTreeModel();
            BeanUtils.copyProperties(rootAttr, node);
            node.setChildren(buildTree(childrenMap, rootAttr.getId()));
            treeNodes.add(node);
        }
        // 构造返回结果，仅返回叶子节点，并倒序 attribute_name_path
        List<Map<String, Object>> resultList = new ArrayList<>();

        // 按 attribute_type_id 和 attribute_type_name 分组
        Map<String, List<KolAttributeTreeModel>> map = treeNodes.stream()
                .collect(Collectors.groupingBy(node -> node.getAttributeTypeId() + "_" + attributeTypes.stream()
                        .filter(attrType -> attrType.getId().equals(node.getAttributeTypeId()))
                        .findFirst().map(KolAttributeType::getTypeName).orElse("")));
        if (isParent != null) {
            for (Map.Entry<String, List<KolAttributeTreeModel>> entry : map.entrySet()) {
                Map<String, Object> resultMap = new HashMap<>();
                String[] split = entry.getKey().split("_");
                resultMap.put("typeId", split[0]);
                resultMap.put("typeName", split[1]);
                resultMap.put("data", entry.getValue());
                resultList.add(resultMap);
            }
            return resultList;
        }

        for (KolAttributeType attrType : attributeTypes) {
            String key = attrType.getId() + "_" + attrType.getTypeName();
            List<KolAttributeTreeModel> nodes = map.getOrDefault(key, new ArrayList<>());
            // 收集所有叶子节点的 ID
            List<String> leafNodeIds = new ArrayList<>();
            for (KolAttributeTreeModel node : nodes) {
                collectLeafNodeIds(node, leafNodeIds);
            }
            // 从 allAttributes 中获取叶子节点并倒序 attribute_name_path
            List<KolAttribute> leafNodes = new ArrayList<>();
            for (String leafId : leafNodeIds) {
                allAttributes.stream()
                        .filter(attr -> attr.getId().equals(leafId))
                        .findFirst()
                        .ifPresent(attr -> {
                            KolAttribute copy = new KolAttribute();
                            /*copy.setId(attr.getId());
                            copy.setAttributeTypeId(attr.getAttributeTypeId());
                            copy.setAttributeName(attr.getAttributeName());
                            copy.setAttributeCodePath(attr.getAttributeCodePath());
                            copy.setSortCode(attr.getSortCode());
                            copy.setIsHasChild(attr.getIsHasChild());
                            copy.setParentId(attr.getParentId());
                            copy.setAttributeEnName(attr.getAttributeEnName());
                            copy.setAttributeEnNamePath(attr.getAttributeEnNamePath());
                            copy.setAttributeCode(attr.getAttributeCode());
                            copy.setAttributeTypeName(attr.getAttributeTypeName());
                            copy.setIsDelete(attr.getIsDelete());
                            copy.setRemark(attr.getRemark());
                            copy.setCreateBy(attr.getCreateBy());
                            copy.setUpdateBy(attr.getUpdateBy());
                            copy.setCreateTime(attr.getCreateTime());
                            copy.setUpdateTime(attr.getUpdateTime());*/
                            BeanUtils.copyProperties(attr, copy);
                            // 倒序 attribute_name_path
                            String[] pathParts = attr.getAttributeNamePath().split("\\|");
                            List<String> pathList = new ArrayList<>(Arrays.asList(pathParts));
                            Collections.reverse(pathList);
                            String reversedPath = String.join("|", pathList);
                            copy.setAttributeNamePath(reversedPath);
                            leafNodes.add(copy);
                        });
            }
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("typeId", attrType.getId());
            resultMap.put("typeName", attrType.getTypeName());
            resultMap.put("data", leafNodes);
            resultList.add(resultMap);
        }

        return resultList;
    }

    @Override
    public List<String> queryProductAttributes(String productId) {
        return this.baseMapper.queryProductAttributes(productId);
    }

    // 递归构建树形结构
    private List<KolAttributeTreeModel> buildTree(Map<String, List<KolAttribute>> childrenMap, String parentId) {
        List<KolAttribute> children = childrenMap.getOrDefault(parentId, new ArrayList<>());
        children.sort(Comparator.comparing(KolAttribute::getSortCode));
        List<KolAttributeTreeModel> treeNodes = new ArrayList<>();
        for (KolAttribute child : children) {
            KolAttributeTreeModel node = new KolAttributeTreeModel();
            node.setId(child.getId());
            node.setAttributeName(child.getAttributeName());
            node.setSortCode(child.getSortCode());
            node.setIsHasChild(child.getIsHasChild());
            node.setAttributeNamePath(child.getAttributeNamePath());
            node.setAttributeTypeId(child.getAttributeTypeId());
            node.setChildren(buildTree(childrenMap, child.getId()));
            treeNodes.add(node);
        }
        return treeNodes;
    }

    // 递归收集叶子节点的 ID，保持顺序
    private void collectLeafNodeIds(KolAttributeTreeModel node, List<String> leafNodeIds) {
        if (node.getIsHasChild() == 0) {
            leafNodeIds.add(node.getId());
        } else {
            for (KolAttributeTreeModel child : node.getChildren()) {
                collectLeafNodeIds(child, leafNodeIds);
            }
        }
    }
}
