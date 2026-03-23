package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.enums.PromotionGoodsType;
import org.jeecg.common.system.vo.KolProductVo;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.mapper.StoreSellerPromotionMapper;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.mapper.StorePromotionGoodsMapper;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsService;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.mapper.KolProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 商家推广产品表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Service
public class StorePromotionGoodsServiceImpl extends ServiceImpl<StorePromotionGoodsMapper, StorePromotionGoods> implements IStorePromotionGoodsService {

    @Autowired
    private StorePromotionGoodsMapper storePromotionGoodsMapper;
    @Autowired
    private StoreSellerPromotionMapper storeSellerPromotionMapper;
    @Autowired
    private KolProductMapper kolProductMapper;

    @Override
    public List<StorePromotionGoodsModel> getList(String promId) {
        return storePromotionGoodsMapper.getList(promId);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateGoodsStatus(StorePromotionGoods storePromotionGoods, boolean flagUpdate) {
        StorePromotionGoods promotionGoods = getById(storePromotionGoods.getId());
        // 获取产品id
        String promId = storePromotionGoods.getPromId();
        Integer goodsStatus = promotionGoods.getGoodsStatus();
        // 判断该推广需求下是否还有其他产品
        LambdaQueryWrapper<StorePromotionGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StorePromotionGoods::getPromId, promId);
        List<StorePromotionGoods> list = this.list(lambdaQueryWrapper);
        StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
        storeSellerPromotion.setId(promId);
        // 判断是否存在其他产品
        if (list.size() > 1) {
            // 存在,判断其他推广产品状态
            List<StorePromotionGoods> goodsList = list.stream().filter(x -> !x.getId().equals(storePromotionGoods.getId())).collect(Collectors.toList());
            List<Integer> goodsStatusList = goodsList.stream().map(StorePromotionGoods::getGoodsStatus).collect(Collectors.toList());
            // 判断当前状态是否结束
            if (goodsStatus != PromotionGoodsType.FINISHEDABNORMAL.getCode() && goodsStatus != PromotionGoodsType.FINISHEDNORMAL.getCode()) {
                // 判断当前修改状态
                // 进行中
                if (goodsStatus == PromotionGoodsType.UNDERWAY.getCode()) {
                    // 修改推广需求状态为进行中
                    storeSellerPromotion.setPromStatus(goodsStatus);
                } else {
                    // 未开始,判断剩余产品状态
                    boolean flagStatus = checkPromGoodsStatus(goodsStatusList);
                    boolean flag = false;
                    if (flagStatus) {
                        // 产品有未结束的,判断是否有进行中的
                        List<StorePromotionGoods> goodsListAway = goodsList.stream().filter(x -> x.getGoodsStatus() == PromotionGoodsType.UNDERWAY.getCode()).collect(Collectors.toList());
                        if (!goodsListAway.isEmpty()) {
                            // 修改推广需求状态为进行中
                            storeSellerPromotion.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
                        } else {
                            flag = true;
                        }
                    } else {
                        flag = true;
                    }
                    if (flag) {
                        // 修改推广需求状态为未开始
                        storeSellerPromotion.setPromStatus(PromotionGoodsType.DNS.getCode());
                    }
                }
            } else {
                // 判断剩余产品状态
                boolean flagStatus = checkPromGoodsStatus(goodsStatusList);
                if (flagStatus) {
                    // 修改推广需求状态为进行中
                    storeSellerPromotion.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
                } else {
                    storeSellerPromotion.setPromStatus(goodsStatus);
                }
            }
        } else {
            // 不存在,修改推广需求状态
            storeSellerPromotion.setPromStatus(goodsStatus);
        }
        if (!Objects.equals(storePromotionGoods.getKolProductId(), promotionGoods.getKolProductId())) {
            // 产品发生变化，同步更新推广品牌列表
            List<String> kolProductIds = list.stream().map(StorePromotionGoods::getKolProductId).collect(Collectors.toList());
            kolProductIds.remove(promotionGoods.getKolProductId());
            kolProductIds.add(storePromotionGoods.getKolProductId());
            List<KolProduct> kolProducts = kolProductMapper.selectBatchIds(kolProductIds);
            String brandNames = kolProducts.stream().map(KolProduct::getBrandName).distinct().collect(Collectors.joining(","));
            storeSellerPromotion.setProductBrand(brandNames);
        }
        storeSellerPromotionMapper.updateById(storeSellerPromotion);
        if (flagUpdate) {
            this.updateById(storePromotionGoods);
        }
    }

    /**
     * 通过sellerId查询
     */
    @Override
    public List<StorePromotionGoods> queryByPromId(String promId) {
        return storePromotionGoodsMapper.queryByPromId(promId);
    }

    /**
     * 功能描述:修改产品状态
     *
     * @param goodsList 产品列表
     * @return void
     * @Date 2021-06-29 15:43:17
     */
    @Override
    public void updateGoods(List<StorePromotionGoods> goodsList) {
        if (!goodsList.isEmpty()) {
            updateBatchById(goodsList);
        }
    }

    /**
     * 功能描述:判断产品状态
     *
     * @Author: nqr
     * @Date 2021-03-12 15:27:34
     */
    private boolean checkPromGoodsStatus(List<Integer> goodsStatusList) {
        boolean flagStatus = false;
        // 判断其他状态是否结束
        for (Integer integer : goodsStatusList) {
            if (integer != PromotionGoodsType.FINISHEDABNORMAL.getCode() && integer != PromotionGoodsType.FINISHEDNORMAL.getCode()) {
                flagStatus = true;
            }
        }
        return flagStatus;
    }

    /**
     * 功能描述:根据推广id，获取推广下所有产品
     *
     * @param promId 推广id
     * @return java.util.List<org.jeecg.modules.instagram.storepromotiongoods.entity.StorePromotionGoods>
     * @Date 2021-08-12 17:20:43
     */
    @Override
    public List<StorePromotionGoods> getPromGoodsList(String promId) {
        LambdaQueryWrapper<StorePromotionGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StorePromotionGoods::getPromId, promId);
        return this.list(queryWrapper);
    }

    @Override
    public void updatePromotionGoodsInfo(KolProductVo productVo) {
        this.lambdaUpdate().eq(StorePromotionGoods::getKolProductId, productVo.getId())
                .set(StorePromotionGoods::getProductAttributes, productVo.getProductAttributes())
                .set(StorePromotionGoods::getGoodsTitle, productVo.getProductName())
                .set(StorePromotionGoods::getGoodsUrl, productVo.getProductUrl())
                .set(StorePromotionGoods::getGoodsPicUrl, productVo.getProductImage()).update();
    }

    @Override
    public boolean getPromGoods(String id) {
        return this.lambdaQuery().eq(StorePromotionGoods::getKolProductId, id).count() > 0;
    }
}
