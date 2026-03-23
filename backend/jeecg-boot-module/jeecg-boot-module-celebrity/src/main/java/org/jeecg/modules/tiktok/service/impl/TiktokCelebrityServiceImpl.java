package org.jeecg.modules.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.model.*;
import org.jeecg.modules.kol.service.IKolCelebrityService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrity;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTags;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityTagsCounselor;
import org.jeecg.modules.tiktok.mapper.TiktokCelebrityMapper;
import org.jeecg.modules.tiktok.model.TkCelebrityModel;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityTagsService;
import org.jeecg.modules.tiktok.service.ITkCelebrityTagsCounselorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: tiktok网红表
 * @Author: xyc
 * @Date: 2024-12-23 17:22:44
 * @Version: V1.1
 * @History: V1.1 - [2024-12-23] - [根据实际业务，新增方法替换原有方法，测试通过后 将删除历史不规范的命名方法] - [xyc]
 */

@Service
public class TiktokCelebrityServiceImpl extends ServiceImpl<TiktokCelebrityMapper, TiktokCelebrity> implements ITiktokCelebrityService {
    @Autowired
    private IKolCelebrityService kolCelebrityService;
    @Autowired
    private ITiktokCelebrityTagsService tkCelebrityTagsService;
    @Autowired
    private ITkCelebrityTagsCounselorService tkCelebrityTagsCounselorService;

