package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.kol.entity.KolAttributeType;

import java.util.List;

/**
 * @Description: 类目类型表
 * @Author: dongruyang
 * @Date: 2025-05-13
 * @Version: V1.0
 */
public interface IKolAttributeTypeService extends IService<KolAttributeType> {
    /**
     * @description: 查询所有类目类型
     * @author: nqr
     * @date: 2025/5/20 16:41
     **/

    List<KolAttributeType> listAll(KolAttributeType kolAttributeType);
}
