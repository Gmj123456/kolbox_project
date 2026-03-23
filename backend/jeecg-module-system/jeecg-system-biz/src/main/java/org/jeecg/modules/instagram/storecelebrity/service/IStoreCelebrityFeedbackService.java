package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityFeedbackModel;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFeedback;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 反馈功能
 * @Author: jeecg-boot
 * @Date:   2021-03-01
 * @Version: V1.0
 */
public interface IStoreCelebrityFeedbackService extends IService<StoreCelebrityFeedback> {
    IPage<StoreCelebrityFeedback> feedbackPageList(Page<StoreCelebrityFeedback> page, StoreCelebrityFeedbackModel storeCelebrityFeedbackModel);
    List<StoreCelebrityFeedback> getCelebrityFeedback(String celebrityId);
}
