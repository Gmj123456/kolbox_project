package org.jeecg.modules.kol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.SystemConstants;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.kol.entity.KolBrand;
import org.jeecg.modules.kol.entity.KolContact;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.entity.KolTagBrand;
import org.jeecg.modules.kol.service.*;
import org.jeecg.modules.kol.wechatexcel.config.WechatDocConfig;
import org.jeecg.modules.kol.wechatexcel.service.WechatService;
import org.jeecg.modules.merchant.entity.CooperationInfo;
import org.jeecg.modules.merchant.entity.MerchantInfo;
import org.jeecg.modules.merchant.service.ICooperationInfoService;
import org.jeecg.modules.merchant.service.IMerchantInfoService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 品牌表
 * @Author: dongruyang
 * @Date: 2023-10-10
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "品牌表")
@RestController
@RequestMapping("/kolBrand")
public class KolBrandController extends JeecgController<KolBrand, IKolBrandService> {
    @Autowired
    private IKolBrandService brandService;
    @Autowired
    private IKolProductService productDetailService;
    @Autowired
    private IKolContactService contactService;
    @Autowired
    private IMerchantInfoService merchantInfoService;
    @Autowired
    private ICooperationInfoService cooperationInfoService;
    @Autowired
    private IKolTagBrandService tagBrandService;
    @Resource
    private WechatService wechatService;
    @Resource
    private WechatDocConfig wechatDocConfig;

    /**
     * 分页列表查询
     *
     * @param kolBrand
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "品牌表-" + SystemConstants.PAGE_LIST_QUERY)
    @Operation(summary = "品牌表-" + SystemConstants.PAGE_LIST_QUERY, description = "品牌表-" + SystemConstants.PAGE_LIST_QUERY)
    @GetMapping(value = "/list")
    public Result<?> queryPageList(KolBrand kolBrand,
                                   @RequestParam(name = SystemConstants.PAGE_NO, defaultValue = SystemConstants.PAGE_NO_DEFAULT) Integer pageNo,
                                   @RequestParam(name = SystemConstants.PAGE_SIZE, defaultValue = SystemConstants.PAGE_SIZE_DEFAULT) Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<KolBrand> queryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(kolBrand.getBrandName())) {
            queryWrapper.like(KolBrand::getBrandName, kolBrand.getBrandName());
        }
        queryWrapper.orderByAsc(KolBrand::getBrandName);
        Page<KolBrand> page = new Page<KolBrand>(pageNo, pageSize);
        IPage<KolBrand> pageList = brandService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param kolBrand
     * @return
     */
    @AutoLog(value = "品牌表列表")
    @Operation(summary = "品牌表品牌表列表", description = "品牌表品牌表列表")
    @GetMapping(value = "/listAll")
    public Result<?> queryListAll(KolBrand kolBrand) {
        List<KolBrand> brandList = brandService.queryListAll(kolBrand.getBrandName());
        return Result.ok(brandList);
    }

