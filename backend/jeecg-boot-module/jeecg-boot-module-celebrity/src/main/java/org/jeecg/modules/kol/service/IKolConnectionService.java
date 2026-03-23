package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.model.KolBrandModel;
import org.jeecg.modules.kol.model.KolSearchModel;

import java.util.List;
import java.util.Map;

/**
 * 获取网红信息、品牌信息，并创建建联记录接口
 *
 * @param <T>
 * @author: xyc
 * @create: 2024-12-19 16:30:28
 * @version: 1.0
 */
public interface IKolConnectionService<T> {

    /**
     * 功能描述：根据网红ID列表获取对应的网红信息
     *
     * @param kolIdList 网红ID列表
     * @param emailMap  网红邮箱映射
     * @return 返回网红信息列表
     * @Author: xyc
     * @Date: 2024-12-20
     */
    List<T> getKolList(List<String> kolIdList, Map<String, String> emailMap);


    /**
     * 功能描述：根据网红信息创建网红联系人记录
     *
     * @param kol   网红实体（可以是 TikTok、Instagram、YouTube 等平台的网红）
     * @return 返回创建的网红联系人记录
     * @Author: xyc
     * @Date: 2024-12-20
     */
    KolContact createKolContact(T kol);

    /**
     * 批量更新网红信息
     *
     * @param kolList 网红信息列表
     * @return 更新结果，通常返回更新成功的数量或成功/失败的标识
     */
    boolean batchUpdateKolList(List<T> kolList);





}

