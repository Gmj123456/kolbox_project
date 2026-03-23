package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.constant.enums.ApprovedStatusType;
import org.jeecg.common.constant.enums.PayStatusEnum;
import org.jeecg.common.constant.enums.PromotionGoodsType;
import org.jeecg.common.util.RestUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionPayment;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerConsume;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.mapper.StorePromotionPaymentMapper;
import org.jeecg.modules.instagram.storepromotion.mapper.StoreSellerConsumeMapper;
import org.jeecg.modules.instagram.storepromotion.mapper.StoreSellerPromotionMapper;
import org.jeecg.modules.instagram.storepromotion.model.PromotionStatisticsModel;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerPromotionModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsService;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.service.ISysDictItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.jeecg.modules.instagram.storepromotion.config.WeChatWorkConfig;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @Description: 商家推广管理
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Service
public class StoreSellerPromotionServiceImpl extends ServiceImpl<StoreSellerPromotionMapper, StoreSellerPromotion> implements IStoreSellerPromotionService {

    @Autowired
    private StoreSellerPromotionMapper storeSellerPromotionMapper;
    @Autowired
    private IStorePromotionGoodsService storePromotionGoodsService;
    @Autowired
    private StoreSellerConsumeMapper storeSellerConsumeMapper;
    @Autowired
    private StorePromotionPaymentMapper storePromotionPaymentMapper;
    @Autowired
    private WeChatWorkConfig weChatWorkConfig;
    @Autowired
    private ISysDictItemService sysDictItemService;
    /**
     * 推广需求维护列表查询
     */
    @Override
    public IPage<StoreSellerPromotionModel> queryStoreSellerPromotionManagementList(Page<StoreSellerPromotionModel> page, @Param("storeSellerPromotionModel") StoreSellerPromotionModel storeSellerPromotionModel) {
        return storeSellerPromotionMapper.queryStoreSellerPromotionManagementList(page, storeSellerPromotionModel);
    }

