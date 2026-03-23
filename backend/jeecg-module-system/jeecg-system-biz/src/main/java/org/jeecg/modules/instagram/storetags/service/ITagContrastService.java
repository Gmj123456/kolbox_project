package org.jeecg.modules.instagram.storetags.service;

import org.jeecg.modules.instagram.storetags.entity.TagContrast;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 标签
 * @Author: jeecg-boot
 * @Date:   2021-01-18
 * @Version: V1.0
 */
public interface ITagContrastService extends IService<TagContrast> {

    /**
     * 根据标签查询对应中文标签
     * */
    TagContrast queryByTag(String tag);
}
