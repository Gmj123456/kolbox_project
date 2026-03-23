package org.jeecg.modules.instagram.storeseller.storeuser.controller;

import java.util.Arrays;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserCounselor;
import org.jeecg.modules.instagram.storeseller.storeuser.model.StoreUserCounselorModel;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserCounselorService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @Description: 商家顾问管理
 * @Author: nqr
 * @Date: 2023-09-14
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家顾问管理")
@RestController
@RequestMapping("/storeUserCounselor")
public class StoreUserCounselorController extends JeecgController<StoreUserCounselor, IStoreUserCounselorService> {
    @Autowired
    private IStoreUserCounselorService storeUserCounselorService;

    /**
     * 分页列表查询
     *
     * @param storeUserCounselor
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家顾问管理-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "商家顾问管理-" + SystemConstants.PAGE_LIST_QUERY, description = "商家顾问管理-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreUserCounselor storeUserCounselor,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        storeUserCounselor.setSysUserId(getUserIdByToken());
        LambdaQueryWrapper<StoreUserCounselor> queryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(storeUserCounselor.getUsername())) {
            queryWrapper.like(StoreUserCounselor::getUsername, storeUserCounselor.getUsername());
        }
        if (oConvertUtils.isNotEmpty(storeUserCounselor.getPhone())) {
            queryWrapper.like(StoreUserCounselor::getPhone, storeUserCounselor.getPhone());
        }
        queryWrapper.eq(StoreUserCounselor::getSysUserId, getUserIdByToken());
        Page<StoreUserCounselor> page = new Page<>(pageNo, pageSize);
        IPage<StoreUserCounselor> pageList = storeUserCounselorService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @AutoLog(value = "商家顾问管理-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "商家顾问管理-" + SystemConstants.PAGE_LIST_QUERY, description = "商家顾问管理-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/getList")
    public Result<?> getList(StoreUserCounselor storeUserCounselor) {
        LambdaQueryWrapper<StoreUserCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserCounselor::getSysUserId, storeUserCounselor.getSysUserId());
        List<StoreUserCounselor> list = storeUserCounselorService.list(queryWrapper);
        return Result.ok(list);
    }

    /**
     * 添加
     *
     * @param storeUserCounselor
     * @return
     */
    @AutoLog(value = "商家顾问管理-" + SystemConstants.ADD)
@Operation(summary = "商家顾问管理-" + SystemConstants.ADD, description = "商家顾问管理-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreUserCounselor storeUserCounselor) {
        storeUserCounselor.setSysUserId(oConvertUtils.isEmpty(storeUserCounselor.getCounselorUserId()) ? getUserIdByToken() : storeUserCounselor.getCounselorUserId());
        String username = storeUserCounselor.getUsername();
        String phone = storeUserCounselor.getPhone();
        LambdaQueryWrapper<StoreUserCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserCounselor::getUsername, username);
        queryWrapper.eq(StoreUserCounselor::getPhone, phone);
        storeUserCounselorService.list(queryWrapper).stream().findFirst().ifPresent(userCounselor -> storeUserCounselor.setId(userCounselor.getId()));
        storeUserCounselorService.saveOrUpdate(storeUserCounselor);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storeUserCounselor
     * @return
     */
    @AutoLog(value = "商家顾问管理-" + SystemConstants.EDIT)
@Operation(summary = "商家顾问管理-" + SystemConstants.EDIT, description = "商家顾问管理-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreUserCounselor storeUserCounselor) {
        String sysUserId = getUserIdByToken();
        String username = storeUserCounselor.getUsername();
        String phone = storeUserCounselor.getPhone();
        LambdaQueryWrapper<StoreUserCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserCounselor::getUsername, username);
        queryWrapper.eq(StoreUserCounselor::getPhone, phone);
        queryWrapper.ne(StoreUserCounselor::getId, storeUserCounselor.getId());
        StoreUserCounselor userCounselor = storeUserCounselorService.list(queryWrapper).stream().findFirst().orElse(null);
        if (userCounselor != null) {
            return Result.error("当前账号已存在！");
        }
        storeUserCounselorService.updateById(storeUserCounselor);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storeUserCounselorModel
     * @return
     */
    @AutoLog(value = "商家顾问管理-更换商家顾问" + SystemConstants.EDIT)
@Operation(summary = "商家顾问管理-更换商家顾问" + SystemConstants.EDIT, description = "商家顾问管理-更换商家顾问" + SystemConstants.EDIT)
    @PutMapping(value = "/editBatch")
    public Result<?> editBatch(@RequestBody StoreUserCounselorModel storeUserCounselorModel) {
        String userId = getUserIdByToken();
        String ids = storeUserCounselorModel.getIds();
        String sysUserId = storeUserCounselorModel.getSysUserId();
        if (userId.equals(sysUserId)) {
            return Result.error("更换成功");
        }
        List<String> list = Arrays.asList(ids.split(","));
        LambdaUpdateWrapper<StoreUserCounselor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(StoreUserCounselor::getId, list);
        updateWrapper.set(StoreUserCounselor::getSysUserId, sysUserId);
        storeUserCounselorService.update(updateWrapper);
        return Result.ok("更换成功");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家顾问管理-" + SystemConstants.DELETE_BY_ID)
@Operation(summary = "商家顾问管理-" + SystemConstants.DELETE_BY_ID, description = "商家顾问管理-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeUserCounselorService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家顾问管理-" + SystemConstants.DELETE_BATCH)
@Operation(summary = "商家顾问管理-" + SystemConstants.DELETE_BATCH, description = "商家顾问管理-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeUserCounselorService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家顾问管理-" + SystemConstants.QUERY_BY_ID)
@Operation(summary = "商家顾问管理-" + SystemConstants.QUERY_BY_ID, description = "商家顾问管理-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreUserCounselor storeUserCounselor = storeUserCounselorService.getById(id);
        return Result.ok(storeUserCounselor);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeUserCounselor
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreUserCounselor storeUserCounselor) {
        return super.exportXls(request, storeUserCounselor, StoreUserCounselor.class, "商家顾问管理");
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
        return super.importExcel(request, response, StoreUserCounselor.class);
    }

    /**
     * 判断当前商务人员下是否存在商家顾问
     *
     * @return
     */
    @AutoLog(value = "商家顾问管理-判断当前商务人员下是否存在商家顾问")
@Operation(summary = "商家顾问管理-判断当前商务人员下是否存在商家顾问", description = "商家顾问管理-判断当前商务人员下是否存在商家顾问")
    @GetMapping(value = "/checkOnlyCounselor")
    public Result<?> checkOnlyCounselor(@RequestParam(name = "username", required = true) String username) {
        String userId = getUserIdByToken();
        LambdaQueryWrapper<StoreUserCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserCounselor::getUsername, username);
        queryWrapper.eq(StoreUserCounselor::getSysUserId, userId);
        List<StoreUserCounselor> list = storeUserCounselorService.list(queryWrapper);
        return Result.ok(list.isEmpty());
    }

}
