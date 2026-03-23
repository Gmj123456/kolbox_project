package org.jeecg.modules.instagram.storecelebrity.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityFeedbackModel;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFeedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 反馈功能
 * @Author: jeecg-boot
 * @Date:   2021-03-01
 * @Version: V1.0
 */
public interface StoreCelebrityFeedbackMapper extends BaseMapper<StoreCelebrityFeedback> {

    /**
     * 功能描述: 获取反馈列表人员
     *
     */
    IPage<StoreCelebrityFeedback> feedbackPageList(Page<StoreCelebrityFeedback> page, StoreCelebrityFeedbackModel storeCelebrityFeedbackModel);

    /**
     * 功能描述: 获取反馈列表信息
     *
     */
    List<StoreCelebrityFeedback> getCelebrityFeedback(@Param("celebrityId") String celebrityId);

}
