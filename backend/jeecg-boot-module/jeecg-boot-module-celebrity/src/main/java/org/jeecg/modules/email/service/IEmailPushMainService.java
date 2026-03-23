package org.jeecg.modules.email.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.email.entity.EmailPushMain;
import org.jeecg.modules.email.entity.EmailPushDetail;
import org.jeecg.modules.email.model.EmailBatchPushModel;
import org.jeecg.modules.email.model.EmailPushMainDetailVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 邮件消息主表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
public interface IEmailPushMainService extends IService<EmailPushMain> {

    /**
     * 批量保存邮件推送记录
     * 
     * @param batchPushModel 批量推送参数模型
     * @param userId         用户ID
     * @param userName       用户名
     * @return 是否保存成功
     */
    Result<?> saveBatchPush(EmailBatchPushModel batchPushModel, String userId, String userName);

    /**
     * 查询主表明细信息列表
     * 
     * @return 主表明细信息VO列表
     */
    List<EmailPushMainDetailVO> getMainWithDetailsList();

    IPage<EmailPushMain> pageList(Page<EmailPushMain> page, EmailPushMain emailPushMain);

    /**
     * 商务批量导入邮件推送记录
     *
     * @param emailPushMain 主表数据
     * @param emailPushDetails 明细数据列表
     * @param userId 用户ID
     * @param userName 用户名
     * @return 是否保存成功
     */
    Result<?> saveBusinessBatchImport(EmailPushMain emailPushMain, List<EmailPushDetail> emailPushDetails, String userId, String userName);
}