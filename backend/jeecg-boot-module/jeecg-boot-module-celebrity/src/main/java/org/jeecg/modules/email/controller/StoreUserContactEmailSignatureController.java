package org.jeecg.modules.email.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.ExamConstants;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.StoreUserContactEmailVo;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.email.entity.StoreUserContactEmailSignature;
import org.jeecg.modules.email.service.IStoreUserContactEmailSignatureService;

import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.constant.SystemConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;

import java.util.Map;

import jakarta.annotation.Resource;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @Description: 建联邮箱签名表
 * @Author: jeecgboot
 * @Date: 2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "建联邮箱签名表")
@RestController
@RequestMapping("/email/signature")
public class StoreUserContactEmailSignatureController
        extends JeecgController<StoreUserContactEmailSignature, IStoreUserContactEmailSignatureService> {
    @Autowired
    private IStoreUserContactEmailSignatureService storeUserContactEmailSignatureService;
    @Resource
    private ISysBaseAPI sysBaseAPI;

    /**
     * 分页列表查询
     *
     * @param storeUserContactEmailSignature
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "建联邮箱签名表-" + ExamConstants.PAGE_LIST_QUERY)
    @Operation(summary = "建联邮箱签名表-" + ExamConstants.PAGE_LIST_QUERY, description = "建联邮箱签名表-"
            + ExamConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreUserContactEmailSignature storeUserContactEmailSignature,
                                   @RequestParam(name = ExamConstants.PAGE_NO, defaultValue = ExamConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = ExamConstants.PAGE_SIZE, defaultValue = ExamConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<StoreUserContactEmailSignature> queryWrapper = QueryGenerator
                .initQueryWrapper(storeUserContactEmailSignature, req.getParameterMap()).lambda();
        Page<StoreUserContactEmailSignature> page = new Page<StoreUserContactEmailSignature>(pageNo, pageSize);
        IPage<StoreUserContactEmailSignature> pageList = storeUserContactEmailSignatureService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeUserContactEmailSignature
     * @return
     */
    @AutoLog(value = "建联邮箱签名表-" + ExamConstants.ADD)
    @Operation(summary = "建联邮箱签名表-" + ExamConstants.ADD, description = "建联邮箱签名表-" + ExamConstants.ADD)
    @PostMapping(value = "/add")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(@RequestBody StoreUserContactEmailSignature storeUserContactEmailSignature) {
        try {
            String userId = getUserIdByToken();

            // 参数校验
            Result<?> validateResult = validateSignatureParams(storeUserContactEmailSignature);
            if (!validateResult.isSuccess()) {
                return validateResult;
            }

            // 检查是否已存在相同的记录（同一个顾问人员的同一个邮箱的签名）
            if (isSignatureExists(storeUserContactEmailSignature.getContactEmailId(),
                    userId,
                    storeUserContactEmailSignature.getSignatureTitle(),
                    null)) {
                return Result.error("该邮箱签名已存在");
            }

            // 设置基础字段
            storeUserContactEmailSignature.setSysUserId(userId);

            // 如果未设置原始内容，则复制签名内容
            if (storeUserContactEmailSignature.getSignatureContentOriginal() == null) {
                storeUserContactEmailSignature.setSignatureContentOriginal(
                        storeUserContactEmailSignature.getSignatureContent());
            }

            // 保存记录
            boolean saveResult = storeUserContactEmailSignatureService.save(storeUserContactEmailSignature);
            if (!saveResult) {
                log.error("保存邮箱签名失败，参数：{}", JSON.toJSONString(storeUserContactEmailSignature));
                return Result.error("保存失败");
            }

            log.info("成功添加邮箱签名，ID：{}", storeUserContactEmailSignature.getId());
            return Result.ok(ExamConstants.ADD_SUCCESS);
        } catch (Exception e) {
            log.error("添加邮箱签名异常：", e);
            return Result.error("添加失败，系统异常");
        }
    }

    /**
     * 编辑
     *
     * @param storeUserContactEmailSignature
     * @return
     */
    @AutoLog(value = "建联邮箱签名表-" + ExamConstants.EDIT)
    @Operation(summary = "建联邮箱签名表-" + ExamConstants.EDIT, description = "建联邮箱签名表-" + ExamConstants.EDIT)
    @PutMapping(value = "/edit")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> edit(@RequestBody StoreUserContactEmailSignature storeUserContactEmailSignature) {
        try {
            String userId = getUserIdByToken();

            // 基础参数校验
            if (storeUserContactEmailSignature.getId() == null || storeUserContactEmailSignature.getId().isEmpty()) {
                return Result.error("ID不能为空");
            }

            // 参数校验
            Result<?> validateResult = validateSignatureParams(storeUserContactEmailSignature);
            if (!validateResult.isSuccess()) {
                return validateResult;
            }

            // 检查记录是否存在
            StoreUserContactEmailSignature existing = storeUserContactEmailSignatureService
                    .getById(storeUserContactEmailSignature.getId());
            if (existing == null) {
                log.warn("编辑邮箱签名时记录不存在，ID：{}", storeUserContactEmailSignature.getId());
                return Result.error("未找到当前记录");
            }

            // 检查是否存在除当前记录外的重复记录
            if (isSignatureExists(storeUserContactEmailSignature.getContactEmailId(),
                    userId,
                    storeUserContactEmailSignature.getSignatureTitle(),
                    storeUserContactEmailSignature.getId())) {
                return Result.error("该邮箱签名已存在");
            }

            // 如果原始内容为空且签名内容有变化，则更新原始内容
            if (storeUserContactEmailSignature.getSignatureContentOriginal() == null &&
                    !existing.getSignatureContent().equals(storeUserContactEmailSignature.getSignatureContent())) {
                storeUserContactEmailSignature.setSignatureContentOriginal(
                        storeUserContactEmailSignature.getSignatureContent());
            }

            // 更新记录
            boolean updateResult = storeUserContactEmailSignatureService.updateById(storeUserContactEmailSignature);
            if (!updateResult) {
                log.error("更新邮箱签名失败，ID：{}，参数：{}",
                        storeUserContactEmailSignature.getId(),
                        JSON.toJSONString(storeUserContactEmailSignature));
                return Result.error("更新失败");
            }

            log.info("成功更新邮箱签名，ID：{}", storeUserContactEmailSignature.getId());
            return Result.ok(ExamConstants.EDIT_SUCCESS);
        } catch (Exception e) {
            log.error("编辑邮箱签名异常：", e);
            return Result.error("编辑失败，系统异常");
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "建联邮箱签名表-" + ExamConstants.DELETE_BY_ID)
    @Operation(summary = "建联邮箱签名表-" + ExamConstants.DELETE_BY_ID, description = "建联邮箱签名表-" + ExamConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeUserContactEmailSignatureService.removeById(id);
        return Result.ok(ExamConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "建联邮箱签名表-" + ExamConstants.DELETE_BATCH)
    @Operation(summary = "建联邮箱签名表-" + ExamConstants.DELETE_BATCH, description = "建联邮箱签名表-" + ExamConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeUserContactEmailSignatureService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(ExamConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "建联邮箱签名表-" + ExamConstants.QUERY_BY_ID)
    @Operation(summary = "建联邮箱签名表-" + ExamConstants.QUERY_BY_ID, description = "建联邮箱签名表-" + ExamConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreUserContactEmailSignature storeUserContactEmailSignature = storeUserContactEmailSignatureService
                .getById(id);
        return Result.ok(storeUserContactEmailSignature);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeUserContactEmailSignature
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request,
                                  StoreUserContactEmailSignature storeUserContactEmailSignature) {
        return super.exportXls(request, storeUserContactEmailSignature, StoreUserContactEmailSignature.class,
                "建联邮箱签名表");
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
        return super.importExcel(request, response, StoreUserContactEmailSignature.class);
    }

    /**
     * 通过建联邮箱id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "建联邮箱签名表-通过建联邮箱id查询")
    @Operation(summary = "建联邮箱签名表-通过建联邮箱id查询", description = "建联邮箱签名表-通过建联邮箱id查询")
    @GetMapping(value = "/getSignatureList")
    public Result<?> getSignatureList(@RequestParam(name = "id", required = true) String id) {
        try {
            List<StoreUserContactEmailSignature> list = storeUserContactEmailSignatureService
                    .lambdaQuery()
                    .eq(StoreUserContactEmailSignature::getContactEmailId, id)
                    .list();
            return Result.ok(list);
        } catch (Exception e) {
            log.error("查询建联邮箱签名列表异常，邮箱ID：{}", id, e);
            return Result.error("查询失败，系统异常");
        }
    }

    /**
     * 校验邮箱签名参数
     *
     * @param signature 邮箱签名实体
     * @return 校验结果
     */
    private Result<?> validateSignatureParams(StoreUserContactEmailSignature signature) {
        if (signature.getContactEmailId() == null || signature.getContactEmailId().isEmpty()) {
            return Result.error("建联邮箱表ID不能为空");
        }
        if (signature.getContactEmail() == null || signature.getContactEmail().isEmpty()) {
            return Result.error("建联邮箱不能为空");
        }
        if (signature.getSignatureTitle() == null || signature.getSignatureTitle().isEmpty()) {
            return Result.error("个性化签名标题不能为空");
        }
        if (signature.getSignatureContent() == null) {
            return Result.error("个性化签名内容不能为空");
        }

        // 验证标题长度
        if (signature.getSignatureTitle().length() > 100) {
            return Result.error("个性化签名标题长度不能超过100个字符");
        }

        // 验证内容长度
        if (signature.getSignatureContent().length() > 2000) {
            return Result.error("个性化签名内容长度不能超过2000个字符");
        }

        return Result.ok();
    }

    /**
     * 检查邮箱签名是否已存在
     *
     * @param contactEmailId 建联邮箱ID
     * @param userId         用户ID
     * @param signatureTitle 签名标题
     * @param excludeId      排除的ID（编辑时使用）
     * @return 是否存在
     */
    private boolean isSignatureExists(String contactEmailId, String userId, String signatureTitle, String excludeId) {
        LambdaQueryWrapper<StoreUserContactEmailSignature> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(StoreUserContactEmailSignature::getContactEmailId, contactEmailId)
                .eq(StoreUserContactEmailSignature::getSysUserId, userId)
                .eq(StoreUserContactEmailSignature::getSignatureTitle, signatureTitle);

        if (excludeId != null && !excludeId.isEmpty()) {
            queryWrapper.ne(StoreUserContactEmailSignature::getId, excludeId);
        }

        return storeUserContactEmailSignatureService.count(queryWrapper) > 0;
    }

    /**
     * 同步Gmail邮箱签名
     *
     * @return
     */
    @AutoLog(value = "建联邮箱签名表-同步Gmail邮箱签名")
    @Operation(summary = "建联邮箱签名表-同步Gmail邮箱签名", description = "建联邮箱签名表-同步Gmail邮箱签名")
    @GetMapping(value = "/syncGmailSignature")
    public Result<?> syncGmailSignature() {
        String userId = getUserIdByToken();
        // 查询当前用户所有的建联邮箱
        List<StoreUserContactEmailVo> contactEmails = sysBaseAPI.getContactEmailByUserId(userId);
        if (contactEmails == null || contactEmails.isEmpty()) {
            log.error("当前用户没有建联邮箱");
            return Result.error("当前用户没有建联邮箱");
        }
        // 过滤所有gmail邮箱
        List<StoreUserContactEmailVo> gmailContactEmails = contactEmails.stream()
                .filter(contactEmail -> contactEmail.getContactEmail().contains("gmail") && contactEmail.getIsAuthorized() == 1 && contactEmail.getType() == 1)
                .toList();
        if (gmailContactEmails.isEmpty()) {
            log.error("当前用户没有gmail邮箱");
            return Result.error("当前用户没有gmail邮箱");
        }
        // 准备调用第三方接口
        try {
            // 创建RestTemplate实例
            RestTemplate restTemplate = new RestTemplate();

            // 从字典中获取第三方接口URL
            List<DictModel> dictModels = sysBaseAPI.queryDictItemsByCode("email_api");
            if (dictModels == null || dictModels.isEmpty()) {
                log.error("未配置邮件API地址");
                return Result.error("未配置邮件API地址");
            }

            DictModel dictModel = dictModels.stream().filter(x -> x.getTitle().equals("email_url")).findFirst().get();
            if (dictModel.getValue() == null || dictModel.getValue().trim().isEmpty()) {
                log.error("邮件API地址配置为空");
                return Result.error("邮件API地址配置为空");
            }

            // 构建完整URL
            String apiUrl = dictModel.getValue().trim() + "/get_gmail_signature";
//            apiUrl = "http://192.168.150.111:8123/api/gmail" + "/get_gmail_signature";
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构造请求参数
            List<Map<String, String>> requestBody = new ArrayList<>();
            for (StoreUserContactEmailVo contactEmail : gmailContactEmails) {
                Map<String, String> param = new HashMap<>();
                param.put("id", contactEmail.getId());
                param.put("email", contactEmail.getContactEmail());
                requestBody.add(param);
            }
            // 创建HttpEntity
            HttpEntity<List<Map<String, String>>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 调用第三方POST接口
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            // 检查调用是否成功（HTTP状态码为2xx即认为成功）
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("同步Gmail签名成功，用户: {}", getUserNameByToken());
                return Result.ok("同步Gmail签名成功");
            } else {
                log.error("同步Gmail签名失败，URL: {}, 状态码: {}", apiUrl, responseEntity.getStatusCode());
                return Result.error("同步Gmail签名失败");
            }
        } catch (Exception e) {
            log.error("同步Gmail签名异常", e);
            return Result.error("同步Gmail签名异常,请稍候重试");
        }
    }

}
