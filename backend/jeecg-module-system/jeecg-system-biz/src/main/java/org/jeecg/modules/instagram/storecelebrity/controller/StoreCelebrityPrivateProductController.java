package org.jeecg.modules.instagram.storecelebrity.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.constant.enums.FeishuSpreadSheetType;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrivateProduct;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPrivateProductService;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolSysUserFeishuSheet;
import org.jeecg.modules.feishu.service.IFeishuService;
import org.jeecg.modules.kol.service.IKolSysUserFeishuSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
* @Description: 私有网红品牌产品关联表
* @Author: nqr
* @Date:   2025-06-03
* @Version: V1.0
*/
@Slf4j
@Tag(name ="私有网红品牌产品关联表")
@RestController
@RequestMapping("/storecelebrity/storeCelebrityPrivateProduct")
public class StoreCelebrityPrivateProductController extends JeecgController<StoreCelebrityPrivateProduct, IStoreCelebrityPrivateProductService> {
   @Autowired
   private IStoreCelebrityPrivateProductService privateProductService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private IKolSysUserFeishuSheetService sheetService;
    @Autowired
    private IFeishuService feishuService;

   /**
    * 分页列表查询
    *
    * @param storeCelebrityPrivateProduct
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "私有网红品牌产品关联表-"+SystemConstants.PAGE_LIST_QUERY)
   @Operation(summary ="私有网红品牌产品关联表-"+SystemConstants.PAGE_LIST_QUERY, description = "私有网红品牌产品关联表-"+SystemConstants.PAGE_LIST_QUERY)
   @GetMapping(value = "/list")
   public Result<?> queryPageList(StoreCelebrityPrivateProduct storeCelebrityPrivateProduct,
                                 @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                 @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<StoreCelebrityPrivateProduct> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebrityPrivateProduct, req.getParameterMap());
       Page<StoreCelebrityPrivateProduct> page = new Page<StoreCelebrityPrivateProduct>(pageNo, pageSize);
       IPage<StoreCelebrityPrivateProduct> pageList = privateProductService.page(page, queryWrapper);
       return Result.ok(pageList);
   }

   /**
    * 添加
    *
    * @param storeCelebrityPrivateProduct
    * @return
    */
   @AutoLog(value = "私有网红品牌产品关联表-"+SystemConstants.ADD)
   @Operation(summary ="私有网红品牌产品关联表-" +SystemConstants.ADD, description = "私有网红品牌产品关联表-" +SystemConstants.ADD)
   @PostMapping(value = "/add")
   public Result<?> add(@RequestBody StoreCelebrityPrivateProduct storeCelebrityPrivateProduct) {
       privateProductService.save(storeCelebrityPrivateProduct);
       return Result.ok(SystemConstants.ADD_SUCCESS);
   }

   /**
    * 编辑
    *
    * @param storeCelebrityPrivateProduct
    * @return
    */
   @AutoLog(value = "私有网红品牌产品关联表-"+SystemConstants.EDIT)
   @Operation(summary ="私有网红品牌产品关联表-"+SystemConstants.EDIT, description = "私有网红品牌产品关联表-" + SystemConstants.EDIT)
   @PutMapping(value = "/edit")
   public Result<?> edit(@RequestBody StoreCelebrityPrivateProduct storeCelebrityPrivateProduct) {
       privateProductService.updateById(storeCelebrityPrivateProduct);
       return Result.ok(SystemConstants.EDIT_SUCCESS);
   }

   /**
    * 通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "私有网红品牌产品关联表-"+SystemConstants.DELETE_BY_ID)
   @Operation(summary ="私有网红品牌产品关联表-" +SystemConstants.DELETE_BY_ID, description = "私有网红品牌产品关联表-"+SystemConstants.DELETE_BY_ID)
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       privateProductService.removeById(id);
       return Result.ok(SystemConstants.DELETE_SUCCESS);
   }

   /**
    * 批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "私有网红品牌产品关联表-"+ SystemConstants.DELETE_BATCH)
   @Operation(summary ="私有网红品牌产品关联表-" + SystemConstants.DELETE_BATCH, description = "私有网红品牌产品关联表-" + SystemConstants.DELETE_BATCH)
   @DeleteMapping(value = "/deleteBatch")
   public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.privateProductService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   @AutoLog(value = "私有网红品牌产品关联表-" + SystemConstants.QUERY_BY_ID)
   @Operation(summary ="私有网红品牌产品关联表-" +SystemConstants.QUERY_BY_ID, description = "私有网红品牌产品关联表-"+SystemConstants.QUERY_BY_ID)
   @GetMapping(value = "/queryById")
   public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
       StoreCelebrityPrivateProduct storeCelebrityPrivateProduct = privateProductService.getById(id);
       return Result.ok(storeCelebrityPrivateProduct);
   }

 /**
  * 导出excel
  *
  * @param request
  * @param storeCelebrityPrivateProduct
  */
 @RequestMapping(value = "/exportXls")
 public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityPrivateProduct storeCelebrityPrivateProduct) {
     return super.exportXls(request, storeCelebrityPrivateProduct, StoreCelebrityPrivateProduct.class, "私有网红品牌产品关联表");
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
     return super.importExcel(request, response, StoreCelebrityPrivateProduct.class);
 }

    /**
     * 通过历史合作、历史选中状态查询产品列表
     *
     * @param relationStatus
     * @return
     */
    @AutoLog(value = "私有网红品牌产品关联表-" + SystemConstants.QUERY_PRODUCT_BY_RELATIONSTATUS)
    @GetMapping(value = "/queryProducts")
