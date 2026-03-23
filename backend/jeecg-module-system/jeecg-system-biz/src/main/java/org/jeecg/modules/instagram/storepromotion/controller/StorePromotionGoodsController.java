package org.jeecg.modules.instagram.storepromotion.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.PromotionGoodsType;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.entity.StorePromotionGoods;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerPromotion;
import org.jeecg.modules.instagram.storepromotion.model.StorePromotionGoodsModel;
import org.jeecg.modules.instagram.storepromotion.service.IStorePromotionGoodsService;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.kol.entity.KolProduct;
import org.jeecg.modules.kol.service.IKolProductService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 商家推广产品表
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家推广产品表")
@RestController
@RequestMapping("/storepromotiongoods/storePromotionGoods")
public class StorePromotionGoodsController extends JeecgController<StorePromotionGoods, IStorePromotionGoodsService> {
    @Autowired
    private IStorePromotionGoodsService storePromotionGoodsService;

    @Autowired
    private IStoreSellerPromotionService storeSellerPromotionService;
    @Resource
    private IKolProductService kolProductService;

    /**
     * 分页列表查询
     *
     * @param storePromotionGoods
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家推广产品表-分页列表查询")
@Operation(summary = "商家推广产品表-分页列表查询", description = "商家推广产品表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorePromotionGoods storePromotionGoods,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StorePromotionGoods> queryWrapper = QueryGenerator.initQueryWrapper(storePromotionGoods, req.getParameterMap());
        Page<StorePromotionGoods> page = new Page<StorePromotionGoods>(pageNo, pageSize);
        IPage<StorePromotionGoods> pageList = storePromotionGoodsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storePromotionGoods
     * @return
     */
    @AutoLog(value = "商家推广产品表-添加")
@Operation(summary = "商家推广产品表-添加", description = "商家推广产品表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorePromotionGoods storePromotionGoods) {
        String promId = storePromotionGoods.getPromId();
        if (oConvertUtils.isEmpty(promId)) {
            return Result.error("添加失败，未获取到相关推广！");
        }
        StoreSellerPromotion sellerPromotion = storeSellerPromotionService.getById(promId);
        if (sellerPromotion == null) {
            return Result.error("添加失败，未获取到相关推广！");
        }
        if (sellerPromotion.getPromStatus() == PromotionGoodsType.FINISHEDNORMAL.getCode() || sellerPromotion.getPromStatus() == PromotionGoodsType.FINISHEDABNORMAL.getCode()) {
            return Result.error("添加失败，当前推广已结束！");
        }

        List<StorePromotionGoods> list = storePromotionGoodsService.lambdaQuery().eq(StorePromotionGoods::getPromId, promId).list();
        List<StorePromotionGoods> goodsList = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getKolProductId())).collect(Collectors.toList());
        boolean match = goodsList.stream().anyMatch(x -> x.getKolProductId().equals(storePromotionGoods.getKolProductId()));
        if (match) {
            return Result.error("添加失败，该产品已添加！");
        }
        storePromotionGoods.setKolProductId(storePromotionGoods.getKolProductId());
        storePromotionGoods.setSellerId(sellerPromotion.getSellerId());
        storePromotionGoods.setSellerName(sellerPromotion.getSellerName());
        storePromotionGoods.setExtensionCode(sellerPromotion.getExtensionCode());
        storePromotionGoods.setGoodsStatus(YesNoStatus.NO.getCode());
        storePromotionGoodsService.save(storePromotionGoods);
        StoreSellerPromotion sellerPromotionNew = new StoreSellerPromotion();
        sellerPromotionNew.setId(sellerPromotion.getId());
        list.add(storePromotionGoods);
        List<String> productIds = list.stream().map(StorePromotionGoods::getKolProductId).collect(Collectors.toList());
        List<KolProduct> kolProducts = (List<KolProduct>) kolProductService.listByIds(productIds);
        String productBrandId = kolProducts.stream().map(KolProduct::getBrandId).collect(Collectors.joining(","));
        String productBrand = kolProducts.stream().map(KolProduct::getBrandName).collect(Collectors.joining(","));
        sellerPromotionNew.setProductBrandId(productBrandId);
        sellerPromotionNew.setProductBrand(productBrand);
        storeSellerPromotionService.updateById(sellerPromotionNew);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storePromotionGoods
     * @return
     */
    @AutoLog(value = "商家推广产品表-编辑")
