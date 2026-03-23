package org.jeecg.modules.instagram.storeseller.storeuser.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.service.IMessagePushDetailService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.SellerCollection;
import org.jeecg.modules.instagram.storeseller.storeuser.model.SellerCollectionAndCelebrityModel;
import org.jeecg.modules.instagram.storeseller.storeuser.model.SellerCollectionModel;
import org.jeecg.modules.instagram.storeseller.storeuser.service.ISellerCollectionService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: 商家网红收藏表
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家网红收藏表")
@RestController
@RequestMapping("/sellercollection/sellerCollection")
public class SellerCollectionController extends JeecgController<SellerCollection, ISellerCollectionService> {
    @Autowired
    private ISellerCollectionService sellerCollectionService;
    @Autowired
    private IMessagePushDetailService pushDetailService;
    @Autowired
    private IStoreCelebrityService storeCelebrityService;

    /**
     * 分页列表查询
     *
     * @param sellerCollection
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家网红收藏表-分页列表查询")
@Operation(summary = "商家网红收藏表-分页列表查询", description = "商家网红收藏表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SellerCollection sellerCollection,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SellerCollection> queryWrapper = QueryGenerator.initQueryWrapper(sellerCollection, req.getParameterMap());
        Page<SellerCollection> page = new Page<SellerCollection>(pageNo, pageSize);
        IPage<SellerCollection> pageList = sellerCollectionService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param sellerCollectionAndCelebrityModel
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "网红收藏-分页列表查询")
@Operation(summary = "网红收藏-分页列表查询", description = "网红收藏-分页列表查询")
    @GetMapping(value = "/parentList")
    public Result<?> parentList(SellerCollectionAndCelebrityModel sellerCollectionAndCelebrityModel,
                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                HttpServletRequest req) {
        String userId = getUserIdByToken();
        sellerCollectionAndCelebrityModel.setSellerId(userId);
        String sellerId = getUserIdByToken();
        /*     sellerCollectionAndCelebrityModel.setSellerId(sellerId);*/

        IPage<SellerCollectionAndCelebrityModel> pageList = sellerCollectionService.parentList(PageUtil.getOrderItems(pageNo, pageSize, req), sellerCollectionAndCelebrityModel);

        List<SellerCollectionAndCelebrityModel> records = pageList.getRecords();
        List<SellerCollectionAndCelebrityModel> resultList = new ArrayList<>();
        for (SellerCollectionAndCelebrityModel priceModel : records) {
            /*  Integer sendEmailCount = getSendEmailCount(sellerId, priceModel.getEmail());*/
            String kolPriceMin = priceModel.getKolPriceMin();
            String kolPriceMax = priceModel.getKolPriceMax();
            if (oConvertUtils.isEmpty(kolPriceMin) && oConvertUtils.isNotEmpty(kolPriceMax)) {
                priceModel.setPriceRange("$" + 0 + " ~ " + Double.valueOf(kolPriceMax).intValue());
            }
            if (oConvertUtils.isNotEmpty(kolPriceMin) && oConvertUtils.isEmpty(kolPriceMax)) {
                priceModel.setPriceRange("$" + Double.valueOf(kolPriceMin).intValue() + " ~ ∞");
            }
            if (oConvertUtils.isNotEmpty(kolPriceMin) && oConvertUtils.isNotEmpty(kolPriceMax)) {
                priceModel.setPriceRange("$" + Double.valueOf(kolPriceMin).intValue() + " ~ " + Double.valueOf(kolPriceMax).intValue());
            }
            /*       priceModel.setEmail("");*/
            resultList.add(priceModel);
        }
        pageList.setRecords(resultList);

        System.out.println(pageList);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param sellerCollection
     * @return
     */
    @AutoLog(value = "商家网红收藏表-添加")
@Operation(summary = "商家网红收藏表-添加", description = "商家网红收藏表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SellerCollection sellerCollection) {

        sellerCollectionService.save(sellerCollection);
        return Result.ok("添加成功！");
    }

    /**
     * 添加
     *
     * @param sellerCollection
     * @return
     */
    @AutoLog(value = "商家网红收藏表-收藏")
@Operation(summary = "商家网红收藏表-收藏", description = "商家网红收藏表-收藏")
    @PostMapping(value = "/changeCollection")
    public Result<?> changeCollection(@RequestBody SellerCollectionModel sellerCollection) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();

