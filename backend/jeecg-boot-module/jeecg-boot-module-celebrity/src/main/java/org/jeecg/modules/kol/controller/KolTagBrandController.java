package org.jeecg.modules.kol.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.entity.KolTagBrand;
import org.jeecg.modules.kol.entity.KolTags;
import org.jeecg.modules.kol.model.KolTagBrandExportModel;
import org.jeecg.modules.kol.model.KolTagBrandModel;
import org.jeecg.modules.kol.service.IKolBrandService;
import org.jeecg.modules.kol.service.IKolTagBrandService;
import org.jeecg.modules.kol.service.IKolTagsService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Description: 标签品牌表
 * @Author: xyc
 * @Date: 2025-01-02 18:46:09
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "标签品牌表")
@RestController
@RequestMapping("/kol/tagsBrand")
public class KolTagBrandController extends JeecgController<KolTagBrand, IKolTagBrandService> {

  @Autowired
  private IKolTagBrandService kolTagsBrandService;
  @Autowired
  private IKolTagsService kolTagsService;
  @Autowired
  private IKolBrandService kolBrandService;

  /**
   * 分页列表查询
   *
   * @param kolTagBrand
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  @AutoLog(value = "标签品牌表-" + SystemConstants.PAGE_LIST_QUERY)
  @Operation(summary = "标签品牌表-" + SystemConstants.PAGE_LIST_QUERY, description = "标签品牌表-" + SystemConstants.PAGE_LIST_QUERY)
  @GetMapping(value = "/list")
  public Result<?> queryPageList(KolTagBrand kolTagBrand, @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
      @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize, HttpServletRequest req) {
    Page<KolTagBrand> page = new Page<>(pageNo, pageSize);
    IPage<KolTagBrand> pageList = kolTagsBrandService.pageList(page, kolTagBrand);
        /*for (KolTagBrand tagsBrand : pageList.getRecords()) {
            String storeTags = tagsBrand.getStoreTags();
            if (oConvertUtils.isNotEmpty(storeTags)) {
                List<String> list = JSONObject.parseArray(storeTags, String.class);
                if (list != null) {
                    tagsBrand.setStoreTags(String.join(",", list));
                    continue;
                }
                tagsBrand.setStoreTags(null);
                continue;
            }
            tagsBrand.setStoreTags(null);
        }*/
    return Result.ok(pageList);
  }

  @GetMapping(value = "/queryList")
  public Result<?> queryList(KolTagBrand kolTagBrand) {
    List<KolTagBrand> list = kolTagsBrandService.list();
    return Result.ok(list);
  }

  /**
   * 添加
   *
   * @return
   */
  @AutoLog(value = "标签品牌表-" + SystemConstants.ADD)
  @Operation(summary = "标签品牌表-" + SystemConstants.ADD, description = "标签品牌表-" + SystemConstants.ADD)
  @PostMapping(value = "/add")
  public Result<?> add(@RequestBody JSONObject jsonObject) {
    JSONArray jsonArray = jsonObject.getJSONArray("tiktokTagsBrands");
    List<KolTagBrand> kolTagBrands = jsonArray.toJavaList(KolTagBrand.class);
    List<KolTagBrand> tkTagsBrands = new ArrayList<>();
    for (KolTagBrand kolTagBrand : kolTagBrands) {
      String tagName = kolTagBrand.getTagName();
      String brandName = kolTagBrand.getBrandName();
      LambdaQueryWrapper<KolTagBrand> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(KolTagBrand::getTagName, tagName);
      queryWrapper.eq(KolTagBrand::getBrandName, brandName);
      int count = Math.toIntExact(kolTagsBrandService.count(queryWrapper));
      if (count > 0) {
        continue;
      }
      if (oConvertUtils.isEmpty(kolTagBrand.getStoreTags())) {
        kolTagBrand.setStoreTags(JSONArray.toJSONString(Collections.emptyList()));
      } else {
        kolTagBrand.setStoreTags(JSONArray.toJSONString(Collections.singletonList(kolTagBrand.getStoreTags())));
      }
      kolTagBrand.setId(IdWorker.getIdStr());
      kolTagBrand.setCreateBy(getUserNameByToken());
      kolTagBrand.setCreateTime(new Date());
      tkTagsBrands.add(kolTagBrand);
    }
    if (tkTagsBrands.isEmpty()) {
      return Result.error("添加失败！");
    }
    kolTagsBrandService.saveBatch(tkTagsBrands);

    return Result.ok(SystemConstants.ADD_SUCCESS);
  }

  /**
   * 编辑
   *
   * @param kolTagBrand
   * @return
   */
  @AutoLog(value = "标签品牌表-" + SystemConstants.EDIT)
  @Operation(summary = "标签品牌表-" + SystemConstants.EDIT, description = "标签品牌表-" + SystemConstants.EDIT)
  @PutMapping(value = "/edit")
  public Result<?> edit(@RequestBody KolTagBrand kolTagBrand) {
    kolTagsBrandService.updateById(kolTagBrand);
    return Result.ok(SystemConstants.EDIT_SUCCESS);
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "标签品牌表-" + SystemConstants.DELETE_BY_ID)
  @Operation(summary = "标签品牌表-" + SystemConstants.DELETE_BY_ID, description = "标签品牌表-" + SystemConstants.DELETE_BY_ID)
  @DeleteMapping(value = "/delete")
  public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
    kolTagsBrandService.removeById(id);
    return Result.ok(SystemConstants.DELETE_SUCCESS);
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "标签品牌表-" + SystemConstants.DELETE_BATCH)
  @Operation(summary = "标签品牌表-" + SystemConstants.DELETE_BATCH, description = "标签品牌表-" + SystemConstants.DELETE_BATCH)
  @DeleteMapping(value = "/deleteBatch")
  public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.kolTagsBrandService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  @AutoLog(value = "标签品牌表-" + SystemConstants.QUERY_BY_ID)
  @Operation(summary = "标签品牌表-" + SystemConstants.QUERY_BY_ID, description = "标签品牌表-" + SystemConstants.QUERY_BY_ID)
  @GetMapping(value = "/queryById")
  public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
    KolTagBrand kolTagBrand = kolTagsBrandService.getById(id);
    return Result.ok(kolTagBrand);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param kolTagBrand
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, KolTagBrand kolTagBrand) {
    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
    String title = "标签品牌";
    List<KolTagBrandExportModel> exportList = kolTagsBrandService.queryList(kolTagBrand);
    for (KolTagBrandExportModel tagsBrand : exportList) {
//            String storeTags = tagsBrand.getStoreTags();
      Integer platformType = tagsBrand.getPlatformType();
      if (oConvertUtils.isNotEmpty(platformType)) {
        switch (platformType) {
          case 0:
            tagsBrand.setPlatformTypeName("IG");
            break;
          case 1:
            tagsBrand.setPlatformTypeName("YT");
            break;
          default:
            tagsBrand.setPlatformTypeName("TK");
            break;
        }
      }
           /* if (oConvertUtils.isNotEmpty(storeTags)) {
                List<String> list = JSONObject.parseArray(storeTags, String.class);
                if (list != null) {
                    tagsBrand.setStoreTags(String.join(",", list));
                    continue;
                }
                tagsBrand.setStoreTags(null);
                continue;
            }
            tagsBrand.setStoreTags(null);
            */
    }
    ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
    mv.addObject(NormalExcelConstants.FILE_NAME, "标签品牌"); // 此处设置的filename无效 ,前端会重更新设置一下
    mv.addObject(NormalExcelConstants.CLASS, KolTagBrandExportModel.class);
    mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title + "数据", "导出人:" + sysUser.getRealname(), title));
    mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
    return mv;

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
      MultipartFile file = entity.getValue();// 获取上传文件对象
      try {
        ImportParams params = new ImportParams();
        params.setHeadRows(1);
        @Cleanup InputStream inputStream = file.getInputStream();
        long start = System.currentTimeMillis();
        List<KolTagBrandModel> list = ExcelImportUtil.importExcel(inputStream, KolTagBrandModel.class, params);
//                List<KolTagBrandModel> list = EasyExcel.read(inputStream).head(KolTagBrandModel.class).sheet().doReadSync();
        HashSet<KolTagBrandModel> dataSet = new HashSet<>(list);
        HashSet<KolTagBrandModel> errorHashSet = new HashSet<>();
        list = new ArrayList<>(dataSet);
        List<KolTagBrandModel> tagsBrands = getTagsBrandModels(list);
        // 判断标签是否存在
        List<KolTags> kolTags = checkTiktokTags(list, errorHashSet);
        // 判断品牌是否存在
        List<KolBrand> kolBrands = checkTiktokBrands(tagsBrands, errorHashSet);

        List<KolTagBrand> kolTagBrands = new ArrayList<>();
        // 查询是否已存在当前数据
        for (KolTagBrandModel brandModel : tagsBrands) {
          String tagName = brandModel.getTagName();
          String brandName = brandModel.getBrandName();
          Integer platformType = brandModel.getPlatformType();
          KolTagBrand tagBrand = kolTagsBrandService.checkTagsBrand(tagName, brandName, platformType);
          Optional<KolTagBrandModel> modelOptional = errorHashSet.stream().filter(x -> x.getTagName().equals(tagName) && x.getBrandName().equals(brandName) && x.getPlatformType().equals(platformType))
              .findFirst();
          if (tagBrand != null) {
            if (modelOptional.isPresent()) {
              modelOptional.get().setErrorMsg("标签、品牌不存在，或数据重复");
            } else {
              brandModel.setErrorMsg("数据重复");
              errorHashSet.add(brandModel);
            }
            continue;
          }
          if (modelOptional.isPresent()) {
            continue;
          }
          KolTagBrand kolTagBrand = new KolTagBrand();
          kolTagBrand.setTagName(tagName);
          kolTagBrand.setBrandName(brandName);
          kolTagBrand.setTagProduct(brandModel.getTagProduct());
          kolTagBrand.setPlatformType(platformType);
          kolTags.stream().filter(x -> x.getTagName().equals(tagName) && x.getPlatformType().equals(platformType)).findFirst().ifPresent(tags -> kolTagBrand.setTagId(tags.getId()));
          kolBrands.stream().filter(x -> x.getBrandName().equals(brandName)).findFirst().ifPresent(tiktokBrand -> kolTagBrand.setBrandId(tiktokBrand.getId()));
          if (oConvertUtils.isNotEmpty(kolTagBrand.getBrandId()) && oConvertUtils.isNotEmpty(kolTagBrand.getTagId())) {
            kolTagBrands.add(kolTagBrand);
          }
        }
        if (!kolTagBrands.isEmpty()) {
          kolTagsBrandService.saveBatch(kolTagBrands);
        }
        log.info("消耗时间{}毫秒", System.currentTimeMillis() - start);
        String successMsg = "文件导入成功，导入" + kolTagBrands.size() + "条数据！";
               /* if (tiktokTagsBrands.isEmpty()) {
                    successMsg = "文件导入失败，";
                }*/
        List<KolTagBrandModel> errorList = new ArrayList<>(errorHashSet);
        if (kolTagBrands.isEmpty()) {
          return Result.error(500, "文件导入失败", errorList);
        } else if (errorList.isEmpty()) {
          return Result.OK(10000, successMsg, Collections.emptyList());
        } else {
          return Result.OK(10001, successMsg + "存在标签、品牌不存在或重复导入的数据", errorList);
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return Result.error("文件导入失败，请检查文件内容是否正确");
      }
    }
    return Result.error("文件导入失败！");
  }

  /**
   * 方法描述：判断品牌是否存在
   *
   * @author nqr
   * @date 2024/07/29 09:09
   **/
  private List<KolBrand> checkTiktokBrands(List<KolTagBrandModel> tagsBrands, HashSet<KolTagBrandModel> errorHashSet) {
    List<String> brandNames = tagsBrands.stream().map(KolTagBrandModel::getBrandName).distinct().collect(Collectors.toList());
    List<KolBrand> kolBrands = kolBrandService.listByNames(brandNames);

    for (KolTagBrandModel tagsBrandModel : tagsBrands) {
      String tagName = tagsBrandModel.getTagName();
      String brandName = tagsBrandModel.getBrandName();
      Optional<KolTagBrandModel> modelOptional = errorHashSet.stream().filter(x -> x.getTagName().equals(tagName) && x.getBrandName().equals(brandName)).findFirst();
      if (modelOptional.isPresent()) {
        KolTagBrandModel kolTagBrandModel = modelOptional.get();
        kolTagBrandModel.setErrorMsg("标签和品牌不存在");
      } else {
        boolean b = kolBrands.stream().anyMatch(x -> x.getBrandName().equals(brandName));
        if (!b) {
          tagsBrandModel.setErrorMsg("品牌不存在");
          errorHashSet.add(tagsBrandModel);
        }
      }
    }
    return kolBrands;
  }

  private List<KolTags> checkTiktokTags(List<KolTagBrandModel> list, HashSet<KolTagBrandModel> errorHashSet) {
    List<String> tagNames = list.stream().map(KolTagBrandModel::getTagName).distinct().collect(Collectors.toList());

    List<KolTags> kolTags = kolTagsService.listByNames(tagNames);

    for (KolTagBrandModel tagsBrandModel : list) {
      Integer platformType = tagsBrandModel.getPlatformType();
      String tagName = tagsBrandModel.getTagName();

      Optional<KolTags> optional = kolTags.stream().filter(x -> x.getTagName().equals(tagName) && x.getPlatformType().equals(platformType)).findFirst();
      if (!optional.isPresent()) {
        tagsBrandModel.setErrorMsg("标签不存在");
        errorHashSet.add(tagsBrandModel);
      }
    }
    return kolTags;
  }

  private static List<KolTagBrandModel> getTagsBrandModels(List<KolTagBrandModel> list) {
    return list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getTagName()) && oConvertUtils.isNotEmpty(x.getBrandName())).collect(Collectors.toList());
  }

}
