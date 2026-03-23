package org.jeecg.modules.instagram.storecelebrity.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityTrial;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityTrialModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityService;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityTrialService;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGrade;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerGradeService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: store_celebrity 网红档案(试用)
 * @Author: zhoushen
 * @Date: 2020-11-06
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "store_celebrity 网红档案(试用)")
@RestController
@RequestMapping("/storecelebritytrial/storeCelebrityTrial")
public class StoreCelebrityTrialController extends JeecgController<StoreCelebrityTrial, IStoreCelebrityTrialService> {
    @Autowired
    private IStoreCelebrityTrialService storeCelebrityTrialService;

    @Autowired
    private IStoreCelebrityService storeCelebrityService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IStoreSellerGradeService sellerGradeService;

    /**
     * 分页列表查询
     *
     * @param storeCelebrityTrialModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "网红档案(试用)-分页列表查询")
@Operation(summary = "store_celebrity 网红档案(试用)-分页列表查询", description = "store_celebrity 网红档案(试用)-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityTrialModel storeCelebrityTrialModel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        String sellerId = getUserIdByToken();
        SysUser sysUser = userService.getById(sellerId);
        StoreSellerGrade sellerGrade = sellerGradeService.getById(sysUser.getGradeId());
        storeCelebrityTrialModel.setSellerId(sellerId);
        Integer gradeLever = sellerGrade.getGradeLever();
        //获取总条数
        Integer allCount = storeCelebrityTrialService.getCount(storeCelebrityTrialModel);
        //总页数
        int pageAllNum = allCount / pageSize;
        if (gradeLever == 0 && sysUser.getUserType() > 0) {
            if (pageNo > pageAllNum - 1 && pageAllNum != 0) {
                return Result.error("请充值后查看！");
            }
        }
        /*        QueryWrapper<StoreCelebrityTrial> queryWrapper = QueryGenerator.initQueryWrapper(storeCelebrityTrialModel, req.getParameterMap());*/
        List<StoreCelebrityTrialModel> resultList = new ArrayList<>();
        Page<StoreCelebrityTrial> page = new Page<>(pageNo, pageSize);
        IPage<StoreCelebrityTrialModel> pageList = storeCelebrityTrialService.getStoreCelebrityTrialList(page, storeCelebrityTrialModel);
        for (StoreCelebrityTrialModel priceModel : pageList.getRecords()) {
            String description = priceModel.getDescription();
            if (oConvertUtils.isNotEmpty(description)) {
                priceModel.setDescription(description.replaceAll("\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}", ""));
            }
            priceModel.setEmail("");
            BigDecimal kolPriceMin = priceModel.getKolPriceMin();
            BigDecimal kolPriceMax = priceModel.getKolPriceMax();
            if (oConvertUtils.isEmpty(kolPriceMin) && oConvertUtils.isNotEmpty(kolPriceMax)) {
                priceModel.setPriceRange("$0" + " ~ " + kolPriceMax.intValue());
            }
            if (oConvertUtils.isNotEmpty(kolPriceMin) && oConvertUtils.isEmpty(kolPriceMax)) {
                priceModel.setPriceRange("$" + kolPriceMin.intValue() + " ~ ∞");
            }
            if (oConvertUtils.isNotEmpty(kolPriceMin) && oConvertUtils.isNotEmpty(kolPriceMax)) {
                priceModel.setPriceRange("$" + kolPriceMin.intValue() + " ~ " + kolPriceMax.intValue());
            }
            resultList.add(priceModel);
        }
        pageList.setRecords(resultList);
        if (gradeLever == 0 && sysUser.getUserType() > 0) {
            pageList.setTotal(3084140);
            pageList.setPages(154207);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageList", pageList);
        jsonObject.put("pageAllNum", pageAllNum - 1);
        return Result.ok(jsonObject);
    }

