package org.jeecg.modules.kol.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * @Description: Entity扩展基类
 * @Author: xyc
 * @Date: 2025-01-02 18:52:08
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExtendedEntity extends BaseEntity {

    /**
     * 删除状态（0=未删除,1=已删除）
     */
//    @TableLogic
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 15,orderNum = "5")
    @Schema(title =  "备注")
    private String remark;

}
