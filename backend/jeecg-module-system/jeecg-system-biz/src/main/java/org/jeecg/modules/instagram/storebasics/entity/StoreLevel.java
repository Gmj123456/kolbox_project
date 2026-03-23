package org.jeecg.modules.instagram.storebasics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 功能描述:红人层级表
 *
 * @Author: nqr
 * @Date 2020-09-10 10:06:15
 */
@Data
@TableName("store_level")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreLevel {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "id")
    private String id;
    /**
     * 任务id
     */
    @Excel(name = "任务id", width = 15)
    @Schema(title =  "任务id")
    private String taskId;
    /**
     * 用户id
     */
    @Excel(name = "用户id", width = 15)
    @Schema(title =  "用户id")
    private String userId;
    /**
     * 关注列表用户id
     */
    @Excel(name = "关注列表用户id", width = 15)
    @Schema(title =  "关注列表用户id")
    private String followingId;
}
