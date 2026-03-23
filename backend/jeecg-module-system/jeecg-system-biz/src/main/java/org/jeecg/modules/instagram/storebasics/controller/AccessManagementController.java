package org.jeecg.modules.instagram.storebasics.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.NetworkUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storebasics.entity.AccessManagement;
import org.jeecg.modules.instagram.storebasics.entity.PromotionPlatform;
import org.jeecg.modules.instagram.storebasics.model.AccessManagementModel;
import org.jeecg.modules.instagram.storebasics.service.IAccessManagementService;
import org.jeecg.modules.instagram.storebasics.service.IPromotionPlatformService;
import org.jeecg.modules.instagram.utils.ToolHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 访问管理
 * @Author: jeecg-boot
 * @Date: 2021-01-23
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "访问管理")
@RestController
@RequestMapping("/accessManagement")
public class AccessManagementController extends JeecgController<AccessManagement, IAccessManagementService> {
    @Autowired
    private IAccessManagementService accessManagementService;

    @Autowired
    private IPromotionPlatformService promotionPlatformService;

    /**
     * 分页列表查询
     *
     * @param accessManagementModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "访问管理-分页列表查询")
    @Operation(summary = "访问管理-分页列表查询", description = "访问管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(AccessManagementModel accessManagementModel,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        Page<AccessManagement> page = new Page<>(pageNo, pageSize);
        IPage<AccessManagement> pageList;
        // 全部访问
        if (accessManagementModel.getType() == 0) {
            pageList = accessManagementService.pageAllList(page, accessManagementModel);
        } else {
            // 独立访问
            List<AccessManagement> aloneList = accessManagementService.getAloneList(accessManagementModel);
            List<String> idList = aloneList.stream().map(x -> x.getId()).collect(Collectors.toList());
            accessManagementModel.setIdList(idList);
            pageList = accessManagementService.pageAllList(page, accessManagementModel);
        }
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param accessManagement
     * @return
     */
    @AutoLog(value = "访问管理-添加")
    @Operation(summary = "访问管理-添加", description = "访问管理-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody AccessManagement accessManagement) {
        accessManagementService.save(accessManagement);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param accessManagement
     * @return
     */
    @AutoLog(value = "访问管理-编辑")
    @Operation(summary = "访问管理-编辑", description = "访问管理-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody AccessManagement accessManagement) {
        accessManagementService.updateById(accessManagement);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "访问管理-通过id删除")
    @Operation(summary = "访问管理-通过id删除", description = "访问管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        accessManagementService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "访问管理-批量删除")
    @Operation(summary = "访问管理-批量删除", description = "访问管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.accessManagementService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "访问管理-通过id查询")
    @Operation(summary = "访问管理-通过id查询", description = "访问管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        AccessManagement accessManagement = accessManagementService.getById(id);
        return Result.ok(accessManagement);
    }

    /**
     * 功能描述:统计数量
     *
     * @Author: nqr
     * @Date 2021-01-23 14:46:24
     */
    @GetMapping(value = "/getCount")
    public Result<?> getCount() {
        JSONObject jsonObject = new JSONObject();
        // 全部访问
        int allCount = accessManagementService.list().size();
        // 独立访问
        List<AccessManagement> aloneList = accessManagementService.getAloneList(new AccessManagementModel());
        jsonObject.put("allCount", allCount);
        jsonObject.put("aloneCount", aloneList.size());
        return Result.ok(jsonObject);
    }

    /**
     * 功能描述:获取推广二维码
     *
     * @Author: nqr
     * @Date 2021-01-23 10:06:49
     */
    @GetMapping(value = "/img")
    public ModelAndView getImg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 平台id
        String platformId = request.getParameter("id");
        String ipAddress = NetworkUtil.getIpAddress(request);
        // 地区
        String district = (String) ToolHttpClient.get("http://whois.pconline.com.cn/ip.jsp?ip=" + ipAddress, new HashMap<>());
        String path;
        String platformName = null;
        if (oConvertUtils.isNotEmpty(platformId)) {
            // 获取平台信息
            PromotionPlatform promotionPlatform = promotionPlatformService.getById(platformId);
            platformName = promotionPlatform.getName();
            path = "https://image.kolbox.com/" + promotionPlatform.getQrCode();
        } else {
            // 默认图片
            path = "https://image.kolbox.com/wx.jpg";
        }
        // 存储信息
        AccessManagement accessManagement = new AccessManagement();
        accessManagement.setIp(ipAddress);
        accessManagement.setCreateTime(new Date());
        accessManagement.setDistrict(district);
        if (oConvertUtils.isNotEmpty(platformId)) {
            accessManagement.setOriginId(platformId);
        }
        if (oConvertUtils.isNotEmpty(platformName)) {
            accessManagement.setOrigin(platformName);
        }
        accessManagementService.save(accessManagement);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("qrcode");
        modelAndView.addObject("imageUrl", path);
        return modelAndView;
        /*File file = new File(path);
        byte[] fileByte = Files.readAllBytes(file.toPath());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(fileByte);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            ServletOutputStream out = response.getOutputStream();
            output.writeTo(out);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }*/
    }
}
