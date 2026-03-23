package org.jeecg.modules.instagram.storetags.entity;

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
 * @Description: 标签
 * @Author: jeecg-boot
 * @Date:   2021-01-18
 * @Version: V1.0
 */
@Data
@TableName("tag_contrast")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TagContrast {
    
	/**tag*/
	@Excel(name = "tag", width = 15)
    @Schema(title =  "tag")
	private String tag;
	/**replaceTag*/
	@Excel(name = "replaceTag", width = 15)
    @Schema(title =  "replaceTag")
	private String replaceTag;
}
