package org.jeecg.modules.email.service.impl;

import org.jeecg.modules.email.entity.EmailAuth;
import org.jeecg.modules.email.mapper.EmailAuthMapper;
import org.jeecg.modules.email.service.IEmailAuthService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 邮箱授权表
 * @Author: dongruyang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
@Service
public class EmailAuthServiceImpl extends ServiceImpl<EmailAuthMapper, EmailAuth> implements IEmailAuthService {

}
