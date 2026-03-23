package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.model.PushCommonModel;
import org.jeecg.modules.instagram.storecelebrity.model.PushUser;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.jeecg.modules.instagram.storecelebrity.entity.MessageTemplate;
import org.jeecg.modules.instagram.storecelebrity.model.MessageTemplateModel;

/**
 * @Description: 发送模板
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
public interface IMessageTemplateService extends IService<MessageTemplate> {

    /**
     * 功能描述:替换模板中的占位符
     *
     * @return void
     * @Author: nqr
     * @Date 2020-05-11 13:47:41
     */
    MessagePushDetail pushEmailMessage(PushCommonModel pushCommonModel, PushUser pushUser);


    /**
     * 分页查询商家顾问下所有商家的模板
     * */
    IPage<MessageTemplate> sellerCounselorPage(Page<MessageTemplate> page, @Param("messageTemplateModel")MessageTemplateModel messageTemplateModel);


}
