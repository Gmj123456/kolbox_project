package org.jeecg.modules.instagram.storebasics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.modules.instagram.storebasics.entity.AccessManagement;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: 访问管理
 * @Author: jeecg-boot
 * @Date: 2021-01-23
 * @Version: V1.0
 */
@Data
public class AccessManagementModel extends AccessManagement {

    /**
     * startCreateTime
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startCreateTime;

    /**
     * endCreateTime
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endCreateTime;

    /**
     * type 0 全部  1独立
     */
    private Integer type;
    private List<String> idList;
}
