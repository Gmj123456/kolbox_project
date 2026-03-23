package org.jeecg.modules.instagram.storemerchant.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.StoreTaskStatusEnum;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityService;
import org.jeecg.modules.instagram.storebasics.entity.StoreLevel;
import org.jeecg.modules.instagram.storebasics.service.IStoreLevelService;
import org.jeecg.modules.instagram.storemerchant.entity.StoreTask;
import org.jeecg.modules.instagram.storemerchant.model.StoreTaskModel;
import org.jeecg.modules.instagram.storemerchant.service.IStoreTaskService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @Description: 爬取任务
 * @Author: nqr
 * @Date: 2020-04-21
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "爬取任务")
@RestController
@RequestMapping("/instagram/storeTask")
public class StoreTaskController extends JeecgController<StoreTask, IStoreTaskService> {
    @Autowired
    private IStoreTaskService storeTaskService;

    @Autowired
    private IStoreCelebrityService storeCelebrityService;

    @Autowired
    private IStoreLevelService storeLevelService;

    /**
     * 分页列表查询
     *
     * @param storeTask
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "爬取任务-分页列表查询")
@Operation(summary = "爬取任务-分页列表查询", description = "爬取任务-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreTask storeTask,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) throws ParseException {
        String sql = "";
        if (oConvertUtils.isNotEmpty(storeTask.getFirstExecDate())) {
            String date = DateUtils.formatDate(storeTask.getFirstExecDate());
            sql = "DATE_FORMAT(first_exec_date, '%Y-%m-%d') = DATE_FORMAT('" + date + "','%Y-%m-%d')";
            System.err.println(sql);
        }
        storeTask.setFirstExecDate(null);
        QueryWrapper<StoreTask> queryWrapper = QueryGenerator.initQueryWrapper(storeTask, req.getParameterMap());
        if (oConvertUtils.isNotEmpty(sql)) {
            queryWrapper.apply(sql);
        }
        Page<StoreTask> page = new Page<>(pageNo, pageSize);
        IPage<StoreTask> pageList = storeTaskService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeTaskModel
     * @return
     */
    @AutoLog(value = "爬取任务-添加")
@Operation(summary = "爬取任务-添加", description = "爬取任务-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreTaskModel storeTaskModel) {
        Integer platformType = storeTaskModel.getPlatformType();
        String word;
        if (storeTaskModel.getFlag() == 0) {
            LambdaQueryWrapper<StoreTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (platformType == 0) {
                word = storeTaskModel.getFirstKolAccount();
                lambdaQueryWrapper.eq(StoreTask::getFirstKolAccount, word);
            } else {
                word = storeTaskModel.getKeyword();
                lambdaQueryWrapper.eq(StoreTask::getKeyword, word);
            }
            List<StoreTask> list = storeTaskService.list(lambdaQueryWrapper);
            if (list.size() > 0) {
                return Result.error("已存在 " + word + " 的任务！");
            }
        }
        storeTaskService.save(storeTaskModel);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeTask
     * @return
     */
    @AutoLog(value = "爬取任务-编辑")
@Operation(summary = "爬取任务-编辑", description = "爬取任务-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreTask storeTask) {
        storeTaskService.updateById(storeTask);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "爬取任务-通过id删除")
@Operation(summary = "爬取任务-通过id删除", description = "爬取任务-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        StoreTask storeTask = storeTaskService.getById(id);
        if (storeTask.getTaskStatus() == StoreTaskStatusEnum.RUNNING.getCode()) {
            return Result.error("进行中的任务不能删除!");
        }
        if (storeTask.getTaskStatus() == StoreTaskStatusEnum.SUCCESSFULLY.getCode()) {
            return Result.error("已完成的任务不能删除!");
        }
        storeTaskService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "爬取任务-批量删除")
@Operation(summary = "爬取任务-批量删除", description = "爬取任务-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        List<StoreTask> storeTasksList = (List<StoreTask>) storeTaskService.listByIds(idList);
        List<String> taskNameList = new ArrayList<>();
        List<String> taskIdList = new ArrayList<>();
        for (StoreTask storeTask : storeTasksList) {
            if (storeTask.getTaskStatus() == StoreTaskStatusEnum.RUNNING.getCode() || storeTask.getTaskStatus() == StoreTaskStatusEnum.SUCCESSFULLY.getCode()) {
                taskNameList.add(storeTask.getTaskName());
            } else {
                taskIdList.add(storeTask.getId());
            }
        }
        if (taskIdList.size() > 0) {
            this.storeTaskService.removeByIds(taskIdList);
        }
        if (taskNameList.size() > 0) {
            return Result.ok(taskNameList);
        }
        return Result.ok("批量删除成功！");
    }

    /**
     * 批量重新执行
     *
     * @param jsonObject
     * @return
     */
    @AutoLog(value = "爬取任务-批量重新执行")
@Operation(summary = "爬取任务-批量重新执行", description = "爬取任务-批量重新执行")
    @PostMapping(value = "/agentBatch")
    public Result<?> agentBatch(@RequestBody JSONObject jsonObject) {
        String ids = (String) jsonObject.get("ids");
        List<String> idList = Arrays.asList(ids.split(","));
        List<StoreTask> storeTasksList = (List<StoreTask>) storeTaskService.listByIds(idList);
        List<String> taskNameList = new ArrayList<>();
        List<String> taskIdList = new ArrayList<>();
        for (StoreTask storeTask : storeTasksList) {
            if (storeTask.getTaskStatus() == StoreTaskStatusEnum.RUNNING.getCode() || storeTask.getTaskStatus() == StoreTaskStatusEnum.SUCCESSFULLY.getCode()) {
                taskNameList.add(storeTask.getTaskName());
            } else {
                taskIdList.add(storeTask.getId());
            }
        }
        if (taskIdList.size() > 0) {
            storeTaskService.agentStatusById(taskIdList);
        }
        if (taskNameList.size() > 0) {
            return Result.ok(taskNameList);
        }
        return Result.ok("重新执行成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "爬取任务-通过id查询")
@Operation(summary = "爬取任务-通过id查询", description = "爬取任务-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreTask storeTask = storeTaskService.getById(id);
        return Result.ok(storeTask);
    }

    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreTask storeTask) {
        return super.exportXls(request, storeTask, StoreTask.class, "爬取任务");
    }

    /**
     * 通过excel导入数据
     *
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        String userIdBytoken = getUserIdByToken();
        String userNameBytoken = getUserNameByToken();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            //params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<StoreTask> list = ExcelImportUtil.importExcel(file.getInputStream(), StoreTask.class, params);
                for (StoreTask storeTask : list) {
                    StoreTask storeTaskNew = new StoreTask();
                    BeanUtils.copyProperties(storeTask, storeTaskNew);
                    String taskName;
                    if (storeTask.getPlatformType() == 0) {
                        taskName = "IG" + System.currentTimeMillis();
                    } else {
                        taskName = "YT" + System.currentTimeMillis();
                    }
                    storeTaskNew.setTaskName(taskName);
                    storeTaskNew.setTaskType(0);
                    storeTaskNew.setTaskStatus(0);
                    storeTaskNew.setCreateBy(userNameBytoken);
                    storeTaskNew.setCreateTime(new Date());
                    storeTaskService.save(storeTaskNew);
                }
               /* long start = System.currentTimeMillis();
                storeTaskService.saveBatch(storeTaskList);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                System.out.println(("消耗时间" + (System.currentTimeMillis() - start) + "毫秒"));*/
                return Result.ok("文件导入成功！数据行数：" + list.size());
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
        return Result.error("文件导入失败！");
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importSeedExcel", method = RequestMethod.POST)
    public Result<?> importSeedExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<String> listError = new ArrayList<>();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {               
                List<StoreTask> list = ExcelImportUtil.importExcel(file.getInputStream(), StoreTask.class, params);
                List<StoreTask> storeTaskList = new ArrayList<>();
                List<StoreLevel> storeLevelList = new ArrayList<>();
                List<StoreCelebrity> storeCelebrities = new ArrayList<>();
                for (StoreTask storeTask : list) {
                    String firstKolAccount = storeTask.getFirstKolAccount();
                    LambdaQueryWrapper<StoreCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(StoreCelebrity::getAccount, firstKolAccount);
                    List<StoreCelebrity> celebrityList = storeCelebrityService.list(lambdaQueryWrapper);
                    //判断该用户存不存在，存在则添加到层级表中，创建任务
                    if (celebrityList != null && celebrityList.size() > 0) {
                        StoreCelebrity storeCelebrity1 = celebrityList.get(0);
                        //判断层级表中是否存在该用户
                        LambdaQueryWrapper<StoreLevel> levelLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        levelLambdaQueryWrapper.eq(StoreLevel::getUserId, storeCelebrity1.getId());
                        List<StoreLevel> levelList = storeLevelService.list(levelLambdaQueryWrapper);
                        if (levelList != null && levelList.size() > 0) {
                            listError.add(storeCelebrity1.getAccount());
                            continue;
                        }
                        String taskId = UUIDGenerator.generate();
                        //创建层级对象
                        StoreLevel storeLevel = new StoreLevel();
                        storeLevel.setTaskId(taskId);
                        storeLevel.setUserId(storeCelebrity1.getId());
                        storeLevelList.add(storeLevel);
                        //创建任务对象
                        StoreTask storeTaskNew = new StoreTask();
                        storeTaskNew.setId(taskId);
                        storeTaskNew.setTaskName("IG-" + storeCelebrity1.getAccount());
                        storeTaskNew.setPlatformType(0);
                        storeTaskNew.setFirstKolAccount(storeCelebrity1.getAccount());
                        //1 表示需要保存层级关系
                        storeTaskNew.setIsLevel(1);
                        storeTaskList.add(storeTaskNew);
                        //添加带货能力标签为 强
                        StoreCelebrity storeCelebrity = new StoreCelebrity();
                        storeCelebrity.setId(storeCelebrity1.getId());
                        storeCelebrity.setNewTag("强");
                        storeCelebrities.add(storeCelebrity1);
                    }
                }
                if (listError.size() > 0) {
                    Result<List<String>> result = new Result<>();
                    result.setSuccess(false);
                    result.setMessage("列表中已存在此数据！");
                    result.setResult(listError);
                    return result;
                }
                long start = System.currentTimeMillis();
                //增加爬取任务
                storeTaskService.saveBatch(storeTaskList);
                //保存到层级表中
                storeLevelService.saveBatch(storeLevelList);
                //修改带货能力标签
                storeCelebrityService.updateBatchById(storeCelebrities);
                System.err.println(("消耗时间" + (System.currentTimeMillis() - start) + "毫秒"));
                return Result.ok("文件导入成功！数据行数：" + list.size());
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
        return Result.error("文件导入失败！");
    }

    /**
     * 功能描述:修改任务状态
     *
     * @Author: nqr
     * @Date 2020-06-29 13:35:25
     */
    @AutoLog(value = "修改任务状态")
@Operation(summary = "修改任务状态", description = "修改任务状态")
    @DeleteMapping(value = "/updateTaskStatus")
    public Result<?> updateTaskStatus(@RequestParam(name = "id", required = true) String id) {
        StoreTask storeTask = storeTaskService.getById(id);
        if (storeTask.getTaskStatus() == StoreTaskStatusEnum.RUNNING.getCode()) {
            return Result.error("进行中的任务不能修改!");
        }
        if (storeTask.getTaskStatus() == StoreTaskStatusEnum.SUCCESSFULLY.getCode()) {
            return Result.error("已完成的任务不能修改!");
        }
        LambdaUpdateWrapper<StoreTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(StoreTask::getId, id);
        lambdaUpdateWrapper.set(StoreTask::getTaskStatus, StoreTaskStatusEnum.NOT_START.getCode());
        storeTaskService.update(lambdaUpdateWrapper);
        return Result.ok("修改成功!");
    }


}
