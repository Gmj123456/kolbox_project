package org.jeecg.modules.email.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.email.entity.EmailPushDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.email.model.EmailMonthQueryVO;

/**
 * @Description: 邮件消息表
 * @Author: dongruyang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
public interface EmailPushDetailMapper extends BaseMapper<EmailPushDetail> {

    List<EmailPushDetail> listByMainId(@Param("emailPushDetail") EmailPushDetail emailPushDetail);

    Map<String, Object> getMonthSummary(@Param("query") EmailMonthQueryVO query);

    List<Map<String, Object>> getCounselorTop(@Param("query") EmailMonthQueryVO query);

    List<Map<String, Object>> getDailyTrend(@Param("query") EmailMonthQueryVO query);

    Map<String, Object> getBusinessMonthSummary(@Param("query") EmailMonthQueryVO query);

    List<Map<String, Object>> getBusinessCounselorTop(@Param("query") EmailMonthQueryVO query);

    List<Map<String, Object>> getBusinessDailyTrend(@Param("query") EmailMonthQueryVO query);
}