    @Override
    public IPage<StoreSellerPromotion> pageList(Page<StoreSellerPromotion> page, StoreSellerPromotionModel storeSellerPromotionModel, String sql) {
        return storeSellerPromotionMapper.pageList(page, storeSellerPromotionModel, sql);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateGoods(List<StorePromotionGoods> editGoodsList, List<StorePromotionGoods> saveGoodsList, List<String> delGoodsIdList, StoreSellerPromotionModel storeSellerPromotionModel) {
        if (!saveGoodsList.isEmpty()) {
            storePromotionGoodsService.saveBatch(saveGoodsList);
        }
        if (!editGoodsList.isEmpty()) {
            storePromotionGoodsService.updateBatchById(editGoodsList);
        }
        if (!delGoodsIdList.isEmpty()) {
            storePromotionGoodsService.removeByIds(delGoodsIdList);
        }
        if (oConvertUtils.isNotEmpty(storeSellerPromotionModel)) {
            this.updateById(storeSellerPromotionModel);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void saveList(StoreSellerPromotionModel storeSellerPromotionModel, List<StorePromotionGoods> goodsListNew) {
        if (oConvertUtils.isNotEmpty(storeSellerPromotionModel)) {
            save(storeSellerPromotionModel);
        }
        if (!goodsListNew.isEmpty()) {
            storePromotionGoodsService.saveBatch(goodsListNew);
        }
    }

    /**
     * 功能描述:修改消费明细状态，修改推广需求付款金额，保存支付记录
     *
     * @param storeSellerConsumeNew 消费明细
     * @param sellerPromotionNew    推广需求
     * @param storePromotionPayment 支付记录
     * @return void
     * @Date 2021-05-25 10:09:05
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void saveAndUpade(StoreSellerConsume storeSellerConsumeNew, StoreSellerPromotion sellerPromotionNew, StorePromotionPayment storePromotionPayment) {
        // 修改消费明细状态
        if (oConvertUtils.isNotEmpty(sellerPromotionNew.getId())) {
            storeSellerConsumeMapper.updateById(storeSellerConsumeNew);
        }
        // 修改推广需求支付金额
        if (oConvertUtils.isNotEmpty(sellerPromotionNew.getId())) {
            updateById(sellerPromotionNew);
        }
        // 保存消费明细
        if (oConvertUtils.isNotEmpty(storePromotionPayment)) {
            storePromotionPaymentMapper.insert(storePromotionPayment);
        }
    }

    /**
     * 功能描述:根据推广单号查询列表
     *
     * @param code
     * @return java.util.List<org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion>
     * @Date 2021-06-05 10:37:48
     */
    @Override
    public List<StoreSellerPromotion> queryByExtensionCode(String code) {
        return storeSellerPromotionMapper.queryByExtensionCode(code);
    }

    /**
     * 功能描述:推广统计
     *
     * @param promotionStatisticsModel
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.instagram.storesellerpromotion.model.StoreSellerPromotionModel>
     * @Date 2021-07-13 16:00:23
     */
    @Override
    public PromotionStatisticsModel getPromotionStatistics(PromotionStatisticsModel promotionStatisticsModel) {
        return storeSellerPromotionMapper.getPromotionStatistics(promotionStatisticsModel);
    }

    /**
     * 功能描述:推广统计明细
     *
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-07-13 15:32:12
     */
    @Override
    public IPage<StoreSellerPromotionModel> getPromotionStatisticsDetail(Page<StoreSellerPromotionModel> page, StoreSellerPromotionModel storeSellerPromotionModel) {
        return storeSellerPromotionMapper.getPromotionStatisticsDetail(page, storeSellerPromotionModel);
    }

    /**
     * 功能描述:推广人员统计
     *
     * @param promotionStatisticsModel
     * @return org.jeecg.modules.instagram.storesellerpromotion.model.PromotionStatisticsModel
     * @Date 2021-07-23 10:01:21
     */
    @Override
    public List<PromotionStatisticsModel> getExtensionStaffStatistics(PromotionStatisticsModel promotionStatisticsModel) {
        return storeSellerPromotionMapper.getExtensionStaffStatistics(promotionStatisticsModel);
    }

    /**
     * 功能描述:网红负责人统计
     *
     * @param promotionStatisticsModel
     * @return java.util.List<org.jeecg.modules.instagram.storesellerpromotion.model.PromotionStatisticsModel>
     * @Date 2021-07-23 15:49:41
     */
    @Override
    public List<PromotionStatisticsModel> getPersonChargeStatistics(PromotionStatisticsModel promotionStatisticsModel) {
        return storeSellerPromotionMapper.getPersonChargeStatistics(promotionStatisticsModel);
    }

    /**
     * 功能描述:根据产品状态判断推广状态
     *
     * @param promId                推广id
     * @param promotionGoodsListNew 推广产品
     * @return org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion
     * @Date 2021-08-12 18:08:12
     */
    @Override
    public StoreSellerPromotion checkPromStatus(String promId, List<StorePromotionGoods> promotionGoodsListNew, JSONObject jsonObject, List<String> promGoodsIds) {
        StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
        storeSellerPromotion.setId(promId);
/*        //判断该推广下是否只有一个产品
        if (promotionGoodsListNew.size() == 1) {
            //修改推广状态为未开始
            storeSellerPromotion.setPromStatus(PromotionGoodsType.DNS.getCode());
            //修改推广审核状态为审核通过
            storeSellerPromotion.setApprovedStatus(ApprovedStatusType.EXAMINATION_PASSED.getCode());
            return storeSellerPromotion;
        }*/
        // 获取所有产品状态
        List<Integer> goodsStatusList = promotionGoodsListNew.stream().map(StorePromotionGoods::getGoodsStatus).collect(Collectors.toList());
        // 进行中
        List<Integer> underwayList = goodsStatusList.stream().filter(x -> x != PromotionGoodsType.FINISHEDABNORMAL.getCode() && x != PromotionGoodsType.FINISHEDNORMAL.getCode()).collect(Collectors.toList());
        // 存在进行中的状态
        if (!underwayList.isEmpty()) {
            // 存在进行中的状态，修改推广状态为进行中
            storeSellerPromotion.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
        } else {
            // 不存在进行中的状态，获取异常结束状态
            List<Integer> unFinishedList = goodsStatusList.stream().filter(x -> x == PromotionGoodsType.FINISHEDABNORMAL.getCode()).collect(Collectors.toList());
            // 存在异常结束状态，修改推广状态为异常结束
            if (!unFinishedList.isEmpty()) {
                storeSellerPromotion.setPromStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
            } else {
                // 不存在异常结束，修改推广状态为正常结束
                storeSellerPromotion.setPromStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
            }
        }
        int isNotNull = 0;
        // 判断所有产品下是否存在网红，不存在修改推广审核状态为审核通过
        for (String promGoodsId : promGoodsIds) {
            boolean notEmpty = oConvertUtils.isNotEmpty(jsonObject.getJSONArray(promGoodsId));
            if (notEmpty) {
                isNotNull++;
            }
        }
        if (isNotNull == 0) {
            // 修改推广审核状态为审核通过
            storeSellerPromotion.setApprovedStatus(ApprovedStatusType.EXAMINATION_PASSED.getCode());
        }
        return storeSellerPromotion;
    }

    /**
     * 功能描述:根据产品状态判断推广状态
     *
     * @param promId                推广id
     * @param promotionGoodsListNew 推广产品
     * @return org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion
     * @Date 2021-08-12 18:08:12
     */
    @Override
    public StoreSellerPromotion checkPromStatusType(String promId, List<StorePromotionGoods> promotionGoodsListNew, JSONObject jsonObject, List<String> promGoodsIds) {
        StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
        storeSellerPromotion.setId(promId);
        // 根据推广id获取当前推广状态
        StoreSellerPromotion sellerPromotion = getById(promId);
        // 获取所有产品状态
        List<Integer> goodsStatusList = promotionGoodsListNew.stream().map(StorePromotionGoods::getGoodsStatus).collect(Collectors.toList());
        // 进行中
        List<Integer> underwayList = goodsStatusList.stream().filter(x -> x != PromotionGoodsType.FINISHEDABNORMAL.getCode() && x != PromotionGoodsType.FINISHEDNORMAL.getCode()).collect(Collectors.toList());
        // 存在进行中的状态
        if (!underwayList.isEmpty()) {
            // 存在进行中的状态，修改推广状态为进行中
            storeSellerPromotion.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
        } else {
            // 已付全款
            if (sellerPromotion.getPayStatus() == PayStatusEnum.PAIDINFULL.getCode()) {
                // 不存在进行中的状态，获取异常结束状态
                List<Integer> unFinishedList = goodsStatusList.stream().filter(x -> x == PromotionGoodsType.FINISHEDABNORMAL.getCode()).collect(Collectors.toList());
                // 存在异常结束状态，修改推广状态为异常结束
                if (!unFinishedList.isEmpty()) {
                    storeSellerPromotion.setPromStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
                } else {
                    // 不存在异常结束，修改推广状态为正常结束
                    storeSellerPromotion.setPromStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
                }
            } else {
                storeSellerPromotion.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
            }
        }
        /*int isNotNull = 0;
        //判断所有产品下是否存在网红，不存在修改推广审核状态为审核通过
        for (String promGoodsId : promGoodsIds) {
            boolean notEmpty = oConvertUtils.isNotEmpty(jsonObject.getJSONArray(promGoodsId));
            if (notEmpty) {
                isNotNull++;
            }
        }
        if (isNotNull == 0) {
            //当前推广状态不为匹配成功时
            if (sellerPromotion.getApprovedStatus() == ApprovedStatusType.PENDING_REVIEW.getCode()) {
                //修改推广审核状态为审核通过
                storeSellerPromotion.setApprovedStatus(ApprovedStatusType.EXAMINATION_PASSED.getCode());
            }
        }*/
        return storeSellerPromotion;
    }

    /**
     * 功能描述:根据产品状态判断推广状态
     *
     * @return org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion
     * @Date 2022-04-21 08:26:14
     */
    @Override
    public StoreSellerPromotion checkPromStatusTypeNew(String promId, List<StorePromotionGoods> promotionGoodsListNew, JSONObject jsonObject, List<String> promGoodsIds, Integer payStatus) {
        StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
        storeSellerPromotion.setId(promId);
        // 根据推广id获取当前推广状态
        StoreSellerPromotion sellerPromotion = getById(promId);
        if (oConvertUtils.isNotEmpty(payStatus)) {
            sellerPromotion.setPayStatus(payStatus);
        }
        // 获取所有产品状态
        List<Integer> goodsStatusList = promotionGoodsListNew.stream().map(StorePromotionGoods::getGoodsStatus).collect(Collectors.toList());
        // 进行中
        boolean underwayFlag = goodsStatusList.stream().anyMatch(x -> x == PromotionGoodsType.DNS.getCode() || x == PromotionGoodsType.UNDERWAY.getCode());
        // 存在进行中的状态
        if (underwayFlag) {
            // 存在进行中的状态，修改推广状态为进行中
            storeSellerPromotion.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
        } else {
            // 判断待付金额
            BigDecimal paymentAmount = Optional.ofNullable(sellerPromotion.getPaymentAmount()).orElse(BigDecimal.ZERO);
            BigDecimal intentionAmount = Optional.ofNullable(sellerPromotion.getIntentionAmount()).orElse(BigDecimal.ZERO);
            BigDecimal refundAmount = Optional.ofNullable(sellerPromotion.getRefundAmount()).orElse(BigDecimal.ZERO);
            BigDecimal paidAmount = paymentAmount.add(intentionAmount).subtract(refundAmount);
            // 已付全款
            if (sellerPromotion.getPayStatus() == PayStatusEnum.PAIDINFULL.getCode() || paidAmount.compareTo(sellerPromotion.getTotalRmbAmount()) == 0 || sellerPromotion.getPayStatus() == PayStatusEnum.DOWITHOUTPAY.getCode() || sellerPromotion.getPayStatus() == PayStatusEnum.TOBEPAID.getCode()) {
                // 不存在进行中的状态，获取异常结束状态
                boolean unFinishedFlag = goodsStatusList.stream().anyMatch(x -> x == PromotionGoodsType.FINISHEDABNORMAL.getCode());
                // 存在异常结束状态，修改推广状态为异常结束
                if (unFinishedFlag) {
                    storeSellerPromotion.setPromStatus(PromotionGoodsType.FINISHEDABNORMAL.getCode());
                } else {
                    // 不存在异常结束，修改推广状态为正常结束
                    storeSellerPromotion.setPromStatus(PromotionGoodsType.FINISHEDNORMAL.getCode());
                }
            } else {
                storeSellerPromotion.setPromStatus(PromotionGoodsType.UNDERWAY.getCode());
            }
        }
        int isNotNull = 0;
        // 判断所有产品下是否存在网红，不存在修改推广审核状态为审核通过
        for (String promGoodsId : promGoodsIds) {
            boolean notEmpty = oConvertUtils.isNotEmpty(jsonObject.getJSONArray(promGoodsId));
            if (notEmpty) {
                isNotNull++;
            }
        }
        if (isNotNull == 0) {
            // 当前推广状态不为匹配成功时
            if (sellerPromotion.getApprovedStatus() == ApprovedStatusType.PENDING_REVIEW.getCode()) {
                // 修改推广审核状态为审核通过
                storeSellerPromotion.setApprovedStatus(ApprovedStatusType.EXAMINATION_PASSED.getCode());
            }
        }
        return storeSellerPromotion;
    }

    /**
     * 功能描述:判断当前推广状态
     *
     * @param storeSellerPromotion 推广需求
     * @return boolean
     * @Date 2021-08-13 09:59:09
     */
    @Override
    public boolean checkFinishStatus(StoreSellerPromotion storeSellerPromotion) {
        Integer promStatus = storeSellerPromotion.getPromStatus();
        // 当前推广为正常结束或异常结束时，判断是否结算完成
        if (promStatus == PromotionGoodsType.FINISHEDNORMAL.getCode() || promStatus == PromotionGoodsType.FINISHEDABNORMAL.getCode()) {
            String id = storeSellerPromotion.getId();
            StoreSellerPromotion promotion = this.getById(id);
            BigDecimal paymentAmount = Optional.ofNullable(promotion.getPaymentAmount()).orElse(BigDecimal.ZERO);
            return promotion.getPayStatus() == PayStatusEnum.PAIDINFULL.getCode() || paymentAmount.compareTo(promotion.getTotalRmbAmount()) == 0;
        }
        return true;
    }

    @Override
    public IPage<StoreSellerPromotionModel> getPaymentStatisticsDetail(Page<StorePromotionPayment> page, StoreSellerPromotionModel storeSellerPromotionModel) {
        return storeSellerPromotionMapper.getPaymentStatisticsDetail(page, storeSellerPromotionModel);
    }

    @Override
    public List<StoreSellerPromotionModel> getPaymentStatisticsDetailByXls(StoreSellerPromotionModel storeSellerPromotionModel) {
        return storeSellerPromotionMapper.getPaymentStatisticsDetailByXls(storeSellerPromotionModel);
    }

    @Override
    public IPage<StoreSellerPromotionModel> getPaymentStatisticsDetailExceedTime(Page<StorePromotionPayment> page, StoreSellerPromotionModel storeSellerPromotionModel) {
        return storeSellerPromotionMapper.getPaymentStatisticsDetailExceedTime(page, storeSellerPromotionModel);
    }

    @Override
    public List<StoreSellerPromotionModel> getPaymentStatisticsDetailExceedTimeByXls(StoreSellerPromotionModel storeSellerPromotionModel) {
        return storeSellerPromotionMapper.getPaymentStatisticsDetailExceedTimeByXls(storeSellerPromotionModel);
    }


    /**
     * 功能描述:更新商务人员列表查询
     *
     * @return java.util.List<org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion>
     * @Date 2023-08-03 15:32:20
     */
    @Override
    public List<LinkedHashMap<String, Object>> getPromotionList(StoreSellerPromotionModel storeSellerPromotionModel) {
        return storeSellerPromotionMapper.getPromotionList(storeSellerPromotionModel);
    }

    /**
     * 功能描述:根据推广单号查询推广信息
     *
     * @return org.jeecg.modules.instagram.storesellerpromotion.entity.StoreSellerPromotion
     * @Date 2023-09-02 14:42:03
     */
    @Override
    public StoreSellerPromotion getByExtensionCode(String extensionCode) {
        LambdaQueryWrapper<StoreSellerPromotion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreSellerPromotion::getExtensionCode, extensionCode);
        return this.getOne(queryWrapper);
    }

    /**
     * 功能描述:获取推广已付总金额
     *
     * @return java.math.BigDecimal
     * @Date 2023-11-23 15:36:07
     */
    @Override
    public BigDecimal getAmount(StoreSellerPromotion sellerPromotion) {
        BigDecimal exchangeRate = sellerPromotion.getExchangeRate();
        // 已付金额
        BigDecimal paymentAmount = Optional.ofNullable(sellerPromotion.getPaymentAmount()).orElse(BigDecimal.ZERO);
        // 意向金
        BigDecimal intentionAmount = Optional.ofNullable(sellerPromotion.getIntentionAmount()).orElse(BigDecimal.ZERO);
        // 退款金额
        BigDecimal refundAmount = Optional.ofNullable(sellerPromotion.getRefundAmount()).orElse(BigDecimal.ZERO);
        // 已付金额
        BigDecimal subtract = paymentAmount.add(intentionAmount).subtract(refundAmount);
        return subtract.divide(exchangeRate, 2, RoundingMode.HALF_UP);
    }

    /**
     * @description: 商务顾问审核推广推广需求
     * @author: nqr
     * @date: 2026/2/3 9:31
     **/
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateList(StoreSellerPromotionModel storeSellerPromotionModel, List<StorePromotionGoods> goodsListNew) {
        if (oConvertUtils.isNotEmpty(storeSellerPromotionModel)) {
            StoreSellerPromotion storeSellerPromotion = new StoreSellerPromotion();
            BeanUtils.copyProperties(storeSellerPromotionModel, storeSellerPromotion);
            updateById(storeSellerPromotion);
        }
        if (!goodsListNew.isEmpty()) {
            storePromotionGoodsService.saveBatch(goodsListNew);
        }
    }

    /**
     * @description: 商家查询推广需求列表
     * @author: nqr
     * @date: 2026/2/3 9:36
     **/
    @Override
    public IPage<StoreSellerPromotion> queryPageSellerList(Page<StoreSellerPromotion> page, StoreSellerPromotionModel storeSellerPromotionModel) {
        return this.baseMapper.queryPageSellerList(page, storeSellerPromotionModel);
    }

    /**
     * @param storeSellerPromotionModel 推广需求模型
     * @return void
     * @description: 发送微信企业号机器人通知
     * @author: nqr
     * @date: 2026/2/4
     **/
    @Override
    public void sendWeChatWorkNotification(StoreSellerPromotionModel storeSellerPromotionModel) {
        String webhookUrl = "";
       try{
           SysDictItem dictItem = sysDictItemService.selectValueByCode("webhookUrl");
           if (dictItem == null){
               webhookUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=8fa600d4-b207-42c7-affa-d9593c5753ce";
           }else{
               webhookUrl = dictItem.getItemValue();
           }
       }catch (Exception e){
           webhookUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=8fa600d4-b207-42c7-affa-d9593c5753ce";
           log.error("获取企业微信机器人Webhook URL失败", e);
       }

        // 如果配置中没有设置Webhook URL，则跳过发送通知
        if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
            System.out.println("企业微信机器人Webhook URL未配置，跳过发送通知");
            return;
        }

        try {
            // 构建消息内容
            String companyName = storeSellerPromotionModel.getSellerName(); // 商家名称
            String productLink = storeSellerPromotionModel.getProductUrl(); // 产品链接
            String platform = getPlatformName(storeSellerPromotionModel.getPromPlatform()); // 推广平台
            String requirement = storeSellerPromotionModel.getPromRequirement(); // 网红要求
            String budget = storeSellerPromotionModel.getPromBudget(); // 推广预算
            String contactInfo = storeSellerPromotionModel.getContactInfo(); // 联系方式
            String remark = storeSellerPromotionModel.getRemark(); // 备注

            // 构建JSON请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("msgtype", "template_card");
            
            JSONObject templateCard = new JSONObject();
            templateCard.put("card_type", "text_notice");
            
            JSONObject source = new JSONObject();
            source.put("icon_url", "https://p.kolbox.com/assets/logo-qlOPfk8G.png");
            source.put("desc", "KOLBOX");
            source.put("desc_color", 0);
            templateCard.put("source", source);
            
            JSONObject mainTitle = new JSONObject();
            mainTitle.put("title", "有新的推广需求请及时处理");
            templateCard.put("main_title", mainTitle);
            
            JSONObject emphasisContent = new JSONObject();
            emphasisContent.put("title", budget != null ? budget : "未填写");
            emphasisContent.put("desc", "推广预算");
            templateCard.put("emphasis_content", emphasisContent);
            
            JSONArray horizontalContentList = new JSONArray();
            
            JSONObject companyItem = new JSONObject();
            companyItem.put("keyname", "商家");
            companyItem.put("value", companyName != null ? companyName : "");
            horizontalContentList.add(companyItem);
            
            JSONObject productLinkItem = new JSONObject();
            productLinkItem.put("keyname", "产品链接");
            productLinkItem.put("value", "点击访问");
            productLinkItem.put("type", 1);
            productLinkItem.put("url", productLink != null ? productLink : "");
            horizontalContentList.add(productLinkItem);
            
            JSONObject platformItem = new JSONObject();
            platformItem.put("keyname", "推广平台");
            platformItem.put("value", platform);
            horizontalContentList.add(platformItem);
            
            JSONObject requirementItem = new JSONObject();
            requirementItem.put("keyname", "网红要求");
            requirementItem.put("value", requirement != null ? (requirement.length() > 20 ? requirement.substring(0, 20) + "..." : requirement) : "未填写");
            horizontalContentList.add(requirementItem);
            
            JSONObject contactItem = new JSONObject();
            contactItem.put("keyname", "联系方式");
            contactItem.put("value", contactInfo != null ? contactInfo : "未填写");
            horizontalContentList.add(contactItem);
            
            JSONObject remarkItem = new JSONObject();
            remarkItem.put("keyname", "备注");
            remarkItem.put("value", remark != null ? (remark.length() > 20 ? remark.substring(0, 20) + "..." : remark) : "未填写");
            horizontalContentList.add(remarkItem);
            
            templateCard.put("horizontal_content_list", horizontalContentList);
            
            JSONArray jumpList = new JSONArray();
            JSONObject jumpItem = new JSONObject();
            jumpItem.put("type", 1);
            jumpItem.put("url", "https://p.kolbox.com");
            jumpItem.put("title", "KOLBOX官网");
            jumpList.add(jumpItem);
            
            templateCard.put("jump_list", jumpList);
            JSONObject cardAction = new JSONObject();
            cardAction.put("type", 1);
            cardAction.put("url", "https://p.kolbox.com");
            templateCard.put("card_action", cardAction);
            requestBody.put("template_card", templateCard);

            // 发送POST请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toJSONString(), headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, requestEntity, String.class);

            if (response.getStatusCode().value() == 200) {
                System.out.println("企业微信机器人通知发送成功");
            } else {
                System.out.println("企业微信机器人通知发送失败，状态码: " + response.getStatusCode().value() + ", 响应内容: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("发送企业微信机器人通知时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param platformCode 平台代码，可能包含多个用逗号分隔的值，如'0,1,2'
     * @return 平台名称，多个平台用逗号分隔
     * @description: 根据平台代码获取平台名称
     **/
    private String getPlatformName(String platformCode) {
        if (platformCode == null || platformCode.trim().isEmpty()) {
            return "未知平台";
        }

        String[] codes = platformCode.split(",");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < codes.length; i++) {
            if (i > 0) {
                result.append(",");
            }

            String code = codes[i].trim();
            switch (code) {
                case "0":
                    result.append("IG");
                    break;
                case "1":
                    result.append("YT");
                    break;
                case "2":
                    result.append("TK");
                    break;
                default:
                    result.append(code);
                    break;
            }
        }

        return result.toString();
    }
}