    /**
     * 添加
     *
     * @param kolBrand
     * @return
     */
    @AutoLog(value = "品牌表-" + SystemConstants.ADD)
    @Operation(summary = "品牌表-" + SystemConstants.ADD, description = "品牌表-" + SystemConstants.ADD)
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody KolBrand kolBrand) {
        List<KolBrand> list = brandService.list();
        for (KolBrand existingBrand : list) {
            if (existingBrand.getBrandName().equalsIgnoreCase(kolBrand.getBrandName())) {
                return Result.error("该品牌已存在");
            }
        }
        brandService.save(kolBrand);
        // 异步更新企微文档
        // asyncUpdateLabels(1, kolBrand);
        return Result.ok(SystemConstants.ADD_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param kolBrand
     * @return
     */
    @AutoLog(value = "品牌表-" + SystemConstants.EDIT)
    @Operation(summary = "品牌表-" + SystemConstants.EDIT, description = "品牌表-" + SystemConstants.EDIT)
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody KolBrand kolBrand) {
        String brandId = kolBrand.getId();
        String brandNameNew = kolBrand.getBrandName();
        List<KolBrand> brandList = brandService.lambdaQuery().ne(KolBrand::getId, brandId).eq(KolBrand::getBrandName, brandNameNew).list();
        if (!brandList.isEmpty()) {
            return Result.error("该品牌已存在");
        }
        // 查询是否关联业务数据 2025年6月20日10:49:39 刘工说有业务数据后品牌名称不能修改  
       /* KolBrand brand = brandService.getById(brandId);
        String brandNameOld = brand.getBrandName();
        if (!brandNameOld.equals(brandNameNew)) {
            if (checkBrand(brandId)) {
                return Result.error("该品牌已关联业务数据，不能修改品牌名称");
            }
        }*/

        // 同步更新所有业务数据品牌名称 2025年9月24日14:52:52 刘工说品牌名称修改后需要同步更新所有业务数据
        brandService.editBrandData(kolBrand);
        // 异步更新企微文档
        // kolBrand.setRecordId(brand.getRecordId());
        // asyncUpdateLabels(2, kolBrand);
        return Result.ok(SystemConstants.EDIT_SUCCESS);
    }

    /**
     * @description: 判断品牌是否存在业务数据
     * @author: nqr
     * @date: 2025/6/20 10:57
     **/

    private boolean checkBrand(String brandId) {
        List<KolProduct> list = productDetailService.lambdaQuery().eq(KolProduct::getBrandId, brandId).list();
        List<KolContact> kolContactList = contactService.lambdaQuery().eq(KolContact::getBrandId, brandId).list();
        List<MerchantInfo> merchantInfoList = merchantInfoService.lambdaQuery().eq(MerchantInfo::getBrandId, brandId).list();
        List<CooperationInfo> cooperationInfoList = cooperationInfoService.lambdaQuery().eq(CooperationInfo::getBrandId, brandId).list();
        List<KolTagBrand> kolTagBrands = tagBrandService.lambdaQuery().eq(KolTagBrand::getBrandId, brandId).list();
        return !list.isEmpty() || !kolContactList.isEmpty() || !merchantInfoList.isEmpty() || !cooperationInfoList.isEmpty() || !kolTagBrands.isEmpty();
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "品牌表-" + SystemConstants.DELETE_BY_ID)
    @Operation(summary = "品牌表-" + SystemConstants.DELETE_BY_ID, description = "品牌表-" + SystemConstants.DELETE_BY_ID)
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        // 查询当前品牌是否与产品关联
        if (checkBrand(id)) {
            return Result.error("该品牌已关联业务数据，不能删除");
        }
        KolBrand brand = brandService.getById(id);
        brandService.removeById(id);
        // 异步更新企微文档
        // asyncUpdateLabels(0, brand);
        return Result.ok(SystemConstants.DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "品牌表-" + SystemConstants.DELETE_BATCH)
    @Operation(summary = "品牌表-" + SystemConstants.DELETE_BATCH, description = "品牌表-" + SystemConstants.DELETE_BATCH)
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.brandService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok(SystemConstants.DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "品牌表-" + SystemConstants.QUERY_BY_ID)
    @Operation(summary = "品牌表-" + SystemConstants.QUERY_BY_ID, description = "品牌表-" + SystemConstants.QUERY_BY_ID)
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        KolBrand kolBrand = brandService.getById(id);
        return Result.ok(kolBrand);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param kolBrand
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, KolBrand kolBrand) {
        return super.exportXls(request, kolBrand, KolBrand.class, "品牌表");
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
            ImportParams params = new ImportParams();
            // params.setTitleRows(2);
            params.setHeadRows(1);
            // params.setNeedSave(true);
            try {
                List<KolBrand> list = ExcelImportUtil.importExcel(file.getInputStream(), KolBrand.class, params);
                if (oConvertUtils.listIsEmpty(list)) {
                    return Result.error("文件导入失败！未获取到有效数据！");
                }

                long start = System.currentTimeMillis();
                boolean match = list.stream().anyMatch(x -> oConvertUtils.isEmpty(x.getBrandName()));
                if (match) {
                    return Result.error("文件导入失败！存在空品牌名称数据！");
                }

               /* List<KolBrand> brands = list.stream()
                        .filter(x -> (oConvertUtils.isNotEmpty(x.getLogoUrl()) && !oConvertUtils.isValidUrl(x.getLogoUrl()))
                                || (oConvertUtils.isNotEmpty(x.getWebsiteUrl()) || !oConvertUtils.isValidUrl(x.getWebsiteUrl())))
                        .collect(Collectors.toList());

                if (!brands.isEmpty()) {
                    return Result.error("文件导入失败！存在无效的品牌Logo或官方网站URL数据！");
                }*/

                // 判断品牌名称是否有重复数据
                Map<String, Long> frequencyMap = list.stream()
                        .map(KolBrand::getBrandName)
                        .collect(Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()
                        ));

                Set<String> duplicates = frequencyMap.entrySet().stream()
                        .filter(entry -> entry.getValue() > 1)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());

                if (!duplicates.isEmpty()) {
                    return Result.error("文件导入失败！存在重复的品牌名称：" + duplicates);
                }
                List<String> brandNames = list.stream().map(KolBrand::getBrandName).collect(Collectors.toList());

                List<KolBrand> brandListUpdate = new ArrayList<>();
                // 按照产品名称查询是否已经存在
                List<KolBrand> kolBrands = brandService.lambdaQuery().in(KolBrand::getBrandName, brandNames).list();
                if (!kolBrands.isEmpty()) {
                    for (KolBrand kolBrand : kolBrands) {
                        list.stream().filter(x -> x.getBrandName().equals(kolBrand.getBrandName())).findFirst().ifPresent(x -> {
                            x.setId(kolBrand.getId());
                            brandListUpdate.add(x);
                        });
                    }
                }
                List<String> collect = brandListUpdate.stream().map(KolBrand::getBrandName).distinct().collect(Collectors.toList());
                List<KolBrand> brandListSave = list.stream().filter(x -> !collect.contains(x.getBrandName())).collect(Collectors.toList());

                brandService.saveOrUpdateImportDataBatch(brandListSave, brandListUpdate);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败,请检查文件内容是否正确");
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
     * 下载模板
     */
    @RequestMapping(value = "/downloadXls", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadXls(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet");

        String[] header = {"品牌名称", "品牌描述", "备注"};
        Row row = sheet.createRow(0);
        // 创建一个居中格式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        // 水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        for (int i = 0; i < header.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(style);  // 设置该单元格的样式为居中
            sheet.setColumnWidth(i, 4000);  // 调整列宽以适应内容
        }
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

    private void asyncUpdateLabels(int type, KolBrand kolBrand) {
     /*   List<KolProduct> products = productService.lambdaQuery().eq(KolProduct::getBrandId, kolBrand.getId()).list();
        if (products.isEmpty()) {
            return;
        }
        // 同步更新企微选项
        try {
            products.stream().forEach(product -> {
                CompletableFuture.runAsync(() -> {
                    // 获取当前操作
                    switch (type) {
                        case 0:
                            // 删除记录
                            wechatService.removeRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getProductTagSheetId(), product.getRecordId());
                            break;
                        case 1:
                            // 新增记录
                            List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>() {{
                                add(new HashMap<String, Object>() {{
                                    put("values", new HashMap<String, Object>() {{
                                        put("品牌", new ArrayList<Map<String, Object>>() {{
                                            add(new HashMap<String, Object>() {{
                                                put("type", "text");
                                                put("text", kolBrand.getBrandName());
                                            }});
                                        }});
                                    }});
                                }});
                            }};
                            List<Record> records = wechatService.createRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getBrandTagSheetId(), recordList);
                            // 更新数据库中对应标签的recordId
                            KolBrand brand = new KolBrand();
                            brand.setId(kolBrand.getId());
                            brand.setRecordId(records.get(0).getRecordId());
                            brandService.updateById(brand);
                            break;
                        case 2:
                            if (oConvertUtils.isEmpty(kolBrand.getRecordId())) {
                                break;
                            }
                            // 编辑记录
                            List<Map<String, Object>> updateRecords = Collections.singletonList(new HashMap<String, Object>() {{
                                put("record_id", kolBrand.getRecordId());
                                put("values", Collections.singletonMap("品牌", Collections.singletonList(new HashMap<String, Object>() {{
                                    put("text", kolBrand.getBrandName());
                                    put("type", "text");
                                }})));
                            }});
                            wechatService.updateRecords(wechatDocConfig.getTemplateDocId(), wechatDocConfig.getBrandTagSheetId(), updateRecords);
                            break;
                    }
                });
            });
        } catch (Exception e) {
            log.error("同步企微品牌失败", e);
        }*/
    }
}
