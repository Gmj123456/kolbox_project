package org.jeecg.modules.instagram.storecelebrity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateProductModel;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;

import java.util.List;

/**
 * @Description: 私有网红品牌产品关联表
 * @Author: nqr
 * @Date: 2025-06-03
 * @Version: V1.0
 */
public interface IStoreCelebrityPrivateProductService extends IService<StoreCelebrityPrivateProduct> {

    /**
     * 通过历史合作、历史选中状态查询产品列表
     *
     * @param relationStatus
     * @return
     */
    List<KolProduct> queryProducts(Integer relationStatus);

    /**
     * @description: 通过私有网红ID查询历史合作和历史选中
     * @author: nqr
     * @date: 2025/9/4 18:31
     **/
    List<StoreCelebrityPrivateProductModel> getProductList(List<String> celebrityPrivateIds);

    /**
     * 从飞书表格导入私有网红产品数据
     *
     * @param spreadSheetUrl 飞书在线表格URL
     * @param accessToken 访问令牌（可选）
     * @return 导入结果
     */
    Result<?> importFromFeishuSheet(String spreadSheetUrl, String accessToken, KolSysUserFeishuSheet feishuSheet);
}
