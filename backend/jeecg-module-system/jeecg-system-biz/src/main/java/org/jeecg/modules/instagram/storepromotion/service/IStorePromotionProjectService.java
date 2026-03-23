package org.jeecg.modules.instagram.storepromotion.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionProject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionProjectModel;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionProjectDetail;

import java.util.List;

/**
 * @Description: 产品网红历史匹配方案
 * @Author: nqr
 * @Date: 2023-09-02
 * @Version: V1.0
 */
public interface IStorePromotionProjectService extends IService<StorePromotionProject> {
    /**
     * 功能描述:导入历史匹配方案
     *
     * @return void
     * @Date 2023-09-02 14:49:07
     */
    void saveImportData(StorePromotionProject storePromotionProjectNew, List<StorePromotionProjectDetail> list, StoreSellerPromotion sellerPromotionNew);

    /**
     * 功能描述:删除方案
     *
     * @return void
     * @Date 2023-09-04 17:31:19
     */
    void removeData(String id);

    IPage<StorePromotionProjectModel> pageList(Page<StorePromotionProjectModel> page, StorePromotionProjectModel storePromotionProjectModel);
}
