package org.jeecg.modules.instagram.storecelebrity.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.subject.Subject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeDetail;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityCounselorChangeLog;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivate;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateCounselor;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityCounselorChangeDetailService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateService;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPrivateCounselorModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateCounselorService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserContactEmailService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static org.jeecg.common.system.util.JwtUtil.getUsername;

/**
 * @Description: 私有网红网红顾问签约表
 * @Author: nqr
 * @Date: 2023-09-05
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "私有网红网红顾问签约表")
@RestController
@RequestMapping("/privateCounselor")
public class StoreCelebrityPrivateCounselorController
        extends JeecgController<StoreCelebrityPrivateCounselor, IStoreCelebrityPrivateCounselorService> {
    @Autowired
    private IStoreCelebrityPrivateCounselorService counselorService;
    @Autowired
    private IStoreCelebrityPrivateService celebrityPrivateService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IStoreUserContactEmailService contactEmailService;
    @Resource
    private IStoreCelebrityCounselorChangeDetailService changeDetailService;

    /**
     * 分页列表查询
     *
     * @param storeCelebrityPrivateCounselor
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "私有网红网红顾问签约表-" + SystemConstants.PAGE_LIST_QUERY)
@Operation(summary = "私有网红网红顾问签约表-" + SystemConstants.PAGE_LIST_QUERY, description = "私有网红网红顾问签约表-"
            + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityPrivateCounselor storeCelebrityPrivateCounselor,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreCelebrityPrivateCounselor> queryWrapper = QueryGenerator
                .initQueryWrapper(storeCelebrityPrivateCounselor, req.getParameterMap());
        Page<StoreCelebrityPrivateCounselor> page = new Page<StoreCelebrityPrivateCounselor>(pageNo, pageSize);
        IPage<StoreCelebrityPrivateCounselor> pageList = counselorService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @AutoLog(value = "私有网红网红顾问签约表-查询所有网红顾问")
@Operation(summary = "私有网红网红顾问签约表-查询所有网红顾问", description = "私有网红网红顾问签约表-查询所有网红顾问")
    @GetMapping(value = "/getAllCelebrityCounselor")
    public Result<?> getAllCelebrityCounselor() {
        List<String> list = counselorService.list().stream()
                .map(StoreCelebrityPrivateCounselor::getCelebrityCounselorId).distinct().collect(Collectors.toList());
        List<SysUser> sysUsers = (List<SysUser>) userService.listByIds(list);
        return Result.ok(sysUsers);
    }

    /**
     * 添加
     *
     * @param storeCelebrityPrivateCounselor
     * @return
     */
    @AutoLog(value = "私有网红网红顾问签约表-" + SystemConstants.ADD)
