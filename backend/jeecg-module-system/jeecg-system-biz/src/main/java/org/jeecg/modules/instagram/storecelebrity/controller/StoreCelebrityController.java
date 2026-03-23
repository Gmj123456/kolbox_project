package org.jeecg.modules.instagram.storecelebrity.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.*;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.*;
import org.jeecg.modules.instagram.storecelebrity.entity.CelebrityConsignee;
import org.jeecg.modules.instagram.storecelebrity.service.ICelebrityConsigneeService;
import org.jeecg.modules.instagram.storebasics.service.ICountryService;
import org.jeecg.modules.instagram.storecelebrity.model.PushCommonModel;
import org.jeecg.modules.instagram.storecelebrity.model.PushUser;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.jeecg.modules.instagram.storecelebrity.model.MessagePushDetailModel;
import org.jeecg.modules.instagram.storecelebrity.service.IMessagePushDetailService;
import org.jeecg.modules.instagram.storecelebrity.service.IMessageTemplateService;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.SellerCollection;
import org.jeecg.modules.instagram.storeseller.storeuser.service.ISellerCollectionService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.model.EmailPostStatusExcelModel;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityExcelImprotModel;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityModel;
import org.jeecg.modules.instagram.storecelebrity.model.StoreCelebrityPriceModel;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityPrice;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityPriceService;
import org.jeecg.modules.instagram.storemerchant.entity.StoreEmailPackagePurchase;
import org.jeecg.modules.instagram.storemerchant.service.IStoreEmailPackagePurchaseService;
import org.jeecg.modules.instagram.storetags.entity.TagContrast;
import org.jeecg.modules.instagram.storetags.service.ITagContrastService;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictItemService;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @Description: 网红档案
 * @Author: jeecg-boot
 * @Date: 2020-04-20
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "网红档案")
@RestController
@RequestMapping("/instagram/storeCelebrity")
public class StoreCelebrityController extends JeecgController<StoreCelebrity, IStoreCelebrityService> {
    @Autowired
    private IStoreCelebrityService storeCelebrityService;

    @Autowired
    private IMessageTemplateService messageTemplateService;

    @Autowired
    private IMessagePushDetailService pushDetailService;

    @Autowired
    private IStoreEmailPackagePurchaseService emailPackagePurchaseService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IStoreCelebrityPriceService storeCelebrityPriceService;

    @Autowired
    private ICelebrityConsigneeService celebrityConsigneeService;

    @Autowired
    private IMessagePushDetailService messagePushDetailService;

    @Autowired
    private ISysDictItemService sysDictItemService;

    @Autowired
    private ISysDictService sysDictService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ICountryService countryService;

    @Autowired
    private ITagContrastService tagContrastService;

    @Autowired
    private ISellerCollectionService sellerCollectionService;

    /**
     * 分页列表查询
     *
     * @param storeCelebrityModel
     * @param pageNo
     * @return
     */
    @AutoLog(value = "网红档案-分页列表查询")
@Operation(summary = "网红档案-分页列表查询", description = "网红档案-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityModel storeCelebrityModel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        Result<JSONObject> result = new Result<>();
        JSONObject jsonObject = new JSONObject();
        String sellerId = getUserIdByToken();
        // 判断当前会员等级
        SysUser sysUser = sysUserService.getById(sellerId);
        storeCelebrityModel.setSellerId(sellerId);
        // 判断用户状态
        // int userStatus = 2;
        int status = checkUserStatus(sysUser);
        // 1 新注册 2 已购买套餐  3购买流量包，无套餐  4套餐到期，邮件包未到期，同3   5套餐到期，邮件包到期，同1
        int userStatus;
        switch (status) {
            case 1:
            case 5:
                // 只展示第一页
                if (pageNo > 1) {
                    pageNo = 1;
                }
                userStatus = 1;
                break;
            case 3:
            case 4:
                if (pageNo > 50) {
                    pageNo = 50;
                }
                userStatus = 3;
                break;
            default:
                userStatus = 2;
                break;
        }
        int pageSize = 20;
        Page<StoreCelebrity> page = new Page<>(pageNo, pageSize);
        page.setSearchCount(false);
        IPage<StoreCelebrityPriceModel> pageList = storeCelebrityService.getStoreList(page, storeCelebrityModel);
        Integer count = storeCelebrityService.getStoreListCount(storeCelebrityModel);
        pageList.setTotal(count);
        List<StoreCelebrityPriceModel> records = pageList.getRecords();
        List<String> emails = records.stream().map(StoreCelebrity::getEmail).collect(Collectors.toList());
        Map<String, Integer> emailMap = new HashMap<>();
        if (!emails.isEmpty()) {
            emailMap = messagePushDetailService.getByEmails(emails, sellerId);
        }
        for (StoreCelebrityPriceModel priceModel : records) {
            String description = priceModel.getDescription();
            if (oConvertUtils.isNotEmpty(description)) {
                priceModel.setDescription(description.replaceAll("\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}", ""));
            }
            String kolPriceMin = priceModel.getKolPriceMin();
            String kolPriceMax = priceModel.getKolPriceMax();
            if (oConvertUtils.isEmpty(kolPriceMin) && oConvertUtils.isNotEmpty(kolPriceMax)) {
                int num = Double.valueOf(kolPriceMax).intValue();
                priceModel.setPriceRange("$" + 0 + " ~ " + convertFans(num));
            }
            if (oConvertUtils.isNotEmpty(kolPriceMin) && oConvertUtils.isEmpty(kolPriceMax)) {
                int num = Double.valueOf(kolPriceMin).intValue();
                priceModel.setPriceRange("$" + convertFans(num) + " ~ ∞");
            }
            if (oConvertUtils.isNotEmpty(kolPriceMin) && oConvertUtils.isNotEmpty(kolPriceMax)) {
                int numMin = Double.valueOf(kolPriceMin).intValue();
                int numMax = Double.valueOf(kolPriceMax).intValue();
                priceModel.setPriceRange("$" + convertFans(numMin) + " ~ " + convertFans(numMax));
            }
            String email = priceModel.getEmail();
            if (emailMap != null && emailMap.containsKey(email)) {
                priceModel.setSendEmailCount(emailMap.getOrDefault(email, 0));
            }
            priceModel.setEmail("");
            priceModel.setUserStatus(userStatus);
        }
        jsonObject.put("current", pageList.getCurrent());
        if (userStatus == 3) {
            jsonObject.put("pages", 50);
            jsonObject.put("total", 1000);
        } else if (userStatus == 1) {
            jsonObject.put("pages", 1);
            jsonObject.put("total", 20);
        } else {
            jsonObject.put("pages", pageList.getPages());
            jsonObject.put("total", pageList.getTotal());
        }
        jsonObject.put("records", pageList.getRecords());
        jsonObject.put("size", pageList.getSize());
        jsonObject.put("userStatus", userStatus);
        result.setCode(200);
        result.setMessage("操作成功！");
        result.setSuccess(true);
        result.setResult(jsonObject);
        return result;
    }

    /**
     * 分页列表查询
     *
     * @param storeCelebrityModel
     * @param pageNo
     * @return
     */
    @AutoLog(value = "网红档案-分页列表查询")
@Operation(summary = "网红档案-分页列表查询", description = "网红档案-分页列表查询")
    @GetMapping(value = "/pageList")
    public Result<?> pageList(StoreCelebrityModel storeCelebrityModel,
                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        storeCelebrityModel.setSellerId(getUserIdByToken());
        Page<StoreCelebrity> page = new Page<>(pageNo, pageSize);
        IPage<StoreCelebrityPriceModel> pageList = storeCelebrityService.getStoreList(page, storeCelebrityModel);
        List<StoreCelebrityPriceModel> list = new ArrayList<>();
        for (StoreCelebrityPriceModel priceModel : pageList.getRecords()) {
            String description = priceModel.getDescription();
            if (oConvertUtils.isNotEmpty(description)) {
                priceModel.setDescription(description.replaceAll("\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}", ""));
            }
            list.add(priceModel);
        }
        pageList.setRecords(list);
        return Result.ok(pageList);
    }


    /**
     * 网红展示页
     *
     * @return
     */
    @AutoLog(value = "网红展示页")
@Operation(summary = "网红展示页", description = "网红展示页")
    @GetMapping(value = "/showList")
    public Result<?> queryPageList() {
        List<StoreCelebrity> list = storeCelebrityService.showList();
        return Result.ok(list);
    }

