package org.jeecg.modules.instagram.storecelebrity.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeDetail;

import java.util.List;
import java.util.Map;

/**
 * @Description: 网红顾问变更日志明细
 * @Author: jeecg-boot
 * @Date: 2025-10-30
 * @Version: V1.0
 */
public interface StoreCelebrityCounselorChangeDetailMapper extends BaseMapper<StoreCelebrityCounselorChangeDetail> {

    List<Map<String,Object>> queryList(@Param("changeDetail") StoreCelebrityCounselorChangeDetail changeDetail);
}
