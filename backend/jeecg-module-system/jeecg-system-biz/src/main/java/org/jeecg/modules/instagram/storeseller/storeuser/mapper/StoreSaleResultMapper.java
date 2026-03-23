package org.jeecg.modules.instagram.storeseller.storeuser.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreSaleResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.instagram.storeseller.storeuser.model.StoreSaleResultModel;

/**
 * @Description: storeSaleResult
 * @Author: jeecg-boot
 * @Date:   2020-10-16
 * @Version: V1.0
 */
public interface StoreSaleResultMapper extends BaseMapper<StoreSaleResult> {

    List<StoreSaleResult> queryBycategoryId(String id);
    IPage<StoreSaleResult> parentList(Page<StoreSaleResult> page, @Param("storeSaleResultModel") StoreSaleResultModel StoreSaleResult);

}
