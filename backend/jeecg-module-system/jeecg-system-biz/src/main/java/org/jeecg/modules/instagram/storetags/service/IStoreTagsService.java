package org.jeecg.modules.instagram.storetags.service;

import org.jeecg.modules.instagram.storetags.entity.StoreTags;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 标签表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
public interface IStoreTagsService extends IService<StoreTags> {
    List<StoreTags> listAll();
}
