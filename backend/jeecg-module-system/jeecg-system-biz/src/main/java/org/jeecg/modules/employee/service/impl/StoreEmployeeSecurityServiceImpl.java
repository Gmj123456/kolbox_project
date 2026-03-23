package org.jeecg.modules.employee.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.employee.entity.StoreEmployeeSecurity;
import org.jeecg.modules.employee.mapper.StoreEmployeeSecurityMapper;
import org.jeecg.modules.employee.service.IStoreEmployeeSecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description: 员工防伪
 * @Author: nqr
 * @Date:   2023-05-29
 * @Version: V1.0
 */
@Service
public class StoreEmployeeSecurityServiceImpl extends ServiceImpl<StoreEmployeeSecurityMapper, StoreEmployeeSecurity> implements IStoreEmployeeSecurityService {
    @Override
    public List<Map<String, Object>> frontList(JSONObject jsonObject) {
        return this.baseMapper.frontList(jsonObject);
    }
}
