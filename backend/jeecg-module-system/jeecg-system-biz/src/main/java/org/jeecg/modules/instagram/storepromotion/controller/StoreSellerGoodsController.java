package org.jeecg.modules.instagram.storepromotion.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.model.StoreSellDetailModel;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGoods;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerGoodsService;
import org.jeecg.modules.instagram.storepromotion.model.SellerCommerceModel;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerGoodsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 商家商品库表
 * @Author: jeecg-boot
 * @Date: 2020-10-02
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家商品库表")
@RestController
@RequestMapping("/storesellergoods/storeSellerGoods")
public class StoreSellerGoodsController extends JeecgController<StoreSellerGoods, IStoreSellerGoodsService> {
    @Autowired
    private IStoreSellerGoodsService storeSellerGoodsService;

    /**
     * 分页列表查询
     *
     * @param storeSellerGoods
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家商品库表-分页列表查询")
@Operation(summary = "商家商品库表-分页列表查询", description = "商家商品库表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreSellerGoods storeSellerGoods,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        if (!"admin".equals(userName)) {
            storeSellerGoods.setSellerId(userId);
        }


        QueryWrapper<StoreSellerGoods> queryWrapper = QueryGenerator.initQueryWrapper(storeSellerGoods, req.getParameterMap());
        Page<StoreSellerGoods> page = new Page<StoreSellerGoods>(pageNo, pageSize);
        IPage<StoreSellerGoods> pageList = storeSellerGoodsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }


    /**
     * 内推商品分页列表查询
     *
     * @param storeSellerGoods
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家商品库表-内推商品分页列表查询")
@Operation(summary = "商家商品库表-内推商品分页列表查询", description = "商家商品库表-内推商品分页列表查询")
    @GetMapping(value = "/pushTheGoodslist")
    public Result<?> queryPushTheGoodslist(StoreSellerGoods storeSellerGoods,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                           HttpServletRequest req) {
        String userName = getUserNameByToken();

        LambdaQueryWrapper<StoreSellerGoods> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreSellerGoods::getCreateBy, userName);
        if (oConvertUtils.isNotEmpty(storeSellerGoods.getGoodsTitle())) {
            lambdaQueryWrapper.like(StoreSellerGoods::getGoodsTitle, storeSellerGoods.getGoodsTitle());
        }

        Page<StoreSellerGoods> page = new Page<StoreSellerGoods>(pageNo, pageSize);
        IPage<StoreSellerGoods> pageList = storeSellerGoodsService.page(page, lambdaQueryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家商品库表-分页列表查询")
@Operation(summary = "商家商品库表-分页列表查询", description = "商家商品库表-分页列表查询")
    @GetMapping(value = "/appGoodsList")
    public Result<?> queryAppPageList(StoreSellerGoodsModel storeSellerGoodsModel,
                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                      HttpServletRequest req) {
        LambdaQueryWrapper<StoreSellerGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreSellerGoods::getApprovedStatus, YesNoStatus.YES.getCode());
        //查询已上架产品
        lambdaQueryWrapper.eq(StoreSellerGoods::getPutawayStatus, YesNoStatus.YES.getCode());
        if (!oConvertUtils.isAllFieldNull(storeSellerGoodsModel)) {
            if (oConvertUtils.isNotEmpty(storeSellerGoodsModel.getGoodsTitle())) {
                lambdaQueryWrapper.like(StoreSellerGoods::getGoodsTitle, storeSellerGoodsModel.getGoodsTitle());
            }
            if (oConvertUtils.isNotEmpty(storeSellerGoodsModel.getPriceStartNum())) {
                if (storeSellerGoodsModel.getPriceStartNum() != 0) {
                    lambdaQueryWrapper.gt(StoreSellerGoods::getGoodsPrice, storeSellerGoodsModel.getPriceStartNum());
                }
            }
            if (oConvertUtils.isNotEmpty(storeSellerGoodsModel.getPriceEndNum())) {
                if (storeSellerGoodsModel.getPriceEndNum() != 0) {
                    lambdaQueryWrapper.lt(StoreSellerGoods::getGoodsPrice, storeSellerGoodsModel.getPriceEndNum());
                }
            }
            if (oConvertUtils.isNotEmpty(storeSellerGoodsModel.getGoodsCategory())) {
                lambdaQueryWrapper.eq(StoreSellerGoods::getGoodsCategory, storeSellerGoodsModel.getGoodsCategory());
            }
            if (storeSellerGoodsModel.getOrder().equals("desc")) {
                String orderWord = storeSellerGoodsModel.getOrderWord();
                if (oConvertUtils.isNotEmpty(orderWord)) {
                    if ("createTime".equals(orderWord)) {
                        //时间
                        lambdaQueryWrapper.orderByDesc(StoreSellerGoods::getCreateTime);
                    }
                    if ("goodsPrice".equals(orderWord)) {
                        // 价格
                        lambdaQueryWrapper.orderByDesc(StoreSellerGoods::getGoodsPrice);
                    }
                }
            } else {
                String orderWord = storeSellerGoodsModel.getOrderWord();
                //佣金
                /*if (oConvertUtils.isNotEmpty(storeSellerGoodsModel.getGoodsServiceAmount())) {
                    lambdaQueryWrapper.orderByAsc(StoreSellerGoods::getGoodsServiceAmount);
                }*/
                if (oConvertUtils.isNotEmpty(orderWord)) {
                    if ("createTime".equals(orderWord)) {
                        //时间
                        lambdaQueryWrapper.orderByAsc(StoreSellerGoods::getCreateTime);
                    }
                    if ("goodsPrice".equals(orderWord)) {
                        //价格
                        lambdaQueryWrapper.orderByAsc(StoreSellerGoods::getGoodsPrice);
                    }
                }
            }
        }
        storeSellerGoodsModel.setApprovedStatus(1);
        Page<StoreSellerGoods> page = new Page<>(pageNo, pageSize);
        IPage<StoreSellerGoods> pageList = storeSellerGoodsService.page(page, lambdaQueryWrapper);
        /*List<StoreSellerGoods> records = pageList.getRecords();
        List<StoreSellerGoods> resultRecords = new ArrayList<>();
        for (StoreSellerGoods record : records) {
            BigDecimal goodsSellerPrice = record.getGoodsSellerPrice();
            //BigDecimal goodsServiceAmount = record.getGoodsServiceAmount();

            BigDecimal multiply = new BigDecimal(100).divide(goodsSellerPrice, 2, BigDecimal.ROUND_HALF_UP).multiply(goodsServiceAmount);
            BigDecimal bigDecimal = multiply.setScale(0, BigDecimal.ROUND_HALF_UP);

            resultRecords.add(record);
        }
        pageList.setRecords(resultRecords);*/
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param storeSellerGoods
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家商品库表-分页列表查询")
@Operation(summary = "商家商品库表-分页列表查询", description = "商家商品库表-分页列表查询")
    @GetMapping(value = "/approvedList")
    public Result<?> queryApprovedPageList(StoreSellerGoods storeSellerGoods,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                           HttpServletRequest req) {
        /*String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        if (!"admin".equals(userName)) {
            storeSellerGoods.setSellerId(userId);
        }*/

        /* storeSellerGoods.setApprovedStatus(YesNoStatus.NO.getCode());*/

        /*      QueryWrapper<StoreSellerGoods> queryWrapper = QueryGenerator.initQueryWrapper(storeSellerGoods, req.getParameterMap());*/


        /*storeSellerGoods.setApprovedStatus(YesNoStatus.NO.getCode());*/
        /*  QueryWrapper<StoreSellerGoods> queryWrapper = QueryGenerator.initQueryWrapper(storeSellerGoods, req.getParameterMap());*/

        Page<StoreSellerGoods> page = new Page<StoreSellerGoods>(pageNo, pageSize);
        LambdaQueryWrapper<StoreSellerGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(storeSellerGoods.getSellerName())) {
            lambdaQueryWrapper.like(StoreSellerGoods::getSellerName, storeSellerGoods.getSellerName());
        }
        if (StringUtils.isNotEmpty(storeSellerGoods.getGoodsTitle())) {
            lambdaQueryWrapper.like(StoreSellerGoods::getGoodsTitle, storeSellerGoods.getGoodsTitle());
        }
        if (StringUtils.isNotEmpty(storeSellerGoods.getGoodsCode())) {
            lambdaQueryWrapper.eq(StoreSellerGoods::getGoodsCode, storeSellerGoods.getGoodsCode());
        }

        IPage<StoreSellerGoods> pageList = storeSellerGoodsService.page(PageUtil.getOrderItems(pageNo, pageSize, req), lambdaQueryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 商品审核
     *
     * @param storeSellerGoods
     * @return
     */
    @AutoLog(value = "商家商品库表-商品审核")
@Operation(summary = "商家商品库表-商品审核", description = "商家商品库表-商品审核")
    @PostMapping(value = "/approved")
    public Result<?> approved(@RequestBody StoreSellerGoods storeSellerGoods) {
        String userId = getUserIdByToken();
        String message = "";
        Integer approvedStatus = storeSellerGoods.getApprovedStatus();
        //审核成功上架状态
        if (storeSellerGoods.getApprovedStatus().equals(YesNoStatus.YES.getCode())) {
            storeSellerGoods.setPutawayStatus(YesNoStatus.YES.getCode());
            storeSellerGoods.setApprovedFailReason("");
        }
        storeSellerGoods.setApprovedUserId(userId);
        storeSellerGoods.setApprovedDate(new Date());
        storeSellerGoodsService.updateById(storeSellerGoods);
        if (approvedStatus.equals(YesNoStatus.YES.getCode())) {
            message = "审核成功!";
        } else {
            message = "驳回成功!";
        }
        return Result.ok(message);
    }


    /**
     * 商品上下架
     *
     * @param storeSellerGoods
     * @return
     */
    @AutoLog(value = "商家商品库表-商品上下架")
@Operation(summary = "商家商品库表-商品上下架", description = "商家商品库表-商品上下架")
    @PostMapping(value = "/putaway")
    public Result<?> putaway(@RequestBody StoreSellerGoods storeSellerGoods) {
        String userId = getUserIdByToken();
        String message = "";
        Integer putawayStatus = storeSellerGoods.getPutawayStatus();
        StoreSellerGoods storeSellerGoodsNew = storeSellerGoodsService.getById(storeSellerGoods.getId());
        if (storeSellerGoodsNew.getApprovedStatus().equals(YesNoStatus.NO.getCode())) {
            return Result.error("商品未审核，商品无法上下架!");
        }
        //审核成功上架状态
        storeSellerGoodsService.updateById(storeSellerGoods);
        if (putawayStatus.equals(YesNoStatus.YES.getCode())) {
            message = "商品上架成功!";
        } else {
            message = "商品下架成功!";
        }
        return Result.ok(message);
    }


    /**
     * 添加
     *
     * @param storeSellerGoods
     * @return
     */
    @AutoLog(value = "商家商品库表-添加")
@Operation(summary = "商家商品库表-添加", description = "商家商品库表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreSellerGoods storeSellerGoods) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        //商品新增默认下架状态
        storeSellerGoods.setPutawayStatus(YesNoStatus.NO.getCode());
        storeSellerGoods.setSellerId(userId);
        storeSellerGoods.setSellerName(userName);
        storeSellerGoods.setApprovedDate(null);
        storeSellerGoods.setApprovedUserId(null);
        storeSellerGoods.setCreateTime(new Date());
        storeSellerGoods.setCreateBy(userName);
        storeSellerGoods.setUpdateBy(null);
        storeSellerGoods.setUpdateTime(null);
        storeSellerGoodsService.save(storeSellerGoods);
        return Result.ok("添加成功！");
    }


    /**
     * 内推商品添加
     *
     * @param storeSellerGoods
     * @return
     */
    @AutoLog(value = "商家商品库表-内推商品添加")
@Operation(summary = "商家商品库表-内推商品添加", description = "商家商品库表-内推商品添加")
    @PostMapping(value = "/pushTheAdd")
    public Result<?> pushTheAdd(@RequestBody StoreSellerGoods storeSellerGoods) {
        String userId = getUserIdByToken();

        //商品新增默认下架状态
        storeSellerGoods.setPutawayStatus(YesNoStatus.NO.getCode());

        //获得当前时间
        Date date = new Date();
        //默认审核人员 为添加人员 默认审核通过通过
        storeSellerGoods.setApprovedDate(date);
        storeSellerGoods.setApprovedUserId(userId);
        storeSellerGoods.setApprovedStatus(YesNoStatus.YES.getCode());

        storeSellerGoods.setCreateTime(null);
        storeSellerGoods.setCreateBy(null);
        storeSellerGoods.setUpdateBy(null);
        storeSellerGoods.setUpdateTime(null);
        storeSellerGoodsService.save(storeSellerGoods);
        return Result.ok("添加成功！");
    }


    /**
     * 编辑
     *
     * @param storeSellerGoods
     * @return
     */
    @AutoLog(value = "商家商品库表-编辑")
@Operation(summary = "商家商品库表-编辑", description = "商家商品库表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreSellerGoods storeSellerGoods) {
        storeSellerGoodsService.updateById(storeSellerGoods);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家商品库表-通过id删除")
@Operation(summary = "商家商品库表-通过id删除", description = "商家商品库表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        StoreSellerGoods storeSellerGoods = storeSellerGoodsService.getById(id);
        if (storeSellerGoods.getApprovedStatus() == YesNoStatus.YES.getCode()) {
            return Result.error("已审核通过，请勿删除!");
        } else if (storeSellerGoods.getApprovedStatus() == YesNoStatus.Exception.getCode()) {
            return Result.error("已拒绝驳回,请勿删除!");
        }
        storeSellerGoodsService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家商品库表-通过id删除")
@Operation(summary = "商家商品库表-通过id删除", description = "商家商品库表-通过id删除")
    @DeleteMapping(value = "/delPushThe")
    public Result<?> delPushThe(@RequestParam(name = "id", required = true) String id) {
        storeSellerGoodsService.removeById(id);
        return Result.ok("删除成功!");
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家商品库表-批量删除")
@Operation(summary = "商家商品库表-批量删除", description = "商家商品库表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeSellerGoodsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家商品库表-通过id查询")
@Operation(summary = "商家商品库表-通过id查询", description = "商家商品库表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreSellerGoods storeSellerGoods = storeSellerGoodsService.getById(id);
        return Result.ok(storeSellerGoods);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeSellerGoods
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreSellerGoods storeSellerGoods) {
        return super.exportXls(request, storeSellerGoods, StoreSellerGoods.class, "商家商品库表");
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
        return super.importExcel(request, response, StoreSellerGoods.class);
    }

    /**
     * 根据Id查询商品及卖家商家顾问
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家商品库表-根据Id查询商品及卖家商家顾问")
@Operation(summary = "商家商品库表-根据Id查询商品及卖家商家顾问", description = "商家商品库表-根据Id查询商品及卖家商家顾问")
    @GetMapping(value = "/queryGoodsAndSellerCounseloById")
    public Result<?> queryGoodsAndSellerCounseloById(@RequestParam(name = "id", required = true) String id) {
        StoreSellerGoods storeSellerGoods = storeSellerGoodsService.queryGoodsAndSellerCounseloById(id);
        return Result.ok(storeSellerGoods);
    }

    /**
     * 根据id查询商品及商品细节和私人网红
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家商品库表-根据id查询商品及商品细节和私人网红")
@Operation(summary = "商家商品库表-根据id查询商品及商品细节和私人网红", description = "商家商品库表-根据id查询商品及商品细节和私人网红")
    @GetMapping(value = "/queryStoreSwllerGoodsAndDetailAndCelebrityPrivateById")
    public Result<?> queryStoreSwllerGoodsAndDetailAndCelebrityPrivateById(@RequestParam(name = "id", required = true) String id) {
        List<StoreSellerGoodsModel> storeSellerGoodsModelList = storeSellerGoodsService.queryStoreSwllerGoodsAndDetailAndCelebrityPrivateById(id);
        return Result.ok(storeSellerGoodsModelList);
    }


    /**
     * 商品管理分页列表查询
     *
     * @param storeSellerGoods
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家商品库表-商品管理分页列表查询")
@Operation(summary = "商家商品库表-商品管理分页列表查询", description = "商家商品库表-商品管理分页列表查询")
    @GetMapping(value = "/managementList")
    public Result<?> queryManagementPageList(StoreSellerGoods storeSellerGoods,
                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             HttpServletRequest req) {

        LambdaQueryWrapper<StoreSellerGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreSellerGoods::getPutawayStatus, YesNoStatus.YES.getCode()).eq(StoreSellerGoods::getApprovedStatus, YesNoStatus.YES.getCode());
        if (StringUtils.isNotEmpty(storeSellerGoods.getGoodsTitle())) {
            lambdaQueryWrapper.like(StoreSellerGoods::getGoodsTitle, storeSellerGoods.getGoodsTitle());
        }
        if (StringUtils.isNotEmpty(storeSellerGoods.getGoodsCode())) {
            lambdaQueryWrapper.eq(StoreSellerGoods::getGoodsCode, storeSellerGoods.getGoodsCode());
        }
        if (StringUtils.isNotEmpty(storeSellerGoods.getSellerName())) {
            lambdaQueryWrapper.like(StoreSellerGoods::getSellerName, storeSellerGoods.getSellerName());
        }

        IPage<StoreSellerGoods> pageList = storeSellerGoodsService.page(PageUtil.getOrderItems(pageNo, pageSize, req), lambdaQueryWrapper);
        return Result.ok(pageList);
    }


    /**
     * 商家带货统计管理列表查询
     *
     * @param sellerCommerceModel
     * @return
     * @Author: zhoushen
     */
    @AutoLog(value = "商家商品库表-商家带货统计管理列表查询")
@Operation(summary = "商家商品库表-商家带货统计管理列表查询", description = "商家商品库表-商家带货统计管理列表查询")
    @GetMapping(value = "/sellerCommerceManagementList")
    public Result<?> sellerCommerceManagementList(SellerCommerceModel sellerCommerceModel) {
        List<SellerCommerceModel> pageList = storeSellerGoodsService.sellerCommerceManagementList(sellerCommerceModel);
        return Result.ok(pageList);
    }


    /**
     * 根据商家id查询带货统计列表
     *
     * @param sellerCommerceModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     * @Author: zhoushen
     */
    @AutoLog(value = "商家商品库表-根据商家id查询带货统计列表")
@Operation(summary = "商家商品库表-根据商家id查询带货统计列表", description = "商家商品库表-根据商家id查询带货统计列表")
    @GetMapping(value = "/queryBySellerId")
    public Result<?> queryBySellerId(SellerCommerceModel sellerCommerceModel,
                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                     HttpServletRequest req) {
        Page<StoreSellDetailModel> page = new Page<>(pageNo, pageSize);
        IPage<StoreSellDetailModel> pageList = storeSellerGoodsService.queryBySellerId(page, sellerCommerceModel);
        return Result.ok(pageList);
    }

}
