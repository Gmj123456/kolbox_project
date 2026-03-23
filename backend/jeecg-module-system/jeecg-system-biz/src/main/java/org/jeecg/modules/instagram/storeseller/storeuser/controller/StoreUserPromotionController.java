package org.jeecg.modules.instagram.storeseller.storeuser.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.YesNoStatus;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserPromotion;
import org.jeecg.modules.instagram.storeseller.storeuser.model.StoreUserPromotionModel;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserPromotionService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserPromotionCelebrity;
import org.jeecg.modules.instagram.storeseller.storeuser.service.IStoreUserPromotionCelebrityService;
import org.jeecg.modules.instagram.utils.ImageUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 商家产品促销
 * @Author: jeecg-boot
 * @Date: 2021-05-21
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家产品促销")
@RestController
@RequestMapping("/storeuserpromotion")
public class StoreUserPromotionController extends JeecgController<StoreUserPromotion, IStoreUserPromotionService> {
    @Autowired
    private IStoreUserPromotionService storeUserPromotionService;
    @Autowired
    private IStoreUserPromotionCelebrityService storeUserPromotionCelebrityService;
    @Autowired
    private IStoreSellerPromotionService sellerPromotionService;

    /**
     * 分页列表查询
     *
     * @param storeUserPromotion
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家产品促销-分页列表查询")
@Operation(summary = "商家产品促销-分页列表查询", description = "商家产品促销-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreUserPromotion storeUserPromotion,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreUserPromotion> queryWrapper = QueryGenerator.initQueryWrapper(storeUserPromotion, req.getParameterMap());
        Page<StoreUserPromotion> page = new Page<StoreUserPromotion>(pageNo, pageSize);
        IPage<StoreUserPromotion> pageList = storeUserPromotionService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeUserPromotionModel
     * @return
     */
    @AutoLog(value = "商家产品促销-添加")
@Operation(summary = "商家产品促销-添加", description = "商家产品促销-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreUserPromotionModel storeUserPromotionModel) {
        String promotionId = oConvertUtils.getUUID();
        StoreUserPromotion storeUserPromotion = new StoreUserPromotion();
        storeUserPromotion.setId(promotionId);
        storeUserPromotion.setUserName(storeUserPromotionModel.getUserName());
        storeUserPromotion.setPromStatus(YesNoStatus.NO.getCode());
        storeUserPromotion.setProductBrand(storeUserPromotionModel.getProductBrand());
        storeUserPromotion.setPromTime(new Date());
        storeUserPromotion.setPhone(storeUserPromotionModel.getPhone());
        storeUserPromotion.setQrCode(storeUserPromotionModel.getQrCode());
        List<StoreUserPromotionCelebrity> celebrityList = storeUserPromotionModel.getCelebrityList();
        List<StoreUserPromotionCelebrity> celebrityListNew = new ArrayList<>();
        for (StoreUserPromotionCelebrity storeUserPromotionCelebrity : celebrityList) {
            if (oConvertUtils.isEmpty(storeUserPromotionCelebrity.getAvatarUrl())) {
                return Result.error("头像不能为空！");
            }
            if (oConvertUtils.isEmpty(storeUserPromotionCelebrity.getAccount())) {
                return Result.error("昵称不能为空！");
            }
            if (oConvertUtils.isEmpty(storeUserPromotionCelebrity.getLikeTags())) {
                return Result.error("擅长类型不能为空！");
            }
            if (oConvertUtils.isEmpty(storeUserPromotionCelebrity.getVideoTags())) {
                return Result.error("视频类型不能为空！");
            }
            if (oConvertUtils.isEmpty(storeUserPromotionCelebrity.getFollowersNum())) {
                return Result.error("粉丝数不能为空！");
            }
            if (oConvertUtils.isEmpty(storeUserPromotionCelebrity.getPromPrice())) {
                return Result.error("金额不能为空！");
            }
            storeUserPromotionCelebrity.setPromId(promotionId);
            celebrityListNew.add(storeUserPromotionCelebrity);
        }
        storeUserPromotionService.saveCelebrityList(storeUserPromotion, celebrityListNew);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeUserPromotionModel
     * @return-
     */
    @AutoLog(value = "商家产品促销-编辑")
@Operation(summary = "商家产品促销-编辑", description = "商家产品促销-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreUserPromotionModel storeUserPromotionModel) {
        LambdaQueryWrapper<StoreUserPromotionCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreUserPromotionCelebrity::getPromId, storeUserPromotionModel.getId());
        List<StoreUserPromotionCelebrity> celebrityListOld = storeUserPromotionCelebrityService.list(lambdaQueryWrapper);
        if (oConvertUtils.isEmpty(storeUserPromotionModel.getSellerPromId())) {
            return Result.error("请选择推广需求！");
        }
        if (!celebrityListOld.isEmpty()) {
            List<String> celebrityIdList = celebrityListOld.stream().map(StoreUserPromotionCelebrity::getId).collect(Collectors.toList());
            storeUserPromotionCelebrityService.removeByIds(celebrityIdList);
        }
        List<StoreUserPromotionCelebrity> celebrityList = storeUserPromotionModel.getCelebrityList();
        List<StoreUserPromotionCelebrity> celebrityListNew = new ArrayList<>();
        for (StoreUserPromotionCelebrity storeUserPromotionCelebrity : celebrityList) {
            storeUserPromotionCelebrity.setPromId(storeUserPromotionModel.getId());
            celebrityListNew.add(storeUserPromotionCelebrity);
        }
        StoreUserPromotion storeUserPromotion = new StoreUserPromotion();
        BeanUtils.copyProperties(storeUserPromotionModel, storeUserPromotion);
    /*    StoreSellerPromotion storeSellerPromotion = sellerPromotionService.getById(storeUserPromotionModel.getSellerPromId());
        if (oConvertUtils.isNotEmpty(storeSellerPromotion)) {
            storeUserPromotion.setSellerPromCode(storeSellerPromotion.getExtensionCode());
            storeUserPromotion.setSellerPromTitle(storeSellerPromotion.getPromTitle());
        }*/
        storeUserPromotionService.updatePromotion(celebrityListNew, storeUserPromotion);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家产品促销-通过id删除")
@Operation(summary = "商家产品促销-通过id删除", description = "商家产品促销-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeUserPromotionService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家产品促销-批量删除")
@Operation(summary = "商家产品促销-批量删除", description = "商家产品促销-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeUserPromotionService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家产品促销-通过id查询")
@Operation(summary = "商家产品促销-通过id查询", description = "商家产品促销-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreUserPromotion storeUserPromotion = storeUserPromotionService.getById(id);
        return Result.ok(storeUserPromotion);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeUserPromotion
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreUserPromotion storeUserPromotion) {
        return super.exportXls(request, storeUserPromotion, StoreUserPromotion.class, "商家产品促销");
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
        return super.importExcel(request, response, StoreUserPromotion.class);
    }

    /**
     * 功能描述:根据方案ID查询方案明细
     *
     * @param id
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-05-29 17:18:21
     */
    @GetMapping(value = "/queryList")
    public Result<?> queryList(@RequestParam(name = "id", required = true) String id) {
        StoreUserPromotion storeUserPromotion = storeUserPromotionService.getById(id);
        LambdaQueryWrapper<StoreUserPromotionCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreUserPromotionCelebrity::getPromId, id);
        List<StoreUserPromotionCelebrity> celebrityList = storeUserPromotionCelebrityService.list(lambdaQueryWrapper);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("storeUserPromotion", storeUserPromotion);
        jsonObject.put("celebrityList", celebrityList);
        return Result.ok(jsonObject);
    }

    /**
     * 功能描述:根据图片url获取base64地址
     *
     * @param path
     * @return org.jeecg.common.api.vo.Result<?>
     * @Date 2021-05-29 17:18:21
     */
    @GetMapping(value = "/getImgBase64")
    public Result<?> getImgBase64(@RequestParam(name = "path", required = true) String path) {
        String base64ByImgUrl = ImageUtils.getBase64ByImgUrl(path);
        return Result.ok("data:image/jpeg;base64," + base64ByImgUrl);
    }

}