    /**
     * 分页列表查询
     *
     * @param storeCelebrityModel
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "网红档案-分页列表查询")
@Operation(summary = "网红档案-分页列表查询", description = "网红档案-分页列表查询")
    @GetMapping(value = "/parentList")
    public Result<?> parentList(StoreCelebrityModel storeCelebrityModel,
                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                HttpServletRequest req) {
        IPage<StoreCelebrity> pageList = storeCelebrityService.parentList(PageUtil.getOrderItems(pageNo, pageSize, req), storeCelebrityModel);
        List<StoreCelebrity> list = new ArrayList<>();
        for (StoreCelebrity storeCelebrity : pageList.getRecords()) {
            String description = storeCelebrity.getDescription();
            if (oConvertUtils.isNotEmpty(description)) {
                storeCelebrity.setDescription(description.replaceAll("\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}", ""));
            }
            list.add(storeCelebrity);
        }
        return Result.ok(list);
    }


    /**
     * 网红验证列表查询
     *
     * @param storeCelebrityModel
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "网红档案-网红验证列表查询")
@Operation(summary = "网红档案-网红验证列表查询", description = "网红档案-网红验证列表查询")
    @GetMapping(value = "/celebrityValidation")
    public Result<?> celebrityValidation(StoreCelebrityModel storeCelebrityModel,
                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest req) {
        IPage<StoreCelebrity> pageList = storeCelebrityService.celebrityValidation(PageUtil.getOrderItems(pageNo, pageSize, req), storeCelebrityModel);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param storeCelebrityModel
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "网红档案-分页列表查询")
@Operation(summary = "网红档案-分页列表查询", description = "网红档案-分页列表查询")
    @GetMapping(value = "/seedFollowingList")
    public Result<?> seedFollowingList(StoreCelebrityModel storeCelebrityModel,
                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                       HttpServletRequest req) {
        Page<StoreCelebrity> page = new Page<StoreCelebrity>(pageNo, pageSize);
        IPage<StoreCelebrity> pageList = storeCelebrityService.seedFollowingList(page, storeCelebrityModel);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询关注列表
     *
     * @param storeCelebrityModel
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "分页列表查询关注列表")
    @GetMapping(value = "/followingList")
    public Result<?> followingList(StoreCelebrityModel storeCelebrityModel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        IPage<StoreCelebrity> pageList = storeCelebrityService.followingList(PageUtil.getOrderItems(pageNo, pageSize, req), storeCelebrityModel);
        return Result.ok(pageList);
    }


    /**
     * 添加
     *
     * @param storeCelebrity
     * @return
     */
    @AutoLog(value = "网红档案-添加")
@Operation(summary = "网红档案-添加", description = "网红档案-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebrity storeCelebrity) {
        String email = storeCelebrity.getEmail();
        String phone = storeCelebrity.getPhone();
        String account = storeCelebrity.getAccount();
        LambdaQueryWrapper<StoreCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(email)) {
            lambdaQueryWrapper.eq(StoreCelebrity::getEmail, email);
        }
        if (oConvertUtils.isNotEmpty(phone)) {
            lambdaQueryWrapper.eq(StoreCelebrity::getPhone, phone);
        }
        if (oConvertUtils.isNotEmpty(account)) {
            lambdaQueryWrapper.eq(StoreCelebrity::getAccount, account);
        }
        List<StoreCelebrity> list = storeCelebrityService.list(lambdaQueryWrapper);
        if (list.isEmpty()) {
//            storeCelebrity.setRemark("1");
            storeCelebrityService.save(storeCelebrity);
        }
        return Result.ok("添加成功！");
    }

    /**
     * 添加
     *
     * @param storeCelebritys
     * @return
     */
    @AutoLog(value = "网红档案-添加")
@Operation(summary = "网红档案-添加", description = "网红档案-添加")
    @PostMapping(value = "/addArray")
    public Result<?> addArray(@RequestBody String storeCelebritys) {
        List<StoreCelebrity> storeCelebrityList = JSON.parseArray(JSON.parseObject(storeCelebritys).getString("storeCelebrityList"), StoreCelebrity.class);
        for (StoreCelebrity storeCelebrity : storeCelebrityList) {
            String email = storeCelebrity.getEmail();
            String phone = storeCelebrity.getPhone();
            String account = storeCelebrity.getAccount();
            LambdaQueryWrapper<StoreCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (oConvertUtils.isNotEmpty(email)) {
                lambdaQueryWrapper.eq(StoreCelebrity::getEmail, email);
            }
            if (oConvertUtils.isNotEmpty(phone)) {
                lambdaQueryWrapper.eq(StoreCelebrity::getPhone, phone);
            }
            if (oConvertUtils.isNotEmpty(account)) {
                lambdaQueryWrapper.eq(StoreCelebrity::getAccount, account);
            }
            List<StoreCelebrity> list = storeCelebrityService.list(lambdaQueryWrapper);
            if (!list.isEmpty()) {
                String id = list.get(0).getId();
                storeCelebrity.setId(id);
            }
            storeCelebrity.setRemark("1");
            storeCelebrityService.saveOrUpdate(storeCelebrity);
        }
        return Result.ok("添加成功！");
    }

    /**
     * 插件添加
     *
     * @param storeCelebrityModel
     * @return
     */
    @AutoLog(value = "网红档案-插件添加")
@Operation(summary = "网红档案-插件添加", description = "网红档案-插件添加")
    @PostMapping(value = "/addOn")
    public Result<?> addOn(@RequestBody StoreCelebrityModel storeCelebrityModel) {
        String email = storeCelebrityModel.getEmail();
        String account = storeCelebrityModel.getAccount();
        List<StoreCelebrity> list = new ArrayList<>();
        if (oConvertUtils.isNotEmpty(email) && oConvertUtils.isNotEmpty(account)) {
            LambdaQueryWrapper<StoreCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(StoreCelebrity::getAccount, account);
            lambdaQueryWrapper.eq(StoreCelebrity::getEmail, email);
            list = storeCelebrityService.list(lambdaQueryWrapper);
        }
        if (list.isEmpty()) {
            storeCelebrityModel.setRemark("1");
            storeCelebrityService.save(storeCelebrityModel);
            // 添加到我的收藏
            SellerCollection newSellerCollection = new SellerCollection();
            newSellerCollection.setSellerId(storeCelebrityModel.getUserId());
            newSellerCollection.setSellerName(storeCelebrityModel.getUsername());
            newSellerCollection.setCelebrityId(storeCelebrityModel.getId());
            newSellerCollection.setCelebrityName(account);
            sellerCollectionService.save(newSellerCollection);
        } else {
            return Result.ok("添加失败，该网红已存在！");
        }
        return Result.ok("添加成功！");
    }

    /**
     * 批量添加
     *
     * @param jsonObject
     * @return
     */
    @AutoLog(value = "网红档案-添加")
@Operation(summary = "网红档案-添加", description = "网红档案-添加")
    @PostMapping(value = "/addList")
    public Result<?> addList(@RequestBody JSONObject jsonObject) {
        List<Map<String, String>> storeCelebrities = (List<Map<String, String>>) jsonObject.get("addList");
        for (int i = 0; i < storeCelebrities.size(); i++) {
            Map<String, String> map = storeCelebrities.get(i);
            StoreCelebrity storeCelebrity = JSON.parseObject(JSON.toJSONString(map), StoreCelebrity.class);
            String email = storeCelebrity.getEmail();
            String account = storeCelebrity.getAccount();
            LambdaQueryWrapper<StoreCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(StoreCelebrity::getEmail, email);
            lambdaQueryWrapper.eq(StoreCelebrity::getAccount, account);
            List<StoreCelebrity> list = storeCelebrityService.list(lambdaQueryWrapper);
            if (list.isEmpty()) {
                storeCelebrity.setRemark("1");
                storeCelebrityService.save(storeCelebrity);
            }
        }
        return Result.ok("添加成功！");
    }


    /**
     * 编辑
     *
     * @param storeCelebrity
     * @return
     */
    @AutoLog(value = "网红档案-编辑")
@Operation(summary = "网红档案-编辑", description = "网红档案-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebrity storeCelebrity) {
        storeCelebrityService.updateById(storeCelebrity);
        return Result.ok("编辑成功!");
    }

    /**
     * 网红验证编辑
     *
     * @param storeCelebrity
     * @return
     */
    @AutoLog(value = "网红档案-网红验证编辑")
@Operation(summary = "网红档案-网红验证编辑", description = "网红档案-网红验证编辑")
    @PutMapping(value = "/editValidation")
    public Result<?> editValidation(@RequestBody StoreCelebrity storeCelebrity) {
        String userId = getUserIdByToken();
        String username = getUserNameByToken();
        storeCelebrity.setVerifyUserId(userId);
        storeCelebrity.setVerifyUserName(username);
        storeCelebrity.setVerifyDate(new Date());
        storeCelebrityService.updateById(storeCelebrity);
        return Result.ok("编辑成功!");
    }


    /**
     * 编辑
     *
     * @param storeCelebrityPriceModel
     * @return
     */
    @AutoLog(value = "网红档案-编辑")