    /**
     * 分页列表查询
     *
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/staticList")
    public Result<?> staticList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                HttpServletRequest req) {
        //获取总条数
        Integer allCount = storeCelebrityTrialService.getStaticListCount();
        //总页数
        int pageAllNum = allCount / pageSize;
        if (pageNo > pageAllNum - 1) {
            return Result.error("请充值后查看！");
        }
        List<StoreCelebrityTrialModel> resultList = new ArrayList<>();
        Page<StoreCelebrityTrial> page = new Page<>(pageNo, pageSize);
        IPage<StoreCelebrityTrialModel> pageList = storeCelebrityTrialService.getStaticList(page);
        for (StoreCelebrityTrialModel priceModel : pageList.getRecords()) {
            String description = priceModel.getDescription();
            if (oConvertUtils.isNotEmpty(description)) {
                priceModel.setDescription(description.replaceAll("\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}", ""));
            }
            priceModel.setEmail("");
            BigDecimal kolPriceMin = priceModel.getKolPriceMin();
            BigDecimal kolPriceMax = priceModel.getKolPriceMax();
            if (oConvertUtils.isEmpty(kolPriceMin) && oConvertUtils.isNotEmpty(kolPriceMax)) {
                priceModel.setPriceRange("$0" + " ~ " + kolPriceMax.intValue());
            }
            if (oConvertUtils.isNotEmpty(kolPriceMin) && oConvertUtils.isEmpty(kolPriceMax)) {
                priceModel.setPriceRange("$" + kolPriceMin.intValue() + " ~ ∞");
            }
            if (oConvertUtils.isNotEmpty(kolPriceMin) && oConvertUtils.isNotEmpty(kolPriceMax)) {
                priceModel.setPriceRange("$" + kolPriceMin.intValue() + " ~ " + kolPriceMax.intValue());
            }
            resultList.add(priceModel);
        }
        pageList.setRecords(resultList);
        pageList.setTotal(3084140);
        pageList.setPages(154207);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageList", pageList);
        jsonObject.put("pageAllNum", pageAllNum - 1);
        return Result.ok(jsonObject);
    }

    /**
     * 添加
     *
     * @param storeCelebrityTrial
     * @return
     */
    @AutoLog(value = "网红档案(试用)-添加")
@Operation(summary = "store_celebrity 网红档案(试用)-添加", description = "store_celebrity 网红档案(试用)-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebrityTrial storeCelebrityTrial) {
        storeCelebrityTrialService.save(storeCelebrityTrial);
        return Result.ok("添加成功！");
    }

    /**
     * 分页列表查询
     *
     * @param storeCelebrityTrialModel
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "网红档案(试用)-分页列表查询")
@Operation(summary = "store_celebrity 网红档案(试用)-分页列表查询", description = "store_celebrity 网红档案(试用)-分页列表查询")
    @GetMapping(value = "/getStoreCelebrityTrialList")
    public Result<?> getStoreCelebrityTrialList(StoreCelebrityTrialModel storeCelebrityTrialModel,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {

        Page<StoreCelebrityTrial> page = new Page<>(pageNo, pageSize);
        storeCelebrityTrialModel.setSellerId(getUserIdByToken());
        IPage<StoreCelebrityTrialModel> pageList = storeCelebrityTrialService.getStoreCelebrityTrialList(page, storeCelebrityTrialModel);
        return Result.ok(pageList);
    }


    /**
     * 批量添加
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红档案(试用)-批量添加")
@Operation(summary = "网红档案(试用)-批量添加", description = "网红档案(试用)-批量添加")
    @DeleteMapping(value = "/batchAddTrial")
    public Result<?> batchAddTrial(@RequestParam(name = "ids", required = true) String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        List<StoreCelebrityTrial> listNew = new ArrayList<>();
        List<StoreCelebrity> storeCelebrities = (List<StoreCelebrity>) storeCelebrityService.listByIds(idList);
        for (StoreCelebrity storeCelebrity : storeCelebrities) {
            //判断该网红是否已添加
            LambdaQueryWrapper<StoreCelebrityTrial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(StoreCelebrityTrial::getId, storeCelebrity.getId());
            List<StoreCelebrityTrial> list = storeCelebrityTrialService.list(lambdaQueryWrapper);
            if (list.size() == 0) {
                StoreCelebrityTrial newStoreCelebrityTrial = new StoreCelebrityTrial();
                BeanUtils.copyProperties(storeCelebrity, newStoreCelebrityTrial);
                listNew.add(newStoreCelebrityTrial);
            }
        }

        this.storeCelebrityTrialService.saveBatch(listNew);
        return Result.ok("添加成功");

    }


    /**
     * 编辑
     *
     * @param storeCelebrityTrial
     * @return
     */
    @AutoLog(value = "网红档案(试用)-编辑")
@Operation(summary = "store_celebrity 网红档案(试用)-编辑", description = "store_celebrity 网红档案(试用)-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebrityTrial storeCelebrityTrial) {
        storeCelebrityTrialService.updateById(storeCelebrityTrial);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红档案(试用)-通过id删除")
@Operation(summary = "store_celebrity 网红档案(试用)-通过id删除", description = "store_celebrity 网红档案(试用)-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeCelebrityTrialService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "网红档案(试用)-批量删除")
@Operation(summary = "store_celebrity 网红档案(试用)-批量删除", description = "store_celebrity 网红档案(试用)-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeCelebrityTrialService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红档案(试用)-通过id查询")
@Operation(summary = "store_celebrity 网红档案(试用)-通过id查询", description = "store_celebrity 网红档案(试用)-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityTrial storeCelebrityTrial = storeCelebrityTrialService.getById(id);
        return Result.ok(storeCelebrityTrial);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebrityTrial
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityTrial storeCelebrityTrial) {
        return super.exportXls(request, storeCelebrityTrial, StoreCelebrityTrial.class, "store_celebrity 网红档案(试用)");
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
        return super.importExcel(request, response, StoreCelebrityTrial.class);
    }

}
