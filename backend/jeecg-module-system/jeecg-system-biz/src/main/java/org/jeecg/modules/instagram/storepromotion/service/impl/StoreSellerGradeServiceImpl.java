package org.jeecg.modules.instagram.storepromotion.service.impl;

import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGrade;
import org.jeecg.modules.instagram.storepromotion.mapper.StoreSellerGradeMapper;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 商家等级表
 * @Author: jeecg-boot
 * @Date:   2020-09-29
 * @Version: V1.0
 */
@Service
public class StoreSellerGradeServiceImpl extends ServiceImpl<StoreSellerGradeMapper, StoreSellerGrade> implements IStoreSellerGradeService {

    @Autowired
    private StoreSellerGradeMapper storeSellerGradeMapper;

    @Override
    public List<StoreSellerGrade> updateGradeList(StoreSellerGrade sellerGrade) {
        return storeSellerGradeMapper.updateGradeList(sellerGrade);
    }

    @Override
    public List<StoreSellerGrade> getList() {
        return storeSellerGradeMapper.getList();
    }
}
