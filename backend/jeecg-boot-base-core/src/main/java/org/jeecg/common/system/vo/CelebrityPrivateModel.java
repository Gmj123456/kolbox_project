package org.jeecg.common.system.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 私有网红表
 * @Author: jeecg-boot
 * @Date: 2020-11-11
 * @Version: V1.0
 */
@Data
public class CelebrityPrivateModel {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(title = "主键")
    private String id;
    /**
     * 账号
     */
    @Excel(name = "账号", width = 20)
    @Schema(title = "账号")
    private String account;
}




