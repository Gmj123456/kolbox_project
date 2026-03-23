package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.kol.entity.KolAttributeType;
import org.jeecg.modules.kol.mapper.KolAttributeTypeMapper;
import org.jeecg.modules.kol.service.IKolAttributeTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 类目类型表
 * @Author: dongruyang
 * @Date:   2025-05-13
 * @Version: V1.0
 */
@Service
public class KolAttributeTypeServiceImpl extends ServiceImpl<KolAttributeTypeMapper, KolAttributeType> implements IKolAttributeTypeService {
    @Override
    public List<KolAttributeType> listAll(KolAttributeType kolAttributeType) {
        return this.baseMapper.listAll(kolAttributeType);
    }
}
