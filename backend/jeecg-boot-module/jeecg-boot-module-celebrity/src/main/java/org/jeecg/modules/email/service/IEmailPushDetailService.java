package org.jeecg.modules.email.service;

import org.jeecg.modules.email.entity.EmailPushDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.email.model.EmailMonthQueryVO;

import java.util.List;
import java.util.Map;

/**
 * @Description: 邮件消息表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
public interface IEmailPushDetailService extends IService<EmailPushDetail> {
    /**
     * @description:根据主表ID查询详情
     * @author: nqr
     * @date: 2025/11/19 13:41
     **/
    List<EmailPushDetail> listByMainId(EmailPushDetail emailPushDetail);

    Map<String, Object> getMonthSummary(EmailMonthQueryVO query);

    List<Map<String, Object>> getCounselorTop(EmailMonthQueryVO query);

    List<Map<String, Object>> getDailyTrend(EmailMonthQueryVO query);

    Map<String, Object> getBusinessMonthSummary(EmailMonthQueryVO query);

    List<Map<String, Object>> getBusinessCounselorTop(EmailMonthQueryVO query);

    List<Map<String, Object>> getBusinessDailyTrend(EmailMonthQueryVO query);

    /**
     * 根据主表ID查询商务明细列表
     *
     * @param mainId 主表ID
     * @return 商务明细列表
     */
    List<EmailPushDetail> listBusinessByMainId(String mainId);
}
