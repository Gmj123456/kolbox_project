package org.jeecg.modules.system.service.impl;

import jakarta.annotation.Resource;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.mapper.SysDictItemMapper;
import org.jeecg.modules.system.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Resource
    private SysDictItemMapper sysDictItemMapper;

    @Override
    public List<SysDictItem> selectItemsByMainId(String mainId) {
        return sysDictItemMapper.selectItemsByMainId(mainId);
    }

    @Override
    public SysDictItem selectValueByCode(String code) {
        return sysDictItemMapper.selectValueByCode(code);
    }
    /**
     * 功能描述:根据code查询字典数据
     *
     * @param code
     * @return java.util.List<org.jeecg.modules.system.entity.SysDictItem>
     * @Date 2021-10-29 08:03:22
     */
    @Override
    public List<SysDictItem> queryValueByDictCode(String code) {
        return sysDictItemMapper.queryValueByDictCode(code);
    }
}
