package org.jeecg.modules.instagram.storecelebrity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityMcn;
import org.jeecg.modules.instagram.storecelebrity.mapper.StoreCelebrityMcnMapper;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityMcnService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: MCN管理
 * @Author: nqr
 * @Date: 2023-09-14
 * @Version: V1.0
 */
@Service
public class StoreCelebrityMcnServiceImpl extends ServiceImpl<StoreCelebrityMcnMapper, StoreCelebrityMcn> implements IStoreCelebrityMcnService {

    /**
     * 功能描述:根据mcn名称查询
     *
     * @return org.jeecg.modules.instagram.storecelebritymcn.entity.StoreCelebrityMcn
     * @Date 2023-09-15 16:37:51
     */
    @Override
    public StoreCelebrityMcn getByMcnName(String mcnName) {
        LambdaQueryWrapper<StoreCelebrityMcn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreCelebrityMcn::getMcnName, mcnName);
        return this.list(queryWrapper).stream().findFirst().orElse(null);
    }
}
