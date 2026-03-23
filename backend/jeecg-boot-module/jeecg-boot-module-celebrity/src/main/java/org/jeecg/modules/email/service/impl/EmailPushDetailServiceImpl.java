package org.jeecg.modules.email.service.impl;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.email.entity.EmailPushDetail;
import org.jeecg.modules.email.entity.EmailPushMain;
import org.jeecg.modules.email.mapper.EmailPushDetailMapper;
import org.jeecg.modules.email.model.EmailMonthQueryVO;
import org.jeecg.modules.email.service.IEmailPushDetailService;
import org.jeecg.modules.email.service.IEmailPushMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 邮件消息表
 * @Author: dongruyang
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Service
public class EmailPushDetailServiceImpl extends ServiceImpl<EmailPushDetailMapper, EmailPushDetail>
        implements IEmailPushDetailService {

    @Autowired
    @Lazy
    private IEmailPushMainService emailPushMainService;

    @Override
    public List<EmailPushDetail> listByMainId(EmailPushDetail emailPushDetail) {
        return baseMapper.listByMainId(emailPushDetail);
    }

    @Override
    public Map<String, Object> getMonthSummary(EmailMonthQueryVO query) {
        return baseMapper.getMonthSummary(query);
    }

    @Override
    public List<Map<String, Object>> getCounselorTop(EmailMonthQueryVO query) {
        return baseMapper.getCounselorTop(query);
    }

    @Override
    public List<Map<String, Object>> getDailyTrend(EmailMonthQueryVO query) {
        // 计算天数差
        if (query.getStartDate() == null || query.getEndDate() == null) {
            throw new JeecgBootException("日期范围不能为空");
        }
        long daysBetween = ChronoUnit.DAYS.between(
                query.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                query.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        );

        if (daysBetween > 1000) {
            throw new JeecgBootException("日期范围不能超过1000天");
        }

        return baseMapper.getDailyTrend(query);
    }

    @Override
    public Map<String, Object> getBusinessMonthSummary(EmailMonthQueryVO query) {
        return baseMapper.getBusinessMonthSummary(query);
    }

    @Override
    public List<Map<String, Object>> getBusinessCounselorTop(EmailMonthQueryVO query) {
        return baseMapper.getBusinessCounselorTop(query);
    }

    @Override
    public List<Map<String, Object>> getBusinessDailyTrend(EmailMonthQueryVO query) {
        return baseMapper.getBusinessDailyTrend(query);
    }

    @Override
    public List<EmailPushDetail> listBusinessByMainId(String mainId) {
        EmailPushMain main = emailPushMainService.getById(mainId);
        if (main == null || main.getBusinessType() != 1) {
            return new ArrayList<>();
        }
        return lambdaQuery()
                .eq(EmailPushDetail::getMainId, mainId)
                .list();
    }
}
