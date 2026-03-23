package org.jeecg.modules.instagram.storecelebrity.entity;

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
 * @Description: 网红收获地址信息
 * @Author: jeecg-boot
 * @Date:   2020-10-13
 * @Version: V1.0
 */
@Data
@TableName("store_celebrity_consignee")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CelebrityConsignee {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
	private String id;
	/**网红ID*/
	@Excel(name = "网红ID", width = 15)
    @Schema(title =  "网红ID")
	private String celebrityId;
	/** 国家ID*/
	@Excel(name = " 国家ID", width = 15)
    @Schema(title =  " 国家ID")
	private String countryId;
	/** 国家名称*/
	@Excel(name = " 国家名称", width = 15)
    @Schema(title =  " 国家名称")
	private String countryName;
	/** 收件人姓名*/
	@Excel(name = " 收件人姓名", width = 15)
    @Schema(title =  " 收件人姓名")
	private String fullname;
	/** 联系电话*/
	@Excel(name = " 联系电话", width = 15)
    @Schema(title =  " 联系电话")
	private String mobile;
	/** 街道门牌号*/
	@Excel(name = " 街道门牌号", width = 15)
    @Schema(title =  " 街道门牌号")
	private String address;
	/**街道门牌号*/
	@Excel(name = "街道门牌号", width = 15)
    @Schema(title =  "街道门牌号")
	private String address2;
	/** 市*/
	@Excel(name = " 市", width = 15)
    @Schema(title =  " 市")
	private String city;
	/** 省*/
	@Excel(name = " 省", width = 15)
    @Schema(title =  " 省")
	private String province;
	/**邮政编码*/
	@Excel(name = "邮政编码", width = 15)
    @Schema(title =  "邮政编码")
	private String zipCode;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
	private String remark;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
	private Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
	private String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
	private Date updateTime;
}
