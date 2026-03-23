package org.jeecg.modules.instagram.storemerchant.controller;

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
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedeemCode;
import org.jeecg.modules.instagram.storemerchant.entity.StoreGradeExchangeDetails;
import org.jeecg.modules.instagram.storemerchant.service.IStoreGradeExchangeDetailsService;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSellerGrade;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerGradeService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 商家兑换等级表
 * @Author: jeecg-boot
 * @Date: 2021-02-03
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "商家兑换等级表")
@RestController
@RequestMapping("/storeGradeExchangeDetails")
public class StoreGradeExchangeDetailsController extends JeecgController<StoreGradeExchangeDetails, IStoreGradeExchangeDetailsService> {
    @Autowired
    private IStoreGradeExchangeDetailsService storeGradeExchangeDetailsService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IStoreSellerGradeService storeSellerGradeService;

    /**
     * 分页列表查询
     *
     * @param storeGradeExchangeDetails
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家兑换等级表-分页列表查询")
@Operation(summary = "商家兑换等级表-分页列表查询", description = "商家兑换等级表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreGradeExchangeDetails storeGradeExchangeDetails,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StoreGradeExchangeDetails> queryWrapper = QueryGenerator.initQueryWrapper(storeGradeExchangeDetails, req.getParameterMap());
        Page<StoreGradeExchangeDetails> page = new Page<>(pageNo, pageSize);
        IPage<StoreGradeExchangeDetails> pageList = storeGradeExchangeDetailsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param storeGradeExchangeDetails
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "商家兑换等级表-分页列表查询")
@Operation(summary = "商家兑换等级表-分页列表查询", description = "商家兑换等级表-分页列表查询")
    @GetMapping(value = "/sellerList")
    public Result<?> sellerList(StoreGradeExchangeDetails storeGradeExchangeDetails,
                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                HttpServletRequest req) {
        String userId = getUserIdByToken();
        QueryWrapper<StoreGradeExchangeDetails> queryWrapper = QueryGenerator.initQueryWrapper(storeGradeExchangeDetails, req.getParameterMap());
        queryWrapper.eq("seller_id", userId);
        Page<StoreGradeExchangeDetails> page = new Page<>(pageNo, pageSize);
        IPage<StoreGradeExchangeDetails> pageList = storeGradeExchangeDetailsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param storeGradeExchangeDetails
     * @return
     */
    @AutoLog(value = "商家兑换等级表-添加")
@Operation(summary = "商家兑换等级表-添加", description = "商家兑换等级表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreGradeExchangeDetails storeGradeExchangeDetails) {
        String userId = getUserIdByToken();
        String code;
        //查询兑换码是否存在
        while (true) {
            //生成兑换码
            RedeemCode redeemCode = new RedeemCode();
            code = redeemCode.getRedeemCode();
            LambdaQueryWrapper<StoreGradeExchangeDetails> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(StoreGradeExchangeDetails::getCouponCode, code);
            int size = storeGradeExchangeDetailsService.list(lambdaQueryWrapper).size();
            if (size <= 0) {
                break;
            }
        }
        storeGradeExchangeDetails.setUserId(userId);
        storeGradeExchangeDetails.setCouponCode(code);
        storeGradeExchangeDetailsService.save(storeGradeExchangeDetails);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeGradeExchangeDetails
     * @return
     */
    @AutoLog(value = "商家兑换等级表-编辑")
@Operation(summary = "商家兑换等级表-编辑", description = "商家兑换等级表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreGradeExchangeDetails storeGradeExchangeDetails) {
        storeGradeExchangeDetailsService.updateById(storeGradeExchangeDetails);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商家兑换等级表-通过id删除")
@Operation(summary = "商家兑换等级表-通过id删除", description = "商家兑换等级表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        String userId = getUserIdByToken();
        // 查询当前兑换码信息
        StoreGradeExchangeDetails storeGradeExchangeDetails = storeGradeExchangeDetailsService.getById(id);
        if (!storeGradeExchangeDetails.getUserId().equals(userId)) {
            return Result.error("只能删除自己创建的!");
        }
        storeGradeExchangeDetailsService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商家兑换等级表-批量删除")
@Operation(summary = "商家兑换等级表-批量删除", description = "商家兑换等级表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeGradeExchangeDetailsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }


    /**
     * 功能描述:兑换等级
     *
     * @Author: nqr
     * @Date 2021-02-03 09:57:00
     */
@Operation(summary = "商家兑换等级", description = "商家兑换等级")
    @GetMapping(value = "/exchangeLevel")
    public Result<?> exchangeLevel(@RequestParam(name = "couponCode", required = true) String couponCode) {
        String userId = getUserIdByToken();
        //查询兑换码是否存在
        LambdaQueryWrapper<StoreGradeExchangeDetails> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreGradeExchangeDetails::getCouponCode, couponCode);
        lambdaQueryWrapper.eq(StoreGradeExchangeDetails::getCouponStatus, YesNoStatus.NO.getCode());
        List<StoreGradeExchangeDetails> list = storeGradeExchangeDetailsService.list(lambdaQueryWrapper);
        //判断兑换码是否存在
        if (list.size() > 0) {
            //存在
            //判断兑换码是否过期
            StoreGradeExchangeDetails storeGradeExchangeDetails = list.get(0);
            //未过期
            if (storeGradeExchangeDetails.getExpirationDate().after(new Date())) {
                SysUser sysUser = sysUserService.getById(userId);
                //修改兑换码明细
                storeGradeExchangeDetails.setSellerId(userId);
                storeGradeExchangeDetails.setSellerName(sysUser.getUsername());
                storeGradeExchangeDetails.setCouponStatus(YesNoStatus.YES.getCode());
                storeGradeExchangeDetails.setExchangeDate(new Date());
                //判断当前用户等级
                String gradeId = storeGradeExchangeDetails.getGradeId();
                StoreSellerGrade storeSellerGrade = storeSellerGradeService.getById(sysUser.getGradeId());
                StoreSellerGrade storeSellerGradeNew = storeSellerGradeService.getById(gradeId);
                //如果当前用户不是试用会员，并且未到期的话，不可兑换
                if (storeSellerGrade.getGradeLever() > 0 && sysUser.getExpirationDate().after(new Date())) {
                    //判断等级是否大于当前等级，大于可以兑换
                    if (storeSellerGrade.getGradeLever() >= storeSellerGradeNew.getGradeLever() && storeSellerGrade.getGradeCycle() >= storeSellerGradeNew.getGradeCycle()) {
                        return Result.error("当前等级未过期，兑换失败！");
                    }
                }
                //修改当前用户等级
                SysUser sysUserNew = new SysUser();
                sysUserNew.setId(sysUser.getId());
                sysUserNew.setGradeId(gradeId);
                sysUserNew.setGradeName(storeSellerGradeNew.getGradeName());
                sysUserNew.setEmailAccountCount(storeSellerGradeNew.getEmailAccountCount());
                sysUserNew.setEmailPushCount(storeSellerGradeNew.getEmailPushCount());
                sysUserNew.setEmailsDay(storeSellerGradeNew.getEmailPushCount());
                //获取过期时间
                Date expirationDate = DateUtils.getFetureMOnthDate(storeGradeExchangeDetails.getGradeCycle(), new Date());
                sysUserNew.setExpirationDate(expirationDate);
                storeGradeExchangeDetailsService.updateGrade(storeGradeExchangeDetails, sysUserNew);
                return Result.ok("兑换成功！");
            } else {
                //过期
                return Result.error("兑换码已过期");
            }
        } else {
            //不存在
            return Result.error("兑换码不存在或已兑换");
        }
    }
}
