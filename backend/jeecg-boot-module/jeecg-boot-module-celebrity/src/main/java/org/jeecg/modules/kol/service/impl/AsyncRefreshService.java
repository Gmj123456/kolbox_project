package org.jeecg.modules.kol.service.impl;

import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.instagram.service.IIgCelebrityTagsService;
import org.jeecg.modules.kol.model.KolTagUpdateDTO;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityRuleService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityTagsService;
import org.jeecg.modules.youtube.service.IYtCelebrityTagsService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @Author: nqr
 * @Date: 2025/8/20 11:16
 * @Description:
 **/
@Service
public class AsyncRefreshService {
    // 注入所需服务（省略 setter 或使用构造函数注入）
    @Resource
    private ITiktokCelebrityTagsService tkCelebrityTagsService;
    @Resource
    private IYtCelebrityTagsService ytCelebrityTagsService;
    @Resource
    private IIgCelebrityTagsService igCelebrityTagsService;
    @Resource
    private ITiktokCelebrityTagsService tkTagsService;
    @Resource
    private IKolTagsService kolTagsService;
    @Resource
    private ITiktokCelebrityRuleService ruleService;

    @Async
    public void asyncRefreshUnallocatedQty(Integer platformType) {
        System.out.println("【异步任务】统计未分配标签数量，开始时间：" + DateUtils.getDate("YYYY-MM-dd HH:mm:ss"));

        try {
            // 获取每个平台最小粉丝数限制
            Integer tkMinNum = ruleService.getMinNum(CommonConstant.TK);
            Integer ytMinNum = ruleService.getMinNum(CommonConstant.YT);
            Integer igMinNum = ruleService.getMinNum(CommonConstant.IG);

            // 组装 SQL 条件
            String tkSql = " AND author_follower_count > " + tkMinNum;
            String ytSql = " AND followers_num > " + ytMinNum;
            String igSql = " AND follower_count > " + igMinNum;

            int allotMaxDays = tkTagsService.getAllotExpireDays();
            Date date = new Date();

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // 根据 platformType 添加对应的任务
            if (platformType == null || platformType == 2) {
                futures.add(CompletableFuture.runAsync(() -> {
                    System.out.println("TK平台开始处理...");
                    processLargeDataWithTempTable(2,
                            () -> tkCelebrityTagsService.getNotAllotTagsList(allotMaxDays, tkSql), date);
                }));
            }

            if (platformType == null || platformType == 1) {
                futures.add(CompletableFuture.runAsync(() -> {
                    System.out.println("YT平台开始处理...");
                    processLargeDataWithTempTable(1,
                            () -> ytCelebrityTagsService.getNotAllotTagsList(allotMaxDays, ytSql), date);
                }));
            }

            if (platformType == null || platformType == 0) {
                futures.add(CompletableFuture.runAsync(() -> {
                    System.out.println("IG平台开始处理...");
                    processLargeDataWithTempTable(0,
                            () -> igCelebrityTagsService.getNotAllotTagsList(allotMaxDays, igSql), date);
                }));
            }

            if (futures.isEmpty()) {
                System.err.println("【异步任务】无效的 platformType：" + platformType);
                return;
            }

            // 等待所有选中的平台任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            System.out.println("【异步任务】统计未分配标签数量，结束时间：" + DateUtils.getDate("YYYY-MM-dd HH:mm:ss"));

        } catch (Exception e) {
            System.err.println("【异步任务】批量处理失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理大数据量平台的更新逻辑 (例如 TK)
     * 采用 "临时表 + 批量更新" 的高效策略
     *
     * @param platformType 平台名称 ("TK")
     * @param updateList   提供数据的函数式接口，返回 List<Map<String, Object>>
     * @param snapshotTime 快照时间
     */
    public void processLargeDataWithTempTable(Integer platformType, Supplier<List<KolTagUpdateDTO>> updateList, Date snapshotTime) {
        try {
            List<KolTagUpdateDTO> kolTagUpdateDTOS = updateList.get();
            if (kolTagUpdateDTOS.isEmpty()) {
                System.out.println(platformType + " 平台没有需要更新的未分配标签数据。");
                return;
            }

            kolTagUpdateDTOS.forEach(dto -> dto.setSnapshotTime(snapshotTime));

            // 使用线程ID和时间戳确保临时表名唯一
            String tempTableName = "temp_kol_tags_update_" + Thread.currentThread().getId() + "_" + System.currentTimeMillis();
            System.out.println(platformType + " 平台开始批量插入数据到临时表 " + tempTableName);

            // 在同一个事务中执行所有操作
            kolTagsService.processWithTempTable(tempTableName, kolTagUpdateDTOS, platformType);

        } catch (Exception e) {
            System.err.println(platformType + " 平台处理失败: " + e.getMessage());
            throw new RuntimeException(platformType + " 平台处理失败", e);
        }
    }

}