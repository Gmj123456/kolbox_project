package org.jeecg.modules.kol.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.model.KolBrandModel;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @Description: 网红品牌开发表-重命名
 * @Author: xyc
 * @Date: 2024年12月4日 14:17:05
 * @Version: V1.1
 * @History: V1.1 - [2024-12-23] - [新增updateContactHistory 用于兼容不同类型的网红更新历史开发记录] - [xyc]
 */
public interface IKolContactService extends IService<KolContact> {

    /**
     * 功能描述：根据网红列表获取网红列表中开发历史
     *
     * @param ids
     * @Param:
     * @Author: fengLiu
     * @Date: 2023/11/3 17:07
     */
    List<KolContact> getKolContactListByIds(List<String> ids);

    List<KolContact> getContactListByIds(List<String> ids);


    /**
     * 功能描述：保存网红建联开发明细
     *
     * @param kolBrandModel
     * @throws JeecgBootException
     */
    void saveKolContact(KolBrandModel kolBrandModel) throws JeecgBootException;

    /**
     * 功能描述：根据邮件推送主表ID删除标记开发记录
     *
     * @param emailPushMainId 邮件推送主表ID
     */
    void deleteKolContactByEmailPushMainId(String emailPushMainId);

    /**
     * 功能描述：根据邮件推送主表ID和网红ID删除单个网红的标记开发记录
     *
     * @param emailPushMainId 邮件推送主表ID
     * @param celebrityId     网红ID
     */
    void deleteKolContactByEmailPushMainIdAndCelebrityId(String emailPushMainId, String celebrityId);


    /**
     * 功能描述：更新网红建联开发历史
     *
     * @param kolModels
     * @param kolContactList
     * @param getUniqueId
     * @param setContactHistory
     */
    <T> void updateContactHistory(List<T> kolModels, List<KolContact> kolContactList,
                                  Function<T, String> getUniqueId, BiConsumer<T, String> setContactHistory);

    /**
     * 功能描述：更新网红建联开发历史
     *
     * @param kolModels
     * @param kolContactList
     * @param getUniqueId
     * @param setContactHistory
     */
    <T> void updateContactHistoryAllot(List<T> kolModels, List<KolContact> kolContactList,
                                       Function<T, String> getUniqueId, BiConsumer<T, String> setContactHistory, BiConsumer<T, Integer> setContactCount);

    <T> void updateContactHistoryAllotOld(List<T> kolModels, List<KolContact> kolContactList,
                                          Function<T, String> getUniqueId, BiConsumer<T, String> setContactHistory, BiConsumer<T, Integer> setContactCount);

    /**
     * 功能描述：根据邮件推送主表ID和网红ID重发网红建联开发记录
     *
     * @param mainId  邮件推送主表ID
     * @param account 网红账号
     */
    void resendKolContactByEmailPushMainIdAndCelebrityId(String mainId, String account);
}