@Operation(summary = "私有网红网红顾问签约表-" + SystemConstants.ADD, description = "私有网红网红顾问签约表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebrityPrivateCounselor storeCelebrityPrivateCounselor) {
        counselorService.save(storeCelebrityPrivateCounselor);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storeCelebrityPrivateCounselor
     * @return
     */
    @AutoLog(value = "私有网红网红顾问签约表-" + SystemConstants.EDIT)
@Operation(summary = "私有网红网红顾问签约表-" + SystemConstants.EDIT, description = "私有网红网红顾问签约表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    @RequiresPermissions(value = {"user:adviser", "editDepart"}, logical = Logical.OR)
    @Transactional(rollbackFor = Exception.class)
    public Result<?> edit(@RequestBody StoreCelebrityPrivateCounselor storeCelebrityPrivateCounselor) {
        String userId = getUserIdByToken();
        // LoginUser sysUser = sysBaseAPI.getUserById(userId);
        String celebrityPrivateId = storeCelebrityPrivateCounselor.getCelebrityPrivateId();
        // 判断当前网红顾问是否修改别人的
        String celebrityCounselorId = storeCelebrityPrivateCounselor.getCelebrityCounselorId();
        LoginUser loginUser = sysBaseAPI.getUserById(celebrityCounselorId);
        Integer isLeave = loginUser.getIsLeave();
        String celebrityCounselorIdNew = celebrityCounselorId;
        String celebrityCounselorName = loginUser.getRealname();

        // 判断是否包含权限
        if (!celebrityCounselorId.equals(userId)) {
            // 判断当前网红顾问是否离职
            if (!hasPermission("editDepart") || isLeave != 1) {
                return Result.error("只允许修改自己的私有网红！");
            }
        }
        /*
         * celebrityCounselorIdNew = userId;
         * celebrityCounselorName = sysUser.getRealname();
         * StoreCelebrityPrivateCounselor counselor =
         * counselorService.getByCelebrityCounselorId(celebrityCounselorIdNew,
         * celebrityPrivateId);
         * if (counselor == null) {
         * counselor = new StoreCelebrityPrivateCounselor();
         * // 创建网红顾问信息
         * counselor.setId(IdWorker.get32UUID());
         * counselor.setCelebrityPrivateId(celebrityPrivateId);
         * counselor.setCelebrityCounselorId(celebrityCounselorIdNew);
         * counselor.setCelebrityCounselorName(celebrityCounselorName);
         * counselor.setEmail(storeCelebrityPrivateCounselor.getEmail());
         * counselor.setContractAmount(storeCelebrityPrivateCounselor.getContractAmount(
         * ));
         * counselor.setSort(1);
         * } else {
         * counselor.setEmail(storeCelebrityPrivateCounselor.getEmail());
         * counselor.setContractAmount(storeCelebrityPrivateCounselor.getContractAmount(
         * ));
         * }
         * counselorService.saveOrUpdate(counselor);
         * }else{
         * counselorService.saveOrUpdate(storeCelebrityPrivateCounselor);
         */

        counselorService.saveOrUpdate(storeCelebrityPrivateCounselor);

        List<StoreCelebrityPrivateCounselor> celebrityPrivateCounselors = counselorService.lambdaQuery()
                .eq(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, celebrityPrivateId).list();
        Optional<StoreCelebrityPrivateCounselor> counselorOptional = celebrityPrivateCounselors.stream()
                .filter(c -> c.getContractTime() != null)
                .max(Comparator.comparing(StoreCelebrityPrivateCounselor::getContractTime));
        if (counselorOptional.isPresent()) {
            StoreCelebrityPrivateCounselor maxContractTimeCounselor = counselorOptional.get();
            StoreCelebrityPrivate celebrityPrivateNew = new StoreCelebrityPrivate();
            celebrityPrivateNew.setId(celebrityPrivateId);
            celebrityPrivateNew.setEmail(maxContractTimeCounselor.getEmail());
            celebrityPrivateNew.setCelebrityCounselorId(maxContractTimeCounselor.getCelebrityCounselorId());
            celebrityPrivateNew.setCelebrityCounselorName(maxContractTimeCounselor.getCelebrityCounselorName());
            celebrityPrivateNew.setContractAmount(maxContractTimeCounselor.getContractAmount());
            celebrityPrivateNew.setCelebrityContactEmail(maxContractTimeCounselor.getCelebrityContactEmail());
            celebrityPrivateNew.setCooperationTime(maxContractTimeCounselor.getContractTime());
            celebrityPrivateService.updateById(celebrityPrivateNew);
        }

        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    public boolean hasPermission(String permissionString) {
        // 获取当前Subject
        Subject subject = SecurityUtils.getSubject();
        // 创建一个权限对象，这里使用了WildcardPermission，但也可以是其他类型的权限
        Permission permission = new WildcardPermission(permissionString);
        // 判断Subject是否有该权限
        return subject.isPermitted(permission);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "私有网红网红顾问签约表-" + SystemConstants.DELETE_BY_ID)
@Operation(summary = "私有网红网红顾问签约表-" + SystemConstants.DELETE_BY_ID, description = "私有网红网红顾问签约表-"
            + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityPrivateCounselor privateCounselor = counselorService.getById(id);
        String celebrityPrivateId = privateCounselor.getCelebrityPrivateId();
        String celebrityCounselorId = privateCounselor.getCelebrityCounselorId();
        // 判断是否只对应一个网红顾问
        LambdaQueryWrapper<StoreCelebrityPrivateCounselor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, celebrityPrivateId);
        List<StoreCelebrityPrivateCounselor> privateCounselors = counselorService.list(queryWrapper);
        // 如果只有一个，则不能删除
        if (privateCounselors.size() == 1) {
            return Result.error("至少保留一个网红顾问！");
        }
        // 查询网红身上是否是当前网红顾问
        StoreCelebrityPrivate celebrityPrivate = celebrityPrivateService
                .getById(privateCounselor.getCelebrityPrivateId());
        if (celebrityPrivate.getCelebrityCounselorId().equals(celebrityCounselorId)) {
            List<StoreCelebrityPrivateCounselor> counselorList = privateCounselors.stream()
                    .filter(item -> !item.getId().equals(id))
                    .sorted(
                            Comparator.comparing(
                                    StoreCelebrityPrivateCounselor::getContractTime,
                                    Comparator.nullsLast(Comparator.naturalOrder())// 空值排在最后,防止历史数据报错
                            ).reversed())
                    .collect(Collectors.toList());
            StoreCelebrityPrivateCounselor counselor = counselorList.get(0);
            // 修改网红身上的网红顾问
            celebrityPrivate.setCelebrityCounselorId(counselor.getCelebrityCounselorId());
            celebrityPrivate.setCelebrityCounselorName(counselor.getCelebrityCounselorName());
            celebrityPrivate.setEmail(counselor.getEmail());
            celebrityPrivate.setContractAmount(counselor.getContractAmount());
            celebrityPrivateService.updateById(celebrityPrivate);
        }
        counselorService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "私有网红网红顾问签约表-" + SystemConstants.DELETE_BATCH)
@Operation(summary = "私有网红网红顾问签约表-" + SystemConstants.DELETE_BATCH, description = "私有网红网红顾问签约表-"
            + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.counselorService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "私有网红网红顾问签约表-" + SystemConstants.QUERY_BY_ID)
@Operation(summary = "私有网红网红顾问签约表-" + SystemConstants.QUERY_BY_ID, description = "私有网红网红顾问签约表-"
            + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityPrivateCounselor storeCelebrityPrivateCounselor = counselorService.getById(id);
        return Result.ok(storeCelebrityPrivateCounselor);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "私有网红网红顾问签约表-" + SystemConstants.QUERY_BY_ID)
@Operation(summary = "私有网红网红顾问签约表-" + SystemConstants.QUERY_BY_ID, description = "私有网红网红顾问签约表-"
            + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryByCelebrityPrivateId")
    public Result<?> queryByCelebrityPrivateId(@RequestParam(name = "id", required = true) String id) {
        List<StoreCelebrityPrivateCounselorModel> privateCounselors = counselorService.queryByCelebrityPrivateId(id);
        return Result.ok(privateCounselors);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebrityPrivateCounselor
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request,
                                  StoreCelebrityPrivateCounselor storeCelebrityPrivateCounselor) {
        return super.exportXls(request, storeCelebrityPrivateCounselor, StoreCelebrityPrivateCounselor.class,
                "私有网红网红顾问签约表");
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
        return super.importExcel(request, response, StoreCelebrityPrivateCounselor.class);
    }

    /**
     * 私有网红-调整顾问
     *
     * @return Result对象
     */
    @AutoLog(value = "私有网红-调整顾问")
@Operation(summary = "私有网红-调整顾问", description = "私有网红-调整顾问")
    @PostMapping(value = "/updateCelebrityCounselor")
    public Result<?> updateCelebrityCounselor(@RequestBody JSONObject jsonObject) {
        // 被调整顾问Id
        String celebrityCounselorOldId = jsonObject.getString("celebrityCounselorOldId");
        // 调整顾问Id
        String celebrityCounselorId = jsonObject.getString("celebrityCounselorId");
        // 需调整网红
        String celebrityPrivateIds = jsonObject.getString("celebrityPrivateIds");
        // 被调整顾问邮箱
        String contactEmailIdOld = jsonObject.getString("contactEmailIdOld");
        // 调整顾问邮箱
        String contactEmailId = jsonObject.getString("contactEmailId");
        // 是否合作 1已合作 0已建联
        int isCooperation = Optional.ofNullable(jsonObject.getInteger("isCooperation")).orElse(0);

        List<String> privateIds = Arrays.asList(celebrityPrivateIds.split(","));
        SysUser counselorOldUser = userService.getById(celebrityCounselorOldId);
        SysUser counselorUser = userService.getById(celebrityCounselorId);
        // StoreUserContactEmail contactEmailOld =
        // Optional.ofNullable(contactEmailService.getById(contactEmailIdOld)).orElse(new
        // StoreUserContactEmail());
        StoreUserContactEmail contactEmail = contactEmailService.getById(contactEmailId);

        // 验证参数
        Result<?> result = updateCelebrityCounselorCheckParams(celebrityCounselorOldId, counselorOldUser, privateIds,
                celebrityCounselorId, counselorUser, contactEmail, isCooperation);
        if (!result.isSuccess()) {
            return result;
        }

        // 查询原私有网红顾问列表
        List<StoreCelebrityPrivateCounselor> privateOldCounselorList = counselorService.lambdaQuery()
                .eq(StoreCelebrityPrivateCounselor::getCelebrityCounselorId, celebrityCounselorOldId)
                .in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, privateIds)
                .list();

        // 验证私有网红是否存在于被替换网红顾问列表中
        List<String> missingPrivateIds = privateIds.stream()
                .filter(privateId -> privateOldCounselorList.stream()
                        .noneMatch(counselor -> counselor.getCelebrityPrivateId().equals(privateId)))
                .collect(Collectors.toList());
        if (!missingPrivateIds.isEmpty()) {
            List<StoreCelebrityPrivate> storeCelebrityPrivates = (List<StoreCelebrityPrivate>) celebrityPrivateService
                    .listByIds(missingPrivateIds);
            return Result.error("以下私有网红不存在于被替换网红顾问列表中：" + storeCelebrityPrivates.stream()
                    .map(StoreCelebrityPrivate::getAccount).collect(Collectors.joining(",")));
        }

        // 查询新的网红顾问私有网红是否存在
        List<StoreCelebrityPrivateCounselor> privateNewCounselorList = counselorService.lambdaQuery()
                .eq(StoreCelebrityPrivateCounselor::getCelebrityCounselorId, celebrityCounselorId)
                .in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, privateIds)
                .list();

        // 构造需要更新和删除的数据列表
        List<StoreCelebrityPrivateCounselor> counselorDelList = new ArrayList<>();
        List<StoreCelebrityPrivateCounselor> counselorSaveList = new ArrayList<>();
        List<StoreCelebrityCounselorChangeDetail> counselorChangeDetails = new ArrayList<>();
        List<StoreUserContactEmail> contactEmailNewList = new ArrayList<>();

        StoreCelebrityCounselorChangeLog counselorChangeLog = new StoreCelebrityCounselorChangeLog();
        counselorChangeLog.setId(IdWorker.get32UUID());
        counselorChangeLog.setOperatorId(getUserIdByToken());
        counselorChangeLog.setOperatorName(getUserNameByToken());
        counselorChangeLog.setFromCounselorId(counselorOldUser.getId());
        counselorChangeLog.setFromCounselorName(counselorOldUser.getUsername());
        counselorChangeLog.setFromContactEmail(oConvertUtils.isEmpty(contactEmailIdOld) ? ""
                : contactEmailService.getById(contactEmailIdOld).getContactEmail());
        counselorChangeLog.setToCounselorId(counselorUser.getId());
        counselorChangeLog.setToCounselorName(counselorUser.getUsername());
        counselorChangeLog.setToContactEmail(contactEmail == null
                ? oConvertUtils.isEmpty(contactEmailId) ? "" : counselorChangeLog.getFromContactEmail()
                : contactEmail.getContactEmail());
        counselorChangeLog.setIsCooperated(isCooperation);
        counselorChangeLog.setChangeStatus(1);

        // 处理两种情况的共同逻辑
        processCounselorTransfers(privateOldCounselorList, privateNewCounselorList,
                counselorUser, contactEmail, counselorSaveList, counselorDelList, isCooperation, contactEmailNewList);

        // 创建日志
        changeDetailService.createCounselorChangeDetail(counselorChangeLog, counselorSaveList, counselorDelList,
                counselorChangeDetails);

        // 更新私有网红最新网红顾问
        List<StoreCelebrityPrivate> celebrityPrivateList = updatePrivateCelebrityCounselors(privateIds,
                counselorSaveList, counselorDelList, counselorUser, contactEmail);

        counselorService.updateCounselorUser(celebrityPrivateList, counselorSaveList, counselorDelList,
                contactEmailNewList, counselorChangeLog, counselorChangeDetails);

        return Result.ok("修改成功");
    }

    /**
     * @description: 私有网红-更新最新网红顾问
     * @author: nqr
     * @date: 2025/10/30 16:05
     **/
    private List<StoreCelebrityPrivate> updatePrivateCelebrityCounselors(List<String> privateIds,
                                                                         List<StoreCelebrityPrivateCounselor> counselorSaveList,
                                                                         List<StoreCelebrityPrivateCounselor> counselorDelList, SysUser counselorUser,
                                                                         StoreUserContactEmail contactEmail) {
        // 查询被替换网红顾问的私有网红列表
        List<StoreCelebrityPrivate> celebrityPrivateList = celebrityPrivateService.lambdaQuery()
                .in(StoreCelebrityPrivate::getId, privateIds).list();

        // 查询私有网红顾问
        List<StoreCelebrityPrivateCounselor> privateCounselors = counselorService.lambdaQuery()
                .in(StoreCelebrityPrivateCounselor::getCelebrityPrivateId, privateIds).list();

        // 将待更新的顾问列表合并到查询结果中，确保使用最新的数据
        // 创建一个映射来跟踪每个顾问ID对应的顾问对象
        Map<String, StoreCelebrityPrivateCounselor> counselorIdMap = new HashMap<>();

        // 先将数据库查询到的顾问放入映射中
        for (StoreCelebrityPrivateCounselor counselor : privateCounselors) {
            counselorIdMap.put(counselor.getId(), counselor);
        }

        // 然后用待更新的顾问覆盖数据库中的顾问
        for (StoreCelebrityPrivateCounselor counselor : counselorSaveList) {
            counselorIdMap.put(counselor.getId(), counselor);
        }

        // 从映射中获取所有顾问对象
        List<StoreCelebrityPrivateCounselor> updatedPrivateCounselors = new ArrayList<>(counselorIdMap.values());

        // 按照网红ID分组顾问列表
        Map<String, List<StoreCelebrityPrivateCounselor>> counselorMap = updatedPrivateCounselors.stream()
                .collect(Collectors.groupingBy(StoreCelebrityPrivateCounselor::getCelebrityPrivateId));

        // 更新网红身上最新网红顾问
        celebrityPrivateList.forEach(celebrityPrivate -> {
            String privateId = celebrityPrivate.getId();
            // 获取该网红的所有顾问列表
            List<StoreCelebrityPrivateCounselor> counselors = counselorMap.getOrDefault(privateId, new ArrayList<>());

            // 过滤出更新后的顾问列表（排除被删除的记录）
            // 创建被删除顾问的ID集合以提高查找效率
            Set<String> delCounselorIds = counselorDelList.stream()
                    .map(StoreCelebrityPrivateCounselor::getId)
                    .collect(Collectors.toSet());

            List<StoreCelebrityPrivateCounselor> updatedCounselors = counselors.stream()
                    .filter(counselor -> !delCounselorIds.contains(counselor.getId()))
                    .collect(Collectors.toList());

            // 获取建联日期最新的顾问
            StoreCelebrityPrivateCounselor latestCounselor = updatedCounselors.stream()
                    .filter(counselor -> counselor.getContractTime() != null)
                    .max(Comparator.comparing(StoreCelebrityPrivateCounselor::getContractTime))
                    .orElse(null);

            // 如果没有找到有建联日期的顾问，则使用任意一个顾问
            if (latestCounselor == null && !updatedCounselors.isEmpty()) {
                latestCounselor = updatedCounselors.get(0);
            }

            // 设置最新的顾问信息到网红身上
            if (latestCounselor != null) {
                celebrityPrivate.setCelebrityCounselorId(latestCounselor.getCelebrityCounselorId());
                celebrityPrivate.setCelebrityCounselorName(latestCounselor.getCelebrityCounselorName());
                celebrityPrivate.setCelebrityContactEmail(latestCounselor.getCelebrityContactEmail());
                // 同时更新签约时间和签约费用
                celebrityPrivate.setContractTime(latestCounselor.getContractTime());
                celebrityPrivate.setContractAmount(latestCounselor.getContractAmount());
            } else {
                // 如果没有顾问信息，使用新顾问信息
                celebrityPrivate.setCelebrityCounselorId(counselorUser.getId());
                celebrityPrivate.setCelebrityCounselorName(counselorUser.getUsername());
                celebrityPrivate.setCelebrityContactEmail(contactEmail.getContactEmail());
            }
        });
        return celebrityPrivateList;
    }

    /**
     * @description: 创建建联邮箱
     * @author: nqr
     * @date: 2025/10/30 15:30
     **/
    private StoreUserContactEmail createContactEmail(StoreUserContactEmail contactEmailOld, SysUser counselorUser) {
        StoreUserContactEmail contactEmailNew = new StoreUserContactEmail();
        BeanUtils.copyProperties(contactEmailOld, contactEmailNew, CommonConstant.ignoreProperties);
        contactEmailNew.setId(IdWorker.get32UUID());
        contactEmailNew.setSysUserId(counselorUser.getId());
        contactEmailNew.setSysUserName(counselorUser.getUsername());
        return contactEmailNew;
    }

    /**
     * @description: 调整网红顾问判断参数
     * @author: nqr
     * @date: 2025/10/30 15:06
     **/
    private Result<?> updateCelebrityCounselorCheckParams(String celebrityCounselorOldId, SysUser counselorOldUser,
                                                          List<String> privateIds, String celebrityCounselorId, SysUser counselorUser,
                                                          StoreUserContactEmail contactEmail, int isCooperation) {
        if (celebrityCounselorOldId.equals(celebrityCounselorId)) {
            return Result.error("更换网红顾问不能相同！");
        }

        if (counselorOldUser == null) {
            return Result.error("未获取到被替换网红顾问！");
        }

        if (counselorUser == null) {
            return Result.error("未获取到替换网红顾问！");
        }

        if (privateIds.isEmpty()) {
            return Result.error("未获取到替换网红顾问的私有网红！");
        }

        if (isCooperation == 1 && contactEmail == null) {
            return Result.error("调整网红顾问建联邮箱不能为空！");
        }

        /*
         * if (isCooperation == 0 && oConvertUtils.isEmpty(contactEmailIdOld)) {
         * return Result.error("被调整网红顾问建联邮箱不能为空！");
         * }
         *
         * if (!contactEmail.getSysUserId().equals(celebrityCounselorId)) {
         * return Result.error("建联邮箱非替换网红顾问邮箱！");
         * }
         */

        return Result.ok();
    }

    /**
     * 处理网红顾问交接逻辑
     *
     * @param counselorOld      原始顾问信息
     * @param counselors        新顾问信息
     * @param counselorUser     新的顾问用户
     * @param contactEmail      联系邮箱
     * @param counselorSaveList 待保存的顾问列表
     * @param counselorDelList  待删除的顾问列表
     * @param isCooperation     是否合作 1已合作 0未合作
     * @return 处理后的新的顾问信息
     */
    private void processCounselorTransfer(StoreCelebrityPrivateCounselor counselorOld,
                                          StoreCelebrityPrivateCounselor counselors, SysUser counselorUser,
                                          StoreUserContactEmail contactEmail, List<StoreCelebrityPrivateCounselor> counselorSaveList,
                                          List<StoreCelebrityPrivateCounselor> counselorDelList, int isCooperation) {
        StoreCelebrityPrivateCounselor counselorNew = new StoreCelebrityPrivateCounselor();
        if (counselors == null) {
            BeanUtils.copyProperties(counselorOld, counselorNew, CommonConstant.ignoreProperties);
            counselorNew.setId(IdWorker.get32UUID());
            counselorNew.setCelebrityCounselorId(counselorUser.getId());
            counselorNew.setCelebrityCounselorName(counselorUser.getUsername());

            // 根据是否合作设置不同的邮箱信息
            if (isCooperation == 1) {
                // 已合作情况下，需要获取最低价格合同对应的邮箱
                ContractInfoResult contractInfoResult = getLowestPriceContractInfo(
                        counselorOld.getContractInfo(), null,
                        counselorOld.getContractTime(), null,
                        counselorOld.getCelebrityContactEmail(), null);
                counselorNew.setCelebrityContactEmail(contactEmail.getContactEmail());
                counselorNew.setContractTime(contractInfoResult.getContractTime());
            } else {
                // 未合作情况下，原建联信息邮箱
                if (contactEmail != null) {
                    counselorNew.setCelebrityContactEmail(counselorOld.getCelebrityContactEmail());
                }
            }

            counselorSaveList.add(counselorNew);
            counselorDelList.add(counselorOld);

        } else {
            // 当交接的网红顾问已经存在记录时，需要合并contractInfo内容
            // 获取历史顾问和新顾问的contractInfo内容
            String oldContractInfo = counselorOld.getContractInfo();
            String newContractInfo = counselors.getContractInfo();

            // 合并合同信息并获取价格最低的合同
            String mergedContractInfo = mergeContractInfo(oldContractInfo, newContractInfo);

            // 获取价格最低的合同对应的建联时间和邮箱
            ContractInfoResult contractInfoResult = getLowestPriceContractInfo(oldContractInfo, newContractInfo,
                    counselorOld.getContractTime(), counselors.getContractTime(),
                    counselorOld.getCelebrityContactEmail(), counselors.getCelebrityContactEmail());

            // 设置新纪录的相关信息
            BeanUtils.copyProperties(counselorOld, counselorNew, CommonConstant.ignoreProperties);
            counselorNew.setId(IdWorker.get32UUID());
            counselorNew.setCelebrityCounselorId(counselorUser.getId());
            counselorNew.setCelebrityCounselorName(counselorUser.getUsername());
            counselorNew.setContractInfo(mergedContractInfo);
            if (isCooperation == 1) {
                counselorNew.setCelebrityContactEmail(contactEmail.getContactEmail());
            } else {
                counselorNew.setCelebrityContactEmail(contractInfoResult.getContactEmail());
            }
            counselorNew.setContractTime(contractInfoResult.getContractTime());

            counselorDelList.add(counselorOld);
            counselorDelList.add(counselors);
            counselorSaveList.add(counselorNew);
        }
    }

    /**
     * 批量处理网红顾问交接逻辑
     *
     * @param privateOldCounselorList 原始顾问列表
     * @param privateNewCounselorList 新顾问列表
     * @param counselorUser           新的顾问用户
     * @param contactEmail            调整网红顾问建联邮箱
     * @param counselorSaveList       待保存的顾问列表
     * @param counselorDelList        待删除的顾问列表
     * @param isCooperation           是否合作
     */
    private void processCounselorTransfers(List<StoreCelebrityPrivateCounselor> privateOldCounselorList,
                                           List<StoreCelebrityPrivateCounselor> privateNewCounselorList, SysUser counselorUser,
                                           StoreUserContactEmail contactEmail, List<StoreCelebrityPrivateCounselor> counselorSaveList,
                                           List<StoreCelebrityPrivateCounselor> counselorDelList, int isCooperation,
                                           List<StoreUserContactEmail> contactEmailNewList) {
        // 处理每个原始顾问
        for (StoreCelebrityPrivateCounselor counselorOld : privateOldCounselorList) {
            StoreCelebrityPrivateCounselor counselors = privateNewCounselorList.stream()
                    .filter(counselorNew -> counselorNew.getCelebrityPrivateId()
                            .equals(counselorOld.getCelebrityPrivateId()))
                    .findFirst().orElse(null);

            processCounselorTransfer(counselorOld, counselors, counselorUser, contactEmail, counselorSaveList,
                    counselorDelList, isCooperation);
        }

        // 如果未合作且联系邮箱为空，则创建联系邮箱
        if (isCooperation == 0) {
            List<StoreUserContactEmail> contactEmails = contactEmailService.lambdaQuery()
                    .eq(StoreUserContactEmail::getSysUserId, counselorUser.getId()).list();
            List<String> emails = counselorSaveList.stream()
                    .map(StoreCelebrityPrivateCounselor::getCelebrityContactEmail).distinct()
                    .collect(Collectors.toList());
            emails.removeAll(
                    contactEmails.stream().map(StoreUserContactEmail::getContactEmail).collect(Collectors.toList()));
            if (!emails.isEmpty()) {
                List<StoreUserContactEmail> contactEmailsList = contactEmailService.lambdaQuery()
                        .in(StoreUserContactEmail::getContactEmail, emails).list();
                for (StoreUserContactEmail userContactEmail : contactEmailsList) {
                    StoreUserContactEmail contactEmailNew = new StoreUserContactEmail();
                    contactEmailNew.setId(IdWorker.get32UUID());
                    contactEmailNew.setSysUserId(counselorUser.getId());
                    contactEmailNew.setSysUserName(counselorUser.getUsername());
                    contactEmailNew.setContactEmail(userContactEmail.getContactEmail());
                    contactEmailNew.setEmailPassword(userContactEmail.getEmailPassword());
                    contactEmailNew.setEmailDisplayName(userContactEmail.getEmailDisplayName());
                    contactEmailNew.setEmailStatus(1);
                    contactEmailNew.setSortCode(userContactEmail.getSortCode());
                    contactEmailNew.setCreateBy(getUserNameByToken());
                    contactEmailNewList.add(contactEmailNew);
                }
            }
        }
    }

    /**
     * 合并两个顾问的合同信息，获取videoTag的并集，并为每个videoTag保留价格较低的合同
     *
     * @param oldContractInfo 历史顾问的合同信息
     * @param newContractInfo 新顾问的合同信息
     * @return 合并后的合同信息
     */
    private String mergeContractInfo(String oldContractInfo, String newContractInfo) {
        try {
            // 用于存储最终结果的Map，key为videoTag，value为价格较低的合同
            Map<String, JSONObject> mergedContracts = new HashMap<>();

            // 解析历史顾问的合同信息
            if (oConvertUtils.isNotEmpty(oldContractInfo)) {
                List<JSONObject> oldContracts = JSON.parseArray(oldContractInfo, JSONObject.class);
                if (oldContracts != null) {
                    for (JSONObject contract : oldContracts) {
                        String videoTag = contract.getString("videoTag");
                        BigDecimal contractAmount = contract.getBigDecimal("contractAmount");

                        if (oConvertUtils.isNotEmpty(videoTag)) {
                            // 如果该videoTag已存在，比较价格，保留较低的
                            if (mergedContracts.containsKey(videoTag)) {
                                BigDecimal existingAmount = mergedContracts.get(videoTag)
                                        .getBigDecimal("contractAmount");
                                if (existingAmount != null && contractAmount != null
                                        && contractAmount.compareTo(existingAmount) < 0) {
                                    // 新价格更低，替换
                                    mergedContracts.put(videoTag, contract);
                                }
                            } else {
                                // 该videoTag不存在，直接添加
                                mergedContracts.put(videoTag, contract);
                            }
                        }
                    }
                }
            }

            // 解析新顾问的合同信息，并与历史顾问的合同信息比较，保留价格较低的
            if (oConvertUtils.isNotEmpty(newContractInfo)) {
                List<JSONObject> newContracts = JSON.parseArray(newContractInfo, JSONObject.class);
                if (newContracts != null) {
                    for (JSONObject contract : newContracts) {
                        String videoTag = contract.getString("videoTag");
                        BigDecimal contractAmount = contract.getBigDecimal("contractAmount");

                        if (oConvertUtils.isNotEmpty(videoTag) && contractAmount != null) {
                            // 如果该videoTag已存在，比较价格，保留较低的
                            if (mergedContracts.containsKey(videoTag)) {
                                BigDecimal existingAmount = mergedContracts.get(videoTag)
                                        .getBigDecimal("contractAmount");
                                if (existingAmount != null && contractAmount.compareTo(existingAmount) < 0) {
                                    // 新价格更低，替换
                                    mergedContracts.put(videoTag, contract);
                                }
                            } else {
                                // 该videoTag不存在，直接添加
                                mergedContracts.put(videoTag, contract);
                            }
                        }
                    }
                }
            }

            // 将Map转换为List并序列化为JSON字符串
            List<JSONObject> result = new ArrayList<>(mergedContracts.values());
            return JSON.toJSONString(result);
        } catch (Exception e) {
            log.error("合并合同信息时发生错误", e);
            // 发生错误时返回历史顾问的合同信息
            return oldContractInfo;
        }
    }

    /**
     * 获取价格最低的合同对应的建联时间和邮箱
     *
     * @param oldContractInfo 历史顾问的合同信息
     * @param newContractInfo 新顾问的合同信息
     * @param oldContractTime 历史顾问的建联时间
     * @param newContractTime 新顾问的建联时间
     * @param oldContactEmail 历史顾问的邮箱
     * @param newContactEmail 新顾问的邮箱
     * @return 包含最低价格合同对应的建联时间和邮箱的结果对象
     */
    private ContractInfoResult getLowestPriceContractInfo(String oldContractInfo, String newContractInfo,
                                                          Date oldContractTime, Date newContractTime, String oldContactEmail, String newContactEmail) {
        try {
            BigDecimal lowestPrice = null;
            Date contractTime = oldContractTime; // 默认使用历史顾问的时间
            String contactEmail = oldContactEmail; // 默认使用历史顾问的邮箱

            // 解析历史顾问的合同信息
            if (oConvertUtils.isNotEmpty(oldContractInfo)) {
                List<JSONObject> oldContracts = JSON.parseArray(oldContractInfo, JSONObject.class);
                if (oldContracts != null) {
                    for (JSONObject contract : oldContracts) {
                        BigDecimal contractAmount = contract.getBigDecimal("contractAmount");
                        if (contractAmount != null) {
                            if (lowestPrice == null || contractAmount.compareTo(lowestPrice) < 0) {
                                lowestPrice = contractAmount;
                                contractTime = oldContractTime;
                                contactEmail = oldContactEmail;
                            }
                        }
                    }
                }
            }

            // 解析新顾问的合同信息
            if (oConvertUtils.isNotEmpty(newContractInfo)) {
                List<JSONObject> newContracts = JSON.parseArray(newContractInfo, JSONObject.class);
                if (newContracts != null) {
                    for (JSONObject contract : newContracts) {
                        BigDecimal contractAmount = contract.getBigDecimal("contractAmount");
                        if (contractAmount != null) {
                            if (lowestPrice == null || contractAmount.compareTo(lowestPrice) < 0) {
                                lowestPrice = contractAmount;
                                contractTime = newContractTime;
                                contactEmail = newContactEmail;
                            }
                        }
                    }
                }
            }

            return new ContractInfoResult(contractTime, contactEmail);
        } catch (Exception e) {
            log.error("获取最低价格合同信息时发生错误", e);
            // 发生错误时返回历史顾问的信息
            return new ContractInfoResult(oldContractTime, oldContactEmail);
        }
    }

    /**
     * 合同信息结果类
     */
    private static class ContractInfoResult {
        private Date contractTime;
        private String contactEmail;

        public ContractInfoResult(Date contractTime, String contactEmail) {
            this.contractTime = contractTime;
            this.contactEmail = contactEmail;
        }

        public Date getContractTime() {
            return contractTime;
        }

        public String getContactEmail() {
            return contactEmail;
        }
    }
}
