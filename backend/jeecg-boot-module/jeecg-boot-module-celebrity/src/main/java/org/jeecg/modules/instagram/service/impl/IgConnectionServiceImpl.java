package org.jeecg.modules.instagram.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.entity.IgCelebrity;
import org.jeecg.modules.instagram.mapper.IgCelebrityMapper;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.service.IKolConnectionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: IG网红建联接口实现类
 * @Author: xyc
 * @Date: 2024-12-19 18:36:37
 * @Version: V1.0
 */
@Service
@Qualifier("igConnectionService")
public class IgConnectionServiceImpl extends ServiceImpl<IgCelebrityMapper, IgCelebrity> implements IKolConnectionService<IgCelebrity> {
    /**
     * 功能描述：根据网红ID列表获取对应的网红信息
     *
     * @param kolIdList 网红ID列表
     * @param emailMap  网红邮箱映射
     * @return 返回网红信息列表
     * @Author: xyc
     * @Date: 2024-12-20
     */
    @Override
    public List<IgCelebrity> getKolList(List<String> kolIdList, Map<String, String> emailMap) {
        List<IgCelebrity> kolList = (List<IgCelebrity>) this.listByIds(kolIdList);
        //循环更新网红邮箱
        kolList.forEach(x -> {
            String email = emailMap.get(x.getId()); // 使用 Map 快速查找邮箱
            if (oConvertUtils.isNotEmpty(email)) {
                x.setEmail(email);
            }
        });
        return kolList;
    }

    /**
     * 功能描述：根据网红信息创建网红联系人记录
     *
     * @param kol 网红实体（可以是 TikTok、Instagram、YouTube 等平台的网红）
     * @return 返回创建的网红联系人记录
     * @Author: xyc
     * @Date: 2024-12-20
     */
    @Override
    public KolContact createKolContact(IgCelebrity kol) {
        KolContact kolContact = new KolContact();
        kolContact.setCelebrityId(kol.getAccount());
        kolContact.setUniqueId(kol.getIgUsername());
        kolContact.setKolEmail(kol.getEmail());
        kolContact.setPlatformType(CommonConstant.IG);
        return kolContact;
    }

    /**
     * 批量更新网红信息
     *
     * @param kolList 网红信息列表
     * @return 更新结果，通常返回更新成功的数量或成功/失败的标识
     */
    @Override
    public boolean batchUpdateKolList(List<IgCelebrity> kolList) {
        // 创建一个新的列表，仅保留 id 和 email 字段
        List<IgCelebrity> updateList = kolList.stream().map(kol -> {
            IgCelebrity updatedKol = new IgCelebrity();
            updatedKol.setId(kol.getId());  // 保留 id
            updatedKol.setEmail(kol.getEmail());  // 保留 email
            // 不设置其他字段，其他字段会保持为 null
            return updatedKol;
        }).collect(Collectors.toList());
        // 批量更新的实现
        return this.updateBatchById(updateList);
    }
}
