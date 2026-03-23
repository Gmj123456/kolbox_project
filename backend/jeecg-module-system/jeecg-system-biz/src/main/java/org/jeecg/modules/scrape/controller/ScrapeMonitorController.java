package org.jeecg.modules.scrape.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.scrape.DTO.ScrapeMonitorDTO;
import org.jeecg.modules.scrape.entity.ScrapeMonitor;
import org.jeecg.modules.scrape.service.IScrapeMonitorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/scrapemonitor")
@Tag(name = "爬虫监控管理")
public class ScrapeMonitorController extends JeecgController<ScrapeMonitor, IScrapeMonitorService> {

    @Autowired
    private IScrapeMonitorService scrapeMonitorService;

    @AutoLog(value = "分页列表查询")
    @Operation(summary = "分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(
            ScrapeMonitor scrapeMonitor,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
            HttpServletRequest req) {

        QueryWrapper<ScrapeMonitor> queryWrapper = new QueryWrapper<>();

        // 条件1：爬虫名称模糊查询
        if (StringUtils.isNotBlank(scrapeMonitor.getCrawlerName())) {
            queryWrapper.like("crawler_name", scrapeMonitor.getCrawlerName());
        }
        // 条件2：设备编码精确匹配
        if (StringUtils.isNotBlank(scrapeMonitor.getDeviceCode())) {
            queryWrapper.like("device_code", scrapeMonitor.getDeviceCode());
        }
        // 条件3：任务状态匹配
        if (scrapeMonitor.getTaskStatus() != null) {
            queryWrapper.eq("task_status", scrapeMonitor.getTaskStatus());
        }

        // 当前查询条件处理正确，无需修改
        if (StringUtils.isNotBlank(scrapeMonitor.getPlatformType())) {
            switch (scrapeMonitor.getPlatformType()) {
                case "0":
                    queryWrapper.like("crawler_name", "ins");
                    break;
                case "1":
                    queryWrapper.like("crawler_name", "yt");
                    break;
                case "2":
                    queryWrapper.like("crawler_name", "tk");
                    break;
                default:
                    log.warn("无效的 platformType 参数: {}", scrapeMonitor.getPlatformType());
            }
        }

        queryWrapper.orderByDesc("create_time");

        Page<ScrapeMonitor> page = new Page<>(pageNo, pageSize);
        IPage<ScrapeMonitor> pageList = scrapeMonitorService.page(page, queryWrapper);

        // 将 ScrapeMonitor 转换为 ScrapeMonitorDTO，并附加 platform 字段
        List<ScrapeMonitorDTO> dtoList = pageList.getRecords().stream().map(item -> {
            ScrapeMonitorDTO dto = new ScrapeMonitorDTO();
            // 复制属性，假设此处使用的是 Spring 的 BeanUtils 或其它转换工具
            BeanUtils.copyProperties(item, dto);
            dto.setPlatformType(getPlatformTypeFromCrawlerName(item.getCrawlerName()));
            return dto;
        }).collect(Collectors.toList());

        // 构造DTO分页对象
        Page<ScrapeMonitorDTO> dtoPage = new Page<>(pageList.getCurrent(), pageList.getSize(), pageList.getTotal());
        dtoPage.setRecords(dtoList);

        return Result.ok(dtoPage);
    }

    // 根据crawlerName解析platformType值
    private String getPlatformTypeFromCrawlerName(String crawlerName) {
        if (StringUtils.isNotBlank(crawlerName)) {
            String lower = crawlerName.toLowerCase();
            if (lower.contains("tk")) {
                return "2";
            } else if (lower.contains("yt")) {
                return "1";
            } else if (lower.contains("ins")) {
                return "0";
            }
        }
        return null;
    }




    // 状态切换接口（修改返回值类型）
    @AutoLog(value = "切换提醒状态")
    @Operation(summary = "切换提醒状态")
    @PostMapping("/reminder")
    public Result<Object> toggleReminder(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        ScrapeMonitor entity = scrapeMonitorService.getById(id);
        if (entity == null) {
            log.error("未找到对应记录，ID: {}", id); // 添加日志以便调试
            return Result.error("找不到对应记录");
        }
        entity.setIsReminder(1 - entity.getIsReminder());
        String username = "system";
        try {
            username = SecurityUtils.getSubject().getPrincipal().toString();
        } catch (Exception ignored) {}
        entity.setUpdateBy(username);
        entity.setUpdateTime(new Date());

        boolean updated = scrapeMonitorService.updateById(entity);
        if (!updated) {
            log.error("更新提醒状态失败，ID: {}", id);
            return Result.error("状态切换失败");
        }
        return Result.ok("状态切换成功");
    }

    // 删除接口（修改返回值类型）
    @AutoLog(value = "删除记录")
    @Operation(summary = "删除记录")
    @DeleteMapping(value = "/delete")
    public Result<Object> delete(@RequestParam(name = "id") String id) {  // 修改此处
        scrapeMonitorService.removeById(id);
        return Result.ok("删除成功");  // 现在类型匹配
    }
}
