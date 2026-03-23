package org.jeecg.modules.instagram.storepromotion.service;

import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGrade;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 商家等级表
 * @Author: jeecg-boot
 * @Date:   2020-09-29
 * @Version: V1.0
 */
public interface IStoreSellerGradeService extends IService<StoreSellerGrade> {

    List<StoreSellerGrade> getList();

    List<StoreSellerGrade> updateGradeList(StoreSellerGrade sellerGrade);
}
