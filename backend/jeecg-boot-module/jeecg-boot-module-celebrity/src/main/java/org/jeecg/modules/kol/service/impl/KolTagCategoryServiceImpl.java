package org.jeecg.modules.kol.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.kol.model.TreeSelectModel;
import org.jeecg.common.util.FillRuleUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolTagCategory;
import org.jeecg.modules.kol.mapper.KolTagCategoryMapper;
import org.jeecg.modules.kol.service.IKolTagCategoryService;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class KolTagCategoryServiceImpl extends ServiceImpl<KolTagCategoryMapper, KolTagCategory> implements IKolTagCategoryService {

    @Lazy
    @Autowired
    private IKolTagsService tagsService;


    /**
     * 新增类目
     *
     * @param kolTagCategory
     */
    @Override
    public void addKolTagCategory(KolTagCategory kolTagCategory) {
        String categoryCode = "";
        String categoryPid = IKolTagCategoryService.ROOT_PID_VALUE;
        String parentNamePath = null;
        String parentCodePath = null;
        if (oConvertUtils.isNotEmpty(kolTagCategory.getParentId())) {
            categoryPid = kolTagCategory.getParentId();

            // PID 不是根节点 说明需要设置父节点 hasChild 为1
            if (!IKolTagCategoryService.ROOT_PID_VALUE.equals(categoryPid)) {
                KolTagCategory parent = baseMapper.selectById(categoryPid);
                if (parent != null) {
                    parentCodePath = parent.getCategoryCodePath();
                    parentNamePath = parent.getCategoryNamePath();
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
        categoryCode = (String) FillRuleUtil.executeRule("tag_category_code_rule", formData);
        // update-end--Author:baihailong  Date:20191209 for：分类字典编码规则生成器做成公用配置
        kolTagCategory.setCategoryCode(categoryCode);
        kolTagCategory.setParentId(categoryPid);
        kolTagCategory.setCategoryCodePath(parentCodePath == null ? categoryCode : parentCodePath + "/" + categoryCode);
        kolTagCategory.setCategoryNamePath(parentNamePath == null ? kolTagCategory.getCategoryName() : parentNamePath + "/" + kolTagCategory.getCategoryName());
        baseMapper.insert(kolTagCategory);
    }


    /**
     * 修改类目-需要考虑业务中冗余的名称同步更新
     *
     * @param kolTagCategory
     */
    @Override
    public void updateKolTagCategory(KolTagCategory kolTagCategory) {

        KolTagCategory entity = this.getById(kolTagCategory.getId());
        if (entity == null) {
            throw new JeecgBootException(QUERY_EMPTY);
        }
        if (!entity.getCategoryName().equals(kolTagCategory.getCategoryName())) {
            // 2025-5-12 16:38:30  增加判断类目名称是否发生改变
            // 判断类目名称是否存在
            KolTagCategory category = this.lambdaQuery().eq(KolTagCategory::getCategoryName, kolTagCategory.getCategoryName()).last("limit 1").one();
            if (category != null && !category.getId().equals(entity.getId())) {
                throw new JeecgBootException("类目名称已存在，请重新输入！");
            }
        }
        if (oConvertUtils.isNotEmpty(entity.getCategoryNamePath())) {
            kolTagCategory.setCategoryNamePath(entity.getCategoryNamePath().replace(entity.getCategoryName(), kolTagCategory.getCategoryName()));
        }
        String old_pid = entity.getParentId();
        String new_pid = kolTagCategory.getParentId();
        boolean isUpdateOld = false;
        boolean isUpdateBusinessCategoryName = false;
        // 如果父节点没有变化 则直接更新数据
        if (old_pid.equals(new_pid)) {
            if (!kolTagCategory.getCategoryName().equals(entity.getCategoryName())) {
                // 2024年11月24日 16:35:59  增加判断类目名称是否发生改变，如果改变 则应同步更新 tiktok_tags 表中的对应的类目名称
                // 同步更新 tiktok_tags 表中的对应的类目名称
                isUpdateBusinessCategoryName = true;
            } else {
                this.updateById(kolTagCategory);
                return;
            }

        } else {
            // 判断父id是否在子集内
            String categoryCodePath = entity.getCategoryCodePath();
            List<KolTagCategory> tagCategories = this.lambdaQuery().like(KolTagCategory::getCategoryCodePath, categoryCodePath).list();
            if (!tagCategories.isEmpty()) {
                String finalNewPid = new_pid;
                tagCategories.stream().map(KolTagCategory::getId).filter(x -> x.equals(finalNewPid)).findFirst().ifPresent(x -> {
                    throw new JeecgBootException("父类目错误，不能设置为子集！");
                });
            }
        }
        if (oConvertUtils.isEmpty(new_pid)) {
            new_pid = ROOT_PARENT_ID_VALUE;
            kolTagCategory.setParentId(new_pid);
        }

        // 如果为根节点 则无需处理
        if (!ROOT_PARENT_ID_VALUE.equals(old_pid)) {
            // 首先统计该父节点下的所有子节点
            Integer childCount = Math.toIntExact(this.count(new QueryWrapper<KolTagCategory>().lambda().eq(KolTagCategory::getParentId, old_pid)));
            isUpdateOld = (childCount == null || childCount <= 1);
        }


        updateWithTrans(kolTagCategory, old_pid, new_pid, isUpdateOld, isUpdateBusinessCategoryName);
    }


    /**
     * 事务更新方法
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateWithTrans(KolTagCategory entity, String oldParentId, String newParentId, boolean isUpdateOld, boolean isUpdateBusinessCategoryName) {
        // 如果类目名称发生变化 则更新tags表对应的类目名称
        if (isUpdateBusinessCategoryName) {
            LambdaUpdateWrapper<KolTags> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(KolTags::getCategoryId, entity.getId());
            wrapper.set(KolTags::getCategoryName, entity.getCategoryName());
            tagsService.update(wrapper);
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
        this.updateById(entity);

    }

    /**
     * 更新hasChild状态
     *
     * @param keyId
     * @param hasChildStatus
     */
    private void updateHasChild(String keyId, Integer hasChildStatus) {
        LambdaUpdateWrapper<KolTagCategory> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(KolTagCategory::getId, keyId);
        wrapper.set(KolTagCategory::getIsHasChild, hasChildStatus);
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
    public List<KolTagCategory> getTreeRootNodeList(Wrapper<KolTagCategory> queryWrapper) {
        List<KolTagCategory> nodeList = this.list(queryWrapper);
        List<KolTagCategory> rootNodeList = new ArrayList<>();
        nodeList.forEach(x -> {
            String parentId = x.getParentId();
            // 如果不为根节点 则递归查找父类 直至根节点
            // 此处算法应该可以优化 例如有重复交叉的父节点 可能存在重复查找的问题
            // 是否可以记录所有中间父节点 循环查找同时 判断是否存在重复的父节点 如有则直接调至下次循环
            // 2023年7月13日17:59:46 xyc
            if (parentId != null && !ROOT_PARENT_ID_VALUE.equals(parentId)) {
                KolTagCategory rootNode = this.getRootNode(parentId);
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
    private KolTagCategory getRootNode(String parentId) {
        KolTagCategory entity = this.getById(parentId);
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
        int tagCount = Math.toIntExact(tagsService.lambdaQuery()
                .in(KolTags::getCategoryId, categoryIdList)
                .count());
        if (tagCount > 0) {
            throw new JeecgBootException("标签业务表中存在引用，禁止删除");
        }
        List<String> allNodeIdList = getAllNodeIds(nodeIdList);
        List<String> updateNodeHasChildList = new ArrayList<>();
        nodeIdList.forEach(x -> {
            KolTagCategory examObjectiveTestCategory = this.getById(x);
            String parentId = examObjectiveTestCategory.getParentId();
            // 需要判断其父节点是否还有其他子节点 即兄弟节点 如有 则无需操作，若为独子或者其他兄弟节点也在删除列表 则应该更新其hasChild=0
            LambdaQueryWrapper<KolTagCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(KolTagCategory::getParentId, parentId);
            queryWrapper.notIn(KolTagCategory::getId, nodeIdList);
            int otherChildCount = (int) this.count(queryWrapper);
            // 如果没有其他子节点 则应更新hasChild=0
            if (otherChildCount == 0) {
                if (!updateNodeHasChildList.contains(parentId)) {
                    updateNodeHasChildList.add(parentId);
                }
            }
        });
        deleteNodeBatch(allNodeIdList, updateNodeHasChildList);
    }

    /**
     * 事务删除操作
     *
     * @param allNodeIdList
     * @param updateNodeHasChildList
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteNodeBatch(List<String> allNodeIdList, List<String> updateNodeHasChildList) {
        this.removeByIds(allNodeIdList);
        updateNodeHasChildList.forEach(x -> {
            updateHasChild(x, YesNoStatus.NO.getCode());
        });
    }

    /**
     * 递归获取所有节点id列表
     *
     * @param parentIdList
     * @return
     */
    private List<String> getAllNodeIds(List<String> parentIdList) {
        List<String> allNodeIdList = new ArrayList<>();
        allNodeIdList.addAll(parentIdList);
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
        LambdaQueryWrapper<KolTagCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KolTagCategory::getParentId, parentId);
        List<String> nodeIdList = this.list(queryWrapper).stream().map(x -> x.getId()).collect(Collectors.toList());
        if (oConvertUtils.isNotEmpty(nodeIdList)) {
            allNodeIdList.addAll(nodeIdList);
            nodeIdList.forEach(x -> {
                getSubNodeIds(x, allNodeIdList);
            });
        }
    }

}