@Operation(summary = "私有网红品牌产品关联表-"+ SystemConstants.QUERY_PRODUCT_BY_RELATIONSTATUS,
            description = "私有网红品牌产品关联表-"+ SystemConstants.QUERY_PRODUCT_BY_RELATIONSTATUS)
    public Result<?> queryProducts(@RequestParam(name = "relationStatus", required = true) Integer relationStatus) {
        try {
            if (oConvertUtils.isEmpty(relationStatus)) {
                return Result.error("合作状态不能为空");
            }

            List<KolProduct> kolProducts  = privateProductService.queryProducts(relationStatus);

            return Result.ok(kolProducts);

        } catch (Exception e) {
            log.error("通过历史合作、历史选中状态查询产品列表, relationStatus: {}", relationStatus, e);
            return Result.error("查询失败，请稍后重试");
        }
    }

    /**
     * 从飞书表格导入私有网红产品数据
     * @return 导入结果
     */
    @AutoLog(value = "私有网红产品-飞书表格导入")
@Operation(summary = "从飞书表格导入私有网红产品数据", 
                 description = "从飞书在线表格获取数据并转换为StoreCelebrityPrivateProduct实体列表")
    @PostMapping("/importFromFeishuSheet")
    public Result<?> importFromFeishuSheet() {
        
        try {
            // 获取当前用户信息
            String userId = getUserIdByToken();
            if (oConvertUtils.isEmpty(userId)) {
                return Result.error("获取用户信息失败，请重新登录");
            }
            
            LoginUser loginUser = sysBaseAPI.getUserById(userId);
            if (loginUser == null) {
                return Result.error("用户不存在或已失效");
            }
            
            log.info("用户[{}]开始从飞书表格导入私有网红产品数据", loginUser.getUsername());
            
            // 获取用户的历史合作产品飞书链接配置
            KolSysUserFeishuSheet feishuSheet = sheetService.lambdaQuery()
                    .eq(KolSysUserFeishuSheet::getSysUserId, userId)
                    .eq(KolSysUserFeishuSheet::getSpreadSheetType, FeishuSpreadSheetType.PRODUCT.getValue())
                    .one();
            
            if (feishuSheet == null) {
                log.warn("用户[{}]未配置历史合作产品飞书文档", loginUser.getUsername());
                return Result.error("未获取到用户的历史合作产品飞书文档，请先配置");
            }
            
            // 构建飞书表格API URL
            String spreadSheetUrl = String.format(
                    "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/%s/values_batch_get?ranges=%s!A1:%s&valueRenderOption=ToString&dateTimeRenderOption=FormattedString",
                    feishuSheet.getSpreadSheetId(),
                    feishuSheet.getSheetId(),
                    feishuSheet.getEndColumn()
            );
            
            log.info("飞书表格URL: {}", spreadSheetUrl);
            
            // 获取飞书访问令牌
            String tenantAccessToken = feishuService.getInternalTenantAccessToken();
            if (oConvertUtils.isEmpty(tenantAccessToken)) {
                log.warn("未获取到飞书tenantAccessToken，可能会影响数据获取");
                return Result.error("获取飞书访问令牌失败，请稍后重试");
            }
            
            // 调用服务层执行导入
            Result<?> importResult = privateProductService.importFromFeishuSheet(spreadSheetUrl, tenantAccessToken, feishuSheet);
            
            log.info("用户[{}]飞书表格导入完成，结果: {}", loginUser.getUsername(), importResult.isSuccess() ? "成功" : "失败");
            
            return importResult;
            
        } catch (Exception e) {
            log.error("从飞书表格导入数据异常", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

}
