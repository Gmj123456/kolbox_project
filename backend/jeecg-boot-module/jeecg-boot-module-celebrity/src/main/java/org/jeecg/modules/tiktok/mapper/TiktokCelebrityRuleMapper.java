package org.jeecg.modules.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.tiktok.entity.TiktokCelebrityRule;

/**
 * @Description: 网红分配规则表
 * @Author: nqr
 * @Date:   2023-10-11
 * @Version: V1.0
 */
public interface TiktokCelebrityRuleMapper extends BaseMapper<TiktokCelebrityRule> {

    Integer getMinNum(@Param("platformType") int platformType);
}
