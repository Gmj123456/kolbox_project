package org.jeecg.modules.instagram.storecelebrity.service;

import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityMcn;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: MCN管理
 * @Author: nqr
 * @Date: 2023-09-14
 * @Version: V1.0
 */
public interface IStoreCelebrityMcnService extends IService<StoreCelebrityMcn> {

    /**
     * 功能描述:根据mcn名称查询
     *
     * @return org.jeecg.modules.instagram.storecelebritymcn.entity.StoreCelebrityMcn
     * @Date 2023-09-15 16:37:36
     */
    StoreCelebrityMcn getByMcnName(String mcnName);
}
