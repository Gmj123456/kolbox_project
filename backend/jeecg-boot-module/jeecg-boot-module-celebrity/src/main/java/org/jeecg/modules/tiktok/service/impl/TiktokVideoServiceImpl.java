package org.jeecg.modules.tiktok.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.URLValidator;
import org.jeecg.modules.tiktok.entity.TiktokVideo;
import org.jeecg.modules.tiktok.mapper.TiktokVideoMapper;
import org.jeecg.modules.tiktok.model.TiktokVideoModel;
import org.jeecg.modules.tiktok.service.ITiktokVideoService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: Tiktok视频获取
 * @Author: dongruyang
 * @Date:   2024-05-30
 * @Version: V1.0
 */
@Service
public class TiktokVideoServiceImpl extends ServiceImpl<TiktokVideoMapper, TiktokVideo> implements ITiktokVideoService {

    @Override
    public boolean checkVideoUrl(String videoUrl) {
        return URLValidator.checkVideoUrl(videoUrl);
    }

    @Override
    public List<TiktokVideo> queryList(TiktokVideoModel tiktokVideoModel) {
        return baseMapper.queryList(tiktokVideoModel);
    }

    @Override
    public IPage<TiktokVideo> queryPageList(Page<TiktokVideo> page, TiktokVideoModel tiktokVideoModel) {
        /*if (tiktokVideoModel.getVideoUrl() != null) {
            tiktokVideoModel.setVideoUrl(encodeUrl(tiktokVideoModel.getVideoUrl()));
        }*/
        return baseMapper.queryPageList(page, tiktokVideoModel);
    }
    private String encodeUrl(String url) {
        // Implement URL encoding logic here if needed
        return url.replace("?", "%3F").replace("&", "%26").replace("=", "%3D");
    }
}
