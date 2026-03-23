package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityFeedbackModel;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityFeedback;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityFeedbackMapper;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 反馈功能
 * @Author: jeecg-boot
 * @Date:   2021-03-01
 * @Version: V1.0
 */
@Service
public class StoreCelebrityFeedbackServiceImpl extends ServiceImpl<StoreCelebrityFeedbackMapper, StoreCelebrityFeedback> implements IStoreCelebrityFeedbackService {

    @Autowired
    StoreCelebrityFeedbackMapper storeCelebrityFeedbackMapper;

    @Override
    public IPage<StoreCelebrityFeedback> feedbackPageList(Page<StoreCelebrityFeedback> page, StoreCelebrityFeedbackModel storeCelebrityFeedbackModel) {
        return storeCelebrityFeedbackMapper.feedbackPageList(page,storeCelebrityFeedbackModel);
    }

    @Override
    public List<StoreCelebrityFeedback> getCelebrityFeedback(String celebrityId) {
        return storeCelebrityFeedbackMapper.getCelebrityFeedback(celebrityId);
    }
}
