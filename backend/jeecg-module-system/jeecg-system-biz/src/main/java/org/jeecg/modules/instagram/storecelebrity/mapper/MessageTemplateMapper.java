package org.jeecg.modules.instagram.storecelebrity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.entity.MessageTemplate;
import org.jeecg.modules.instagram.storecelebrity.model.MessageTemplateModel;


/**
 * @Description: 消息发送模板
 * @Author: jeecg-boot
 * @Date:   2020-05-11
 * @Version: V1.0
 */
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {

    IPage<MessageTemplate> sellerCounselorPage(Page<MessageTemplate> page, @Param("messageTemplateModel")MessageTemplateModel messageTemplateModel);
}
