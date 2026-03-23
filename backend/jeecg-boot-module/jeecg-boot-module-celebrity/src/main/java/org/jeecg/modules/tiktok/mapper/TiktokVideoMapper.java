package org.jeecg.modules.tiktok.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.tiktok.entity.TiktokVideo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.tiktok.model.TiktokVideoModel;

import java.util.List;

/**
 * @Description: Tiktok视频获取
 * @Author: dongruyang
 * @Date:   2024-05-30
 * @Version: V1.0
 */
public interface TiktokVideoMapper extends BaseMapper<TiktokVideo> {

    List<TiktokVideo> queryList(@Param("tiktokVideoModel") TiktokVideoModel tiktokVideoModel);

    IPage<TiktokVideo> queryPageList(@Param("page") Page<TiktokVideo> page, @Param("tiktokVideoModel") TiktokVideoModel tiktokVideoModel);
}
