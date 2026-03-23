package org.jeecg.modules.merchant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.service.IKolBrandService;
import org.jeecg.modules.merchant.entity.MerchantInfo;
import org.jeecg.modules.merchant.service.IMerchantInfoService;
import org.jeecgframework.poi.excel.ExcelImportCheckUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description: 商家信息表
 * @Author: dongruyang
 * @Date: 2025-03-12
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家信息表")
@RestController
@RequestMapping("/merchantInfo")
public class MerchantInfoController extends JeecgController<MerchantInfo, IMerchantInfoService> {

  @Autowired
  private IMerchantInfoService merchantInfoService;
  @Autowired
  private ISysBaseAPI sysBaseAPI;
  @Autowired
  private IKolBrandService brandService;

  /**
   * 分页列表查询
   *
   * @param merchantInfo
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  @AutoLog(value = "商家信息表-" + SystemConstants.PAGE_LIST_QUERY)
  @Operation(summary = "商家信息表-" + SystemConstants.PAGE_LIST_QUERY, description = "商家信息表-" + SystemConstants.PAGE_LIST_QUERY)
  @GetMapping(value = "/list")
  public Result<?> queryPageList(MerchantInfo merchantInfo, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
      @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
    Page<MerchantInfo> page = new Page<MerchantInfo>(pageNo, pageSize);
    IPage<MerchantInfo> pageList = merchantInfoService.pageList(page, merchantInfo);
    return Result.ok(pageList);
  }

  /**
   * 添加
   *
   * @param merchantInfo
   * @return
   */
  @AutoLog(value = "商家信息表-" + SystemConstants.ADD)
  @Operation(summary = "商家信息表-" + SystemConstants.ADD, description = "商家信息表-" + SystemConstants.ADD)
  @PostMapping(value = "/add")
  public Result<?> add(@RequestBody MerchantInfo merchantInfo) {
    merchantInfoService.save(merchantInfo);
    return Result.ok(SystemConstants.ADD_SUCCESS);
  }

