package org.jeecg.modules.email.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.email.entity.EmailPushMain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 邮件消息主表
 * @Author: dongruyang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
public interface EmailPushMainMapper extends BaseMapper<EmailPushMain> {

    IPage<EmailPushMain> pageList(Page<EmailPushMain> page, @Param("emailPushMain") EmailPushMain emailPushMain);
}
