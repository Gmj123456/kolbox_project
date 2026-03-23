package org.jeecg.modules.youtube.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.model.*;
import org.jeecg.modules.kol.service.IKolCelebrityService;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTags;
import org.jeecg.modules.youtube.entity.YoutubeCelebrityTagsCounselor;
import org.jeecg.modules.youtube.mapper.YtCelebrityMapper;
import org.jeecg.modules.youtube.model.YtCelebrityModel;
import org.jeecg.modules.youtube.service.IYtCelebrityService;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsCounselorService;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: YT网红接口实现层
 * @Author: xyc
 * @Date: 2024-12-26 17:27:31
 * @Version: V1.0
 */
@Service
public class YtCelebrityServiceImpl extends ServiceImpl<YtCelebrityMapper, YoutubeCelebrity> implements IYtCelebrityService {

    @Autowired
    private IYtCelebrityTagsService ytCelebrityTagsService;
    @Autowired
    private IKolCelebrityService kolCelebrityService;
    @Autowired
    private IYtCelebrityTagsCounselorService ytCelebrityTagsCounselorService;

    /**
     * tk网红总数
     *
     * @param searchModel
     * @return
     */
    @Override
    public Integer getCelebrityCount(KolSearchModel searchModel) {
        return baseMapper.getCelebrityCount(searchModel);
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
     * 获取tk网红列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    @Override
    public IPage<YtCelebrityModel> getCelebrityModelList(Page<YtCelebrityModel> page, KolSearchModel searchModel) {
        return baseMapper.getCelebrityModelList(page, searchModel);
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
            kolCount = this.baseMapper.getAllottedKolCount(searchModel);
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
     * @return
     */
    @Override
    public IPage<YtCelebrityModel> getYtAllottedKolList(Page<YtCelebrityModel> page, KolSearchModel searchModel) {
        return baseMapper.getYtAllottedKolList(page, searchModel);
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

    /**
     * 从网红标签表中获取标签信息
     * 根据网红账号分组聚合标签列表
     *
     * @param uniqueIdList 网红id列表
     * @param kolModelList 网红模型列表，用于获取用户名并更新标签信息
     */
    public void updateKolTagList_back(List<String> uniqueIdList, List<KolBaseModel> kolModelList, KolSearchModel searchModel) {
        // 创建查询条件，配置部分必要字段
        LambdaQueryWrapper<YoutubeCelebrityTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(YoutubeCelebrityTags::getAccount, YoutubeCelebrityTags::getTagName, YoutubeCelebrityTags::getIsAllot, YoutubeCelebrityTags::getTagType,
                YoutubeCelebrityTags::getAllotTime);
        queryWrapper.in(YoutubeCelebrityTags::getAccount, uniqueIdList);
        if (oConvertUtils.isNotEmpty(searchModel.getCountryCode())) {
            queryWrapper.eq(YoutubeCelebrityTags::getCountry, searchModel.getCountryCode());
        }
        // 获取网标签列表
        List<YoutubeCelebrityTags> celebrityTagsList = ytCelebrityTagsService.list(queryWrapper);

        // 先按照IgUsername对IgCelebrityTags进行分组，得到以IgUsername为键，对应IgCelebrityTags对象列表为值的Map
        Map<String, List<YoutubeCelebrityTags>> tagsGroupList = celebrityTagsList.stream().collect(Collectors.groupingBy(YoutubeCelebrityTags::getAccount));

        // 用于存储最终结果，以IgUsername为键，对应IgTagModel对象列表为值的Map
        Map<String, List<KolAllotTagModel>> kolTagMap = new HashMap<>();

        // 遍历分组后的Map，将每个分组中的IgCelebrityTags元素转换为IgTagModel元素
        tagsGroupList.forEach((username, tagsList) -> {
            List<KolAllotTagModel> kolAllotTagModelList = tagsList.stream().map(tag -> {
                KolAllotTagModel kolAllotTagModel = new KolAllotTagModel();
                kolAllotTagModel.setTagName(tag.getTagName());
                kolAllotTagModel.setIsAllot(tag.getIsAllot());
                kolAllotTagModel.setAllotTime(tag.getAllotTime());
                kolAllotTagModel.setTagType(tag.getTagType());
                return kolAllotTagModel;
            }).collect(Collectors.toList());
            kolTagMap.put(username, kolAllotTagModelList);
        });

        // 遍历网红模型列表，为每个网红模型添加对应的标签信息（假设x.getUniqueId()获取的就是用户名用于匹配）
        kolModelList.forEach(x -> {
            List<KolAllotTagModel> tagModelList = kolTagMap.getOrDefault(x.getUniqueId(), Collections.emptyList());
            if (x.getPlatformType() == 1) {
                tagModelList = kolTagMap.getOrDefault(x.getAccount(), Collections.emptyList());
            }
            if (oConvertUtils.listIsNotEmpty(tagModelList)) {
                x.setTagList(tagModelList);
            }

        });
    }

    @Override
    public void updateKolTagList(List<String> uniqueIdList, List<KolBaseModel> kolModelList, KolSearchModel searchModel) {
        List<YoutubeCelebrityTagsCounselor> celebrityTagsList = new ArrayList<>();
        String tempTableName = searchModel.getTempTableName();
        try {
            kolCelebrityService.createUniqueIdTempTable(tempTableName, uniqueIdList);
            celebrityTagsList = ytCelebrityTagsCounselorService.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kolCelebrityService.dropTempTable(tempTableName);
        }

        // 先按照IgUsername对IgCelebrityTags进行分组，得到以IgUsername为键，对应IgCelebrityTags对象列表为值的Map
        Map<String, List<YoutubeCelebrityTagsCounselor>> tagsGroupList = celebrityTagsList.stream().collect(Collectors.groupingBy(YoutubeCelebrityTagsCounselor::getAccount));

        // 用于存储最终结果，以IgUsername为键，对应IgTagModel对象列表为值的Map
        Map<String, List<KolAllotTagModel>> kolTagMap = new HashMap<>();

        // 遍历分组后的Map，将每个分组中的IgCelebrityTags元素转换为IgTagModel元素
        tagsGroupList.forEach((username, tagsList) -> {
            List<KolAllotTagModel> kolAllotTagModelList = tagsList.stream().map(tag -> {
                KolAllotTagModel kolAllotTagModel = new KolAllotTagModel();
                kolAllotTagModel.setTagName(tag.getTagName());
                kolAllotTagModel.setIsAllot(1);
                kolAllotTagModel.setAllotTime(tag.getAllotTime());
                kolAllotTagModel.setTagType(tag.getTagType());
                return kolAllotTagModel;
            }).collect(Collectors.toList());
            kolTagMap.put(username, kolAllotTagModelList);
        });

        // 遍历网红模型列表，为每个网红模型添加对应的标签信息（假设x.getUniqueId()获取的就是用户名用于匹配）
        kolModelList.forEach(x -> {
            List<KolAllotTagModel> tagModelList = kolTagMap.getOrDefault(x.getUniqueId(), Collections.emptyList());
            if (x.getPlatformType() == 1) {
                tagModelList = kolTagMap.getOrDefault(x.getAccount(), Collections.emptyList());
            }
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

    /**
     * 方法描述: 获取未分配完标签网红数量
     *
     * @author nqr
     * @date 2025/01/06 15:03
     **/
    @Override
    public IPage<Map<String, Object>> getNotAllotTagsList(Page<YtCelebrityModel> page, KolSearchModel kolSearchModel) {
        return baseMapper.getNotAllotTagsList(page, kolSearchModel);
    }

    @Override
    public YoutubeCelebrity getByAccount(String uniqueId) {
        LambdaQueryWrapper<YoutubeCelebrity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(YoutubeCelebrity::getUsername, uniqueId);
        return this.list(queryWrapper).stream().findFirst().orElse(null);
    }


    @Override
    public List<KolAllotExcelModel> getAllotListByIds(List<String> selectionList) {
        if (oConvertUtils.isEmpty(selectionList)) {
            return Collections.emptyList();
        }
        List<YoutubeCelebrity> celebrities = this.lambdaQuery().in(YoutubeCelebrity::getId, selectionList).orderByDesc(YoutubeCelebrity::getCreateTime).list();
        return celebrities.stream().map(celebrity -> {
            KolAllotExcelModel celebrityModel = new KolAllotExcelModel();
            BeanUtils.copyProperties(celebrity, celebrityModel);
            celebrityModel.setAccount(celebrity.getUsername());
            celebrityModel.setUniqueId(celebrity.getAccount());
            celebrityModel.setNickname(celebrity.getNickname());
            celebrityModel.setAuthorFollowerCountStr(oConvertUtils.formatLargeNumber(Math.toIntExact(celebrity.getFollowersNum())));
            celebrityModel.setBioLink("https://www.youtube.com/@" + celebrity.getUsername());
            return celebrityModel;
        }).collect(Collectors.toList());
    }

    @Override
    public int getNotAllotTagsListCount(KolSearchModel kolSearchModel) {
        return this.baseMapper.getNotAllotTagsListCount(kolSearchModel);
    }

    @Override
    public IPage<KolCelebrityModel> getCelebrityScreeningList(Page<KolCelebrityModel> page, KolSearchModel searchModel) {
        return this.baseMapper.getCelebrityScreeningList(page, searchModel);

    }

    @Override
    public void updateKolTagListNew(List<String> uniqueIdList, List<KolCelebrityModel> kolModelList, KolSearchModel searchModel) {
        List<YoutubeCelebrityTags> celebrityTagsList = new ArrayList<>();
        String tempTableName = searchModel.getTempTableName();
        try {
            kolCelebrityService.createUniqueIdTempTable(tempTableName, uniqueIdList);
            celebrityTagsList = ytCelebrityTagsService.getCelebrityTagsList(searchModel, uniqueIdList, tempTableName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kolCelebrityService.dropTempTable(tempTableName);
        }

        // 先按照IgUsername对IgCelebrityTags进行分组，得到以IgUsername为键，对应IgCelebrityTags对象
        // 列表为值的Map
        Map<String, List<YoutubeCelebrityTags>> tagsGroupList = celebrityTagsList.stream().collect(Collectors.groupingBy(YoutubeCelebrityTags::getAccount));

        // 用于存储最终结果，以IgUsername为键，对应IgTagModel对象列表为值的Map
        Map<String, List<KolAllotTagModel>> kolTagMap = new HashMap<>();

        // 遍历分组后的Map，将每个分组中的IgCelebrityTags元素转换为IgTagModel元素
        tagsGroupList.forEach((username, tagsList) -> {
            List<KolAllotTagModel> kolAllotTagModelList = tagsList.stream().map(tag -> {
                KolAllotTagModel kolAllotTagModel = new KolAllotTagModel();
                kolAllotTagModel.setTagName(tag.getTagName());
                kolAllotTagModel.setIsAllot(tag.getIsAllot());
                kolAllotTagModel.setAllotTime(tag.getAllotTime());
                kolAllotTagModel.setTagType(tag.getTagType());
                return kolAllotTagModel;
            }).collect(Collectors.toList());
            kolTagMap.put(username, kolAllotTagModelList);
        });

        // 遍历网红模型列表，为每个网红模型添加对应的标签信息（假设x.getUniqueId()获取的就是用户名用于匹配）
        kolModelList.forEach(x -> {
            List<KolAllotTagModel> tagModelList = kolTagMap.getOrDefault(x.getAccount(), Collections.emptyList());
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
        Integer allottedKolCount = 0;
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