    @Override
    public TiktokCelebrity getUniqueId(String uniqueId) {
        LambdaQueryWrapper<TiktokCelebrity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TiktokCelebrity::getUniqueId, uniqueId);
        List<TiktokCelebrity> lists = baseMapper.selectList(queryWrapper);
        return lists.stream().findFirst().orElse(null);
    }

    /**
     * tk网红总数
     *
     * @param searchModel
     * @return
     */
    @Override
    public Long getTkCelebrityCount(KolSearchModel searchModel) {
        return this.baseMapper.getTkCelebrityCount(searchModel);
    }

    /**
     * 获取tk网红列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    @Override
    public IPage<TkCelebrityModel> getTkCelebrityModelList(Page<TkCelebrityModel> page, KolSearchModel searchModel) {
        return this.baseMapper.getTkCelebrityModelList(page, searchModel);
    }

    /**
     * 查询已分配网红总数
     *
     * @param searchModel
     * @return
     */
    @Override
    public Integer getTkAllottedKolCount(KolSearchModel searchModel) {
        return this.baseMapper.getTkAllottedKolCount(searchModel);
    }

    /**
     * 查询已分配网红列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    @Override
    public IPage<TkCelebrityModel> getTkAllottedKolList(Page<TkCelebrityModel> page, KolSearchModel searchModel) {
        return this.baseMapper.getTkAllottedKolList(page, searchModel);
    }

    /**
     * 查询已分配网红总数
     *
     * @param searchModel
     * @return
     */
    @Override
    public Integer getAllottedKolCount(KolSearchModel searchModel) {
        Integer kolCount = 0;
        try {
            kolCelebrityService.createTempTable(searchModel);
            kolCount = this.baseMapper.getTkAllottedKolCount(searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        return kolCount;
    }

    /**
     * 查询已分配网红列表
     *
     * @param page
     * @param searchModel
     * @return 返回通用分页结果
     */
    @Override
    public IPage<KolBaseModel> getAllottedKolList(Page<KolBaseModel> page, KolSearchModel searchModel) {
        return baseMapper.getAllottedKolList(page, searchModel);
    }

    @Override
    public Integer getCelebrityCount(KolSearchModel searchModel) {
        return this.getTkAllottedKolCount(searchModel);
    }

    @Override
    public Integer getCelebrityScreeningCount(KolSearchModel searchModel) {
        Integer allottedKolCount = 0;
        try {
            kolCelebrityService.createTempTable(searchModel);
            allottedKolCount = baseMapper.getCelebrityScreeningCount(searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        return allottedKolCount;
    }


    /**
     * 从网红标签表中获取标签信息
     * 根据网红账号分组聚合标签列表
     *
     * @param uniqueIdList 网红id列表
     * @param kolModelList 网红模型列表，用于获取用户名并更新标签信息
     */
    public void updateKolTagList_back(List<String> uniqueIdList, List<KolBaseModel> kolModelList, KolSearchModel searchModel) {
        // 创建查询条件，配置部分必要字段
        LambdaQueryWrapper<TiktokCelebrityTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(TiktokCelebrityTags::getUniqueId,
                TiktokCelebrityTags::getAuthorId,
                TiktokCelebrityTags::getTagName,
                TiktokCelebrityTags::getIsAllot,
                TiktokCelebrityTags::getTagType,
                TiktokCelebrityTags::getAllotTime);
        queryWrapper.in(TiktokCelebrityTags::getAuthorId, uniqueIdList);
        if (oConvertUtils.isNotEmpty(searchModel.getCountryCode())) {
            queryWrapper.eq(TiktokCelebrityTags::getCountry, searchModel.getCountryCode());
        }
        // 获取网标签列表
        List<TiktokCelebrityTags> celebrityTagsList = tkCelebrityTagsService.list(queryWrapper);

        // 先按照IgUsername对IgCelebrityTags进行分组，得到以IgUsername为键，对应IgCelebrityTags对象列表为值的Map
        Map<String, List<TiktokCelebrityTags>> tagsGroupList = celebrityTagsList.stream()
                .collect(Collectors.groupingBy(TiktokCelebrityTags::getAuthorId));

        // 用于存储最终结果，以IgUsername为键，对应IgTagModel对象列表为值的Map
        Map<String, List<KolAllotTagModel>> kolTagMap = new HashMap<>();

        // 遍历分组后的Map，将每个分组中的IgCelebrityTags元素转换为IgTagModel元素
        tagsGroupList.forEach((username, tagsList) -> {
            List<KolAllotTagModel> kolAllotTagModelList = tagsList.stream()
                    .map(tag -> {
                        KolAllotTagModel kolAllotTagModel = new KolAllotTagModel();
                        kolAllotTagModel.setTagName(tag.getTagName());
                        kolAllotTagModel.setIsAllot(tag.getIsAllot());
                        kolAllotTagModel.setAllotTime(tag.getAllotTime());
                        kolAllotTagModel.setTagType(tag.getTagType());
                        return kolAllotTagModel;
                    })
                    .collect(Collectors.toList());
            kolTagMap.put(username, kolAllotTagModelList);
        });

        // 遍历网红模型列表，为每个网红模型添加对应的标签信息（假设x.getUniqueId()获取的就是用户名用于匹配）
        kolModelList.forEach(x -> {
            List<KolAllotTagModel> tagModelList = kolTagMap.getOrDefault(x.getAuthorId(), Collections.emptyList());
            if (oConvertUtils.listIsNotEmpty(tagModelList)) {
                x.setTagList(tagModelList);
            }
        });
    }

    @Override
    public void updateKolTagList(List<String> uniqueIdList, List<KolBaseModel> kolModelList, KolSearchModel searchModel) {

        List<TiktokCelebrityTagsCounselor> celebrityTagsList = new ArrayList<>();
        String tempTableName = searchModel.getTempTableName();
        try {
            kolCelebrityService.createUniqueIdTempTable(tempTableName, uniqueIdList);
            celebrityTagsList = tkCelebrityTagsCounselorService.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kolCelebrityService.dropTempTable(tempTableName);
        }

        // 先按照IgUsername对IgCelebrityTags进行分组，得到以IgUsername为键，对应IgCelebrityTags对象列表为值的Map
        Map<String, List<TiktokCelebrityTagsCounselor>> tagsGroupList = celebrityTagsList.stream()
                .collect(Collectors.groupingBy(TiktokCelebrityTagsCounselor::getAuthorId));

        // 用于存储最终结果，以IgUsername为键，对应IgTagModel对象列表为值的Map
        Map<String, List<KolAllotTagModel>> kolTagMap = new HashMap<>();

        // 遍历分组后的Map，将每个分组中的IgCelebrityTags元素转换为IgTagModel元素
        tagsGroupList.forEach((username, tagsList) -> {
            List<KolAllotTagModel> kolAllotTagModelList = tagsList.stream()
                    .map(tag -> {
                        KolAllotTagModel kolAllotTagModel = new KolAllotTagModel();
                        kolAllotTagModel.setTagName(tag.getTagName());
                        kolAllotTagModel.setIsAllot(1);
                        kolAllotTagModel.setAllotTime(tag.getAllotTime());
                        kolAllotTagModel.setTagType(tag.getTagType());
                        return kolAllotTagModel;
                    })
                    .collect(Collectors.toList());
            kolTagMap.put(username, kolAllotTagModelList);
        });

        // 遍历网红模型列表，为每个网红模型添加对应的标签信息（假设x.getUniqueId()获取的就是用户名用于匹配）
        kolModelList.forEach(x -> {
            List<KolAllotTagModel> tagModelList = kolTagMap.getOrDefault(x.getAuthorId(), Collections.emptyList());
            if (oConvertUtils.listIsNotEmpty(tagModelList)) {
                List<KolAllotTagModel> uniqueByTagName = tagModelList.stream()
                        .filter(Objects::nonNull) // 过滤 null 元素
                        .filter(tag -> tag.getTagName() != null) // 过滤 tagName 为 null 的对象
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        KolAllotTagModel::getTagName, // 以 tagName 为 key
                                        Function.identity(),          // 保留原对象
                                        (existing, replacement) -> existing // 冲突时保留第一个
                                ),
                                map -> new ArrayList<>(map.values())
                        ));
                x.setTagList(uniqueByTagName);
            }
        });
    }

    @Override
    public List<KolAllotExcelModel> getAllotListByIds(List<String> selectionList) {
        if (oConvertUtils.isEmpty(selectionList)) {
            return Collections.emptyList();
        }
        List<TiktokCelebrity> celebrities = this.lambdaQuery().in(TiktokCelebrity::getId, selectionList).orderByDesc(TiktokCelebrity::getCreateTime).list();
        return celebrities.stream().map(celebrity -> {
            KolAllotExcelModel celebrityModel = new KolAllotExcelModel();
            BeanUtils.copyProperties(celebrity, celebrityModel);
            celebrityModel.setAccount(celebrity.getUniqueId());
            celebrityModel.setNickname(celebrity.getNickname());
            celebrityModel.setAuthorFollowerCountStr(oConvertUtils.formatLargeNumber(celebrity.getAuthorFollowerCount()));
            celebrityModel.setBioLink("https://www.tiktok.com/@" + celebrity.getUniqueId());
            return celebrityModel;
        }).collect(Collectors.toList());
    }

    @Override
    public IPage<KolCelebrityModel> getCelebrityScreeningList(Page<KolCelebrityModel> page, KolSearchModel searchModel) {
        return this.baseMapper.getCelebrityScreeningList(page, searchModel);

    }

    /**
     * 批量更新网红模型的标签信息
     * 优化版本：提高处理速度，减少内存使用，保留原有注释代码
     * 
     * @param uniqueIdList 网红唯一ID列表
     * @param kolModelList 需要更新标签的网红模型列表
     * @param searchModel 搜索模型，包含临时表名等查询条件
     */
    @Override
    public void updateKolTagListNew(List<String> uniqueIdList, List<KolCelebrityModel> kolModelList, KolSearchModel searchModel) {
        // 如果输入列表为空或网红模型列表为空，直接返回，避免不必要的数据库操作
        if (oConvertUtils.listIsEmpty(uniqueIdList) || oConvertUtils.listIsEmpty(kolModelList)) {
            return;
        }
        
        // 预先创建网红ID到模型的映射，避免后续的查找复杂度
        Map<String, KolCelebrityModel> kolModelMap = kolModelList.stream()
                .collect(Collectors.toMap(KolCelebrityModel::getAuthorId, model -> model, (existing, replacement) -> existing));
        
        // 初始化标签列表容器
        List<TiktokCelebrityTags> celebrityTagsList = new ArrayList<>();
        String tempTableName = searchModel.getTempTableName();
        
        try {
            // 创建临时表并查询标签数据
            kolCelebrityService.createUniqueIdTempTable(tempTableName, uniqueIdList);
            celebrityTagsList = tkCelebrityTagsService.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
        } catch (Exception e) {
            System.out.println("查询网红标签数据异常: " + e.getMessage());
        } finally {
            // 确保临时表被清理，防止数据库资源泄漏
            kolCelebrityService.dropTempTable(tempTableName);
        }
        
        // 如果查询结果为空，直接返回，避免后续无意义的处理
        if (oConvertUtils.listIsEmpty(celebrityTagsList)) {
            return;
        }

        // 按照AuthorId对TiktokCelebrityTags进行分组，得到以AuthorId为键，对应标签列表为值的Map
        Map<String, List<KolAllotTagModel>> kolTagMap = celebrityTagsList.parallelStream()
                .collect(Collectors.groupingBy(
                    TiktokCelebrityTags::getAuthorId,
                    Collectors.mapping(tag -> {
                        // 【优化6】直接在mapping中创建KolAllotTagModel，减少中间集合的创建
                        KolAllotTagModel kolAllotTagModel = new KolAllotTagModel();
                        kolAllotTagModel.setTagName(tag.getTagName());
                        kolAllotTagModel.setIsAllot(tag.getIsAllot());
                        kolAllotTagModel.setAllotTime(tag.getAllotTime());
                        kolAllotTagModel.setTagType(tag.getTagType());
                        return kolAllotTagModel;
                    }, Collectors.toList())
                ));

        // 只遍历有标签数据的网红，避免无效的查找操作
        // 为网红模型添加对应的标签信息
        kolTagMap.forEach((authorId, tagModelList) -> {
            KolCelebrityModel kolModel = kolModelMap.get(authorId);
            if (oConvertUtils.listIsNotEmpty(tagModelList)) {
                List<KolAllotTagModel> uniqueByTagName = tagModelList.stream()
                        .filter(Objects::nonNull) // 过滤 null 元素
                        .filter(tag -> tag.getTagName() != null) // 过滤 tagName 为 null 的对象
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        KolAllotTagModel::getTagName, // 以 tagName 为 key
                                        Function.identity(),          // 保留原对象
                                        (existing, replacement) -> existing // 冲突时保留第一个
                                ),
                                map -> new ArrayList<>(map.values())
                        ));
                kolModel.setTagList(uniqueByTagName);
            }
        });
    }

    @Override
    public IPage<KolBaseModel> getAllottedKolListNew(Page<KolBaseModel> page, KolSearchModel searchModel) {
        kolCelebrityService.createTempTable(searchModel);
        IPage<KolBaseModel> kolBaseModelIPage = new Page<>();
        Integer allottedKolCount = 0;
        try {
            kolBaseModelIPage = this.baseMapper.getAllottedKolList(page, searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // 设置总数
            kolBaseModelIPage.setTotal(allottedKolCount);
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        return kolBaseModelIPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<KolCelebrityModel> getCelebrityScreeningListNew(Page<KolCelebrityModel> page, KolSearchModel searchModel) {
        kolCelebrityService.createTempTable(searchModel);
        IPage<KolCelebrityModel> pageList = new Page<>();
        Integer allottedKolCount = 5000;
        try {
            pageList = this.baseMapper.getCelebrityScreeningList(page, searchModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            pageList.setTotal(allottedKolCount);
            kolCelebrityService.dropTempTable(searchModel.getTempTableName());
        }
        return pageList;
    }
}
