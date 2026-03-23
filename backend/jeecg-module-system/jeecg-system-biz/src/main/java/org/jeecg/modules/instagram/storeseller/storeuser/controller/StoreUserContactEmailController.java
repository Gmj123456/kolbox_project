package org.jeecg.modules.instagram.storeseller.storeuser.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SheetConstants;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.email.entity.StoreUserContactEmailSignature;
import org.jeecg.modules.email.service.IStoreUserContactEmailSignatureService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;
import org.jeecg.modules.instagram.storeseller.storeuser.model.StoreUserContactEmailModel;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserContactEmailService;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.service.IKolSysUserFeishuSheetService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 网红顾问建联邮箱表
 * @Author: jeecg-boot
 * @Date: 2024-01-15
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红顾问建联邮箱表")
@RestController
@RequestMapping("/storeUserContactEmail")
public class StoreUserContactEmailController
        extends JeecgController<StoreUserContactEmail, IStoreUserContactEmailService> {
    @Autowired
    private IStoreUserContactEmailService storeUserContactEmailService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IFeishuService feishuService;
    @Autowired
    private IKolSysUserFeishuSheetService feishuSheetService;
    @Autowired
    private IStoreUserContactEmailSignatureService signatureService;

    /**
     * 分页列表查询
     *
     * @param storeUserContactEmail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红顾问建联邮箱表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "网红顾问建联邮箱表-" + SystemConstants.PAGE_LIST_QUERY, description = "网红顾问建联邮箱表-"
            + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreUserContactEmail storeUserContactEmail,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        if (!oConvertUtils.isAdmin(getUserNameByToken())) {
            storeUserContactEmail.setSysUserId(getUserIdByToken());
        }
        QueryWrapper<StoreUserContactEmail> queryWrapper = QueryGenerator.initQueryWrapper(storeUserContactEmail,
                req.getParameterMap());
        Page<StoreUserContactEmail> page = new Page<StoreUserContactEmail>(pageNo, pageSize);
        IPage<StoreUserContactEmail> pageList = storeUserContactEmailService.page(page, queryWrapper);
        List<StoreUserContactEmail> records = pageList.getRecords();
        if (records.isEmpty()) {
            return Result.ok(pageList);
        }
        List<StoreUserContactEmailModel> resultList = new ArrayList<>();
        List<String> ids = records.stream().map(StoreUserContactEmail::getId).collect(Collectors.toList());
        List<StoreUserContactEmailSignature> list = signatureService.lambdaQuery()
                .in(StoreUserContactEmailSignature::getContactEmailId, ids).list();

        for (StoreUserContactEmail record : records) {
            StoreUserContactEmailModel model = new StoreUserContactEmailModel();
            BeanUtils.copyProperties(record, model);
            List<StoreUserContactEmailSignature> collect = list.stream()
                    .filter(signature -> signature.getContactEmailId().equals(record.getId()))
                    .collect(Collectors.toList());
            if (!collect.isEmpty()) {
                model.setSignatureList(collect);
            }
            resultList.add(model);
        }
        IPage<StoreUserContactEmailModel> resultPageList = new Page<>();
        resultPageList.setSize(pageList.getSize());
        resultPageList.setCurrent(pageList.getCurrent());
        resultPageList.setTotal(pageList.getTotal());
        resultPageList.setPages(pageList.getPages());
        resultPageList.setRecords(resultList);
        return Result.ok(resultPageList);
    }

    /**
     * 根据网红顾问查询建联邮箱列表
     *
     * @param storeUserContactEmail
     * @param req
     * @return
     */
    @AutoLog(value = "网红顾问建联邮箱表-根据网红顾问查询建联邮箱列表")
    @Operation(summary = "网红顾问建联邮箱表-根据网红顾问查询建联邮箱列表", description = "网红顾问建联邮箱表-根据网红顾问查询建联邮箱列表")
    @GetMapping(value = "/queryListByCounselor")
    public Result<?> queryListByCounselor(StoreUserContactEmail storeUserContactEmail,
                                          HttpServletRequest req) {
        LambdaQueryWrapper<StoreUserContactEmail> queryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(storeUserContactEmail.getSysUserId())) {
            queryWrapper.eq(StoreUserContactEmail::getSysUserId, storeUserContactEmail.getSysUserId());
        } else {
            queryWrapper.eq(StoreUserContactEmail::getSysUserId, getUserIdByToken());
        }
        if (oConvertUtils.isNotEmpty(storeUserContactEmail.getContactEmail())) {
            queryWrapper.eq(StoreUserContactEmail::getContactEmail, storeUserContactEmail.getContactEmail());
        }
        if (oConvertUtils.isNotEmpty(storeUserContactEmail.getType())) {
            queryWrapper.eq(StoreUserContactEmail::getType, storeUserContactEmail.getType());
        }
        List<StoreUserContactEmail> contactEmails = storeUserContactEmailService.list(queryWrapper);

        if (!contactEmails.isEmpty()) {
            List<String> contactEmailIds = contactEmails.stream()
                    .map(StoreUserContactEmail::getId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<StoreUserContactEmailSignature> signatureWrapper = new LambdaQueryWrapper<>();
            signatureWrapper.in(StoreUserContactEmailSignature::getContactEmailId, contactEmailIds);
            signatureWrapper.orderByAsc(StoreUserContactEmailSignature::getSortCode);
            List<StoreUserContactEmailSignature> allSignatures = signatureService.list(signatureWrapper);

            for (StoreUserContactEmail email : contactEmails) {
                List<StoreUserContactEmailSignature> signatures = allSignatures.stream()
                        .filter(sig -> email.getId().equals(sig.getContactEmailId()))
                        .collect(Collectors.toList());
                email.setSignatureList(signatures);
            }
        }

        return Result.ok(contactEmails);
    }

    /**
     * 建联邮箱更换网红顾问
     *
     * @return
     */
    @AutoLog(value = "网红顾问建联邮箱表-建联邮箱更换网红顾问")
    @Operation(summary = "网红顾问建联邮箱表-建联邮箱更换网红顾问", description = "网红顾问建联邮箱表-建联邮箱更换网红顾问")
    @GetMapping(value = "/updateContactEmailCounselor")
    public Result<?> updateContactEmailCounselor(
            @RequestParam(name = "celebrityCounselorOldId", required = true) String celebrityCounselorOldId,
            @RequestParam(name = "contactEmails", required = true) String contactEmails,
            @RequestParam(name = "celebrityCounselorId", required = true) String celebrityCounselorId) {
        if (celebrityCounselorOldId.equals(celebrityCounselorId)) {
            return Result.error("建联邮箱更换网红顾问不能相同！");
        }
        List<String> emails = Arrays.asList(contactEmails.split(","));
        SysUser counselorUser = sysUserService.getById(celebrityCounselorId);

        if (null == counselorUser) {
            return Result.error("未获取到替换网红顾问！");
        }
        SysUser counselorOldUser = sysUserService.getById(celebrityCounselorOldId);
        if (null == counselorOldUser) {
            return Result.error("未获取到被替换网红顾问！");
        }
        // 获取被替换网红顾问邮箱列表
        LambdaQueryWrapper<StoreUserContactEmail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserContactEmail::getSysUserId, celebrityCounselorOldId);
        List<StoreUserContactEmail> storeUserContactEmails = storeUserContactEmailService.list(queryWrapper);
        // 判断emailIds是否在storeUserContactEmails里面
        List<String> missingEmails = emails.stream()
                .filter(contactEmail -> storeUserContactEmails.stream()
                        .noneMatch(email -> email.getContactEmail().equals(contactEmail)))
                .collect(Collectors.toList());

        if (!missingEmails.isEmpty()) {
            return Result.error("以下邮箱不存在于被替换网红顾问的邮箱列表中：" + String.join(", ", missingEmails));
        }
        // 过滤出 storeUserContactEmails 中在 emails 列表里的元素
        List<StoreUserContactEmail> filteredEmails = storeUserContactEmails.stream()
                .filter(email -> emails.contains(email.getContactEmail()))
                .collect(Collectors.toList());
        // 更新邮箱变更网红顾问
        // 更新私有网红变更私有网红建联邮箱对应的网红顾问
        // 更新私有网红建联邮箱对应的网红顾问
        storeUserContactEmailService.updateCounselorByEmail(filteredEmails, counselorUser, counselorOldUser);
        updateSheetData(0, celebrityCounselorId);
        return Result.ok("更换网红顾问成功");
    }

    /**
     * 添加
     *
     * @param storeUserContactEmail
     * @return
     */
    @AutoLog(value = "网红顾问建联邮箱表-" + SystemConstants.ADD)
    @Operation(summary = "网红顾问建联邮箱表-" + SystemConstants.ADD, description = "网红顾问建联邮箱表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreUserContactEmail storeUserContactEmail) {
        String userId = getUserIdByToken();
        String contactEmail = storeUserContactEmail.getContactEmail();
        String emailPassword = storeUserContactEmail.getEmailPassword();
        if (oConvertUtils.isEmpty(contactEmail)) {
            return Result.error("邮箱不能为空！");
        }

        Integer type = storeUserContactEmail.getType();
        if (oConvertUtils.isEmpty(type)) {
            return Result.error("邮箱类型不能为空！");
        }

        if (type == 1) {
            if (!contactEmail.contains("gmail.com")) {
                if (oConvertUtils.isEmpty(emailPassword)) {
                    return Result.error("邮箱密码不能为空！");
                }
                storeUserContactEmail.setIsAuthorized(1);
            }
        }

        LambdaQueryWrapper<StoreUserContactEmail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserContactEmail::getContactEmail, contactEmail);
        queryWrapper.eq(StoreUserContactEmail::getSysUserId, userId);
        StoreUserContactEmail emailServiceOne = storeUserContactEmailService.getOne(queryWrapper);
        if (emailServiceOne != null) {
            return Result.error("邮箱已存在！");
        }
        storeUserContactEmail.setSysUserId(userId);
        storeUserContactEmail.setSysUserName(getUserNameByToken());

        storeUserContactEmailService.save(storeUserContactEmail);
        if (type == 0) {
            // 修改飞书sheet
            updateSheetData(0, userId);
        }
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    private void updateSheetData(int type, String userId) {
        List<StoreUserContactEmail> list = storeUserContactEmailService.lambdaQuery()
                .eq(StoreUserContactEmail::getSysUserId, userId).eq(StoreUserContactEmail::getEmailStatus, 1).list();
        List<String> emails = list.stream().map(StoreUserContactEmail::getContactEmail).collect(Collectors.toList());
        KolSysUserFeishuSheet feishuSheet = feishuSheetService.lambdaQuery()
                .eq(KolSysUserFeishuSheet::getSysUserId, userId)
                .eq(KolSysUserFeishuSheet::getSpreadSheetType, SheetConstants.CONTACT_EMAIL).one();
        if (feishuSheet == null) {
            System.out.println("userId" + userId + "，没有飞书建联邮箱文档");
            return;
        }
        feishuService.insertOrUpdatePersonalTagsSheet(type, feishuSheet, emails);
    }

    /**
     * 编辑
     *
     * @param storeUserContactEmail
     * @return
     */
    @AutoLog(value = "网红顾问建联邮箱表-" + SystemConstants.EDIT)
    @Operation(summary = "网红顾问建联邮箱表-" + SystemConstants.EDIT, description = "网红顾问建联邮箱表-"
            + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreUserContactEmail storeUserContactEmail) {
        String userId = getUserIdByToken();
        String contactEmail = storeUserContactEmail.getContactEmail();
        Integer type = storeUserContactEmail.getType();
        String emailPassword = storeUserContactEmail.getEmailPassword();

        if (oConvertUtils.isEmpty(contactEmail)) {
            return Result.error("邮箱不能为空！");
        }

        if (oConvertUtils.isEmpty(type)) {
            return Result.error("邮箱类型不能为空！");
        }

        if (type == 1) {
            if (!contactEmail.contains("gmail.com") && oConvertUtils.isEmpty(emailPassword)) {
                return Result.error("邮箱密码不能为空！");
            }
            if (!contactEmail.contains("gmail.com") && oConvertUtils.isNotEmpty(emailPassword)) {
                storeUserContactEmail.setIsAuthorized(1);
            }
        }

        LambdaQueryWrapper<StoreUserContactEmail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreUserContactEmail::getContactEmail, contactEmail);
        queryWrapper.eq(StoreUserContactEmail::getSysUserId, userId);
        queryWrapper.ne(StoreUserContactEmail::getId, storeUserContactEmail.getId());
        StoreUserContactEmail emailServiceOne = storeUserContactEmailService.getOne(queryWrapper);
        if (emailServiceOne != null) {
            return Result.error("邮箱已存在！");
        }
        storeUserContactEmailService.updateById(storeUserContactEmail);
        if (type == 0) {
            updateSheetData(0, userId);
        }
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红顾问建联邮箱表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "网红顾问建联邮箱表-" + SystemConstants.DELETE_BY_ID, description = "网红顾问建联邮箱表-"
            + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id,
                            @RequestParam(name = "type", required = true) Integer type) {
        String userId = getUserIdByToken();
        storeUserContactEmailService.removeById(id);
        if (type == 0) {
            updateSheetData(1, userId);
        }
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红顾问建联邮箱表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "网红顾问建联邮箱表-" + SystemConstants.DELETE_BATCH, description = "网红顾问建联邮箱表-"
            + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeUserContactEmailService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红顾问建联邮箱表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "网红顾问建联邮箱表-" + SystemConstants.QUERY_BY_ID, description = "网红顾问建联邮箱表-"
            + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreUserContactEmail storeUserContactEmail = storeUserContactEmailService.getById(id);
        return Result.ok(storeUserContactEmail);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeUserContactEmail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreUserContactEmail storeUserContactEmail) {
        return super.exportXls(request, storeUserContactEmail, StoreUserContactEmail.class, "网红顾问建联邮箱表");
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
        return super.importExcel(request, response, StoreUserContactEmail.class);
    }

}
