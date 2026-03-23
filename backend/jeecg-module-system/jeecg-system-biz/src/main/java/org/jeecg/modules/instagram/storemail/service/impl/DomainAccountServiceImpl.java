package org.jeecg.modules.instagram.storemail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.instagram.storemail.entity.DomainAccount;
import org.jeecg.modules.instagram.storemail.mapper.DomainAccountMapper;
import org.jeecg.modules.instagram.storemail.service.IDomainAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 阿里云账号管理
 * @Author: jeecg-boot
 * @Date: 2020-11-27
 * @Version: V1.0
 */
@Service
public class DomainAccountServiceImpl extends ServiceImpl<DomainAccountMapper, DomainAccount> implements IDomainAccountService {

    @Autowired
    private DomainAccountMapper domainAccountMapper;
}
