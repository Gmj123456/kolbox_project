package org.jeecg.modules.tiktok.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityRule;
import org.jeecg.modules.tiktok.mapper.TiktokCelebrityRuleMapper;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityRuleService;
import org.springframework.stereotype.Service;

/**
 * @Description: 网红分配规则表
 * @Author: nqr
 * @Date:   2023-10-11
 * @Version: V1.0
 */
@Service
public class TiktokCelebrityRuleServiceImpl extends ServiceImpl<TiktokCelebrityRuleMapper, TiktokCelebrityRule> implements ITiktokCelebrityRuleService {

    @Override
    public Integer getMinNum(int platformType) {
        return this.baseMapper.getMinNum(platformType);
    }
}
