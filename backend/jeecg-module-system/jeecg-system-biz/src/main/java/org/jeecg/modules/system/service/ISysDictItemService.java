package org.jeecg.modules.system.service;

import org.jeecg.modules.system.entity.SysDictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface ISysDictItemService extends IService<SysDictItem> {

    /**
     * 通过字典id查询字典项
     * @param mainId 字典id
     * @return
     */
    public List<SysDictItem> selectItemsByMainId(String mainId);

    SysDictItem selectValueByCode(String emailTemplateTestNum);
    /**
     * 功能描述:根据code查询字典数据
     *
     * @param code
     * @return java.util.List<org.jeecg.modules.system.entity.SysDictItem>
     * @Date 2021-10-29 08:02:53
     */
    List<SysDictItem> queryValueByDictCode(String code);
}
