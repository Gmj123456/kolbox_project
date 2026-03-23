package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.PromotionGoodsType;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsService;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoodsCelebrity;
import org.jeecg.modules.instagram.storepromotion.mapper.StorePromotionGoodsCelebrityMapper;
import org.jeecg.modules.instagram.storepromotion.model.GoodsCelebrityModel;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsCelebrityModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsCelebrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.CelebrityPromStatus.PROM_STATUS_3;
import static org.jeecg.common.constant.CelebrityPromStatus.PROM_STATUS_FINISH;

/**
 * @Description: 商家推广产品网红关联表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Service
public class StorePromotionGoodsCelebrityServiceImpl extends ServiceImpl<StorePromotionGoodsCelebrityMapper, StorePromotionGoodsCelebrity> implements IStorePromotionGoodsCelebrityService {

    @Autowired
    private StorePromotionGoodsCelebrityMapper storePromotionGoodsCelebrityMapper;
    @Autowired
    private IStorePromotionGoodsService storePromotionGoodsService;
    @Autowired
    private IStoreCelebrityPrivateService storeCelebrityPrivateService;
    @Autowired
    private IStoreSellerPromotionService storeSellerPromotionService;

    /**
     * 功能描述: 删除该需求下的网红
     *
     * @Author: nqr
     * @Date 2021-02-08 17:03:58
     */
    @Override
    public void delProm(String id, List<String> goodsIdList) {
        storePromotionGoodsCelebrityMapper.delProm(id, goodsIdList);
    }

    /**
     * 编辑私有网红
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStorePromotionGoodsCelebrityById(StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel) {
        // 当前产品id
        String goodsId = storePromotionGoodsCelebrityModel.getGoodsId();
        // 当前推广网红id
        String goodsCelebrityId = storePromotionGoodsCelebrityModel.getId();
        // 当前推广id
        String promId = storePromotionGoodsCelebrityModel.getPromId();
        // 需要修改的状态
        Integer celebrityPromStatus = storePromotionGoodsCelebrityModel.getCelebrityPromStatus();
        // 判断网红状态，如果已经填写订单号修改状态为待上线
        if (oConvertUtils.isNotEmpty(storePromotionGoodsCelebrityModel.getGoodsWaybill()) && celebrityPromStatus == YesNoStatus.NO.getCode()) {
            storePromotionGoodsCelebrityModel.setCelebrityPromStatus(YesNoStatus.YES.getCode());
        }
        // 修改网红粉丝数、平均点赞数、平均互动率、平均观看量、总视频数、总点赞量
        StoreCelebrityPrivate storeCelebrityPrivate = new StoreCelebrityPrivate();
        storeCelebrityPrivate.setId(storePromotionGoodsCelebrityModel.getCelebrityPrivateId());
        storeCelebrityPrivate.setFollowersNum(storePromotionGoodsCelebrityModel.getFollowersNum());
        storeCelebrityPrivate.setLikeAvgNum(storePromotionGoodsCelebrityModel.getLikeAvgNum());
        storeCelebrityPrivate.setInteractAvgNum(storePromotionGoodsCelebrityModel.getInteractAvgNum());
        storeCelebrityPrivate.setPlayAvgNum(BigInteger.valueOf(storePromotionGoodsCelebrityModel.getPlayAvgNum()));
        storeCelebrityPrivate.setVideoCount(storePromotionGoodsCelebrityModel.getVideoCount());
        storeCelebrityPrivate.setLikeCount(storePromotionGoodsCelebrityModel.getLikeCount());
        storeCelebrityPrivateService.updateById(storeCelebrityPrivate);
        // 修改网红状态
        storePromotionGoodsCelebrityMapper.updateById(storePromotionGoodsCelebrityModel);
        // 获取当前产品下其余网红列表
        LambdaQueryWrapper<StorePromotionGoodsCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getGoodsId, goodsId);
        lambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getIsDel, YesNoStatus.NO.getCode());
        List<StorePromotionGoodsCelebrity> celebrityList = this.list(lambdaQueryWrapper);
        // 判断其余网红状态
        StorePromotionGoods storePromotionGoodsNew = checkGoodsStatus(celebrityList, celebrityPromStatus, goodsCelebrityId);
        storePromotionGoodsNew.setId(goodsId);
        // 修改产品状态
        storePromotionGoodsService.updateById(storePromotionGoodsNew);
        storePromotionGoodsNew.setPromId(promId);
        // 判断所有产品状态，修改推广状态
        storePromotionGoodsService.updateGoodsStatus(storePromotionGoodsNew, false);
    }


    /**
     * 删除匹配网红,应付金额改变
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> handleDelete(StorePromotionGoodsCelebrity storePromotionGoodsCelebrity) {
        /*
        1、判断当前网红所对应的产品下是否有其他网红，判断其他网红修改产品状态
        2、判断当前产品对应的推广需求下是否有其他产品，判断其他产品状态，修改推广需求状态
        3、 根据网红id删除网红
        */
        String goodsId = storePromotionGoodsCelebrity.getGoodsId();
        String promId = storePromotionGoodsCelebrity.getPromId();
        String ids = storePromotionGoodsCelebrity.getId() + ",";
        List<StorePromotionGoods> goodsList = delCelebrity(goodsId, promId, ids);
        for (StorePromotionGoods storePromotionGoods : goodsList) {
            // 修改产品状态
            storePromotionGoodsService.updateById(storePromotionGoods);
            // 判断所有产品状态，修改推广状态
            storePromotionGoodsService.updateGoodsStatus(storePromotionGoods, false);
        }
        // 根据网红id删除网红
        updateCelebrityStatus(Arrays.asList(ids.split(",")));
        return Result.ok("删除成功");
    }

    @Override
    public List<StorePromotionGoodsCelebrity> queryCelebritiesById(String id) {
        return storePromotionGoodsCelebrityMapper.queryCelebritiesById(id);
    }

    /**
     * 功能描述:判断产品状态
     *
     * @Author: nqr
     * @Date 2021-03-13 09:08:47
     */
    private StorePromotionGoods checkGoodsStatus(List<StorePromotionGoodsCelebrity> celebrityList, Integer celebrityPromStatus, String goodsCelebrityId) {
        StorePromotionGoods storePromotionGoodsNew = new StorePromotionGoods();
        // 获取其余网红
        List<StorePromotionGoodsCelebrity> otherCelebrityList = celebrityList.stream().filter(x -> !x.getCelebrityPrivateId().equals(goodsCelebrityId)).collect(Collectors.toList());
        // 获取其余网红状态
        List<Integer> celebrityStatusList = otherCelebrityList.stream().map(StorePromotionGoodsCelebrity::getCelebrityPromStatus).collect(Collectors.toList());
        // 进行中标识
        boolean underwayFlag = false;
        // 正常结束标识
        boolean ednormalFlag = false;
        // 异常结束标识
        boolean edabnormalFlag = false;
        // 判断推广网红是否只有一个
        if (otherCelebrityList.size() > 1) {
            // 获取其余网红未完成的状态
            List<Integer> otherStatus = celebrityStatusList.stream().filter(x -> x != YesNoStatus.FINISH.getCode() && x != YesNoStatus.Exception.getCode()).collect(Collectors.toList());
            // 表示有未完成的
            if (otherStatus.size() > 0) {
                // 修改产品状态为进行中
                underwayFlag = true;
            } else {
                // 获取异常完成的状态列表
                List<Integer> statusException = celebrityStatusList.stream().filter(x -> x == YesNoStatus.Exception.getCode()).collect(Collectors.toList());
                // 判断当前状态,如果为终止或者完成，则修改产品状态为已结束（异常）
                if (celebrityPromStatus != YesNoStatus.Exception.getCode() && celebrityPromStatus != YesNoStatus.FINISH.getCode()) {
                    // 当前网红状态未结束,修改产品状态为进行中
                    underwayFlag = true;
                } else {
                    if (statusException.size() > 0) {
                        // 将产品状态改为已结束（异常）
                        edabnormalFlag = true;
                    } else {
                        // 将产品状态改为已结束（正常）
                        ednormalFlag = true;
                    }
                }
            }
        } else {
            // 当前产品下只有一个网红时，判断需要修改的状态
            if (celebrityPromStatus == YesNoStatus.FINISH.getCode()) {
                // 将产品状态改为已结束（正常）
                ednormalFlag = true;
            } else if (celebrityPromStatus == YesNoStatus.Exception.getCode()) {
                // 将产品状态改为已结束（异常）
                edabnormalFlag = true;
            } else {
                // 修改产品状态为进行中
                underwayFlag = true;
            }
        }
        // 将产品状态改为进行中
        if (underwayFlag) {
            storePromotionGoodsNew.setGoodsStatus(PromotionGoodsType.UNDERWAY.getCode());
        }
        // 将产品状态改为正常结束
        if (ednormalFlag) {
            storePromotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
        }
        // 将产品状态改为已结束（异常）
        if (edabnormalFlag) {
            storePromotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
        }
        return storePromotionGoodsNew;
    }

    /**
     * 功能描述:批量删除匹配网红
     *
     * @param ids
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-06-29 15:03:17
     */
    @Override
    public void delCelebrityBatch(String ids) {
        // 根据id获取产品网红信息
        List<String> goodsCelebritieslIdList = Arrays.asList(ids.split(","));
        if (!goodsCelebritieslIdList.isEmpty()) {
            String id = goodsCelebritieslIdList.get(0);
            StorePromotionGoodsCelebrity goodsCelebrity = getById(id);
            if (oConvertUtils.isNotEmpty(goodsCelebrity)) {
                String goodsId = goodsCelebrity.getGoodsId();
                String promId = goodsCelebrity.getPromId();
                List<StorePromotionGoods> goodsList = delCelebrity(goodsId, promId, ids);
                for (StorePromotionGoods storePromotionGoods : goodsList) {
                    // 修改产品状态
                    storePromotionGoodsService.updateById(storePromotionGoods);
                    // 判断所有产品状态，修改推广状态
                    storePromotionGoodsService.updateGoodsStatus(storePromotionGoods, false);
                }
                // 根据网红id删除网红
                updateCelebrityStatus(Arrays.asList(ids.split(",")));
            }
        }
    }

    /**
     * 功能描述:删除网红，判断产品，推广需求状态
     *
     * @param goodsId 商品id
     * @param promId  推广id
     * @param ids     商品网红id
     * @return void
     * @Date 2021-06-29 15:33:48
     */
    private List<StorePromotionGoods> delCelebrity(String goodsId, String promId, String ids) {
        String[] goodsCelIdList = ids.split(",");
        List<StorePromotionGoods> goodsList = new ArrayList<>();
        for (String goodsCelebritieslId : goodsCelIdList) {
            // 构建推广产品
            StorePromotionGoods storePromotionGoodsNew = new StorePromotionGoods();
            storePromotionGoodsNew.setId(goodsId);
            storePromotionGoodsNew.setPromId(promId);
            // 获取当前产品下所有的网红
            LambdaQueryWrapper<StorePromotionGoodsCelebrity> celebrityWrapper = new LambdaQueryWrapper<>();
            celebrityWrapper.eq(StorePromotionGoodsCelebrity::getGoodsId, goodsId);
            celebrityWrapper.ne(StorePromotionGoodsCelebrity::getId, goodsCelebritieslId);
            celebrityWrapper.eq(StorePromotionGoodsCelebrity::getIsDel, YesNoStatus.NO.getCode());
            List<StorePromotionGoodsCelebrity> celebrityList = this.list(celebrityWrapper);
            // 获取其余网红状态
            List<Integer> celebrityStatusList = celebrityList.stream().map(StorePromotionGoodsCelebrity::getCelebrityPromStatus).collect(Collectors.toList());
            // 判断其余网红状态，存在其余网红
            if (celebrityStatusList.size() > 0) {
                // 获取未结束的状态
                List<Integer> otherCelebrityStatusList = celebrityStatusList.stream().filter(x -> x != YesNoStatus.FINISH.getCode() && x != YesNoStatus.Exception.getCode()).collect(Collectors.toList());
                // 判断是否存在为完成的网红状态
                if (otherCelebrityStatusList.size() > 0) {
                    // 修改产品状态为进行中
                    storePromotionGoodsNew.setGoodsStatus(PromotionGoodsType.UNDERWAY.getCode());
                } else {
                    // 所有网红状态都已结束
                    // 获取是否有异常结束的状态
                    List<Integer> endStatusList = celebrityStatusList.stream().filter(x -> x == YesNoStatus.Exception.getCode()).collect(Collectors.toList());
                    if (endStatusList.size() > 0) {
                        // 修改网红状态为异常结束
                        storePromotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
                    } else {
                        // 修改网红状态为正常结束
                        storePromotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
                    }
                }
            } else {
                // 不存在其他网红，修改当前产品状态为未开始
                storePromotionGoodsNew.setGoodsStatus(PromotionGoodsType.DNS.getCode());
            }
            goodsList.add(storePromotionGoodsNew);
        }
        return goodsList;
    }

    /**
     * 功能描述:查看历史匹配网红
     *
     * @param promId 推广id
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-06-29 17:32:28
     */
    @Override
    public IPage<StorePromotionGoodsModel> getOldCelebrity(Page<StorePromotionGoodsModel> page, String promId) {
        return storePromotionGoodsCelebrityMapper.getOldCelebrity(page, promId);
    }

    /**
     * 功能描述:获取产品网红列表
     *
     * @param goodsId         产品id
     * @param celebrityIdList 网红列表id
     * @param isDel           删除状态
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-08-11 17:06:16
     */
    @Override
    public List<StorePromotionGoodsCelebrity> goodsCelebrityList(String goodsId, List<String> celebrityIdList, int isDel) {
        LambdaQueryWrapper<StorePromotionGoodsCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getGoodsId, goodsId);
        lambdaQueryWrapper.in(StorePromotionGoodsCelebrity::getCelebrityPrivateId, celebrityIdList);
        lambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getIsDel, isDel);
        return this.list(lambdaQueryWrapper);
    }

    /**
     * 功能描述:保存或修改
     *
     * @param storeSellerPromotion  推广需求
     * @param storePromotionGoods   推广产品
     * @param goodsCelebrityListNew 网红列表
     * @return void
     * @Date 2021-08-11 17:08:54
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrSave(StoreSellerPromotion storeSellerPromotion, StorePromotionGoods storePromotionGoods, List<StorePromotionGoodsCelebrity> goodsCelebrityListNew) {
        if (oConvertUtils.isNotEmpty(storeSellerPromotion)) {
            storeSellerPromotionService.updateById(storeSellerPromotion);
        }
        if (oConvertUtils.isNotEmpty(storePromotionGoods)) {
            storePromotionGoodsService.updateById(storePromotionGoods);
        }
        if (!goodsCelebrityListNew.isEmpty()) {
            this.saveBatch(goodsCelebrityListNew);
        }
    }

    /**
     * 功能描述:修改网红删除状态
     *
     * @param idList
     * @return void
     * @Date 2021-06-29 15:40:01
     */
    private void updateCelebrityStatus(List<String> idList) {
        LambdaUpdateWrapper<StorePromotionGoodsCelebrity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(StorePromotionGoodsCelebrity::getId, idList);
        wrapper.set(StorePromotionGoodsCelebrity::getIsDel, YesNoStatus.YES.getCode());
        update(wrapper);
    }

    /**
     * 功能描述:获取需要删除的网红信息
     *
     * @param removeIdList 网红id
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-08-12 17:15:45
     */
    @Override
    public List<StorePromotionGoodsCelebrity> getCelebrityList(List<String> removeIdList) {
        LambdaQueryWrapper<StorePromotionGoodsCelebrity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StorePromotionGoodsCelebrity::getId, removeIdList);
        return this.list(queryWrapper);
    }

    /**
     * 功能描述:获取产品下的所有网红
     *
     * @param promGoodsIds 所有推广产品id
     * @return java.util.Map<java.lang.String, org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-08-12 17:23:46
     */
    @Override
    public Map<String, List<StorePromotionGoodsCelebrity>> getGoodsCelebrityList(List<String> promGoodsIds, String goodsCelebrityId, Integer celebrityPromStatus) {
        Map<String, List<StorePromotionGoodsCelebrity>> map = new HashMap<>();
        for (String promGoodsId : promGoodsIds) {
            LambdaQueryWrapper<StorePromotionGoodsCelebrity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StorePromotionGoodsCelebrity::getGoodsId, promGoodsId);
            queryWrapper.eq(StorePromotionGoodsCelebrity::getIsDel, YesNoStatus.NO.getCode());
            List<StorePromotionGoodsCelebrity> celebrities = this.list(queryWrapper);
            if (oConvertUtils.isNotEmpty(goodsCelebrityId)) {
                List<StorePromotionGoodsCelebrity> celebritiesNew = new ArrayList<>();
                for (StorePromotionGoodsCelebrity celebrity : celebrities) {
                    if (celebrity.getId().equals(goodsCelebrityId)) {
                        celebrity.setCelebrityPromStatus(celebrityPromStatus);
                    }
                    celebritiesNew.add(celebrity);
                }
                map.put(promGoodsId, celebritiesNew);
            } else {
                map.put(promGoodsId, celebrities);
            }
        }
        return map;
    }

    /**
     * 功能描述:获取产品下的所有网红
     *
     * @param promGoodsIds 所有推广产品id
     * @return java.util.Map<java.lang.String, org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-08-12 17:23:46
     */
    @Override
    public Map<String, List<StorePromotionGoodsCelebrity>> getGoodsCelebrityListNew(List<String> promGoodsIds, String goodsCelebrityId,
                                                                                    Integer celebrityPromStatus, Integer flag, Integer removeCurrentFlag) {
        Map<String, List<StorePromotionGoodsCelebrity>> map = new HashMap<>();

        // 查询当前产品网红
        LambdaQueryWrapper<StorePromotionGoodsCelebrity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StorePromotionGoodsCelebrity::getGoodsId, promGoodsIds);
        queryWrapper.eq(StorePromotionGoodsCelebrity::getIsDel, YesNoStatus.NO.getCode());
        if (oConvertUtils.isNotEmpty(flag)) {
            queryWrapper.eq(StorePromotionGoodsCelebrity::getCelebrityStatus, YesNoStatus.YES.getCode());
        }
        if (removeCurrentFlag == YesNoStatus.YES.getCode()) {
            queryWrapper.ne(StorePromotionGoodsCelebrity::getId, goodsCelebrityId);
        }
        List<StorePromotionGoodsCelebrity> goodsCelebrityList = this.list(queryWrapper);

        // 循环产品id
        for (String promGoodsId : promGoodsIds) {
            // 获取当前产品对应的网红
            List<StorePromotionGoodsCelebrity> celebrities = goodsCelebrityList.stream().filter(x -> x.getGoodsId().equals(promGoodsId)).collect(Collectors.toList());
            // 判断是否存在产品网红id
            if (oConvertUtils.isNotEmpty(goodsCelebrityId) && oConvertUtils.isNotEmpty(celebrityPromStatus)) {
                // 过滤出当前网红，修改网红推广状态为现在已修改后的网红状态
                List<StorePromotionGoodsCelebrity> goodsCelebrities = celebrities.stream().filter(x -> x.getId().equals(goodsCelebrityId)).collect(Collectors.toList());
                if (!goodsCelebrities.isEmpty()) {
                    goodsCelebrities.get(0).setCelebrityPromStatus(celebrityPromStatus);
                }
            }
            map.put(promGoodsId, celebrities);
        }
        return map;
    }

    /**
     * 功能描述:判断网红状态
     *
     * @param goodsCelebrityList 需要删除的网红
     * @param map                所有网红
     * @return java.util.Map<java.lang.String, java.util.List < org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>>
     * @Date 2021-08-12 17:30:59
     */
    @Override
    public JSONObject checkCelebrityStatus(List<StorePromotionGoodsCelebrity> goodsCelebrityList, List<StorePromotionGoods> promotionGoodsList, Map<String, List<StorePromotionGoodsCelebrity>> map) {
        JSONObject jsonObject = new JSONObject();
        List<StorePromotionGoods> promotionGoodsListNew = new ArrayList<>();
        // 遍历产品
        for (StorePromotionGoods storePromotionGoods : promotionGoodsList) {
            StorePromotionGoods promotionGoodsNew = new StorePromotionGoods();
            String goodsId = storePromotionGoods.getId();
            promotionGoodsNew.setId(goodsId);
            // 获取该产品下需要删除的网红
            List<StorePromotionGoodsCelebrity> productCelebrity = goodsCelebrityList.stream().filter(x -> x.getGoodsId().equals(goodsId)).collect(Collectors.toList());
            if (productCelebrity.isEmpty()) {
                jsonObject.put(goodsId, map.get(goodsId));
                promotionGoodsNew.setGoodsStatus(storePromotionGoods.getGoodsStatus());
                promotionGoodsListNew.add(promotionGoodsNew);
                continue;
            }
            // 获取该产品下所有网红
            List<StorePromotionGoodsCelebrity> allCelebrityList = map.get(goodsId);
            // 判断是否相等
            if (productCelebrity.size() == allCelebrityList.size()) {
                jsonObject.put(goodsId, null);
                // 该产品下所有网红都被删除，修改产品状态为未开始
                promotionGoodsNew.setGoodsStatus(PromotionGoodsType.DNS.getCode());
            } else {
                // 存在未删除网红，获取未删除的网红
                allCelebrityList.removeAll(productCelebrity);
                jsonObject.put(goodsId, allCelebrityList);
                // 获取剩余进行中的网红
                List<StorePromotionGoodsCelebrity> progressCelList = allCelebrityList.stream().filter(x -> x.getCelebrityPromStatus() != YesNoStatus.FINISH.getCode() && x.getCelebrityPromStatus() != YesNoStatus.Exception.getCode()).collect(Collectors.toList());
                if (!progressCelList.isEmpty()) {
                    // 修改产品状态为进行中
                    promotionGoodsNew.setGoodsStatus(PromotionGoodsType.UNDERWAY.getCode());
                } else {
                    // 获取异常结束的网红
                    List<StorePromotionGoodsCelebrity> unusualCelList = allCelebrityList.stream().filter(x -> x.getCelebrityPromStatus() == YesNoStatus.Exception.getCode()).collect(Collectors.toList());
                    if (!unusualCelList.isEmpty()) {
                        // 修改产品状态为异常结束
                        promotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
                    } else {
                        // 修改产品状态为正常结束
                        promotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
                    }
                }
            }
            promotionGoodsListNew.add(promotionGoodsNew);
        }
        jsonObject.put(CommonConstant.PROMOTION_GOODS_LIST_NEW, promotionGoodsListNew);
        return jsonObject;
    }

    /**
     * 功能描述:判断产品状态
     *
     * @param map 所有网红
     * @return java.util.Map<java.lang.String, java.util.List < org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>>
     * @Date 2021-08-12 17:30:59
     */
    @Override
    public JSONObject checkGoodsStatus(List<StorePromotionGoods> promotionGoodsList, Map<String, List<StorePromotionGoodsCelebrity>> map) {
        JSONObject jsonObject = new JSONObject();
        List<StorePromotionGoods> promotionGoodsListNew = new ArrayList<>();
        // 遍历产品
        for (StorePromotionGoods storePromotionGoods : promotionGoodsList) {
            String goodsId = storePromotionGoods.getId();
            StorePromotionGoods promotionGoodsNew = new StorePromotionGoods();
            promotionGoodsNew.setId(goodsId);
            // 如果当前产品下所有网红状态都已完成，则产品状态为已结束
            // 获取该产品下所有网红
            List<StorePromotionGoodsCelebrity> allCelebrityList = map.get(goodsId);
            if (allCelebrityList == null) {
                continue;
            }
            // 获取进行中的网红
            Optional<StorePromotionGoodsCelebrity> inProgressCel = allCelebrityList.stream()
                    .filter(x -> !x.getCelebrityPromStatus().equals(PROM_STATUS_3) && !x.getCelebrityPromStatus().equals(PROM_STATUS_FINISH))
                    .findFirst();

            Optional<StorePromotionGoodsCelebrity> unusualCel = allCelebrityList.stream()
                    .filter(x -> x.getCelebrityPromStatus() == YesNoStatus.Exception.getCode())
                    .findFirst();

            if (inProgressCel.isPresent()) {
                // 修改产品状态为进行中
                promotionGoodsNew.setGoodsStatus(PromotionGoodsType.UNDERWAY.getCode());
            } else if (unusualCel.isPresent()) {
                // 修改产品状态为异常结束
                promotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
            } else {
                // 修改产品状态为正常结束
                promotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
            }
            promotionGoodsListNew.add(promotionGoodsNew);
        }
        jsonObject.put(CommonConstant.PROMOTION_GOODS_LIST_NEW, promotionGoodsListNew);
        return jsonObject;
    }

    /**
     * 功能描述:判断产品状态
     *
     * @param map 所有网红
     * @return java.util.Map<java.lang.String, java.util.List < org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>>
     * @Date 2021-08-12 17:30:59
     */
    @Override
    public JSONObject checkGoodsStatusNew(List<StorePromotionGoods> promotionGoodsList, Map<String, List<StorePromotionGoodsCelebrity>> map) {
        JSONObject jsonObject = new JSONObject();
        List<StorePromotionGoods> promotionGoodsListNew = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 遍历产品
        for (StorePromotionGoods storePromotionGoods : promotionGoodsList) {
            String goodsId = storePromotionGoods.getId();
            StorePromotionGoods promotionGoodsNew = new StorePromotionGoods();
            promotionGoodsNew.setId(goodsId);
            // 如果当前产品下所有网红状态都已完成，则产品状态为已结束
            // 获取该产品下所有网红
            List<StorePromotionGoodsCelebrity> allCelebrityList = map.get(goodsId);
            // 获取进行中的网红
            List<StorePromotionGoodsCelebrity> progressCelList = allCelebrityList.stream().filter(x -> x.getCelebrityPromStatus() != YesNoStatus.FINISH.getCode() && x.getCelebrityPromStatus() != YesNoStatus.Exception.getCode()).collect(Collectors.toList());
            if (allCelebrityList.isEmpty()) {
                // 修改产品状态为未开始
                promotionGoodsNew.setGoodsStatus(PromotionGoodsType.DNS.getCode());
            } else {
                if (!progressCelList.isEmpty()) {
                    // 修改产品状态为进行中
                    promotionGoodsNew.setGoodsStatus(PromotionGoodsType.UNDERWAY.getCode());
                } else {
                    // 获取异常结束的网红
                    List<StorePromotionGoodsCelebrity> unusualCelList = allCelebrityList.stream().filter(x -> x.getCelebrityPromStatus() == YesNoStatus.Exception.getCode()).collect(Collectors.toList());
                    if (!unusualCelList.isEmpty()) {
                        // 修改产品状态为异常结束
                        promotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
                    } else {
                        // 修改产品状态为正常结束
                        promotionGoodsNew.setGoodsStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
                    }
                    // 网红推广费
                    BigDecimal promAll = unusualCelList.stream().map(x -> Optional.ofNullable(x.getPromPrice()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal paypalAll = unusualCelList.stream().map(x -> Optional.ofNullable(x.getCelebrityPaypalFees()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    totalAmount = totalAmount.add(promAll).add(paypalAll);
                }
            }
            promotionGoodsListNew.add(promotionGoodsNew);
        }
        jsonObject.put(CommonConstant.PROMOTION_GOODS_LIST_NEW, promotionGoodsListNew);
        jsonObject.put(CommonConstant.TOTAL_AMOUNT_NEW, totalAmount);
        return jsonObject;
    }


    /**
     * 功能描述:处理结果
     *
     * @param removeIdList          需要删除的产品网红id
     * @param promotionGoodsListNew 需要修改状态的产品
     * @param storeSellerPromotion  需要修改状态的推广
     * @return void
     * @Date 2021-08-13 08:31:08
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateData(List<String> removeIdList, List<StorePromotionGoods> promotionGoodsListNew, StoreSellerPromotion storeSellerPromotion) {
        // 修改需要删除的网红状态
        if (!removeIdList.isEmpty()) {
            updateCelebrityStatus(removeIdList);
        }
        // 修改产品状态
        if (!promotionGoodsListNew.isEmpty()) {
            storePromotionGoodsService.updateBatchById(promotionGoodsListNew);
        }
        // 修改推广状态
        if (!oConvertUtils.isAllFieldNull(storeSellerPromotion)) {
            storeSellerPromotionService.updateById(storeSellerPromotion);
        }
    }

    /**
     * 功能描述:编辑网红属性
     *
     * @param promotionGoodsListNew 需要修改的产品状态
     * @param storeSellerPromotion  需要修改的推广状态
     * @return void
     * @Date 2021-08-13 10:18:42
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsCelebrityById(List<StorePromotionGoods> promotionGoodsListNew, StoreSellerPromotion storeSellerPromotion) {
        if (!promotionGoodsListNew.isEmpty()) {
            storePromotionGoodsService.updateBatchById(promotionGoodsListNew);
        }
        if (!oConvertUtils.isAllFieldNull(storeSellerPromotion)) {
            storeSellerPromotionService.updateById(storeSellerPromotion);
        }
    }

    /**
     * 功能描述:获取当前推广下所有网红金额
     *
     * @param promId            推广id
     * @param celebrityPriceNew 当前网红费用
     * @param goodsCelebrityId  当前产品网红id
     * @return java.math.BigDecimal
     * @Date 2021-09-28 08:16:21
     */
    @Override
    public BigDecimal getCelebrityTotalAmount(String promId, BigDecimal celebrityPriceNew, String goodsCelebrityId) {
        StorePromotionGoodsCelebrity goodsCelebrity = storePromotionGoodsCelebrityMapper.getRemainingAmount(promId, goodsCelebrityId);
        BigDecimal celebrityPriceOld = goodsCelebrity.getCelebrityPrice();
        if (oConvertUtils.isNotEmpty(celebrityPriceOld) && oConvertUtils.isNotEmpty(celebrityPriceNew)) {
            return celebrityPriceOld.add(celebrityPriceNew).setScale(2, RoundingMode.DOWN);
        }
        if (oConvertUtils.isEmpty(celebrityPriceOld)) {
            return celebrityPriceNew;
        }
        return BigDecimal.ZERO;
    }

    /**
     * 功能描述:获取当前推广下所有网红金额
     *
     * @param promId            推广id
     * @param celebrityPriceNew 当前网红费用
     * @param goodsCelebrityId  当前产品网红id
     * @return java.math.BigDecimal
     * @Date 2021-09-28 08:16:21
     */
    @Override
    public JSONObject getCelebrityTotalAmountNew(String promId, BigDecimal celebrityPriceNew, BigDecimal taxAmount, BigDecimal promPrice, String goodsCelebrityId) {
        JSONObject jsonObject = new JSONObject();
        // 查询除当前网红外其余网红费用
        StorePromotionGoodsCelebrity goodsCelebrity = storePromotionGoodsCelebrityMapper.getRemainingAmountNew(promId, goodsCelebrityId);
        // 其余网红总费用
        BigDecimal allCelebrityPriceOld = goodsCelebrity.getCelebrityPrice();
        jsonObject.put(CommonConstant.ALL_CELEBRITY_PRICE, allCelebrityPriceOld.add(celebrityPriceNew).setScale(4, RoundingMode.DOWN));
        // 其余网红总税费费用
        BigDecimal allTaxAmountOld = goodsCelebrity.getTaxAmount();
        jsonObject.put(CommonConstant.ALL_TAX_AMOUNT, allTaxAmountOld.add(taxAmount).setScale(4, RoundingMode.DOWN));
        // 其余网红总税费费用
        BigDecimal allCelebrityPaypalFeesOld = goodsCelebrity.getCelebrityPaypalFees();
        jsonObject.put(CommonConstant.ALL_CELEBRITY_PAYPAL_FEES, allCelebrityPaypalFeesOld.add(BigDecimal.ZERO).setScale(4, RoundingMode.DOWN));
        // 其余网红推广费
        BigDecimal allCelebrityPromPriceOld = goodsCelebrity.getPromPrice();
        jsonObject.put(CommonConstant.ALL_CELEBRITY_PROM_PRICE, allCelebrityPromPriceOld.add(promPrice).setScale(4, RoundingMode.DOWN));
        return jsonObject;
    }

    /**
     * 功能描述:网红费用统计
     *
     * @param page
     * @param storePromotionGoodsCelebrityModel
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2021-09-28 15:18:11
     */
    @Override
    public IPage<StorePromotionGoodsCelebrityModel> getCelebrityStatistics(Page<StorePromotionGoodsCelebrityModel> page, StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel) {
        return storePromotionGoodsCelebrityMapper.getCelebrityStatistics(page, storePromotionGoodsCelebrityModel);
    }

    /**
     * 功能描述:网红费用统计 excel
     *
     * @param goodsCelebrityModel
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.model.StorePromotionGoodsCelebrityModel>
     * @Date 2021-09-28 16:11:38
     */
    @Override
    public List<GoodsCelebrityModel> getCelebrityStatisticsList(GoodsCelebrityModel goodsCelebrityModel) {
        return storePromotionGoodsCelebrityMapper.getCelebrityStatisticsList(goodsCelebrityModel);
    }

    /**
     * 功能描述:网红费用统计总额
     *
     * @param storePromotionGoodsCelebrityModel
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.model.StorePromotionGoodsCelebrityModel>
     * @Date 2021-09-28 16:11:38
     */
    @Override
    public List<StorePromotionGoodsCelebrityModel> listAll(StorePromotionGoodsCelebrityModel storePromotionGoodsCelebrityModel) {
        return storePromotionGoodsCelebrityMapper.listAll(storePromotionGoodsCelebrityModel);
    }

    /**
     * 功能描述:修改产品网红选中状态，修改推广网红费用
     *
     * @param storeSellerPromotion
     * @param promotionGoodsCelebrity
     * @return void
     * @Date 2021-10-18 11:30:57
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsCelebrity(StoreSellerPromotion storeSellerPromotion, StorePromotionGoodsCelebrity promotionGoodsCelebrity) {
        // 修改推广网红费用
        storeSellerPromotionService.updateById(storeSellerPromotion);
        // 修改产品网红选中状态
        updateById(promotionGoodsCelebrity);
    }

    /**
     * 功能描述:修改产品网红选中状态，修改推广网红费用
     *
     * @param storeSellerPromotion
     * @param promotionGoodsCelebrity
     * @return void
     * @Date 2021-10-18 11:30:57
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsCelebrityNew(StoreSellerPromotion storeSellerPromotion,
                                        StorePromotionGoodsCelebrity promotionGoodsCelebrity,
                                        List<StorePromotionGoods> promotionGoodsListNew) {
        // 修改推广网红费用
        storeSellerPromotionService.updateById(storeSellerPromotion);
        // 修改产品网红选中状态
        updateById(promotionGoodsCelebrity);
        if (!promotionGoodsListNew.isEmpty()) {
            storePromotionGoodsService.updateBatchById(promotionGoodsListNew);
        }
    }

    /**
     * 功能描述:判断推广状态
     *
     * @param sellerPromotionNow
     * @return void
     * @Date 2022-02-09 10:04:01
     */
    @Override
    public Integer checkPromStatus(StoreSellerPromotion sellerPromotionNow) {
        String promotionId = sellerPromotionNow.getId();
        // 获取当前推广下所有的产品
        List<StorePromotionGoods> promotionGoodsList = storePromotionGoodsService.getPromGoodsList(promotionId);
        // 判断产品状态，如果存在未结束产品，则修改推广状态为进行中，如果都已完成，则修改为已完成
        boolean goodsFlag = promotionGoodsList.stream().anyMatch(x -> (x.getGoodsStatus() != PromotionGoodsType.FINISHEDNORMAL.getCode() && x.getGoodsStatus() != PromotionGoodsType.FINISHEDABNORMAL.getCode()));
        // 存在未结束的产品，则推广状态修改为进行中
        if (goodsFlag) {
            return PromotionGoodsType.UNDERWAY.getCode();
        } else {
            // 产品都已结束，获取所有产品网红，判断网红状态是否存在异常结束，存在则修改推广状态为异常完成
            List<String> promGoodsIds = promotionGoodsList.stream().map(StorePromotionGoods::getId).collect(Collectors.toList());
            LambdaQueryWrapper<StorePromotionGoodsCelebrity> celebrityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            celebrityLambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getPromId, promotionId);
            celebrityLambdaQueryWrapper.in(StorePromotionGoodsCelebrity::getGoodsId, promGoodsIds);
            celebrityLambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getCelebrityStatus, YesNoStatus.YES.getCode());
            celebrityLambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getIsDel, YesNoStatus.NO.getCode());
            List<StorePromotionGoodsCelebrity> goodsCelebrityList = this.list(celebrityLambdaQueryWrapper);
            // 获取未完成的网红
            boolean flag = goodsCelebrityList.stream().anyMatch(x -> x.getCelebrityPromStatus() != YesNoStatus.FINISH.getCode() && x.getCelebrityPromStatus() != YesNoStatus.Exception.getCode());
            if (flag) {
                return PromotionGoodsType.UNDERWAY.getCode();
            }
            boolean flagException = goodsCelebrityList.stream().anyMatch(x -> x.getCelebrityPromStatus() == YesNoStatus.Exception.getCode());
            // 存在异常结束网红，修改推广状态为异常结束
            if (flagException) {
                return PromotionGoodsType.FINISHEDABNORMAL.getCode();
            } else {
                // 不存在异常结束网红，修改推广状态为正常结束
                return PromotionGoodsType.FINISHEDNORMAL.getCode();
            }
        }
    }

    /**
     * 功能描述:计算网红paypal费
     *
     * @return java.math.BigDecimal
     * @Date 2022-04-19 09:17:21
     */
    @Override
    public BigDecimal getPayPalNew(BigDecimal promPriceNew, BigDecimal paypalRate, BigDecimal paypalBaseFees) {
        BigDecimal fixedFee = BigDecimal.ONE.subtract(paypalRate.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP));
        return (promPriceNew.add(paypalBaseFees)).divide(fixedFee, 2, BigDecimal.ROUND_HALF_UP).subtract(promPriceNew);
    }

    /**
     * 功能描述: 计算推广费，网红成本，网红paypal费用，总推广费，总推广费金额
     *
     * @param promId           推广id
     * @param celebrityPrice   网红费用
     * @param taxAmount        网红paypal费
     * @param promPrice        网红推广费
     * @param goodsCelebrityId 产品网红id
     * @return org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion
     * @Date 2022-04-20 18:06:56
     */
    @Override
    public StoreSellerPromotion updateAmount(String promId, BigDecimal exchangeRate, BigDecimal celebrityPrice, BigDecimal taxAmount, BigDecimal promPrice, String goodsCelebrityId) {
        // 获取网红费用
        JSONObject jsonObject = this.getCelebrityTotalAmountNew(promId, celebrityPrice, taxAmount, promPrice, goodsCelebrityId);
        BigDecimal celebrityTotalAmount = jsonObject.getBigDecimal(CommonConstant.ALL_CELEBRITY_PRICE);
        BigDecimal taxAmountAll = jsonObject.getBigDecimal(CommonConstant.ALL_TAX_AMOUNT);
        BigDecimal promPriceAll = jsonObject.getBigDecimal(CommonConstant.ALL_CELEBRITY_PROM_PRICE);

        // 计算总推广费
        BigDecimal totalAmount = promPriceAll.add(taxAmountAll);

        StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
        storeSellerPromotion.setId(promId);
        // 设置推广网红费用,税费,总推广费
        storeSellerPromotion.setCelebrityTotalAmount(celebrityTotalAmount);
        storeSellerPromotion.setTotalTaxAmount(taxAmountAll);
        storeSellerPromotion.setPromAmount(promPriceAll);
        storeSellerPromotion.setTotalAmount(totalAmount);
        storeSellerPromotion.setTotalRmbAmount(totalAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));
        return storeSellerPromotion;
    }

    /**
     * 功能描述:查询当前推广下所有已选中网红
     *
     * @param promId
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoodscelebrity.entity.StorePromotionGoodsCelebrity>
     * @Date 2022-04-23 14:29:11
     */
    @Override
    public List<StorePromotionGoodsCelebrity> getByPromId(String promId) {
        LambdaQueryWrapper<StorePromotionGoodsCelebrity> celebrityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        celebrityLambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getPromId, promId);
        celebrityLambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getIsDel, YesNoStatus.NO.getCode());
        celebrityLambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getCelebrityStatus, YesNoStatus.YES.getCode());
        return this.list(celebrityLambdaQueryWrapper);
    }

    @Override
    public List<StorePromotionGoodsCelebrity> getByPromIdAll(String promId) {
        LambdaQueryWrapper<StorePromotionGoodsCelebrity> celebrityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        celebrityLambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getPromId, promId);
        celebrityLambdaQueryWrapper.eq(StorePromotionGoodsCelebrity::getIsDel, YesNoStatus.NO.getCode());
        return this.list(celebrityLambdaQueryWrapper);
    }

    /**
     * 功能描述:编辑推广网红
     *
     * @return void
     * @Date 2023-08-04 09:36:14
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editGoodsCelebrity(StorePromotionGoodsCelebrity storePromotionGoodsCelebrity, List<StorePromotionGoods> promotionGoodsListNew, StoreSellerPromotion storeSellerPromotion) {
        if (!oConvertUtils.isAllFieldNull(storePromotionGoodsCelebrity)) {
            this.updateById(storePromotionGoodsCelebrity);
        }
        if (!promotionGoodsListNew.isEmpty()) {
            storePromotionGoodsService.updateBatchById(promotionGoodsListNew);
        }
        if (!oConvertUtils.isAllFieldNull(storeSellerPromotion)) {
            storeSellerPromotionService.updateById(storeSellerPromotion);
        }
    }

    @Override
    public List<StorePromotionGoodsCelebrity> getByGoodsId(List<String> goodsIds, String id) {
        LambdaQueryWrapper<StorePromotionGoodsCelebrity> celebrityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        celebrityLambdaQueryWrapper.in(StorePromotionGoodsCelebrity::getGoodsId, goodsIds);
        celebrityLambdaQueryWrapper.ne(StorePromotionGoodsCelebrity::getId, id);
        return this.list(celebrityLambdaQueryWrapper);
    }

    /**
     * 功能描述:批量修改网红
     *
     * @return void
     * @Date 2023-11-20 14:43:56
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsCelebrityBatch(List<StorePromotionGoodsCelebrity> goodsCelebrityOldList, StoreSellerPromotion sellerPromotionNew) {
        updateBatchById(goodsCelebrityOldList);
        storeSellerPromotionService.updateById(sellerPromotionNew);
    }
}
