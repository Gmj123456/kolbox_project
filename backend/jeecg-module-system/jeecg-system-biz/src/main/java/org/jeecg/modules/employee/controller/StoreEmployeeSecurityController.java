package org.jeecg.modules.employee.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.LimitSubmit;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.employee.entity.StoreEmployeeSecurity;
import org.jeecg.modules.employee.service.IStoreEmployeeSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 员工防伪
 * @Author: nqr
 * @Date: 2023-05-29
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "员工防伪")
@RestController
@RequestMapping("/employeeSecurity")
public class StoreEmployeeSecurityController extends JeecgController<StoreEmployeeSecurity, IStoreEmployeeSecurityService> {
    @Autowired
    private IStoreEmployeeSecurityService storeEmployeeSecurityService;

    /**
     * 分页列表查询
     *
     * @param storeEmployeeSecurity
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "员工防伪-" + SystemConstants.PAGE_LIST_QUERY)
  @Operation(summary = "员工防伪-" + SystemConstants.PAGE_LIST_QUERY, description = "员工防伪-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreEmployeeSecurity storeEmployeeSecurity,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreEmployeeSecurity> queryWrapper = QueryGenerator.initQueryWrapper(storeEmployeeSecurity, req.getParameterMap());
        Page<StoreEmployeeSecurity> page = new Page<StoreEmployeeSecurity>(pageNo, pageSize);
        IPage<StoreEmployeeSecurity> pageList = storeEmployeeSecurityService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeEmployeeSecurity
     * @return
     */
    @AutoLog(value = "员工防伪-" + SystemConstants.ADD)
  @Operation(summary = "员工防伪-" + SystemConstants.ADD, description = "员工防伪-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreEmployeeSecurity storeEmployeeSecurity) {
        storeEmployeeSecurityService.save(storeEmployeeSecurity);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param storeEmployeeSecurity
     * @return
     */
    @AutoLog(value = "员工防伪-" + SystemConstants.EDIT)
  @Operation(summary = "员工防伪-" + SystemConstants.EDIT, description = "员工防伪-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreEmployeeSecurity storeEmployeeSecurity) {
        storeEmployeeSecurityService.updateById(storeEmployeeSecurity);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "员工防伪-" + SystemConstants.DELETE_BY_ID)
  @Operation(summary = "员工防伪-" + SystemConstants.DELETE_BY_ID, description = "员工防伪-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeEmployeeSecurityService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "员工防伪-" + SystemConstants.DELETE_BATCH)
  @Operation(summary = "员工防伪-" + SystemConstants.DELETE_BATCH, description = "员工防伪-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeEmployeeSecurityService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "员工防伪-" + SystemConstants.QUERY_BY_ID)
  @Operation(summary = "员工防伪-" + SystemConstants.QUERY_BY_ID, description = "员工防伪-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreEmployeeSecurity storeEmployeeSecurity = storeEmployeeSecurityService.getById(id);
        return Result.ok(storeEmployeeSecurity);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeEmployeeSecurity
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreEmployeeSecurity storeEmployeeSecurity) {
        return super.exportXls(request, storeEmployeeSecurity, StoreEmployeeSecurity.class, "员工防伪");
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
        return super.importExcel(request, response, StoreEmployeeSecurity.class);
    }

    /**
     * 员工防伪判断
     *
     * @param keyword 查询关键字
     * @param lang    语言参数，zh-中文，en-英文
     * @return
     */
    @AutoLog(value = "员工防伪-判断")
  @Operation(summary = "员工防伪-判断", description = "员工防伪-判断")
    @GetMapping(value = "/checkEmployee")
    @LimitSubmit(key = "checkEmployee:#keyword", limit = 3, needAllWait = true)
    public Result<?> checkEmployee(@RequestParam(name = "keyword", required = true) String keyword,
                                   @RequestParam(name = "lang", defaultValue = "zh") String lang) {
        boolean isEnglish = "en".equalsIgnoreCase(lang);

        if (oConvertUtils.isEmpty(keyword)) {
            String message = isEnglish ?
                    "Please enter a valid name, phone number, email, or WeChat ID to verify our staff member." :
                    "未匹配到相关人员信息，请重新输入";
            return Result.error(message);
        }

        StoreEmployeeSecurity nameOrNickname = isNameOrNickname(keyword);
        StoreEmployeeSecurity phoneOrWeChatOrEmail = isPhoneOrWeChatOrEmail(keyword);

        if (nameOrNickname != null) {
            String name = nameOrNickname.getName();
            String nameEn = nameOrNickname.getNameEn();
            String originalJob = nameOrNickname.getJob();
            String job = isEnglish ? translateJobToEnglish(originalJob) : originalJob;

            if (isEnglish) {
                String result = " (WeChat nickname)";
                if (name.equals(keyword)) {
                    result = "";
                } else if (nameEn.equals(keyword)) {
                    result = " (English name)";
                }
                return Result.ok("✓ Verified! \"" + keyword + "\" is a legitimate " + job + " at HaiMa International" + result + ". For security purposes, we recommend using their phone number, email, or WeChat ID for precise verification.");
            } else {
                String result = "的微信昵称";
                if (name.equals(keyword)) {
                    result = "";
                } else if (nameEn.equals(keyword)) {
                    result = "的英文名";
                }
                return Result.ok("匹配成功，\"" + keyword + "\"是我们海玛国际【" + job + "】" + result + "，姓名和微信昵称不唯一，建议使用手机号、邮箱地址或者微信号进行精确查询");
            }
        } else if (phoneOrWeChatOrEmail != null) {
            String phone = oConvertUtils.isEmpty(phoneOrWeChatOrEmail.getPhone()) ? "" : phoneOrWeChatOrEmail.getPhone();
            String email = oConvertUtils.isEmpty(phoneOrWeChatOrEmail.getEmail()) ? "" : phoneOrWeChatOrEmail.getEmail();
            String originalJob = oConvertUtils.isEmpty(phoneOrWeChatOrEmail.getJob()) ? "商务经理" : phoneOrWeChatOrEmail.getJob();
            String job = isEnglish ? translateJobToEnglish(originalJob) : originalJob;

            if (isEnglish) {
                String contactType = "WeChat ID";
                if (phone.equals(keyword)) {
                    contactType = "phone number";
                } else if (email.equals(keyword)) {
                    contactType = "email address";
                }
                return Result.ok("✓ Verified! This " + contactType + " belongs to " + job + " at HaiMa International. You can safely proceed with business discussions. Thank you for verifying with us!");
            } else {
                String result = "微信号";
                if (phone.equals(keyword)) {
                    result = "手机号";
                } else if (email.equals(keyword)) {
                    result = "邮箱";
                }
                return Result.ok("匹配成功，\"" + keyword + "\"是我们海玛国际【" + job + "】的" + result + "，您可以继续与其进行商务洽谈合作，感谢您的支持");
            }
        }

        String errorMessage = isEnglish ?
                "⚠️ SECURITY ALERT: We could not verify \"" + keyword + "\" as an official HaiMa International team member. Please double-check the information to avoid potential fraud. Stay safe!" :
                "匹配失败，您输入的\"" + keyword + "\"不是我们海玛国际的官方工作人员！请检查后重试，谨防被骗！！！";
        return Result.error(errorMessage);
    }

    private StoreEmployeeSecurity isNameOrNickname(String keyword) {
        LambdaQueryWrapper<StoreEmployeeSecurity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(x -> x.eq(StoreEmployeeSecurity::getName, keyword).or().eq(StoreEmployeeSecurity::getNameEn, keyword).or().eq(StoreEmployeeSecurity::getNameWechat, keyword));
        List<StoreEmployeeSecurity> list = storeEmployeeSecurityService.list(queryWrapper);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    private StoreEmployeeSecurity isPhoneOrWeChatOrEmail(String keyword) {
        LambdaQueryWrapper<StoreEmployeeSecurity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(x -> x.eq(StoreEmployeeSecurity::getPhone, keyword).or().eq(StoreEmployeeSecurity::getEmail, keyword).or().eq(StoreEmployeeSecurity::getWechatNum, keyword));
        List<StoreEmployeeSecurity> list = storeEmployeeSecurityService.list(queryWrapper);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 将中文职位名称翻译为英文
     *
     * @param chineseJob 中文职位名称
     * @return 英文职位名称
     */
    private String translateJobToEnglish(String chineseJob) {
        if (oConvertUtils.isEmpty(chineseJob)) {
            return "Business Manager";
        }

        // 常见职位中英文对照表
        switch (chineseJob) {
            case "商务经理":
                return "Business Manager";
            case "销售经理":
                return "Sales Manager";
            case "市场经理":
                return "Marketing Manager";
            case "产品经理":
                return "Product Manager";
            case "项目经理":
                return "Project Manager";
            case "人力资源经理":
                return "HR Manager";
            case "财务经理":
                return "Finance Manager";
            case "技术经理":
                return "Technical Manager";
            case "运营经理":
                return "Operations Manager";
            case "客户经理":
                return "Account Manager";
            case "业务经理":
                return "Business Manager";
            case "总经理":
                return "General Manager";
            case "副总经理":
                return "Deputy General Manager";
            case "总监":
                return "Director";
            case "主管":
                return "Supervisor";
            case "专员":
                return "Specialist";
            case "助理":
                return "Assistant";
            case "顾问":
                return "Consultant";
            default:
                // 如果没有匹配的翻译，返回原文或默认值
                return "Business Manager";
        }
    }

    /**
     * 获取跨境日历
     */
    @AutoLog(value = "获取跨境日历")
  @Operation(summary = "获取跨境日历", description = "获取跨境日历")
    @PostMapping(value = "/calendar/front_list")
    public Result<?> frontList(@RequestBody JSONObject jsonObject) {
        List<Map<String, Object>> maps = storeEmployeeSecurityService.frontList(jsonObject);
        return Result.ok(maps);
    }

}