        if (sellerCollection.getStatus() == 1) {
            LambdaQueryWrapper<SellerCollection> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SellerCollection::getCelebrityId, sellerCollection.getId());
            lambdaQueryWrapper.eq(SellerCollection::getSellerId, userId);
            List<SellerCollection> sellerCollections = sellerCollectionService.list(lambdaQueryWrapper);
            if (null != sellerCollections && sellerCollections.size() > 0) {
                return Result.error("已经收藏，请勿重复收藏");
            } else {
                /*sellerCollectionService.getById()*/
                SellerCollection newSellerCollection = new SellerCollection();
                newSellerCollection.setSellerId(userId);
                newSellerCollection.setSellerName(userName);
                newSellerCollection.setCelebrityId(sellerCollection.getId());
                newSellerCollection.setCelebrityName(sellerCollection.getAccount());
                sellerCollectionService.save(newSellerCollection);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("resultMessage", "收藏成功");
                jsonObject.put("collectionId", newSellerCollection.getId());
                return Result.ok(jsonObject);
            }
        } else if (sellerCollection.getStatus() == 0) {
            sellerCollectionService.removeById(sellerCollection.getCollectionId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("resultMessage", "取消收藏");
            jsonObject.put("collectionId", "");
            return Result.ok(jsonObject);
        }
        return Result.ok();
    }

    /**
     * 批量收藏
     *
     * @return
     */
    @AutoLog(value = "商家网红收藏表-批量收藏")
@Operation(summary = "商家网红收藏表-批量收藏", description = "商家网红收藏表-批量收藏")
    @GetMapping(value = "/changeAllCollection")
    public Result<?> changeAllCollection(String id, Integer status) {
        String userId = getUserIdByToken();
        String userName = getUserNameByToken();
        List<SellerCollection> listNew = new ArrayList<>();
        List<String> idList = Arrays.asList(id.split(","));
        //添加
        if (status == 1) {
            //查询选择的网红信息
            List<StoreCelebrity> storeCelebrities = (List<StoreCelebrity>) storeCelebrityService.listByIds(idList);
            //查询当前用户是否已关注该网红
            //判断该网红是否已收藏
            LambdaQueryWrapper<SellerCollection> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SellerCollection::getSellerId, userId);
            lambdaQueryWrapper.in(SellerCollection::getCelebrityId, idList);
            List<SellerCollection> list = sellerCollectionService.list(lambdaQueryWrapper);
            List<String> sellerCollectionList = list.stream().map(SellerCollection::getCelebrityId).collect(Collectors.toList());
            List<StoreCelebrity> celebrityList;
            if (list.size() != 0) {
                celebrityList = storeCelebrities.stream().filter(x -> !sellerCollectionList.contains(x.getId())).collect(Collectors.toList());
            } else {
                celebrityList = storeCelebrities;
            }
            System.out.println(celebrityList);
            for (StoreCelebrity storeCelebrity : celebrityList) {
                //添加
                String nickName = storeCelebrity.getNickName();
                SellerCollection newSellerCollection = new SellerCollection();
                newSellerCollection.setSellerId(userId);
                newSellerCollection.setSellerName(userName);
                newSellerCollection.setCelebrityId(storeCelebrity.getId());
                newSellerCollection.setCelebrityName(nickName);
                listNew.add(newSellerCollection);
            }
            sellerCollectionService.saveBatch(listNew);
            /*for (StoreCelebrity storeCelebrity : storeCelebrities) {
                //判断该网红是否已收藏
                LambdaQueryWrapper<SellerCollection> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(SellerCollection::getSellerId, userId);
                lambdaQueryWrapper.eq(SellerCollection::getCelebrityId, storeCelebrity.getId());
                List<SellerCollection> list = sellerCollectionService.list(lambdaQueryWrapper);
                if (list.size() == 0) {
                    //添加
                    String nickName = storeCelebrity.getNickName();
                    SellerCollection newSellerCollection = new SellerCollection();
                    newSellerCollection.setSellerId(userId);
                    newSellerCollection.setSellerName(userName);
                    newSellerCollection.setCelebrityId(storeCelebrity.getId());
                    newSellerCollection.setCelebrityName(nickName);
                    listNew.add(newSellerCollection);
                }
            }
            sellerCollectionService.saveBatch(listNew);*/
        } else {
            LambdaUpdateWrapper<SellerCollection> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(SellerCollection::getSellerId, userId);
            lambdaUpdateWrapper.in(SellerCollection::getCelebrityId, idList);
            //删除
            sellerCollectionService.remove(lambdaUpdateWrapper);
        }

        return Result.ok("操作成功！");

    }


    /**
     * 编辑
     *
     * @param sellerCollection
     * @return
     */
    @AutoLog(value = "商家网红收藏表-编辑")
@Operation(summary = "商家网红收藏表-编辑", description = "商家网红收藏表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody SellerCollection sellerCollection) {
        sellerCollectionService.updateById(sellerCollection);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家网红收藏表-通过id删除")
@Operation(summary = "商家网红收藏表-通过id删除", description = "商家网红收藏表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sellerCollectionService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家网红收藏表-批量删除")
@Operation(summary = "商家网红收藏表-批量删除", description = "商家网红收藏表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sellerCollectionService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家网红收藏表-通过id查询")
@Operation(summary = "商家网红收藏表-通过id查询", description = "商家网红收藏表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SellerCollection sellerCollection = sellerCollectionService.getById(id);
        return Result.ok(sellerCollection);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param sellerCollection
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SellerCollection sellerCollection) {
        return super.exportXls(request, sellerCollection, SellerCollection.class, "商家网红收藏表");
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
        return super.importExcel(request, response, SellerCollection.class);
    }

}
