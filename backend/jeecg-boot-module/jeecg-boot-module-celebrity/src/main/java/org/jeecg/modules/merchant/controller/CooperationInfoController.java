package org.jeecg.modules.merchant.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
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
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.service.IKolBrandService;
import org.jeecg.modules.merchant.entity.CooperationInfo;
import org.jeecg.modules.merchant.entity.CooperationInfo;
import org.jeecg.modules.merchant.service.ICooperationInfoService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.ExcelImportCheckUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.constant.SystemConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @Description: 商家合作信息表
 * @Author: dongruyang
 * @Date: 2025-03-12
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家合作信息表")
@RestController
@RequestMapping("/cooperationInfo")
public class CooperationInfoController extends JeecgController<CooperationInfo, ICooperationInfoService> {

  @Autowired
  private ICooperationInfoService cooperationInfoService;
  @Autowired
  private ISysBaseAPI sysBaseAPI;
  @Autowired
  private IKolBrandService brandService;

  /**
   * 分页列表查询
   *
   * @param cooperationInfo
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  @AutoLog(value = "商家合作信息表-" + SystemConstants.PAGE_LIST_QUERY)
  @Operation(summary = "商家合作信息表-" + SystemConstants.PAGE_LIST_QUERY, description = "商家合作信息表-" + SystemConstants.PAGE_LIST_QUERY)
  @GetMapping(value = "/list")
  public Result<?> queryPageList(CooperationInfo cooperationInfo, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
      @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
    Page<CooperationInfo> page = new Page<CooperationInfo>(pageNo, pageSize);
    IPage<CooperationInfo> pageList = cooperationInfoService.pageList(page, cooperationInfo);
    return Result.ok(pageList);
  }

  /**
   * 添加
   *
   * @param cooperationInfo
   * @return
   */
  @AutoLog(value = "商家合作信息表-" + SystemConstants.ADD)
  @Operation(summary = "商家合作信息表-" + SystemConstants.ADD, description = "商家合作信息表-" + SystemConstants.ADD)
  @PostMapping(value = "/add")
  public Result<?> add(@RequestBody CooperationInfo cooperationInfo) {
    cooperationInfoService.save(cooperationInfo);
    return Result.ok(SystemConstants.ADD_SUCCESS);
  }

