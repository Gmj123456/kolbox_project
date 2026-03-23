package org.jeecg.modules.tiktok.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.tiktok.entity.TiktokVideo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.tiktok.model.TiktokVideoModel;

import java.util.List;

/**
 * @Description: Tiktok视频获取
 * @Author: dongruyang
 * @Date:   2024-05-30
 * @Version: V1.0
 */
public interface ITiktokVideoService extends IService<TiktokVideo> {

    boolean checkVideoUrl(String videoUrl);

    /**
    * 功能描述： 批量导出查询
    * @Param:
     * @param tiktokVideoModel
    * @Author: fengLiu
    * @Date: 2024-05-30 16:42
    */
    List<TiktokVideo> queryList(TiktokVideoModel tiktokVideoModel);

    IPage<TiktokVideo> queryPageList(Page<TiktokVideo> page, TiktokVideoModel tiktokVideoModel);
}