@Operation(summary = "商家推广产品表-编辑", description = "商家推广产品表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorePromotionGoods storePromotionGoods) {
        List<StorePromotionGoods> list = storePromotionGoodsService.lambdaQuery().eq(StorePromotionGoods::getPromId, storePromotionGoods.getPromId()).list();
        List<StorePromotionGoods> goodsList = list.stream().filter(x -> oConvertUtils.isNotEmpty(x.getKolProductId())).collect(Collectors.toList());
        boolean match = goodsList.stream().anyMatch(x -> x.getKolProductId().equals(storePromotionGoods.getKolProductId()) && !x.getId().equals(storePromotionGoods.getId()));
        if (match) {
            return Result.error("添加失败，该产品已添加！");
        }
        // flag 标识需要修改产品状态
        storePromotionGoodsService.updateGoodsStatus(storePromotionGoods, true);
        return Result.ok("编辑成功!");
    }

    /**
     * 编辑
     *
     * @param storePromotionGoods
     * @return
     */
    @AutoLog(value = "商家推广产品表-编辑商品信息")
@Operation(summary = "商家推广产品表-编辑商品信息", description = "商家推广产品表-编辑商品信息")
    @PutMapping(value = "/editGoods")
    public Result<?> editGoods(@RequestBody StorePromotionGoods storePromotionGoods) {
        String id = storePromotionGoods.getId();
        StorePromotionGoods promotionGoods = storePromotionGoodsService.getById(id);
        if (promotionGoods == null) {
            return Result.error("未获取到商品信息!");
        }
        StorePromotionGoods promotionGoodsNew = new StorePromotionGoods();
        promotionGoodsNew.setId(id);
        promotionGoodsNew.setGoodsTitle(storePromotionGoods.getGoodsTitle());
        promotionGoodsNew.setGoodsUrl(storePromotionGoods.getGoodsUrl());
        promotionGoodsNew.setGoodsPicUrl(storePromotionGoods.getGoodsPicUrl());
        // flag 标识需要修改产品状态
        storePromotionGoodsService.updateById(promotionGoodsNew);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家推广产品表-通过id删除")
@Operation(summary = "商家推广产品表-通过id删除", description = "商家推广产品表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        StorePromotionGoods storePromotionGoods = storePromotionGoodsService.getById(id);
        StoreSellerPromotion storeSellerPromotion = storeSellerPromotionService.getById((storePromotionGoods.getPromId()));
        Integer approvedStatus = storeSellerPromotion.getApprovedStatus();
        if (approvedStatus == YesNoStatus.NO.getCode()) {
            // 查询当前推广需求下是否存在别的产品
            LambdaQueryWrapper<StorePromotionGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(StorePromotionGoods::getPromId, storePromotionGoods.getPromId());
            List<StorePromotionGoods> goodsList = storePromotionGoodsService.list(lambdaQueryWrapper);
            if (goodsList.size() > 1) {
                storePromotionGoodsService.removeById(id);
                return Result.ok("删除成功!");
            } else {
                return Result.error("删除失败，必须保留一个产品!");
            }
        } else {
            return Result.error("需求已审核，不可删除!");
        }
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家推广产品表-批量删除")
@Operation(summary = "商家推广产品表-批量删除", description = "商家推广产品表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storePromotionGoodsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家推广产品表-通过id查询")
@Operation(summary = "商家推广产品表-通过id查询", description = "商家推广产品表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorePromotionGoods storePromotionGoods = storePromotionGoodsService.getById(id);
        return Result.ok(storePromotionGoods);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storePromotionGoods
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorePromotionGoods storePromotionGoods) {
        return super.exportXls(request, storePromotionGoods, StorePromotionGoods.class, "商家推广产品表");
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
        return super.importExcel(request, response, StorePromotionGoods.class);
    }

    /**
     * 通过seller_id查询
     *
     * @param promId
     * @return
     */
    @AutoLog(value = "商家推广产品表-通过seller_id查询")
@Operation(summary = "商家推广产品表-通过seller_id查询", description = "商家推广产品表-通过seller_id查询")
    @GetMapping(value = "/queryByPromId")
    public Result<?> queryByPromId(@RequestParam(name = "promId", required = true) String promId) {
        List<StorePromotionGoods> storePromotionGoodsList = storePromotionGoodsService.queryByPromId(promId);
        return Result.ok(storePromotionGoodsList);
    }

    /**
     * 功能描述:根据推广需求id获取产品和网红信息
     *
     * @Author: nqr
     * @Date 2021-03-15 11:40:23
     */
    @GetMapping(value = "/getList")
    public Result<?> getList(@RequestParam(name = "promId", required = true) String promId) {
        List<StorePromotionGoodsModel> list = storePromotionGoodsService.getList(promId);
        return Result.ok(list);
    }

    /**
     * 方法描述: 根据推广需求id导出产品和网红信息
     *
     * @author nqr
     * @date 2024/03/12 10:56
     **/
    @RequestMapping(value = "/exportList")
    public ModelAndView exportList(@RequestParam(name = "promId", required = true) String promId) {
        List<StorePromotionGoodsModel> list = storePromotionGoodsService.getList(promId);
        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "推广匹配");
        mv.addObject(NormalExcelConstants.CLASS, StorePromotionGoodsModel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("推广匹配报表", "导出人:" + getRealName(), "推广匹配"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }
}
