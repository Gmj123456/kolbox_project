package org.jeecg.modules.kol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.model.KolSearchModel;

import java.util.List;

/***
 * @Description: 网红标签分配服务接口
 * @param <T>
 */
public interface IKolTagAllotService<T> {
    /**
     * 获取网红分配历史明细视图模型数据列表
     *
     * @param page
     * @param searchModel
     * @return
     */
    IPage<T> getKolTagAllottedList(IPage<T> page, KolSearchModel searchModel);

    /**
     * 方法描述: 获取网红分配历史明细视图模型数据列表总数
     *
     * @author nqr
     * @date 2025/01/08 10:27
     **/
    int getKolTagAllottedListCount(KolSearchModel searchModel);

    /**
     * 根据顾问ID获取分配的品牌列表
     *
     * @param counselorId 顾问ID
     * @return 品牌列表
     */
    List<KolBrand> getBrandListByCounselorId(String counselorId);
}
