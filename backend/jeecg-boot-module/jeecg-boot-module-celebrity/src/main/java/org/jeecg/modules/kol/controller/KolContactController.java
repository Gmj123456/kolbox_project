package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.model.KolBrandModel;
import org.jeecg.modules.tiktok.entity.TiktokCelebrity;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.service.IKolBrandService;
import org.jeecg.modules.kol.service.IKolContactService;
import org.jeecg.modules.tiktok.service.ITiktokCelebrityService;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecg.modules.youtube.service.IYtCelebrityService;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 网红品牌开发表-重命名
 * @Author: xyc
 * @Date: 2024年12月4日 14:43:17
 * @Version: V1.1
 */
@Slf4j
@Tag(name = "网红品牌开发表")
@RestController
@RequestMapping("/kol/kolContact")
public class KolContactController extends JeecgController<KolContact, IKolContactService> {
    @Autowired
    private IKolContactService kolContactService;
    @Autowired
    private ITiktokCelebrityService tiktokCelebrityService;
    @Autowired
    private IYtCelebrityService youtubeCelebrityService;
    @Autowired
    private IKolBrandService kolBrandService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * 分页列表查询
     *
     * @param kolContact
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红品牌开发表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "网红品牌开发表-" + SystemConstants.PAGE_LIST_QUERY, description = "网红品牌开发表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolContact kolContact,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<KolContact> queryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(kolContact.getBrandName())) {
            queryWrapper.like(KolContact::getBrandName, kolContact.getBrandName());
        }
        Page<KolContact> page = new Page<KolContact>(pageNo, pageSize);
        IPage<KolContact> pageList = kolContactService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param kolContact
     * @return
     */
    @AutoLog(value = "网红品牌开发表-" + SystemConstants.ADD)
    @Operation(summary = "网红品牌开发表-" + SystemConstants.ADD, description = "网红品牌开发表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolContact kolContact) {
        List<KolContact> list = kolContactService.list();
        for (KolContact contact : list) {
            if (contact.getBrandName().equals(kolContact.getBrandName())) {
                return Result.error("品牌已存在");
            }
        }
        kolContactService.save(kolContact);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param kolContact
     * @return
     */
    @AutoLog(value = "网红品牌开发表-" + SystemConstants.EDIT)
    @Operation(summary = "网红品牌开发表-" + SystemConstants.EDIT, description = "网红品牌开发表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolContact kolContact) {
        kolContactService.updateById(kolContact);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红品牌开发表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "网红品牌开发表-" + SystemConstants.DELETE_BY_ID, description = "网红品牌开发表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        kolContactService.removeById(id);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红品牌开发表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "网红品牌开发表-" + SystemConstants.DELETE_BATCH, description = "网红品牌开发表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.kolContactService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红品牌开发表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "网红品牌开发表-" + SystemConstants.QUERY_BY_ID, description = "网红品牌开发表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolContact kolContact = kolContactService.getById(id);
        return Result.ok(kolContact);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolContact
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolContact kolContact) {
        return super.exportXls(request, kolContact, KolContact.class, "网红品牌开发表");
    }

    /**
     * 通过excel导入开发历史
     *
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String type = multipartRequest.getParameter("platformType");
        int platformType = Integer.parseInt(type);
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                long start = System.currentTimeMillis();
                // 获取上传文件列表
                List<KolContact> list = ExcelImportUtil.importExcel(file.getInputStream(), KolContact.class, params);
                list = list.stream()
                        .filter(x -> oConvertUtils.isNotEmpty(x.getUniqueId()))
                        .collect(Collectors.toList());
                if (list.isEmpty()) {
                    return Result.error("文件导入失败，请检查文件内容是否正确！");
                }
                List<KolContact> errorList = new ArrayList<>();
                List<KolContact> saveList = new ArrayList<>();
                for (KolContact kolContact : list) {

                    String brandName = kolContact.getBrandName();
                    KolBrand kolBrand = kolBrandService.getBrandName(brandName);
                    if (kolBrand == null) {
                        errorList.add(kolContact);
                        continue;
                    }
                    kolContact.setBrandId(kolBrand.getId());
                    String celebrityCounselorName = kolContact.getCounselorName();
                    LoginUser celebrityCounselor = sysBaseAPI.getCelebrityCounselor(celebrityCounselorName);
                    if (celebrityCounselor == null) {
                        errorList.add(kolContact);
                        continue;
                    }
                    kolContact.setCounselorId(celebrityCounselor.getId());
                    kolContact.setCounselorShortName(celebrityCounselor.getShortName());
                    String uniqueId = kolContact.getUniqueId();
                    if (CommonConstant.TK == platformType) {
                        TiktokCelebrity tiktokCelebrity = tiktokCelebrityService.getUniqueId(uniqueId);
                        if (tiktokCelebrity == null) {
                            errorList.add(kolContact);
                            continue;
                        }
                    } else if (CommonConstant.YT == platformType) {
                        YoutubeCelebrity youtubeCelebrity = youtubeCelebrityService.getByAccount(uniqueId);
                        if (youtubeCelebrity == null) {
                            errorList.add(kolContact);
                            continue;
                        }
                        uniqueId = youtubeCelebrity.getAccount();
                        kolContact.setUniqueId(uniqueId);
                    }

                    kolContact.setPlatformType(platformType);
                    saveList.add(kolContact);
                }
                kolContactService.saveBatch(saveList);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                Result<List<KolContact>> result = new Result<>();
                if (errorList.isEmpty()) {
                    result.setSuccess(true);
                    result.setCode(200);
                } else {
                    result.setSuccess(false);
                    result.setCode(500);
                }
                result.setMessage("文件导入成功！成功条数：" + saveList.size() + "失败条数：" + errorList.size());
                result.setResult(errorList);
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败，请检查文件内容是否正确！");
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
     * 网红建联开发设置为已开发
     *
     * @param kolBrandModel
     * @return
     */
    @AutoLog(value = "网红品牌开发表-设置为已开发")
    @Operation(summary = "网红品牌开发表-设置为已开发", description = "网红品牌开发表-设置为已开发")
    @PostMapping(value = "/createContact")
    public Result<?> createContact(@RequestBody KolBrandModel kolBrandModel) {
        String productId = kolBrandModel.getProductId();
        if (oConvertUtils.isEmpty(productId)) {
            return Result.error("请选择产品");
        }
        kolContactService.saveKolContact(kolBrandModel);
        return Result.ok("标记完成");
    }
}