@Operation(summary = "网红档案-编辑", description = "网红档案-编辑")
    @PutMapping(value = "/editCelebrity")
    public Result<?> editCelebrity(@RequestBody StoreCelebrityPriceModel storeCelebrityPriceModel) {
        if (oConvertUtils.isEmpty(storeCelebrityPriceModel.getId())) {
            return Result.ok("参数错误!");
        }
        CelebrityConsignee celebrityConsignee = new CelebrityConsignee();
        StoreCelebrityPrice storeCelebrityPrice = new StoreCelebrityPrice();
        StoreCelebrity storeCelebrity = new StoreCelebrity();
        if (storeCelebrityPriceModel.getEditType() == YesNoStatus.NO.getCode()) {
            // 更新报价
            BeanUtils.copyProperties(storeCelebrityPriceModel, storeCelebrityPrice);
            storeCelebrityPrice.setCelebrityId(storeCelebrityPriceModel.getId());
            LambdaQueryWrapper<StoreCelebrityPrice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(StoreCelebrityPrice::getCelebrityId, storeCelebrityPriceModel.getId());
            List<StoreCelebrityPrice> storeCelebrityPrices = storeCelebrityPriceService.list(lambdaQueryWrapper);

            if (null != storeCelebrityPrices && storeCelebrityPrices.size() > 0) {
                storeCelebrityPrice.setId(storeCelebrityPrices.get(0).getId());
            } else {
                storeCelebrityPrice.setId("");
            }

            storeCelebrity.setId(storeCelebrityPriceModel.getId());
            storeCelebrity.setStar(storeCelebrityPriceModel.getStar());
            storeCelebrity.setGender(storeCelebrityPriceModel.getGender());
            storeCelebrity.setAge(storeCelebrityPriceModel.getAge());
            storeCelebrity.setEmail(storeCelebrityPriceModel.getEmail());
            storeCelebrity.setCountry(storeCelebrityPriceModel.getCountry());
            storeCelebrity.setFollowingNum(storeCelebrityPriceModel.getFollowingNum());
            storeCelebrity.setFollowersNum(storeCelebrityPriceModel.getFollowersNum());
            storeCelebrity.setPostsNum(storeCelebrityPriceModel.getPostsNum());
            storeCelebrity.setCommentAvgNum(storeCelebrityPriceModel.getCommentAvgNum());
            storeCelebrity.setPlayAvgNum(storeCelebrityPriceModel.getPlayAvgNum());
            storeCelebrity.setStar(storeCelebrityPriceModel.getStar());
            storeCelebrity.setKolPriceMin(storeCelebrityPriceModel.getKolPriceMin());
            storeCelebrity.setKolPriceMax(storeCelebrityPriceModel.getKolPriceMax());
            storeCelebrity.setRemark(storeCelebrityPriceModel.getRemark());
            storeCelebrity.setUpdateBy(getUserNameByToken());
            storeCelebrity.setUpdateTime(new Date());
        }
        if (storeCelebrityPriceModel.getEditType() == YesNoStatus.YES.getCode()) {
            // 更新收货地址
            BeanUtils.copyProperties(storeCelebrityPriceModel, celebrityConsignee);
            celebrityConsignee.setRemark(storeCelebrityPriceModel.getConsigneeRemark());
            celebrityConsignee.setCelebrityId(storeCelebrityPriceModel.getId());
            LambdaQueryWrapper<CelebrityConsignee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(CelebrityConsignee::getCelebrityId, storeCelebrityPriceModel.getId());
            List<CelebrityConsignee> celebrityConsignees = celebrityConsigneeService.list(lambdaQueryWrapper);

            if (null != celebrityConsignees && celebrityConsignees.size() > 0) {
                celebrityConsignee.setId(celebrityConsignees.get(0).getId());
            } else {
                celebrityConsignee.setId("");
            }

        }
        storeCelebrityService.storeCelebrityEdit(storeCelebrity, storeCelebrityPrice, celebrityConsignee);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "网红档案-通过id查询")
