package org.jeecg.modules.instagram.storepromotion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGrade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 商家等级表
 * @Author: jeecg-boot
 * @Date:   2020-09-29
 * @Version: V1.0
 */
public interface StoreSellerGradeMapper extends BaseMapper<StoreSellerGrade> {

    List<StoreSellerGrade> getList();

    List<StoreSellerGrade> updateGradeList(@Param("sellerGrade") StoreSellerGrade sellerGrade);
}
