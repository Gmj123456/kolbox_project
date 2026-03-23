package org.jeecg.modules.email.service.impl;

import org.jeecg.modules.email.entity.EmailTemplateBusiness;
import org.jeecg.modules.email.mapper.EmailTemplateBusinessMapper;
import org.jeecg.modules.email.service.IEmailTemplateBusinessService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商务Email模板
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Service
public class EmailTemplateBusinessServiceImpl extends ServiceImpl<EmailTemplateBusinessMapper, EmailTemplateBusiness> implements IEmailTemplateBusinessService {

}
