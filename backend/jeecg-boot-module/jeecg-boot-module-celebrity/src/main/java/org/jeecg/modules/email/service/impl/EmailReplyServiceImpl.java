package org.jeecg.modules.email.service.impl;

import org.jeecg.modules.email.entity.EmailReply;
import org.jeecg.modules.email.mapper.EmailReplyMapper;
import org.jeecg.modules.email.service.IEmailReplyService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 邮件回复表
 * @Author: dongruyang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
@Service
public class EmailReplyServiceImpl extends ServiceImpl<EmailReplyMapper, EmailReply> implements IEmailReplyService {

}
