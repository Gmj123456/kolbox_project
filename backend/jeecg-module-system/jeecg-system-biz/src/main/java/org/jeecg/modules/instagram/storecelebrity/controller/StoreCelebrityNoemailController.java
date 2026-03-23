package org.jeecg.modules.instagram.storecelebrity.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrity;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityService;
import org.jeecg.modules.instagram.storecelebrity.entity.StoreCelebrityNoemail;
import org.jeecg.modules.instagram.storecelebrity.service.IStoreCelebrityNoemailService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @Description: youtube无邮箱数据
 * @Author: jeecg-boot
 * @Date: 2021-03-13
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "youtube无邮箱数据")
@RestController
@RequestMapping("/ytnoemail")
public class StoreCelebrityNoemailController extends JeecgController<StoreCelebrityNoemail, IStoreCelebrityNoemailService> {
    @Autowired
    private IStoreCelebrityNoemailService storeCelebrityNoemailService;
    @Autowired
    private IStoreCelebrityService storeCelebrityService;

    /**
     * 分页列表查询
     *
     * @param storeCelebrityNoemail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "youtube无邮箱数据-分页列表查询")
@Operation(summary = "youtube无邮箱数据-分页列表查询", description = "youtube无邮箱数据-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StoreCelebrityNoemail storeCelebrityNoemail,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        LambdaQueryWrapper<StoreCelebrityNoemail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
         Page<StoreCelebrityNoemail> page = new Page<StoreCelebrityNoemail>(pageNo, pageSize);
        IPage<StoreCelebrityNoemail> pageList = storeCelebrityNoemailService.page(page, lambdaQueryWrapper);
        return Result.ok(pageList);
    }


    /**
     * 添加
     *
     * @param storeCelebrityNoemail
     * @return
     */
    @AutoLog(value = "youtube无邮箱数据-添加")
@Operation(summary = "youtube无邮箱数据-添加", description = "youtube无邮箱数据-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StoreCelebrityNoemail storeCelebrityNoemail) {
        storeCelebrityNoemailService.save(storeCelebrityNoemail);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storeCelebrityNoemail
     * @return
     */
    @AutoLog(value = "youtube无邮箱数据-编辑")
@Operation(summary = "youtube无邮箱数据-编辑", description = "youtube无邮箱数据-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StoreCelebrityNoemail storeCelebrityNoemail) {
        storeCelebrityNoemailService.updateById(storeCelebrityNoemail);
        return Result.ok("编辑成功!");
    }


    /**
     * 编辑
     *
     * @param storeCelebrityNoemail
     * @return
     */
    @AutoLog(value = "youtube无邮箱数据-编辑")
@Operation(summary = "youtube无邮箱数据-编辑", description = "youtube无邮箱数据-编辑")
    @PutMapping(value = "/editEmail")
    public Result<?> editEmail(@RequestBody StoreCelebrityNoemail storeCelebrityNoemail) {
        //表示删除
        storeCelebrityNoemail.setDelFlag(1);
        storeCelebrityNoemailService.updateById(storeCelebrityNoemail);
        // 查询网红详细数据
        StoreCelebrityNoemail storeCelebrityNoEmailDetail =  storeCelebrityNoemailService.getById(storeCelebrityNoemail.getId());
        StoreCelebrity storeCelebrity = new StoreCelebrity();
        BeanUtils.copyProperties(storeCelebrityNoEmailDetail,storeCelebrity);
        storeCelebrity.setFollowersNum(storeCelebrityNoEmailDetail.getFollowersNum().longValue());
        storeCelebrity.setFollowingNum(storeCelebrityNoEmailDetail.getFollowingNum().longValue());
        storeCelebrity.setPlayAvgNum(storeCelebrityNoEmailDetail.getPlayAvgNum().longValue());

        //查询该网红是否存在
        LambdaQueryWrapper<StoreCelebrity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCelebrity::getAccount,storeCelebrity.getAccount());
        StoreCelebrity storeCelebrityIsExit = storeCelebrityService.getOne(lambdaQueryWrapper);
        if (!oConvertUtils.isNotEmpty(storeCelebrityIsExit)){
            // 新增
            storeCelebrity.setId(null);
            storeCelebrityService.save(storeCelebrity);
        }
        return Result.ok("编辑成功!");
    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "youtube无邮箱数据-通过id删除")
@Operation(summary = "youtube无邮箱数据-通过id删除", description = "youtube无邮箱数据-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        storeCelebrityNoemailService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "youtube无邮箱数据-批量删除")
@Operation(summary = "youtube无邮箱数据-批量删除", description = "youtube无邮箱数据-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.storeCelebrityNoemailService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "youtube无邮箱数据-通过id查询")
@Operation(summary = "youtube无邮箱数据-通过id查询", description = "youtube无邮箱数据-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StoreCelebrityNoemail storeCelebrityNoemail = storeCelebrityNoemailService.getById(id);
        return Result.ok(storeCelebrityNoemail);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storeCelebrityNoemail
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StoreCelebrityNoemail storeCelebrityNoemail) {
        return super.exportXls(request, storeCelebrityNoemail, StoreCelebrityNoemail.class, "youtube无邮箱数据");
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
        return super.importExcel(request, response, StoreCelebrityNoemail.class);
    }

    @GetMapping(value = "/getList")
    public Result<?> getList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                             @RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize, HttpServletRequest req) {
        LambdaQueryWrapper<StoreCelebrityNoemail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCelebrityNoemail::getEmail, "");
        lambdaQueryWrapper.orderByDesc(StoreCelebrityNoemail::getFollowersNum);
        IPage<StoreCelebrityNoemail> page = storeCelebrityNoemailService.page(PageUtil.getOrderItems(pageNo, pageSize, req), lambdaQueryWrapper);
        return Result.ok(page);
    }

    @GetMapping(value = "/updateKolDataById")
    public Result<?> updateKolDataById(JSONObject jsonObject) {
        String id = jsonObject.getString("id");
        String email = jsonObject.getString("email");
        LambdaUpdateWrapper<StoreCelebrityNoemail> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(StoreCelebrityNoemail::getId, id);
        lambdaUpdateWrapper.set(StoreCelebrityNoemail::getEmail, email);
        storeCelebrityNoemailService.update(lambdaUpdateWrapper);
        return Result.ok("修改成功！");
    }


}
