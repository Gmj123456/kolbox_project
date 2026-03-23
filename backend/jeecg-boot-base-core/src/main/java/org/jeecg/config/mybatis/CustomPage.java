package org.jeecg.config.mybatis;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/9/22 14:31
 * @Description:
 **/
public class CustomPage<T> extends Page<T> {
    private final long originalPageSize; // 原始 pageSize，用于 OFFSET 计算

    public CustomPage(long current, long originalPageSize, long querySize, HttpServletRequest req) {
        super(current, querySize); // 设置 size 为 pageSize + 1，用于 LIMIT
        this.originalPageSize = originalPageSize; // 保存原始 pageSize

        // 从 req 获取排序信息（复用 PageUtil.getOrderItems 逻辑）
        OrderItem orderItem = new OrderItem();
        String orderColumn = req.getParameter("orderColumn"); // 假设 PageUtil.orderColumn 是 "orderColumn"
        if (!StringUtils.isBlank(orderColumn)) {
            orderColumn = camelToUnderline(orderColumn); // 假设 oConvertUtils.camelToUnderline 的逻辑
            orderItem.setColumn(orderColumn);
            orderItem.setAsc(req.getParameter("orderType").equalsIgnoreCase("asc")); // 假设 PageUtil.orderAsc 是 "asc"
            List<OrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem);
            this.setOrders(orderItems);
        }
    }

    @Override
    public long offset() {
        // 重写 OFFSET 计算，使用原始 pageSize
        return (getCurrent() - 1) * originalPageSize;
    }

    // 模拟 oConvertUtils.camelToUnderline 方法
    private String camelToUnderline(String param) {
        if (StringUtils.isBlank(param)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < param.length(); i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}