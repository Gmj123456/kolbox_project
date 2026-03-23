package org.jeecg.modules.email.service.impl;

import org.jeecg.modules.email.entity.EmailTemplateCategory;
import org.jeecg.modules.email.mapper.EmailTemplateCategoryMapper;
import org.jeecg.modules.email.service.IEmailTemplateCategoryService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: Email模版所属类目
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Service
public class EmailTemplateCategoryServiceImpl extends ServiceImpl<EmailTemplateCategoryMapper, EmailTemplateCategory> implements IEmailTemplateCategoryService {

}
