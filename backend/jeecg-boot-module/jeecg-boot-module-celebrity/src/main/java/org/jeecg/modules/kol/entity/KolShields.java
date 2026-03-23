package org.jeecg.modules.kol.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 网红屏蔽名单表
 * @Author: dongruyang
 * @Date:   2025-10-18
 * @Version: V1.0
 */
@Data
@TableName("kol_shields")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="网红屏蔽名单表")
public class KolShields {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**唯一ID TK:author_id YT:account IG:account*/
	@Excel(name = "唯一ID TK:author_id YT:account IG:account", width = 15)
    @Schema(title =  "唯一ID TK:author_id YT:account IG:account")
	private String celebrityId;
	/**网红唯一ID*/
	@Excel(name = "网红唯一ID", width = 15)
    @Schema(title =  "网红唯一ID")
	private String uniqueId;
	/**平台类型（0:Instagram;1:YouTube;2:TikTok）*/
	@Excel(name = "平台类型（0:Instagram;1:YouTube;2:TikTok）", width = 15)
    @Schema(title =  "平台类型（0:Instagram;1:YouTube;2:TikTok）")
	private Integer platformType;
	/**网红顾问ID*/
	@Excel(name = "网红顾问ID", width = 15)
    @Schema(title =  "网红顾问ID")
	private String counselorId;
	/**网红顾问名称*/
	@Excel(name = "网红顾问名称", width = 15)
    @Schema(title =  "网红顾问名称")
	private String counselorName;
	/**网红顾问短名称*/
	@Excel(name = "网红顾问短名称", width = 15)
    @Schema(title =  "网红顾问短名称")
	private String counselorShortName;
	/**屏蔽时间*/
	@Excel(name = "屏蔽时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "屏蔽时间")
	private java.util.Date shieldTime;
	/**屏蔽原因*/
	@Excel(name = "屏蔽原因", width = 15)
    @Schema(title =  "屏蔽原因")
	private Object shieldReason;
	/**品牌ID*/
	@Excel(name = "品牌ID", width = 15)
    @Schema(title =  "品牌ID")
	private String brandId;
	/**品牌名称*/
	@Excel(name = "品牌名称", width = 15)
    @Schema(title =  "品牌名称")
	private String brandName;
	/**产品备ID*/
	@Excel(name = "产品备ID", width = 15)
    @Schema(title =  "产品备ID")
	private String productId;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @Schema(title =  "产品名称")
	private String productName;
	/**删除状态（0:未删除;1:已删除）*/
	@Excel(name = "删除状态（0:未删除;1:已删除）", width = 15)
    @Schema(title =  "删除状态（0:未删除;1:已删除）")
	private Integer isDelete;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
	private java.util.Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
	private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
	private java.util.Date updateTime;
}
