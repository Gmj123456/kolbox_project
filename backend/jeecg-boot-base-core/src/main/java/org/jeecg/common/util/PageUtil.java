package org.jeecg.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页相关工具类
 */
public class PageUtil {

    public static String orderColumn = "column";
    public static String orderType = "order";
    public static String orderAsc = "asc";

    /**
     * 组合页面参数，包含处理排序字段
     *
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    public static <T> Page<T> getOrderItems(Integer pageNo, Integer pageSize, HttpServletRequest req) {
        Page<T> page = new Page<>(pageNo, pageSize);
        OrderItem orderItem = new OrderItem();
        String orderColumn = req.getParameter(PageUtil.orderColumn);
        if (StringUtils.isBlank(orderColumn)) {
            return page;
        }
        orderColumn = oConvertUtils.camelToUnderline(orderColumn);
        orderItem.setColumn(orderColumn);
        orderItem.setAsc(req.getParameter(PageUtil.orderType).equalsIgnoreCase(PageUtil.orderAsc));
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        page.setOrders(orderItems);
        return page;
    }

    /**
     * @description: 分页处理
     * @author: nqr
     * @date: 2025/9/4 10:02
     **/
    public static <T> IPage<T> processPagination(IPage<T> pageList, int pageNo, int pageSize) {
        boolean hasNext = false;
        List<T> records = pageList.getRecords();
        List<T> resultRecords = records;
        int actualPageSize = pageSize + 1; // actualPageSize 固定为 pageSize + 1

        if (records.size() == actualPageSize) {
            // 取出了 pageSize + 1 条记录 → 说明还有下一页
            hasNext = true;
            resultRecords = records.subList(0, pageSize); // 截取前 pageSize 条记录返回
        }

        int total;
        if (hasNext) {
            // 存在下一页 → 设置 total 为 5000
            total = 5000;
        } else {
            // 没有下一页 → 精确计算 total
            total = (pageNo - 1) * pageSize + resultRecords.size();
        }
        pageList.setSize(pageSize);
        pageList.setRecords(resultRecords);
        pageList.setTotal(total);
        return pageList;
    }
}
