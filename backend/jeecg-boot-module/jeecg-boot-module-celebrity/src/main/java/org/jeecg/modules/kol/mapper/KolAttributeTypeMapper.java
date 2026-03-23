package org.jeecg.modules.kol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.kol.entity.KolAttributeType;

import java.util.List;

/**
 * @Description: 类目类型表
 * @Author: dongruyang
 * @Date: 2025-05-13
 * @Version: V1.0
 */
public interface KolAttributeTypeMapper extends BaseMapper<KolAttributeType> {
    /**
     * @description: 查询所有类目类型
     * @author: nqr
     * @date: 2025/5/20 16:41
     **/
    List<KolAttributeType> listAll(@Param("kolAttributeType") KolAttributeType kolAttributeType);
}
