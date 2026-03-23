package org.jeecg.modules.employee.mapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.employee.entity.StoreEmployeeSecurity;

import java.util.List;
import java.util.Map;

/**
 * @Description: 员工防伪
 * @Author: nqr
 * @Date:   2023-05-29
 * @Version: V1.0
 */
public interface StoreEmployeeSecurityMapper extends BaseMapper<StoreEmployeeSecurity> {

    List<Map<String, Object>> frontList(@Param("jsonObject") JSONObject jsonObject);
}
