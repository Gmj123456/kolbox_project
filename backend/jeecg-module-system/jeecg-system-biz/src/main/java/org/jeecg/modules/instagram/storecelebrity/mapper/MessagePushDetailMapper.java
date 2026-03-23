package org.jeecg.modules.instagram.storecelebrity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public interface MessagePushDetailMapper extends BaseMapper<MessagePushDetail> {

    /**
     * 功能描述: 沟通记录列表
     *
     * @Author: nqr
     * @Date 2020-10-20 09:51:38
     */
    IPage<MessagePushDetail> pageList(Page<MessagePushDetail> page, @Param("messagePushDetailModel") MessagePushDetailModel messagePushDetailModel);

    IPage<MessagePushDetail> detailsPageList(Page<MessagePushDetail> page, @Param("detailsMerchantEmailModel") DetailsMerchantEmailModel detailsMerchantEmailModel);

    List<MessagePushEmailCountModel> queryCountPageList(@Param("messagePushEmailCountModel") MessagePushEmailCountModel messagePushEmailCount);

    IPage<CelebrityEmailStatisticsModel> queryCelebrityCount(Page<CelebrityEmailStatisticsModel> page, @Param("celebrityEmailStatisticsModel") CelebrityEmailStatisticsModel celebrityEmailStatisticsModel);

    List<CelebrityEmailStatisticsModel> querySellerCount(@Param("celebrityEmailStatisticsModel") CelebrityEmailStatisticsModel celebrityEmailStatisticsModel);

    List<FinancialStatisticsModel> financialStatistics(@Param("financialStatisticsModel") FinancialStatisticsModel financialStatisticsModel);

    MessagePushDetailModel getDetailList(@Param("detailModel") MessagePushDetailModel detailModel);

    List<MessagePushDetail> getEmailSendResult();

    List<MessagePushDetailModel> getDomainResult(@Param("format") String format);

    Map<String, Integer> getByEmails(@Param("emails") List<String> emails, @Param("sellerId") String sellerId);
}
