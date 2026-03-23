package org.jeecg.modules.instagram.storecelebrity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateCounselorModel;

import java.util.List;

/**
 * @Description: 私有网红网红顾问签约表
 * @Author: nqr
 * @Date:   2023-09-05
 * @Version: V1.0
 */
public interface StoreCelebrityPrivateCounselorMapper extends BaseMapper<StoreCelebrityPrivateCounselor> {

    List<StoreCelebrityPrivateCounselorModel> queryByCelebrityPrivateId(@Param("id") String id);

    /**
     * 功能描述：更新建联邮箱对应网红顾问
     * @Param:
     * @param counselorModel
     * @Author: fengLiu
     * @Date: 2024-01-15 18:02
     */
    void updateCounselorByEmails(@Param("counselorModel") StoreCelebrityPrivateCounselorModel counselorModel);
}
