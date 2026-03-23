package org.jeecg.modules.kol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.constant.enums.PlatformType;
import org.jeecg.modules.kol.entity.KolShields;
import org.jeecg.modules.kol.mapper.KolShieldsMapper;
import org.jeecg.modules.kol.model.KolBaseModel;
import org.jeecg.modules.kol.model.KolSearchModel;
import org.jeecg.modules.kol.model.KolShieldsModel;
import org.jeecg.modules.kol.service.IKolShieldsService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 网红屏蔽名单表
 * @Author: dongruyang
 * @Date:   2025-10-18
 * @Version: V1.0
 */
@Service
public class KolShieldsServiceImpl extends ServiceImpl<KolShieldsMapper, KolShields> implements IKolShieldsService {

    @Override
    public IPage<KolShieldsModel> pageShields(Page<KolShieldsModel> page, KolShieldsModel kolShieldsModel) {
        return this.baseMapper.pageShields(page,kolShieldsModel);
    }

    @Override
    public void setShields(IPage<KolBaseModel> pageList, KolSearchModel searchModel) {
        List<KolBaseModel> records = pageList.getRecords();
        if (CollectionUtils.isEmpty(records)) return;
        //获取当前网红顾问，所选平台的所有屏蔽网红列表
        LambdaQueryWrapper<KolShields> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(KolShields::getCounselorId,searchModel.getCounselorId());
        queryWrapper.eq(KolShields::getPlatformType,searchModel.getPlatformType());
        queryWrapper.eq(KolShields::getIsDelete,0);
        List<KolShields> kolShields=this.baseMapper.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(kolShields)){
            records.parallelStream().forEach(record ->
                    processRecord(record, kolShields));
        }
    }

    private void processRecord(KolBaseModel record, List<KolShields> kolShields) {
        // 根据平台类型获取对应的账号ID
        String account = PlatformType.TIKTOK.getCode() == record.getPlatformType() ? 
                         record.getAuthorId() : record.getAccount();
        
        // 使用流式处理查找匹配的屏蔽记录
        List<KolShields> collect = kolShields.stream()
                .filter(x -> x.getCelebrityId().equals(account))
                .collect(Collectors.toList());
        
        // 如果找到匹配的屏蔽记录，则设置到record中
        if (CollectionUtils.isNotEmpty(collect)) {
            record.setKolShields(collect);
        }
    }
}
