package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.jeecg.modules.instagram.storecelebrity.mapper.MessagePushDetailMapper;
import org.jeecg.modules.instagram.storecelebrity.model.*;
import org.jeecg.modules.instagram.storecelebrity.service.IMessagePushDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description: 消息推送日志表
 * @Author: jeecg-boot
 * @Date: 2020-01-17
 * @Version: V1.0
 */
@Service
public class MessagePushDetailServiceImpl extends ServiceImpl<MessagePushDetailMapper, MessagePushDetail> implements IMessagePushDetailService {


    @Autowired
    private MessagePushDetailMapper messagePushDetailMapper;

    /**
     * 功能描述: 沟通记录列表
     *
     * @Author: nqr
     * @Date 2020-10-20 09:51:38
     */
    @Override
    public IPage<MessagePushDetail> pageList(Page<MessagePushDetail> page, MessagePushDetailModel messagePushDetailModel) {
        return messagePushDetailMapper.pageList(page, messagePushDetailModel);
    }

    @Override
    public IPage<MessagePushDetail> detailsPageList(Page<MessagePushDetail> page, DetailsMerchantEmailModel detailsMerchantEmailModel) {
        return messagePushDetailMapper.detailsPageList(page, detailsMerchantEmailModel);
    }

    @Override
    public List<MessagePushEmailCountModel> queryCountPageList(MessagePushEmailCountModel messagePushEmailCountModel) {
        return messagePushDetailMapper.queryCountPageList(messagePushEmailCountModel);
    }

    @Override
    public IPage<CelebrityEmailStatisticsModel> queryCelebrityCount(Page<CelebrityEmailStatisticsModel> page, CelebrityEmailStatisticsModel celebrityEmailStatisticsModel) {
        return messagePushDetailMapper.queryCelebrityCount(page, celebrityEmailStatisticsModel);
    }

    @Override
    public List<CelebrityEmailStatisticsModel> querySellerCount(CelebrityEmailStatisticsModel celebrityEmailStatisticsModel) {
        return messagePushDetailMapper.querySellerCount(celebrityEmailStatisticsModel);
    }

    @Override
    public MessagePushDetailModel getDetailList(MessagePushDetailModel detailModel) {
        return messagePushDetailMapper.getDetailList(detailModel);
    }

    @Override
    public List<MessagePushDetail> getEmailSendResult() {
        return messagePushDetailMapper.getEmailSendResult();
    }

    @Override
    public List<FinancialStatisticsModel> financialStatistics(FinancialStatisticsModel financialStatisticsModel) {
        return messagePushDetailMapper.financialStatistics(financialStatisticsModel);
    }

    @Override
    public List<MessagePushDetailModel> getDomainResult(String format) {
        return messagePushDetailMapper.getDomainResult(format);
    }

    @Override
    public Map<String, Integer> getByEmails(List<String> emails, String sellerId) {
        return messagePushDetailMapper.getByEmails(emails, sellerId);
    }
}
