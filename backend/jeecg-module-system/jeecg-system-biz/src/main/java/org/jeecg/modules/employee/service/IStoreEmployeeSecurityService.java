package org.jeecg.modules.employee.service;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.employee.entity.StoreEmployeeSecurity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 员工防伪
 * @Author: nqr
 * @Date:   2023-05-29
 * @Version: V1.0
 */
public interface IStoreEmployeeSecurityService extends IService<StoreEmployeeSecurity> {

    List<Map<String, Object>> frontList(JSONObject jsonObject);
}
