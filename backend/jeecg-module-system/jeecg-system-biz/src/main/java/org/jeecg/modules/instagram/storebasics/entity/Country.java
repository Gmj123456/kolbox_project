package org.jeecg.modules.instagram.storebasics.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 国家表
 * @Author: jeecg-boot
 * @Date:   2021-01-18
 * @Version: V1.0
 */
@Data
@TableName("country")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Country {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private Integer id;
	/**国家名称*/
	@Excel(name = "国家名称", width = 15)
    @Schema(title =  "国家名称")
	private String country;
	/**英文简称*/
	@Excel(name = "英文简称", width = 15)
    @Schema(title =  "英文简称")
	private String shortCode;
	/**国家英文名称*/
	@Excel(name = "国家英文名称", width = 15)
    @Schema(title =  "国家英文名称")
	private String countryEnName;
}