@Operation(summary = "网红档案-通过id查询", description = "网红档案-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrity storeCelebrity = storeCelebrityService.getById(id);
        return Result.ok(storeCelebrity);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebrity
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCelebrity storeCelebrity) {
        return super.exportXls(request, storeCelebrity, StoreCelebrity.class, "网红档案");
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
        List<String> listError = new ArrayList<>();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            // params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<StoreCelebrity> list = ExcelImportUtil.importExcel(file.getInputStream(), StoreCelebrity.class, params);
                for (StoreCelebrity storeCelebrity : list) {
                    LambdaQueryWrapper<StoreCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(StoreCelebrity::getEmail, storeCelebrity.getEmail());
                    List<StoreCelebrity> celebrityList = storeCelebrityService.list(lambdaQueryWrapper);
                    if (celebrityList != null && celebrityList.size() > 0) {
                        listError.add(storeCelebrity.getEmail());
                        continue;
                    }
                    StoreCelebrity storeCelebrityNew = new StoreCelebrity();
                    storeCelebrityNew.setAccount(storeCelebrity.getAccount());
                    storeCelebrityNew.setAvatarUrl(storeCelebrity.getAvatarUrl());
                    if (oConvertUtils.isNotEmpty(storeCelebrity.getUsername())) {
                        storeCelebrityNew.setUsername(storeCelebrity.getUsername());
                    } else {
                        storeCelebrityNew.setUsername(storeCelebrity.getAccount());
                    }
                    storeCelebrityNew.setNickName(storeCelebrity.getNickName());
                    storeCelebrityNew.setEmail(storeCelebrity.getEmail());
                    storeCelebrityNew.setCountry(storeCelebrity.getCountry());
                    storeCelebrityNew.setPlatformType(storeCelebrity.getPlatformType());
                    storeCelebrityNew.setRemark("1");
                    storeCelebrityService.save(storeCelebrityNew);
                }
                if (listError.size() > 0) {
                    Result<List<String>> result = new Result<>();
                    result.setSuccess(false);
                    result.setMessage("上传信息中已存在此邮箱！");
                    result.setResult(listError);
                    return result;
                }
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
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
     * 修改redis中当日发送总数量
     */
    @GetMapping(value = "/updateEmailDayNum")
    public Result<?> updateEmailDayNum(@RequestParam(name = "ids") String ids, @RequestParam(name = "noSendIds") String noSendIds) {
        List<String> idList = Arrays.asList(ids.split(","));
        List<String> noSendIdList = Arrays.asList(noSendIds.split(","));
        List<String> collect = idList.stream().filter(x -> !noSendIdList.contains(x)).collect(Collectors.toList());
        for (String s : collect) {
            redisUtil.get(s + "_num");

        }
        return Result.ok();
    }


    /**
     * 批量发送邮件
     *
     * @return
     */
    @AutoLog(value = "批量发送邮件")
    @RequestMapping(value = "/pushEmailBatch", method = RequestMethod.GET)
    public Result<?> pushEmailBatch(PushCommonModel pushCommonModel) {
        log.info("开始发送邮件 用户：" + getUserNameByToken());
        log.info("用户Id：" + pushCommonModel.getIds());
        Result<?> result = new Result<>();
        String userIdBytoken = getUserIdByToken();
        String userNameBytoken = getUserNameByToken();
        SysUser sysUser = sysUserService.getById(userIdBytoken);
        // 判断这个用户的群发功能状态
        if (Integer.parseInt(sysUser.getIsEnableEmail()) != YesNoStatus.YES.getCode()) {
            return Result.error("您的发送邮件功能已暂停，可能原因：开发信不规范，请联系您的商家顾问！");
        }
        // 查询选择用户的email
        List<String> idList = Arrays.asList(pushCommonModel.getIds().split(","));
        // 存储可发送网红信息id
        List<String> idListNews;
        // 存储不可发送网红信息id
        List<String> idListNo = new ArrayList<>();
        List<JSONObject> resultList;
        try {
            // 添加前判断，同一商家、同一网红发送次数以及总发送次数是否符合
            resultList = getResult(userIdBytoken, idList, pushCommonModel.getSendTime(), pushCommonModel.getEmailTitle());
            resultList.forEach(x -> {
                String kolId = x.getString("id");
                idListNo.add(kolId);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Result.error("发送失败！");
        }
        try {
            // 从已选择的网红id中过滤出可发送的
            idListNews = idList.stream().filter(x -> !idListNo.contains(x)).collect(Collectors.toList());
            HashSet<String> idSet = new HashSet<>(idListNews);
            List<String> idListNew = new ArrayList<>(idSet);
            if (idListNew.size() > 0) {
                pushCommonModel.setSellerId(userIdBytoken);
                pushCommonModel.setSellerName(userNameBytoken);
                pushCommonModel.setIds(idListNew.stream().map(String::valueOf).collect(Collectors.joining(",")));
                // 存储错误记录
                List<JSONObject> listError;
                Integer emailDay;
                // 判断当前用户是否过期
                if (sysUser.getExpirationDate().before(new Date())) {
                    emailDay = 0;
                } else {
                    emailDay = sysUser.getEmailsDay();
                }
                // 获取用户流量包总额度
                int packageNum = checkUserPackage(userIdBytoken);
                // 判断用户当天数量为0是，再判断是否有流量包
                if (emailDay == 0) {
                    if (packageNum == 0) {
                        result.setSuccess(false);
                        result.setCode(10001);
                        result.setMessage("可发送邮件数量为0，请购买后再发送！");
                        return result;
                    }
                }
                int over = packageNum + sysUser.getEmailsDay();
                // 判断剩余总额度是否大于0
                if (over <= 0) {
                    result.setSuccess(false);
                    result.setCode(10001);
                    result.setMessage("当日发送邮件数量已达上限！");
                    return result;
                }
                // 判断是否存在重复邮箱，存在则直接返回前台进行提示
                listError = checkEmailDou(idListNew, userIdBytoken);
                // 判断选择数量是否超过剩余额度
                if (over < idListNew.size()) {
                    result.setSuccess(false);
                    result.setCode(10002);
                    result.setMessage("所选数量大于可发送数量，今日可发送数量剩余：" + over + " 封");
                    return result;
                }
                if (listError.size() > 0) {
                    JSONObject jsonObject = new JSONObject();
                    Result<JSONObject> result2 = new Result<>();
                    result2.setSuccess(false);
                    result2.setCode(10005);
                    result2.setMessage("选择中有重复发送邮箱");
                    jsonObject.put("listError", listError);
                    jsonObject.put("notSendList", resultList);
                    result2.setResult(jsonObject);
                    return result2;
                }
                // 没有重复邮箱则发送邮件
                JSONObject jsonObject = pushAll(pushCommonModel);
                List<MessagePushDetail> pushDetailList = (List<MessagePushDetail>) jsonObject.get("pushDetailList");
                List<MessagePushDetail> gradePushDetailList = (List<MessagePushDetail>) jsonObject.get("gradePushDetailList");
                List<StoreEmailPackagePurchase> packagePurchaseList = (List<StoreEmailPackagePurchase>) jsonObject.get("packagePurchaseList");
                SysUser sysUserNew = (SysUser) jsonObject.get("sysUserNew");
                if (oConvertUtils.isNotEmpty(gradePushDetailList)) {
                    pushDetailList.addAll(gradePushDetailList);
                    storeCelebrityService.addPushEmail(pushDetailList, packagePurchaseList, sysUserNew);
                } else {
                    storeCelebrityService.addPushEmail(pushDetailList, packagePurchaseList, sysUserNew);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Result.error("发送失败！");
        }
        if (resultList.size() > 0) {
            Result<List<JSONObject>> addErrorResult = new Result<>();
            // 发送次数超出，添加失败
            addErrorResult.setCode(10007);
            addErrorResult.setResult(resultList);
            addErrorResult.setSuccess(false);
            addErrorResult.setMessage("操作成功！");
            return addErrorResult;
        }
        return Result.ok("创建成功!");
    }

    /**
     * 批量发送邮件 确认发送重复邮箱
     *
     * @return
     */
    @AutoLog(value = "批量发送邮件 确认发送重复邮箱")
    @RequestMapping(value = "/pushAllEmailBatch", method = RequestMethod.GET)
    public Result<?> pushAllEmailBatch(PushCommonModel pushCommonModel) {
        pushCommonModel.setSellerId(getUserIdByToken());
        pushCommonModel.setSellerName(getUserNameByToken());
        String ids = pushCommonModel.getIds();
        List<String> idsList = Arrays.asList(ids.split(","));
        HashSet<String> idSet = new HashSet<>(idsList);
        List<String> idList = new ArrayList<>(idSet);
        // 判断是否存在不可发送id,重新设置需要发送的id
        if (oConvertUtils.isNotEmpty(pushCommonModel.getNoSendList())) {
            List<String> idNoList = Arrays.asList(pushCommonModel.getNoSendList().split(","));
            List<String> collect = idList.stream().filter(x -> !idNoList.contains(x)).collect(Collectors.toList());
            pushCommonModel.setIds(String.join(",", collect));
        }
        if (oConvertUtils.isNotEmpty(pushCommonModel.getIds())) {
            JSONObject jsonObject = pushAll(pushCommonModel);
            List<MessagePushDetail> pushDetailList = (List<MessagePushDetail>) jsonObject.get("pushDetailList");
            List<MessagePushDetail> gradePushDetailList = (List<MessagePushDetail>) jsonObject.get("gradePushDetailList");
            List<StoreEmailPackagePurchase> packagePurchaseList = (List<StoreEmailPackagePurchase>) jsonObject.get("packagePurchaseList");
            SysUser sysUserNew = (SysUser) jsonObject.get("sysUserNew");
            if (oConvertUtils.isNotEmpty(gradePushDetailList) && oConvertUtils.isNotEmpty(pushDetailList)) {
                pushDetailList.addAll(gradePushDetailList);
                storeCelebrityService.addPushEmail(pushDetailList, packagePurchaseList, sysUserNew);
            } else if (oConvertUtils.isEmpty(gradePushDetailList)) {
                storeCelebrityService.addPushEmail(pushDetailList, packagePurchaseList, sysUserNew);
            }
        } else {
            return Result.ok("操作成功!");
        }
        return Result.ok("创建成功!");
    }

    /**
     * 功能描述:添加邮件前判断
     *
     * @Author: nqr
     * @Date 2020-12-04 13:48:42
     */
    private List<JSONObject> getResult(String sellerId, List<String> kolIdList, String sendDate, String emailTitle) {
        List<JSONObject> list = new ArrayList<>();
        // 查询字典表中配置的当天发送总数量
        SysDictItem dictItem = sysDictItemService.selectValueByCode(DictCode.EMAIL_SEND_DAY);
        int emailSendDayMaxNum = Integer.parseInt(dictItem.getItemValue());
        System.out.println("当日最大发送数量 :" + emailSendDayMaxNum);
        // 从字典表获取网红当日收到的最大邮件数
        SysDictItem sysDictItem = sysDictItemService.selectValueByCode(DictCode.CELEBRITY_MAXIMUM_DAY);
        int celebrityMaximumDay = Integer.parseInt(sysDictItem.getItemValue());
        System.out.println("网红当日收到的最大邮件数 :" + celebrityMaximumDay);
        // 从字典表中获取配置的可发送邮件时间范围
        List<DictModel> dictList = sysDictService.selectItemListByCode("email_send_range");
        String sendStart = dictList.stream().filter(x -> x.getText().equals("send_start")).collect(Collectors.toList()).get(0).getValue();
        String sendEnd = dictList.stream().filter(x -> x.getText().equals("send_end")).collect(Collectors.toList()).get(0).getValue();
        System.out.println("可发送邮件时间范围 :" + sendStart + "点 - " + sendEnd + "点");
        int start = Integer.parseInt(sendStart);
        int end = Integer.parseInt(sendEnd);
        // 获取每天邮件发送时间
        String startTime = sendDate + " " + "0" + start + ":00:00";
        String endTime = sendDate + " " + end + ":00:00";
        System.out.println(startTime);
        System.out.println(endTime);
        Date startSendTime = DateUtils.str2dateS(startTime);
        Date endSendTime = DateUtils.str2dateS(endTime);
        // 每小时发送邮件数
        List<DictModel> dictItemList = sysDictService.selectItemListByCode("job_send");
        String jobSendNumber = dictItemList.stream().filter(x -> x.getText().equals("send_number")).collect(Collectors.toList()).get(0).getValue();
        int hourSendNum = Integer.parseInt(jobSendNumber) * 12;
        // 存储递归后当天的数量
        Map<String, Integer> sendNumMap = new HashMap<>();
        for (String kolId : kolIdList) {
            Map<String, Object> map = checkCelebrity(sellerId, kolId, sendDate, emailTitle, 0, emailSendDayMaxNum, celebrityMaximumDay, startSendTime, endSendTime, hourSendNum, sendNumMap);
            if ((int) map.get("code") != 200) {
                StoreCelebrity storeCelebrity = storeCelebrityService.getById(kolId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", storeCelebrity.getId());
                jsonObject.put("nickName", storeCelebrity.getNickName());
                jsonObject.put("code", map.get("code"));
                list.add(jsonObject);
            }
        }
        return list;
    }

    /**
     * 添加邮箱发送记录
     */
    private JSONObject pushAll(PushCommonModel pushCommonModel) {
        String userId = pushCommonModel.getSellerId();
        String userName = pushCommonModel.getSellerName();
        // 查询选择用户的email
        List<String> idList = Arrays.asList(pushCommonModel.getIds().split(","));
        SysUser sysUser = sysUserService.getById(userId);
        SysUser sysUserNew = new SysUser();
        // 当天剩余额度
        int num = sysUser.getEmailsDay();
        Date expirationDate = sysUser.getExpirationDate();
        // 判断当前用户是否过期，过期后设置当天额度为0
        if (expirationDate.before(new Date())) {
            num = 0;
            sysUserNew.setId(sysUser.getId());
            sysUserNew.setEmailsDay(0);
        } else {
            num = Math.max(num, 0);
        }
        // 获取用户流量包总额度
        int packageNum = checkUserPackage(userId);
        // 当天剩余总额度
        int over = packageNum + sysUser.getEmailsDay();
        List<String> delList = new ArrayList<>();
        List<MessagePushDetail> pushDetailList = new ArrayList<>();
        // 如果当前额度大于0，优先使用账号额度
        if (num > 0) {
            int len;
            if (num > idList.size()) {
                len = idList.size();
            } else {
                len = num;
            }
            String sendTime = pushCommonModel.getSendTime();
            System.out.println(sendTime);
            for (int i = 0; i < len; i++) {
                delList.add(idList.get(i));
                // 添加发送历史表
                MessagePushDetail messagePushDetail = addPushEmailDetail(userId, userName, idList.get(i), pushCommonModel);
                pushDetailList.add(messagePushDetail);
            }
            sysUserNew.setId(sysUser.getId());
            sysUserNew.setEmailsDay(sysUser.getEmailsDay() - len);
        }
        JSONObject jsonObject = new JSONObject();
        if (idList.size() > 0) {
            // 删除账号额度已添加的
            String s1 = "";
            for (String value : idList) {
                s1 += (value + ",");
            }
            s1 = s1.substring(0, s1.length() - 1);
            List<String> balance = new LinkedList<>(Arrays.asList(s1.split(",")));
            for (String s : delList) {
                balance.remove(s);
            }
            idList = balance;
            // 使用流量包发送,判断剩余总额度大于已选择的邮件数量
            if (over > 0 && over >= (idList.size() - num)) {
                // 修改邮箱使用额度
                jsonObject = updateEmailCount(userId, userName, pushCommonModel, idList);
            }
        }
        if (pushDetailList.size() > 0 && oConvertUtils.isNotEmpty(pushDetailList.get(0))) {

            jsonObject.put("gradePushDetailList", pushDetailList);
        } else {
            jsonObject.put("gradePushDetailList", null);
        }
        jsonObject.put("sysUserNew", sysUserNew);
        return jsonObject;
    }

    /**
     * 功能描述:添加发送历史表
     *
     * @param kolId 网红id
     * @return void
     * @Author: nqr
     * @Date 2020-10-06 08:29:15
     */
    private MessagePushDetail addPushEmailDetail(String userId, String username, String kolId, PushCommonModel pushCommonModel) {
        StoreCelebrity storeCelebrity = storeCelebrityService.getById(kolId);
        MessagePushDetail messagePushDetail = null;
        if (oConvertUtils.isNotEmpty(storeCelebrity) && oConvertUtils.isNotEmpty(storeCelebrity.getEmail())) {
            PushUser pushUser = new PushUser();
            pushUser.setKolId(storeCelebrity.getId());
            pushUser.setKolName(storeCelebrity.getNickName());
            pushUser.setKolEmail(storeCelebrity.getEmail());
            pushUser.setSellerId(userId);
            pushUser.setSellerName(username);
            pushUser.setReamrk(pushCommonModel.getRemark());
            messagePushDetail = messageTemplateService.pushEmailMessage(pushCommonModel, pushUser);
        }
        return messagePushDetail;
    }

    /**
     * 修改邮箱使用额度
     *
     * @param userId          商户id
     * @param username        商户名称
     * @param pushCommonModel 传递参数
     * @param idList          网红用户id
     */
    private JSONObject updateEmailCount(String userId, String username, PushCommonModel pushCommonModel, List<String> idList) {
        List<String> delList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        // 查询出可用流量包
        LambdaQueryWrapper<StoreEmailPackagePurchase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.and(i -> i.eq(StoreEmailPackagePurchase::getSellerId, userId).or().eq(StoreEmailPackagePurchase::getInviteSellerId, userId));
        lambdaQueryWrapper.and(i -> i.gt(StoreEmailPackagePurchase::getCurrentPackageNum, YesNoStatus.NO.getCode()).or().gt(StoreEmailPackagePurchase::getInviteCurrentPackageNum, YesNoStatus.NO.getCode()));
        lambdaQueryWrapper.ge(StoreEmailPackagePurchase::getExpirationDate, new Date());
        lambdaQueryWrapper.orderByAsc(StoreEmailPackagePurchase::getExpirationDate);
        List<StoreEmailPackagePurchase> list = emailPackagePurchaseService.list(lambdaQueryWrapper);
        List<StoreEmailPackagePurchase> updateList = new ArrayList<>();
        List<MessagePushDetail> pushDetailList = new ArrayList<>();
//        int emailCount = 0;
        String sendTime = pushCommonModel.getSendTime();
        System.out.println(sendTime);
        /*Integer emailNum = pushCommonModel.getEmailNum();
        Integer emailTime = pushCommonModel.getEmailTime();*/
        for (StoreEmailPackagePurchase storeEmailPackagePurchase : list) {
            StoreEmailPackagePurchase packagePurchase = new StoreEmailPackagePurchase();
            packagePurchase.setId(storeEmailPackagePurchase.getId());
            Integer currentPackageNum;
            if (userId.equals(storeEmailPackagePurchase.getSellerId())) {
                currentPackageNum = storeEmailPackagePurchase.getCurrentPackageNum();
            } else {
                currentPackageNum = storeEmailPackagePurchase.getInviteCurrentPackageNum();
            }
            if (currentPackageNum == 0) {
                continue;
            }
            int len;
            if (currentPackageNum > idList.size()) {
                len = idList.size();
            } else {
                len = currentPackageNum;
            }
            for (int i = 0; i < len; i++) {
//                Date date = DateUtils.str2date(sendTime);
//                System.out.println("emailCount:" + emailCount);
                pushCommonModel.setPackageId(storeEmailPackagePurchase.getId());
                pushCommonModel.setPackageName(storeEmailPackagePurchase.getPackageName());
                pushCommonModel.setPackagePurchaseId(storeEmailPackagePurchase.getId());
                delList.add(idList.get(i));
               /* //判断添加邮件数量，每10封邮件间隔10分钟
                if (emailCount / emailNum > 0) {
                    //发送时间
                    Date date1 = new Date();
                    try {
                        if (date.before(date1)) {
                            date = date1;
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    Date fetureDateMinute = DateUtils.getFetureDateMinute(date, emailTime * (emailCount / emailNum));
                    pushCommonModel.setSendTime(DateUtils.dateToString(fetureDateMinute));
                }*/
                System.err.println(pushCommonModel.getSendTime());
                // 添加到发送历史表
                MessagePushDetail messagePushDetail = addPushEmailDetail(userId, username, idList.get(i), pushCommonModel);
                pushDetailList.add(messagePushDetail);
//                emailCount++;
            }
            int size = idList.size();

            if (idList.size() > 0) {
                StringBuilder s1 = new StringBuilder();
                for (String id : idList) {
                    s1.append(id).append(",");
                }
                s1 = new StringBuilder(s1.substring(0, s1.length() - 1));
                List<String> balance = new LinkedList<>(Arrays.asList(s1.toString().split(",")));
                // 删除账号额度已添加的
                for (String s : delList) {
                    balance.remove(s);
                }
                idList = balance;
            }
            // 处理流量包
            // 剩余选择发送数量，大于流量包数量
            if (idList.size() > 0) {
                // 修改当前流量包为0
                // 清空该流量包
                packagePurchase.setCurrentPackageNum(0);
                updateList.add(packagePurchase);
            } else {
                Integer currentPackageNum1;
                if (userId.equals(storeEmailPackagePurchase.getSellerId())) {
                    currentPackageNum1 = storeEmailPackagePurchase.getCurrentPackageNum();
                } else {
                    currentPackageNum1 = storeEmailPackagePurchase.getInviteCurrentPackageNum();
                }
                packagePurchase.setCurrentPackageNum(currentPackageNum1 - size);
                updateList.add(packagePurchase);
                break;
            }
            updateList.add(packagePurchase);
        }
        jsonObject.put("pushDetailList", pushDetailList);
        jsonObject.put("packagePurchaseList", updateList);
        // 修改额度使用情况
//        emailPackagePurchaseService.updateBatchById(updateList);
        return jsonObject;
    }


    // 判断该商户是否给该邮箱发送过邮件
    private boolean getKolEmailData(String sellerId, String kolEmail) {
        // 根据商户id，网红邮箱，发送状态判断
        LambdaQueryWrapper<MessagePushDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MessagePushDetail::getSellerId, sellerId);
        lambdaQueryWrapper.eq(MessagePushDetail::getKolEmail, kolEmail);
        lambdaQueryWrapper.ge(MessagePushDetail::getIsSend, 0);
        List<MessagePushDetail> list = pushDetailService.list(lambdaQueryWrapper);
        return list.size() <= 0;
    }

    /**
     * 判断流量包总额
     */
    private int checkUserPackage(String sellerId) {
        LambdaQueryWrapper<StoreEmailPackagePurchase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.and(i -> i.eq(StoreEmailPackagePurchase::getSellerId, sellerId).or().eq(StoreEmailPackagePurchase::getInviteSellerId, sellerId));
        lambdaQueryWrapper.and(i -> i.gt(StoreEmailPackagePurchase::getCurrentPackageNum, YesNoStatus.NO.getCode()).or().gt(StoreEmailPackagePurchase::getInviteCurrentPackageNum, YesNoStatus.NO.getCode()));
        lambdaQueryWrapper.ge(StoreEmailPackagePurchase::getExpirationDate, new Date());
        List<StoreEmailPackagePurchase> list = emailPackagePurchaseService.list(lambdaQueryWrapper);
        int i = 0;
        if (list.size() > 0) {
            for (StoreEmailPackagePurchase storeEmailPackagePurchase : list) {
                if (storeEmailPackagePurchase.getSellerId().equals(sellerId)) {
                    i += storeEmailPackagePurchase.getCurrentPackageNum();
                } else {
                    i += storeEmailPackagePurchase.getInviteCurrentPackageNum();
                }
            }
        }
        return i;
    }

    /**
     * 判断重复邮箱
     */
    private List<JSONObject> checkEmailDou(List<String> idList, String userId) {
        List<JSONObject> listError = new ArrayList<>();
        List<StoreCelebrity> userList = (List<StoreCelebrity>) storeCelebrityService.listByIds(idList);
        // 存储使用额度
        for (StoreCelebrity storeCelebrity : userList) {
            String email = storeCelebrity.getEmail();
            if (oConvertUtils.isNotEmpty(email)) {
                // 查询邮件发送历史表中是否存在该邮箱
                boolean flag = getKolEmailData(userId, email);
                if (!flag) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", storeCelebrity.getId());
                    jsonObject.put("nickName", storeCelebrity.getNickName());
                    listError.add(jsonObject);
                }
            }
        }
        return listError;
    }


    /**
     * 功能描述:判断当前用户状态
     *
     * @return
     * @Author: nqr
     * @Date 2020-10-22 16:53:51
     */
    private int checkUserStatus(SysUser sysUser) {
        // 套餐过期时间
        Date expirationDate = null;
        if (oConvertUtils.isNotEmpty(sysUser.getExpirationDate())) {
            expirationDate = sysUser.getExpirationDate();
        }
        // 邮箱数量
        Integer emailAccountCount = sysUser.getEmailAccountCount();
        // 邮件额度
        Integer emailPushCount = sysUser.getEmailPushCount();
        // 获取流量包额度
        int packageNum = checkUserPackage(sysUser.getId());
        // 0 新注册 1 已购买套餐  2购买流量包，无套餐  3套餐到期，邮件包未到期   4 套餐到期，邮件包到期
        int status = 0;
        // 新注册用户
        if (oConvertUtils.isEmpty(expirationDate) && emailAccountCount == 0 && emailPushCount == 0) {
            status = 1;
        } else if (oConvertUtils.isNotEmpty(expirationDate) && emailAccountCount > 0 && emailPushCount > 0 && expirationDate.after(new Date())) {
            // 已购买套餐，并且套餐未过期
            status = 2;
        } else if (packageNum > 0 && oConvertUtils.isEmpty(expirationDate) && emailAccountCount == 0 && emailPushCount == 0) {
            // 购买流量包，无套餐
            status = 3;
        } else if (oConvertUtils.isNotEmpty(expirationDate) && expirationDate.before(new Date()) && packageNum > 0) {
            // 套餐到期，邮件包未到期
            status = 4;
        } else if (oConvertUtils.isNotEmpty(expirationDate) && expirationDate.before(new Date()) && packageNum == 0) {
            // 套餐到期，邮件包到期
            status = 5;
        }
        return status;
    }

    /**
     * 功能描述:判断同一商家、同一网红 发送次数(全部 和 单日发送次数)
     *
     * @param sellerId            商家ID
     * @param kolId               网红ID
     * @param sendDate            发送日期
     * @param emailTitle          邮件标题
     * @param dayMaxNum           最大递归次数
     * @param emailSendDayMaxNum  当天最大发送数量
     * @param celebrityMaximumDay 网红当天最大接受邮件数量
     * @param startSendTime       开始发送邮件时间
     * @param endSendTime         结束发送邮件时间
     * @param hourSendNum         每小时发送邮件数
     * @param sendNumMap          存储递归后可发送数量
     * @text 返回状态码说明
     * 　　　10005   超过最大递归数
     * 　　　10008   同一商家同一网红同一天存在发送任务
     * 　　　500     当日不可发送
     * 　　　200     可以正常发送
     * @Author: nqr
     * @Date 2020-12-01 09:15:54
     */
    public Map<String, Object> checkCelebrity(String sellerId, String kolId, String sendDate, String emailTitle, int dayMaxNum, int emailSendDayMaxNum, int celebrityMaximumDay, Date startSendTime, Date endSendTime, int hourSendNum, Map<String, Integer> sendNumMap) {
        Map<String, Object> resultMap = new HashMap<>();
        // 判断同一商家、同一网红、同一天，只能发一次（忽略标题内容）
        boolean exists = judgeCelebrityExists(sellerId, kolId, sendDate);
        if (!exists) {
            // 不可以发送
            resultMap.put("code", 10005);
            return resultMap;
        }
        // 判断同一商家、同一网红、同一标题发送的邮件总次数
        int sellerSendNum = getSellerSendNum(sellerId, kolId, emailTitle);
        System.out.println("商家向同一网红发送次数 :" + sellerSendNum);
        // 查询网红当天接收邮件数量
        int kolEmailDay;
        // 标识是否递归
        int recursion = 0;
        // 判断同一商家、同一网红、同一标题发送的邮件总次数
        if (sellerSendNum >= 3) {
            // 超过三次驳回
            resultMap.put("code", 10005);
            return resultMap;
        } else {
            // 判断当日发送总次数
            while (true) {
                Map<String, Integer> dayMap = getEmailDay2(sendDate, emailSendDayMaxNum, dayMaxNum, startSendTime, endSendTime, hourSendNum, sendNumMap);
                int dayCode = dayMap.get("code");
                dayMaxNum = dayMap.get("dayMaxNum");
                // 超出最大递归，驳回
                if (dayCode == 10005) {
                    resultMap.put("code", 10005);
                    return resultMap;
                }
                if (dayCode == 500) {
                    recursion = 1;
                    sendDate = getFetureDate(sendDate);
                    // 已存在发送任务数量
                    int emailSendAllNum = getEmailSendAllNum(sendDate);
                    // 判断递归后是否存在发送任务，存在发送数量+1，不存在新增
                    sendDate = getDateNum(sendDate, sendNumMap, dayMaxNum, emailSendDayMaxNum, emailSendAllNum);
                    if (sendDate.equals("")) {
                        resultMap.put("code", 10005);
                        return resultMap;
                    }
                    boolean flay = sendNumMap.containsKey(sendDate);
                    if (flay) {
                        sendNumMap.put(sendDate, sendNumMap.get(sendDate) + 1);
                    } else {
                        sendNumMap.put(sendDate, 0);
                    }
                } else {
                    // 网红当天接收数量
                    kolEmailDay = getKolEmailDay(kolId, sendDate);
                    // 判断网红当天接收数量和最大接受数量
                    if (kolEmailDay >= celebrityMaximumDay) {
                        sendDate = getFetureDate(sendDate);
                        // 已存在发送任务数量
                        int emailSendAllNum = getEmailSendAllNum(sendDate);
                        // 判断递归后是否存在发送任务，存在发送数量+1，不存在新增
                        sendDate = getDateNum(sendDate, sendNumMap, dayMaxNum, emailSendDayMaxNum, emailSendAllNum);
                        if (sendDate.equals("")) {
                            resultMap.put("code", 10005);
                            return resultMap;
                        }
                        boolean flay = sendNumMap.containsKey(sendDate);
                        if (flay) {
                            sendNumMap.put(sendDate, sendNumMap.get(sendDate) + 1);
                        } else {
                            sendNumMap.put(sendDate, 0);
                        }
                    } else {
                        break;
                    }
                }
            }
            if (kolEmailDay > 0) {
                // 判断同一商家、同一网红、同一天，只能发一次（忽略标题内容）
                boolean sendNumFlag = judgeCelebrityExists(sellerId, kolId, sendDate);
                if (!sendNumFlag) {
                    // 不可以发送
                    resultMap.put("kolId", kolId);
                    resultMap.put("sendDate", sendDate);
                    resultMap.put("code", 10005);
                    return resultMap;
                }
            }
            boolean flag = sendNumMap.containsKey(sendDate);
            if (flag) {
      /*          if (sendNumMap.get(sendDate) + 1 > 10) {
                    return checkCelebrity(sellerId, kolId, sendDate, emailTitle, dayMaxNum, emailSendDayMaxNum, celebrityMaximumDay, startSendTime, endSendTime, hourSendNum, sendNumMap);
                }*/
                if (recursion == 0) {
                    sendNumMap.put(sendDate, sendNumMap.get(sendDate) + 1);
                }
            } else {
                sendNumMap.put(sendDate, 1);
            }
            // 设置5分钟内有效
            redisUtil.set(sellerId + "_" + kolId, sendDate, 60);
            System.err.println(kolId + "_" + sendDate);
        }
        resultMap.put("code", 200);
        return resultMap;
    }

    /**
     * 功能描述:判断递归后是否存在发送任务
     *
     * @Author: nqr
     * @Date 2020-12-23 18:20:39
     */
    private String getDateNum(String sendDate, Map<String, Integer> sendNumMap, int dayMaxNum, int emailSendDayMaxNum, int emailSendAllNum) {
        if (dayMaxNum > 7) {
            return "";
        }
        if (sendNumMap.containsKey(sendDate)) {
            Integer addNum = sendNumMap.get(sendDate);
            if ((addNum + emailSendAllNum) > emailSendDayMaxNum) {
                sendDate = getFetureDate(sendDate);
                // 已存在发送任务数量
                emailSendAllNum = getEmailSendAllNum(sendDate);
                return getDateNum(sendDate, sendNumMap, dayMaxNum, emailSendDayMaxNum, emailSendAllNum);
            }
        }
        return sendDate;
    }


    /**
     * 功能描述:查询当日邮件数量
     *
     * @Author: nqr
     * @Date 2020-12-01 09:47:03
     */
    public Map<String, Object> getEmailDay(String sendDate, int emailSendDay, int dayMaxNum) {
        Map<String, Object> map = new HashMap<>();
        if (dayMaxNum > 7) {
            map.put("code", 10005);
            return map;
        }
        dayMaxNum++;
        String key = sendDate + "_all";
        Object o = redisUtil.get(key);
        long expire = redisUtil.getExpire(key);
        int allDateNum = 0;
        if (oConvertUtils.isNotEmpty(o)) {
            allDateNum = (int) o;
        } else {
            int kolKeyExpirationTime = getKolKeyExpirationTime(sendDate);
            redisUtil.set(key, 1, kolKeyExpirationTime);
        }
        if (emailSendDay > allDateNum) {
            redisUtil.set(key, allDateNum + 1, expire);
            // 当日额度不满，允许发送
            map.put("code", 200);
            return map;
        } else {
            // 驳回
            map.put("code", 500);
            map.put("num", dayMaxNum);
            return map;
        }
    }


    /**
     * 功能描述:查询当日邮件数量（修改）
     *
     * @param sendDate      发送日期
     * @param emailSendDay  当日邮件推送最大数量
     * @param dayMaxNum     递归次数
     * @param startSendTime 发送邮件开始时间
     * @param endSendTime   发送邮件结束时间
     * @param hourSendNum   每小时发送邮件数量
     * @Author: nqr
     * @Date 2020-12-15 08:33:03
     */
    public Map<String, Integer> getEmailDay2(String sendDate, int emailSendDay, int dayMaxNum, Date startSendTime, Date endSendTime, int hourSendNum, Map<String, Integer> sendNumMap) {
        Map<String, Integer> map = new HashMap<>();
        if (dayMaxNum > 7) {
            map.put("code", 10005);
            map.put("dayMaxNum", 0);
            map.put("emailSendAllNum", 0);
            return map;
        }
        dayMaxNum++;
        // 获取当日发送总次数
        int num = 0;
        if (sendNumMap.containsKey(sendDate)) {
            num = sendNumMap.get(sendDate);
        }
        // 已存在发送任务数量
        int emailSendAllNum = getEmailSendAllNum(sendDate);
        int allDateNum = emailSendAllNum + num;
        if (sendNumMap.containsKey(sendDate) && emailSendAllNum == emailSendDay) {
            sendNumMap.put(sendDate, emailSendDay);
        }
        if (emailSendDay > allDateNum) {
            // 判断当前太平洋时间是否在该范围内
            // 当前时间在发送邮件前
            Date tPtime = getTPtime();
            if (tPtime.after(startSendTime) && tPtime.before(endSendTime)) {
                // 当前时间在发送邮件时间内,获取距离当前时间最近的小时数
                Date datePoint = DateUtils.getDatePoint(tPtime, 1);
                // 获取距离发送结束还剩多少小时
                int datePoor = DateUtils.getDatePoor(endSendTime, datePoint);
                // 获取剩余小时可添加邮件数量
                if (datePoor == 0) {
                    // 驳回
                    map.put("code", 500);
                    map.put("num", dayMaxNum);
                    return map;
                }
                // 获取当前已发送数量
                int sendSuccessNum = getSendSuccessNum(sendDate);
                // 剩余小时发送最大数量
                int hourMaxNum = hourSendNum * datePoor;
                // 剩余未发送数量
                int waitSendNum = allDateNum - sendSuccessNum;
                // 剩余小时最大发送数量小于剩余未发送数量  驳回
                if (hourMaxNum < waitSendNum) {
                    // 驳回
                    map.put("code", 500);
                    map.put("num", dayMaxNum);
                    return map;
                }
            } else if (tPtime.after(endSendTime)) {
                // 超过邮件发送时间 驳回 当日不可添加
                map.put("code", 500);
                map.put("num", dayMaxNum);
                return map;
            }
            // 当日额度不满，允许发送
            map.put("code", 200);
            map.put("dayMaxNum", dayMaxNum);
            map.put("emailSendAllNum", emailSendAllNum);
        } else {
            // 驳回
            map.put("code", 500);
            map.put("dayMaxNum", dayMaxNum);
            map.put("emailSendAllNum", emailSendAllNum);
        }
        return map;
    }

    /**
     * 功能描述: 获取当天已发送邮件数量
     *
     * @Author: nqr
     * @Date 2020-12-15 15:09:43
     */
    private int getSendSuccessNum(String sendDate) {
        MessagePushDetailModel detailModel = new MessagePushDetailModel();
        detailModel.setEmailPushDate(sendDate);
        detailModel.setIsSend(YesNoStatus.YES.getCode());
        MessagePushDetailModel messagePushDetailModel = messagePushDetailService.getDetailList(detailModel);
        return messagePushDetailModel.getCount();
    }

    /**
     * 功能描述: 获取商家给该网红发送的总次数
     *
     * @Author: nqr
     * @Date 2020-12-04 09:57:27
     */
    private int getSellerSendNum(String sellerId, String kolId, String emailTitle) {
        MessagePushDetailModel detailModel = new MessagePushDetailModel();
        detailModel.setKolId(kolId);
        detailModel.setSellerId(sellerId);
        detailModel.setEmailTitle(emailTitle);
        detailModel.setIsSend(YesNoStatus.CANCEL.getCode());
        MessagePushDetailModel messagePushDetailModel = messagePushDetailService.getDetailList(detailModel);
        return messagePushDetailModel.getCount();
    }

    /**
     * 功能描述: 获取过期时间
     *
     * @Author: nqr
     * @Date 2020-12-01 17:59:25
     */
    private int getKolKeyExpirationTime(String sendDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DateUtils.str2Date(sendDate, simpleDateFormat);
        Date tpDate = getTPtime();
        Date fetureDateHour;
        if (tpDate.before(date)) {
            fetureDateHour = DateUtils.getFetureDateHour(24, date);
        } else {
            String format = simpleDateFormat.format(tpDate);
            Date date1 = DateUtils.str2Date(format, simpleDateFormat);
            fetureDateHour = DateUtils.getFetureDateHour(24, date1);
        }
        System.out.println("过期时间：" + DateUtils.date2Str(fetureDateHour));
        long l = DateUtil.betweenMs(tpDate, fetureDateHour);
        int i = BigDecimal.valueOf(l).divide(BigDecimal.valueOf(1000), 0, BigDecimal.ROUND_HALF_UP).intValue();
        System.out.println("键值过期秒数：" + i);
        return i;
    }

    /**
     * 功能描述:获取太平洋时间
     *
     * @Author: nqr
     * @Date 2020-12-03 17:38:13
     */
    public Date getTPtime() {
        Date tpDate = DateUtils.getFetureDateHour(-16, new Date());
        System.out.println("太平洋时间：" + DateUtils.date2Str(tpDate));
        return tpDate;
    }

    /**
     * 功能描述:获取下一天日期
     *
     * @Author: nqr
     * @Date 2020-12-03 17:41:26
     */
    public String getFetureDate(String sendDate) {
        Date date = DateUtils.str2dateS(sendDate);
        Date fetureDate = DateUtils.getFetureDate(1, date);
        return new SimpleDateFormat("yyyy-MM-dd").format(fetureDate);
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importMessageResultExcel", method = RequestMethod.POST)
    public Result<?> importMessageResultExcel(HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        String userName = getUserNameByToken();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<EmailPostStatusExcelModel> emailPostStatusExcelModelList = ExcelImportUtil.importExcel(file.getInputStream(), EmailPostStatusExcelModel.class, params);
                List<List<EmailPostStatusExcelModel>> averageAssignList;
                if (emailPostStatusExcelModelList != null && emailPostStatusExcelModelList.size() > 0) {
                    CountDownLatch countDownLatch = new CountDownLatch(NumberOfCopies.DEFAULT_NUMBER_OF_COPIES.getCode());
                    averageAssignList = AverageAssignUtil.averageAssign(emailPostStatusExcelModelList, NumberOfCopies.DEFAULT_NUMBER_OF_COPIES.getCode());
                    for (int i = 0; i < averageAssignList.size(); i++) {
                        List<EmailPostStatusExcelModel> modelList = averageAssignList.get(i);
                        // CountDownLatch闭锁,确保线程已经全部执行完毕
                        Thread thread = new Thread(() -> {
                            try {
                                updateCelebrityIsSendStatus(modelList, userName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                countDownLatch.countDown();
                            }
                        });
                        thread.start();
                    }
                    countDownLatch.await();
                    log.info("共消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                    return Result.ok("文件导入完成!");
                }
                return Result.ok("无可导入数据!");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
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

    // 根据email修改网红发送邮件状态
    public void updateCelebrityIsSendStatus(List<EmailPostStatusExcelModel> list, String userName) {
        List<StoreCelebrity> updateList = new ArrayList<>();
        for (EmailPostStatusExcelModel emailPostStatusExcelModel : list) {
            String email = emailPostStatusExcelModel.getReciveAddress();
            String status = emailPostStatusExcelModel.getPostStatus();
            List<StoreCelebrity> storeCelebrityList = storeCelebrityService.queryByEmail(email);
            if (storeCelebrityList != null && storeCelebrityList.size() > 0) {
                storeCelebrityList.forEach(storeCelebrity -> {
                    StoreCelebrity storeCelebrityNew = new StoreCelebrity();
                    storeCelebrityNew.setId(storeCelebrity.getId());
                    storeCelebrityNew.setUpdateBy(userName);
                    storeCelebrityNew.setUpdateTime(new Date());
                    switch (status) {
                        case "成功":
                            storeCelebrityNew.setIsSendStatus(EmailSendStatus.SERVICE.getCode());
                            break;
                        case "失败":
                            storeCelebrityNew.setIsSendStatus(EmailSendStatus.INVALID.getCode());
                            storeCelebrityNew.setDelFlag(YesNoStatus.YES.getCode());
                            storeCelebrityNew.setSendFailReason("失败");
                            break;
                        case "无效地址":
                            storeCelebrityNew.setIsSendStatus(EmailSendStatus.INVALID.getCode());
                            storeCelebrityNew.setDelFlag(YesNoStatus.YES.getCode());
                            storeCelebrityNew.setSendFailReason("无效地址");
                            break;
                        case "垃圾邮件":
                            storeCelebrityNew.setIsSendStatus(EmailSendStatus.INVALID.getCode());
                            storeCelebrityNew.setDelFlag(YesNoStatus.YES.getCode());
                            storeCelebrityNew.setSendFailReason("垃圾邮件");
                            break;
                        default:
                            break;
                    }
                    updateList.add(storeCelebrityNew);
                });
            }
        }
        storeCelebrityService.updateBatchById(updateList);
        log.info("修改网红数量为: " + updateList.size());
    }


    /**
     * 功能描述:获取同一商家，同一网红，同一天，发送次数
     *
     * @Author: nqr
     * @Date 2020-12-17 11:52:07
     */
    private boolean judgeCelebrityExists(String sellerId, String kolId, String sendDate) {
        MessagePushDetailModel detailModel = new MessagePushDetailModel();
        detailModel.setKolId(kolId);
        detailModel.setSellerId(sellerId);
        detailModel.setEmailPushDate(sendDate);
        detailModel.setIsSend(YesNoStatus.CANCEL.getCode());
        MessagePushDetailModel messagePushDetailModel = messagePushDetailService.getDetailList(detailModel);
        return messagePushDetailModel.getCount() <= 0;
    }

    /**
     * 功能描述:获取网红当日接收邮件数量
     *
     * @return
     * @Author: nqr
     * @Date 2020-12-23 08:38:44
     */

    private int getKolEmailDay(String kolId, String sendDate) {
        MessagePushDetailModel detailModel = new MessagePushDetailModel();
        detailModel.setKolId(kolId);
        detailModel.setEmailPushDate(sendDate);
        detailModel.setIsSend(YesNoStatus.CANCEL.getCode());
        MessagePushDetailModel messagePushDetailModel = messagePushDetailService.getDetailList(detailModel);
        return messagePushDetailModel.getCount();
    }

    /**
     * 功能描述: 获取当日发送邮件总次数
     *
     * @param sendDate 发送日期
     * @return int
     * @Author: nqr
     * @Date 2020-12-23 10:45:42
     */
    private int getEmailSendAllNum(String sendDate) {
        MessagePushDetailModel detailModel = new MessagePushDetailModel();
        detailModel.setEmailPushDate(sendDate);
        detailModel.setIsSend(YesNoStatus.CANCEL.getCode());
        MessagePushDetailModel messagePushDetailModel = messagePushDetailService.getDetailList(detailModel);
        return messagePushDetailModel.getCount();
    }

    /**
     * 功能描述: 转换粉丝数
     *
     * @return
     * @Author: nqr
     * @Date 2021-01-18 10:25:41
     */

    private String convertFans(int num) {
        if (num >= 1000 && num < 1000000) {
            return String.format("%.1f", (num / 1000.0)) + 'K';
        }
        return String.valueOf(num);
    }

    /**
     * 通过excel导入网红数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importCelebrityExcel", method = RequestMethod.POST)
    public Result<?> importCelebrityExcel(HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        String userName = getUserNameByToken();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            try {
                InputStream inputStream = file.getInputStream();
                List<StoreCelebrityExcelImprotModel> storeCelebrityExcelImprotModelList = WorkBookUtil.getWorkbook(inputStream, StoreCelebrityExcelImprotModel.class);
                log.info("excel数据共有: " + storeCelebrityExcelImprotModelList.size() + "条, 获得excel数据共消耗时间: " + (System.currentTimeMillis() - start) + "毫秒");

                List<StoreCelebrity> updateBatchList = new ArrayList<>();
                List<StoreCelebrity> insertBatchList = new ArrayList<>();
//                long start1 = System.currentTimeMillis();
                final CountDownLatch countDownLatch = new CountDownLatch(5);
                if (storeCelebrityExcelImprotModelList.size() > 0) {
                    List<List<StoreCelebrityExcelImprotModel>> averageAssignList = averageAssign(storeCelebrityExcelImprotModelList, 5);
                    for (int i = 0, length = averageAssignList.size(); i < length; i++) {
                        List<StoreCelebrityExcelImprotModel> modelList = averageAssignList.get(i);
                        // CountDownLatch闭锁,确保线程已经全部执行完毕
                        Thread thread = new Thread(() -> {
                            try {
                                updateBatchList.addAll(((handleCelebrityData(modelList, userName)).get(0)));
                                insertBatchList.addAll(((handleCelebrityData(modelList, userName)).get(1)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                countDownLatch.countDown();
                            }
                        });
                        thread.start();
                    }
                    countDownLatch.await();
                }

                if (updateBatchList.size() > 0) {
                    storeCelebrityService.updateBatchById(updateBatchList);
                }
                if (insertBatchList.size() > 0) {
                    storeCelebrityService.saveBatch(insertBatchList);
                }
                log.info("数据处理完毕,共" + storeCelebrityExcelImprotModelList.size() + " 条记录,更新数据: " + updateBatchList.size() + " 条, 新增数据: " + insertBatchList.size() + " 条,共用时: " + (System.currentTimeMillis() - start) + "毫秒");
                return Result.ok("文件导入成功");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
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
     * 处理网红数据(根据username判断,存在则更新,不存在新增)
     */
    public List<List<StoreCelebrity>> handleCelebrityData(List<StoreCelebrityExcelImprotModel> storeCelebrityExcelImprotModelList, String uName) {
        long start = System.currentTimeMillis();
        List<List<StoreCelebrity>> resultList = new ArrayList<>();
        List<StoreCelebrity> updateBatchList = new ArrayList<>();
        List<StoreCelebrity> insertBatchList = new ArrayList<>();
        storeCelebrityExcelImprotModelList.stream().forEach(storeCelebrityExcelImprotModel -> {
            String userName = storeCelebrityExcelImprotModel.getUsername();
            String countryEnName = storeCelebrityExcelImprotModel.getCountry();
            String shortCode = countryService.queryByCountryEnName(countryEnName) != null ? countryService.queryByCountryEnName(countryEnName).getShortCode() : null;
            // 通过粉丝数计算星级、网红最低报价、网红最高报价
            Integer star;
            String kolPriceMin;
            String kolPriceMax;
            if (storeCelebrityExcelImprotModel.getFollowers() >= 100000) {
                star = 5;  // Enum useless
                kolPriceMin = "1000";
                kolPriceMax = "5000";
            } else if (10000 <= storeCelebrityExcelImprotModel.getFollowers() && storeCelebrityExcelImprotModel.getFollowers() < 100000) {
                star = 4;
                kolPriceMin = "100";
                kolPriceMax = "1000";
            } else if (5000 <= storeCelebrityExcelImprotModel.getFollowers() && storeCelebrityExcelImprotModel.getFollowers() < 10000) {
                star = 3;
                kolPriceMin = "100";
                kolPriceMax = "1000";
            } else {
                star = 3;
                kolPriceMin = "0";
                kolPriceMax = "100";
            }
            // 查询标签
            StringBuffer liketagsBuffer = new StringBuffer();
            if (oConvertUtils.isNotEmpty(storeCelebrityExcelImprotModel.getHashTag())) {
                String[] hashTagArray = storeCelebrityExcelImprotModel.getHashTag().split("#");
                for (String hashTag : hashTagArray) {
                    if (!"".equals(hashTag)) {
                        TagContrast tagContrast = tagContrastService.queryByTag(hashTag);
                        liketagsBuffer.append(oConvertUtils.isNotEmpty(tagContrast) ? tagContrast.getReplaceTag() : hashTag).append(",");
                    }
                }
            }
            // 通过判断userName判断是否存在该网红
            List<StoreCelebrity> storeCelebrityList = storeCelebrityService.queryByUserName(userName);
            if (storeCelebrityList != null && storeCelebrityList.size() > 0) {
                // 编辑操作
                for (StoreCelebrity storeCelebrity : storeCelebrityList) {
                    String id = storeCelebrity.getId();
                    updateBatchList.add(handleCelebrity(id, storeCelebrityExcelImprotModel, uName, shortCode, star, kolPriceMin, kolPriceMax, liketagsBuffer));
                }
            } else {
                // 新增操作
                insertBatchList.add(handleCelebrity(null, storeCelebrityExcelImprotModel, uName, shortCode, star, kolPriceMin, kolPriceMax, liketagsBuffer));
            }
        });
        resultList.add(updateBatchList);
        resultList.add(insertBatchList);
        return resultList;
    }


    /**
     * 封装数据对象
     */
    public StoreCelebrity handleCelebrity(String id, StoreCelebrityExcelImprotModel storeCelebrityExcelImprotModel, String uName, String shortCode, Integer star, String kolPriceMin, String kolPriceMax, StringBuffer liketagsBuffer) {
        StoreCelebrity storeCelebrity = new StoreCelebrity();
        // 如果Id不为空,为编辑对象,反之为新增对象
        if (oConvertUtils.isNotEmpty(id)) {
            storeCelebrity.setId(id);
            storeCelebrity.setUpdateTime(new Date());
        } else {
            storeCelebrity.setCreateBy(uName);
            storeCelebrity.setCreateTime(new Date());
        }
        storeCelebrity.setAccount(storeCelebrityExcelImprotModel.getUsername());
        storeCelebrity.setUsername(storeCelebrityExcelImprotModel.getUsername());
        storeCelebrity.setNickName(storeCelebrityExcelImprotModel.getFullName());
        storeCelebrity.setPhone(storeCelebrityExcelImprotModel.getPhoneNo());
        storeCelebrity.setEmail(storeCelebrityExcelImprotModel.getEmail());
        storeCelebrity.setWebsite(storeCelebrityExcelImprotModel.getWebsite());
        storeCelebrity.setPhone(storeCelebrityExcelImprotModel.getPhoneNo());
        storeCelebrity.setCategory(storeCelebrityExcelImprotModel.getCategory());
        storeCelebrity.setDescription(storeCelebrityExcelImprotModel.getBio());
        storeCelebrity.setPostsNum(storeCelebrityExcelImprotModel.getPostCount());
        storeCelebrity.setFollowersNum(storeCelebrityExcelImprotModel.getFollowers());
        storeCelebrity.setFollowingNum(storeCelebrityExcelImprotModel.getFollowing());
        storeCelebrity.setAvatarUrl(storeCelebrityExcelImprotModel.getProfilePicLink());
        storeCelebrity.setPlatformType(PlatformType.INSTAGRAM.getCode());
        storeCelebrity.setIsImport(YesNoStatus.YES.getCode());
        storeCelebrity.setCountry(shortCode);
        // 星级、网红最低报价、网红最高报价
        storeCelebrity.setStar(star);
        storeCelebrity.setKolPriceMin(kolPriceMin);
        storeCelebrity.setKolPriceMax(kolPriceMax);
        // 喜欢的标签
        storeCelebrity.setLikeTags(liketagsBuffer.length() == 0 ? liketagsBuffer.toString() : liketagsBuffer.toString().substring(0, liketagsBuffer.length() - 1));

        return storeCelebrity;
    }


    /**
     * 集合拆分工具
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  // 然后是商
        int offset = 0;// 偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }


    /**
     * 通过userName查询网红
     *
     * @param userName
     * @return
     */
    @AutoLog(value = "网红档案-通过userName查询网红")
@Operation(summary = "网红档案-通过userName查询网红", description = "网红档案-通过userName查询网红")
    @GetMapping(value = "/queryByUserName")
    public Result<?> queryByUserName(@RequestParam(name = "userName", required = true) String userName) {
        return Result.ok(storeCelebrityService.queryByUserName(userName));
    }
}
