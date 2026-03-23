package org.jeecg.modules.tiktok.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityRule;

/**
 * @Description: 网红分配规则表
 * @Author: nqr
 * @Date:   2023-10-11
 * @Version: V1.0
 */
public interface ITiktokCelebrityRuleService extends IService<TiktokCelebrityRule> {

    Integer getMinNum(int platformType);
}