  /**
   * 编辑
   *
   * @param cooperationInfo
   * @return
   */
  @AutoLog(value = "商家合作信息表-" + SystemConstants.EDIT)
  @Operation(summary = "商家合作信息表-" + SystemConstants.EDIT, description = "商家合作信息表-" + SystemConstants.EDIT)
  @PutMapping(value = "/edit")
  public Result<?> edit(@RequestBody CooperationInfo cooperationInfo) {
    cooperationInfoService.updateById(cooperationInfo);
    return Result.ok(SystemConstants.EDIT_SUCCESS);
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "商家合作信息表-" + SystemConstants.DELETE_BY_ID)
  @Operation(summary = "商家合作信息表-" + SystemConstants.DELETE_BY_ID, description = "商家合作信息表-" + SystemConstants.DELETE_BY_ID)
  @DeleteMapping(value = "/delete")
  public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
    cooperationInfoService.removeById(id);
    return Result.ok(SystemConstants.DELETE_SUCCESS);
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "商家合作信息表-" + SystemConstants.DELETE_BATCH)
  @Operation(summary = "商家合作信息表-" + SystemConstants.DELETE_BATCH, description = "商家合作信息表-" + SystemConstants.DELETE_BATCH)
  @DeleteMapping(value = "/deleteBatch")
  public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.cooperationInfoService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  @AutoLog(value = "商家合作信息表-" + SystemConstants.QUERY_BY_ID)
  @Operation(summary = "商家合作信息表-" + SystemConstants.QUERY_BY_ID, description = "商家合作信息表-" + SystemConstants.QUERY_BY_ID)
  @GetMapping(value = "/queryById")
  public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
    CooperationInfo cooperationInfo = cooperationInfoService.getById(id);
    return Result.ok(cooperationInfo);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param cooperationInfo
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, CooperationInfo cooperationInfo) {
    return super.exportXls(request, cooperationInfo, CooperationInfo.class, "商家合作信息表");
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
        boolean aBoolean = ExcelImportCheckUtil.check(file.getInputStream(), CooperationInfo.class, params);
        if (!aBoolean) {
          return Result.error("文件格式错误！");
        }
        //项目状态
        List<DictModel> projectStatus = sysBaseAPI.queryDictItemsByCode("project_status");
        //商务人员
        List<LoginUser> businessUserList = sysBaseAPI.getBusinessUserList();
        //品牌
        List<KolBrand> kolBrands = brandService.queryListAll("");
        //获客方式
        List<DictModel> customerAcquisitionMethod = sysBaseAPI.queryDictItemsByCode("customer_acquisition_method");

        long start = System.currentTimeMillis();
        // 获取上传文件列表
        List<CooperationInfo> list = ExcelImportUtil.importExcel(file.getInputStream(), CooperationInfo.class, params);
        // 删除数组中空对象
        list = list.stream().filter(x -> !oConvertUtils.isAllFieldNull(x)).collect(Collectors.toList());
        List<CooperationInfo> listNew = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        int count = 0;
        for (CooperationInfo cooperationInfo : list) {
          count++;
          if (checkData(cooperationInfo, kolBrands, businessUserList, projectStatus, customerAcquisitionMethod, errorList, count)) {
            continue;
          }
          listNew.add(cooperationInfo);
        }
        cooperationInfoService.saveBatch(listNew);
        log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
        int i = list.size() - listNew.size();
        return i > 0 ? Result.OK("文件导入成功！成功行数：" + listNew.size() + ",失败行数：" + i, errorList) : Result.ok("文件导入成功！数据行数：" + listNew.size());
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
  private boolean checkData(CooperationInfo cooperationInfo, List<KolBrand> kolBrands, List<LoginUser> businessUserList, List<DictModel> projectStatus, List<DictModel> customerAcquisitionMethod,
      List<String> errorList, int count) {
    if (oConvertUtils.isEmpty(cooperationInfo.getBrandName()) || oConvertUtils.isEmpty(cooperationInfo.getBusinessName())) {
      errorList.add("第" + count + "行，品牌或商务为空");
      return true;
    }
    Optional.ofNullable(cooperationInfo.getBrandName()).flatMap(brandName -> kolBrands.stream().filter(x -> x.getBrandName().equals(brandName)).findFirst())
        .ifPresent(x -> cooperationInfo.setBrandId(x.getId()));

    Optional.ofNullable(cooperationInfo.getBusinessName()).flatMap(businessName -> businessUserList.stream().filter(x -> x.getRealname().equals(businessName)).findFirst())
        .ifPresent(x -> cooperationInfo.setBusinessId(x.getId()));

    Optional.ofNullable(cooperationInfo.getProjectStatus()).flatMap(level -> projectStatus.stream().filter(x -> x.getText().equals(level)).findFirst().map(DictModel::getValue))
        .ifPresent(cooperationInfo::setProjectStatus);

    Optional.ofNullable(cooperationInfo.getCustomerAcquisitionMethod())
        .flatMap(method -> customerAcquisitionMethod.stream().filter(x -> x.getText().equals(method)).findFirst().map(DictModel::getValue)).ifPresent(cooperationInfo::setCustomerAcquisitionMethod);
    try {
      if (oConvertUtils.isNotEmpty(cooperationInfo.getIgQuantityStr())) {
        cooperationInfo.setIgQuantity(Integer.parseInt(cooperationInfo.getIgQuantityStr()));
      }
    } catch (Exception e) {
      System.out.println("IG数量转换错误");
      errorList.add("第" + count + "行，IG数量输入错误");
      return true;
    }
    try {
      if (oConvertUtils.isNotEmpty(cooperationInfo.getYtQuantityStr())) {
        cooperationInfo.setYtQuantity(Integer.parseInt(cooperationInfo.getYtQuantityStr()));
      }
    } catch (Exception e) {
      System.out.println("YT数量转换错误");
      errorList.add("第" + count + "行，YT数量输入错误");
      return true;
    }
    try {
      if (oConvertUtils.isNotEmpty(cooperationInfo.getTtQuantityStr())) {
        cooperationInfo.setTtQuantity(Integer.parseInt(cooperationInfo.getTtQuantityStr()));
      }
    } catch (Exception e) {
      System.out.println("TT数量转换错误");
      errorList.add("第" + count + "行，TT数量输入错误");
      return true;
    }
    try {
      if (oConvertUtils.isNotEmpty(cooperationInfo.getCooperationAmountStr())) {
        cooperationInfo.setCooperationAmount(new BigDecimal(cooperationInfo.getCooperationAmountStr()));
      }
    } catch (Exception e) {
      System.out.println("合作金额转换错误");
      errorList.add("第" + count + "行，合作金额输入错误");
      return true;
    }
    try {
      if (oConvertUtils.isNotEmpty(cooperationInfo.getCooperationDateStr())) {
        cooperationInfo.setCooperationDate(parseExcelDate(cooperationInfo.getCooperationDateStr()));
      }
    } catch (Exception e) {
      System.out.println("合作日期转换错误");
      errorList.add("第" + count + "行，合作日期输入错误，格式为：2025-01-01");
      return true;
    }
    return false;
  }

  private Date parseExcelDate(String dateStr) {
    if (dateStr == null || dateStr.trim().isEmpty()) {
      return null;
    }

    try {
      // 1. 判断是否是 Excel 日期数字（45678 这种情况）
      if (dateStr.matches("\\d+")) {
        double dateNum = Double.parseDouble(dateStr);
        return HSSFDateUtil.getJavaDate(dateNum);
      }

      // 2. 可能是常见的日期格式
      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");

      try {
        return sdf1.parse(dateStr);
      } catch (ParseException ignored) {}

      try {
        return sdf2.parse(dateStr);
      } catch (ParseException ignored) {}

    } catch (Exception e) {
      e.printStackTrace(); // 这里可以用日志代替
    }

    return null;
  }

  /**
   * 下载模板
   */
  @RequestMapping(value = "/downloadXls", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void downloadXls(HttpServletResponse response) throws IOException {
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = wb.createSheet("Sheet");

    String[] header = {"合作日期(格式为：2025-01-01)", "品牌", "产品计划", "商务", "合作金额", "获客方式", "TT数量", "IG数量", "YT数量", "最新合作动态", "项目状态", "备注"};
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
    //项目状态
    createDropdownList(sheet, getDropdownItemsFromDict("project_status"), 10);

    //商务人员
    createDropdownList(sheet, getDropdownItems(sysBaseAPI.getBusinessUserList(), LoginUser::getRealname), 3);

    //品牌
    createDropdownList(sheet, getDropdownItems(brandService.queryListAll(""), KolBrand::getBrandName), 1);

    //获客方式
    createDropdownList(sheet, getDropdownItemsFromDict("customer_acquisition_method"), 5);

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