  /**
   * 编辑
   *
   * @param merchantInfo
   * @return
   */
  @AutoLog(value = "商家信息表-" + SystemConstants.EDIT)
  @Operation(summary = "商家信息表-" + SystemConstants.EDIT, description = "商家信息表-" + SystemConstants.EDIT)
  @PutMapping(value = "/edit")
  public Result<?> edit(@RequestBody MerchantInfo merchantInfo) {
    merchantInfoService.updateById(merchantInfo);
    return Result.ok(SystemConstants.EDIT_SUCCESS);
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "商家信息表-" + SystemConstants.DELETE_BY_ID)
  @Operation(summary = "商家信息表-" + SystemConstants.DELETE_BY_ID, description = "商家信息表-" + SystemConstants.DELETE_BY_ID)
  @DeleteMapping(value = "/delete")
  public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
    merchantInfoService.removeById(id);
    return Result.ok(SystemConstants.DELETE_SUCCESS);
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "商家信息表-" + SystemConstants.DELETE_BATCH)
  @Operation(summary = "商家信息表-" + SystemConstants.DELETE_BATCH, description = "商家信息表-" + SystemConstants.DELETE_BATCH)
  @DeleteMapping(value = "/deleteBatch")
  public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.merchantInfoService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  @AutoLog(value = "商家信息表-" + SystemConstants.QUERY_BY_ID)
  @Operation(summary = "商家信息表-" + SystemConstants.QUERY_BY_ID, description = "商家信息表-" + SystemConstants.QUERY_BY_ID)
  @GetMapping(value = "/queryById")
  public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
    MerchantInfo merchantInfo = merchantInfoService.getById(id);
    return Result.ok(merchantInfo);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param merchantInfo
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, MerchantInfo merchantInfo) {
    return super.exportXls(request, merchantInfo, MerchantInfo.class, "商家信息表");
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
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
    for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
      MultipartFile file = entity.getValue();
      ImportParams params = new ImportParams();
      params.setHeadRows(1);
      params.setNeedSave(true);
      try {
        boolean aBoolean = ExcelImportCheckUtil.check(file.getInputStream(), MerchantInfo.class, params);
        if (!aBoolean) {
          return Result.error("文件格式错误！");
        }
        //客户量级
        List<DictModel> customerScale = sysBaseAPI.queryDictItemsByCode("customer_scale");
        //商务人员
        List<LoginUser> businessUserList = sysBaseAPI.getBusinessUserList();
        //品牌
        List<KolBrand> kolBrands = brandService.queryListAll("");
        //获客方式
        List<DictModel> customerAcquisitionMethod = sysBaseAPI.queryDictItemsByCode("customer_acquisition_method");
        //销售渠道
        List<DictModel> salesChannel = sysBaseAPI.queryDictItemsByCode("distribution_channel");
        //合作状态
        List<DictModel> cooperationStatus = sysBaseAPI.queryDictItemsByCode("cooperation_status");

        long start = System.currentTimeMillis();
        // 获取上传文件列表
        List<MerchantInfo> list = ExcelImportUtil.importExcel(file.getInputStream(), MerchantInfo.class, params);
        // 删除数组中空对象
        list = list.stream().filter(x -> !oConvertUtils.isAllFieldNull(x)).collect(Collectors.toList());
        List<MerchantInfo> listNew = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        for (MerchantInfo merchantInfo : list) {
          if (checkData(merchantInfo, kolBrands, businessUserList, customerScale, customerAcquisitionMethod, salesChannel, cooperationStatus)) {
            errorList.add("品牌、商务、公司名称不能为空！");
            continue;
          }
          listNew.add(merchantInfo);
        }
        merchantInfoService.saveBatch(listNew);
        log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
        int i = list.size() - listNew.size();
        return i > 0 ? Result.ok("文件导入成功！成功行数：" + listNew.size() + ",失败行数：" + i) : Result.OK("文件导入成功！数据行数：" + listNew.size(), errorList);

      } catch (Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return Result.error("文件导入失败,请检查文件内容是否正确！");
      } finally {
        try {
          file.getInputStream().close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return Result.error("文件导入失败，请检查文件内容是否正确");
  }

  /**
   * 方法描述：判断空值，并设置字典值
   *
   * @author nqr
   * @date 2025/3/13 18:17
   **/
  private boolean checkData(MerchantInfo merchantInfo, List<KolBrand> kolBrands, List<LoginUser> businessUserList, List<DictModel> customerScale, List<DictModel> customerAcquisitionMethod,
      List<DictModel> salesChannel, List<DictModel> cooperationStatus) {
    if (oConvertUtils.isEmpty(merchantInfo.getBrandName()) || oConvertUtils.isEmpty(merchantInfo.getBusinessName()) || oConvertUtils.isEmpty(merchantInfo.getCompanyName())) {
      return true;
    }

    Optional.ofNullable(merchantInfo.getBrandName()).flatMap(brandName -> kolBrands.stream().filter(x -> x.getBrandName().equals(brandName)).findFirst())
        .ifPresent(x -> merchantInfo.setBrandId(x.getId()));

    Optional.ofNullable(merchantInfo.getBusinessName()).flatMap(businessName -> businessUserList.stream().filter(x -> x.getRealname().equals(businessName)).findFirst())
        .ifPresent(x -> merchantInfo.setBusinessId(x.getId()));

    Optional.ofNullable(merchantInfo.getCustomerLevel()).flatMap(level -> customerScale.stream().filter(x -> x.getText().equals(level)).findFirst().map(DictModel::getValue))
        .ifPresent(merchantInfo::setCustomerLevel);

    Optional.ofNullable(merchantInfo.getCustomerAcquisitionMethod()).flatMap(method -> customerAcquisitionMethod.stream().filter(x -> x.getText().equals(method)).findFirst().map(DictModel::getValue))
        .ifPresent(merchantInfo::setCustomerAcquisitionMethod);

    Optional.ofNullable(merchantInfo.getSalesChannel()).flatMap(channel -> salesChannel.stream().filter(x -> x.getText().equals(channel)).findFirst().map(DictModel::getValue))
        .ifPresent(merchantInfo::setSalesChannel);

    Optional.ofNullable(merchantInfo.getCooperationStatus()).flatMap(status -> cooperationStatus.stream().filter(x -> x.getText().equals(status)).findFirst().map(DictModel::getValue))
        .ifPresent(merchantInfo::setCooperationStatus);
    return false;
  }

  /**
   * 下载模板
   */
  @RequestMapping(value = "/downloadXls", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void downloadXls(HttpServletResponse response) throws IOException {
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = wb.createSheet("Sheet");

    String[] header = {"客户量级", "商务", "品牌", "主推产品", "获客方式", "销售渠道", "公司名称", "地址", "品牌关键对接人", "电话", "微信号", "飞书号", "邮箱", "商务对接微信号", "商务对接飞书号",
        "商务电话", "合作状态", "备注"};
    Row row = sheet.createRow(0);
    //创建一个居中格式
    CellStyle style = wb.createCellStyle();
    style.setAlignment(HorizontalAlignment.CENTER);      //创建一个居中格式
    //水平居中
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    for (int i = 0; i < header.length; i++) {
      Cell cell = row.createCell(i);
      cell.setCellValue(header[i]);
      cell.setCellStyle(style);       //设置该单元格的样式为居中
      sheet.setColumnWidth(i, 4000);       //调整列宽以适应内容
    }
    //设置下拉框的值
    setDropdown(sheet);
    try {
      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("", "UTF-8"));
      ServletOutputStream outputStream = response.getOutputStream();
      wb.write(outputStream);
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void createDropdownList(HSSFSheet sheet, String[] dropdownValues, int column) {
    DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(dropdownValues);

    if (dropdownValues.length > 20) {
      // 创建命名范围
      String rangeName = "dropdown"; // 可以给命名范围起一个名称
      Name name = sheet.getWorkbook().createName();
      name.setNameName(rangeName);

      // 在某个工作表中创建一个临时工作表，保存所有下拉列表的选项
      HSSFSheet tempSheet = sheet.getWorkbook().createSheet("sheet2");

      // 将所有 dropdownValues 填充到临时工作表中
      for (int i = 0; i < dropdownValues.length; i++) {
        Row row = tempSheet.createRow(i);
        Cell cell = row.createCell(0);
        cell.setCellValue(dropdownValues[i]);
      }

      // 设置命名范围，指向临时工作表的所有数据
      name.setRefersToFormula(tempSheet.getSheetName() + "!$A$1:$A$" + dropdownValues.length);
      dvConstraint = DVConstraint.createFormulaListConstraint(rangeName);
    }
    CellRangeAddressList addressList = new CellRangeAddressList(1, 65535, column, column);
    DataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
    sheet.addValidationData(dataValidation);
  }

  /**
   * 功能描述:设置下拉框
   *
   * @param sheet
   * @author nqr
   * @date 2024-01-15 13:35
   */
  private void setDropdown(HSSFSheet sheet) {
    //客户量级
    createDropdownList(sheet, getDropdownItemsFromDict("customer_scale"), 0);

    //商务人员
    createDropdownList(sheet, getDropdownItems(sysBaseAPI.getBusinessUserList(), LoginUser::getRealname), 1);

    //品牌
    createDropdownList(sheet, getDropdownItems(brandService.queryListAll(""), KolBrand::getBrandName), 2);

    //获客方式
    createDropdownList(sheet, getDropdownItemsFromDict("customer_acquisition_method"), 4);

    //销售渠道
    createDropdownList(sheet, getDropdownItemsFromDict("distribution_channel"), 5);

    //合作状态
    createDropdownList(sheet, getDropdownItemsFromDict("cooperation_status"), 16);
  }

  //从字典模型列表创建下拉选项
  private String[] getDropdownItemsFromDict(String dictCode) {
    return sysBaseAPI.queryDictItemsByCode(dictCode).stream().map(DictModel::getText).toArray(String[]::new);
  }

  //从任意列表和映射函数创建下拉选项
  private <T> String[] getDropdownItems(List<T> items, Function<T, String> mapper) {
    return items.stream().map(mapper).toArray(String[]::new);
  }
}
