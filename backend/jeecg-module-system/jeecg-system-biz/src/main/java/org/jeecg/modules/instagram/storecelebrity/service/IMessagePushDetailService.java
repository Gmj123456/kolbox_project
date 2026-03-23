package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.jeecg.modules.instagram.storecelebrity.model.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: 消息推送日志表
 * @Author: jeecg-boot
 * @Date: 2020-01-17
 * @Version: V1.0
 */
public interface IMessagePushDetailService extends IService<MessagePushDetail> {

    /**
     * 功能描述: 沟通记录列表
     *
     * @Author: nqr
     * @Date 2020-10-20 09:51:38
     */
    IPage<MessagePushDetail> pageList(Page<MessagePushDetail> page, MessagePushDetailModel messagePushDetailModel);

    IPage<MessagePushDetail> detailsPageList(Page<MessagePushDetail> page, DetailsMerchantEmailModel detailsMerchantEmailModel);

    List<MessagePushEmailCountModel> queryCountPageList(MessagePushEmailCountModel messagePushEmailCountModel);

    /**
     * 网红邮箱发送统计
     */
    IPage<CelebrityEmailStatisticsModel> queryCelebrityCount(Page<CelebrityEmailStatisticsModel> page, CelebrityEmailStatisticsModel celebrityEmailStatisticsModel);

    /**
     * 商家给网红发送统计
     */
    List<CelebrityEmailStatisticsModel> querySellerCount(CelebrityEmailStatisticsModel celebrityEmailStatisticsModel);

    /**
     * 财务统计
     */
    List<FinancialStatisticsModel> financialStatistics(@Param("financialStatisticsModel") FinancialStatisticsModel financialStatisticsModel);

    /**
     * 获取发送明细
     */
    MessagePushDetailModel getDetailList(MessagePushDetailModel detailModel);

    /**
     * 获取发送总数（成功，失败）
     */
    List<MessagePushDetail> getEmailSendResult();

    /**
     * 获取域名使用情况
     */
    List<MessagePushDetailModel> getDomainResult(String format);

    /**
     * 功能描述:根据邮箱查询
     *
     * @return java.util.Map<java.lang.String, java.lang.Integer>
     * @Date 2023-10-09 14:57:20
     */
    Map<String, Integer> getByEmails(List<String> emails, String sellerId);
}
