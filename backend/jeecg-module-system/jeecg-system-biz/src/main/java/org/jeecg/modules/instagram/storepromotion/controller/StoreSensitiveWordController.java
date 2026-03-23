package org.jeecg.modules.instagram.storepromotion.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.NumberOfCopies;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.AverageAssignUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSensitiveWord;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSensitiveWordService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @Description: 敏感词表
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "敏感词表")
@RestController
@RequestMapping("/storeSensitiveWord/storeSensitiveWord")
public class StoreSensitiveWordController extends JeecgController<StoreSensitiveWord, IStoreSensitiveWordService> {
    @Autowired
    private IStoreSensitiveWordService storeSensitiveWordService;

    /**
     * 分页列表查询
     *
     * @param storeSensitiveWord
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "敏感词表-分页列表查询")
@Operation(summary = "敏感词表-分页列表查询", description = "敏感词表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreSensitiveWord storeSensitiveWord,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreSensitiveWord> queryWrapper = QueryGenerator.initQueryWrapper(storeSensitiveWord, req.getParameterMap());
        Page<StoreSensitiveWord> page = new Page<StoreSensitiveWord>(pageNo, pageSize);
        IPage<StoreSensitiveWord> pageList = storeSensitiveWordService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeSensitiveWord
     * @return
     */
    @AutoLog(value = "敏感词表-添加")
@Operation(summary = "敏感词表-添加", description = "敏感词表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreSensitiveWord storeSensitiveWord) {
        storeSensitiveWordService.save(storeSensitiveWord);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeSensitiveWord
     * @return
     */
    @AutoLog(value = "敏感词表-编辑")
@Operation(summary = "敏感词表-编辑", description = "敏感词表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreSensitiveWord storeSensitiveWord) {
        storeSensitiveWordService.updateById(storeSensitiveWord);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "敏感词表-通过id删除")
@Operation(summary = "敏感词表-通过id删除", description = "敏感词表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeSensitiveWordService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "敏感词表-批量删除")
@Operation(summary = "敏感词表-批量删除", description = "敏感词表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeSensitiveWordService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "敏感词表-通过id查询")
@Operation(summary = "敏感词表-通过id查询", description = "敏感词表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreSensitiveWord storeSensitiveWord = storeSensitiveWordService.getById(id);
        return Result.ok(storeSensitiveWord);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeSensitiveWord
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreSensitiveWord storeSensitiveWord) {
        return super.exportXls(request, storeSensitiveWord, StoreSensitiveWord.class, "敏感词表");
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        String userName = getUserNameByToken();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);

            return handleData(file, params, userName, start);
        }
        return Result.error("文件导入失败！");
    }



    /**
     * 处理导入文件数据,把数据分成几份使用多线程处理
     */
    public Result<?> handleData(MultipartFile file, ImportParams params, String userName, long start) {
        try {
            List<StoreSensitiveWord> storeSensitiveWordList = ExcelImportUtil.importExcel(file.getInputStream(), StoreSensitiveWord.class, params);
            List<List<StoreSensitiveWord>> averageAssignList;
            if (oConvertUtils.isEmpty(storeSensitiveWordList) || storeSensitiveWordList.size() == 0) {
                return Result.ok("无可导入数据!");
            }
            CountDownLatch countDownLatch = new CountDownLatch(NumberOfCopies.DEFAULT_NUMBER_OF_COPIES.getCode());
            averageAssignList = AverageAssignUtil.averageAssign(storeSensitiveWordList, NumberOfCopies.DEFAULT_NUMBER_OF_COPIES.getCode());
            for (int i = 0; i < averageAssignList.size(); i++) {
                List<StoreSensitiveWord> modelList = averageAssignList.get(i);
                //CountDownLatch闭锁,确保线程已经全部执行完毕
                Thread thread = new Thread(() -> {
                    try {
                        importSensitiveWord(modelList, userName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                });
                thread.start();
            }
            countDownLatch.await();
            log.info("共消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
            return Result.ok("文件导入完成!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("文件导入失败:" + e.getMessage());
        } finally {
            try {
                file.getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //批量新增
    public void importSensitiveWord(List<StoreSensitiveWord> list, String userName) {
        for (StoreSensitiveWord storeSensitiveWord : list) {
            storeSensitiveWord.setCreateBy(userName);
            storeSensitiveWord.setCreateTime(new Date());
        }
        storeSensitiveWordService.saveBatch(list);
        log.info("导入敏感词共: " + list.size() + "条~~~~~~~");
    }

}
