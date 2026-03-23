package org.jeecg.modules.instagram.storepromotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionProject;
import org.jeecg.modules.instagram.storepromotion.mapper.StorePromotionProjectMapper;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionProjectModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionProjectService;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionProjectDetail;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionProjectDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 产品网红历史匹配方案
 * @Author: nqr
 * @Date: 2023-09-02
 * @Version: V1.0
 */
@Service
public class StorePromotionProjectServiceImpl extends ServiceImpl<StorePromotionProjectMapper, StorePromotionProject> implements IStorePromotionProjectService {
    @Autowired
    private IStoreSellerPromotionService sellerPromotionService;
    @Autowired
    private IStorePromotionProjectDetailService projectDetailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveImportData(StorePromotionProject storePromotionProjectNew, List<StorePromotionProjectDetail> list, StoreSellerPromotion sellerPromotionNew) {
        sellerPromotionService.updateById(sellerPromotionNew);
        if (!oConvertUtils.isAllFieldNull(storePromotionProjectNew)) {
            this.save(storePromotionProjectNew);
        }
        if (!list.isEmpty()) {
            projectDetailService.saveBatch(list);
        }
    }

    /**
     * 功能描述:删除方案
     *
     * @return void
     * @Date 2023-09-04 17:31:27
     */
    @Override
    @Transactional(rollbackFor = Exception.class)

    public void removeData(String id) {
        this.removeById(id);
        LambdaUpdateWrapper<StorePromotionProjectDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StorePromotionProjectDetail::getProjectId, id);
        projectDetailService.remove(updateWrapper);
    }

    @Override
    public IPage<StorePromotionProjectModel> pageList(Page<StorePromotionProjectModel> page, StorePromotionProjectModel storePromotionProjectModel) {
        return this.baseMapper.pageList(page, storePromotionProjectModel);
    }
}
