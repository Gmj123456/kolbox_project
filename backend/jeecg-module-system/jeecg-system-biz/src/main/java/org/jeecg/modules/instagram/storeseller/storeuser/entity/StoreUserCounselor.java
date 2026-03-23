package org.jeecg.modules.instagram.storeseller.storeuser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * @Description: 商家顾问管理
 * @Author: nqr
 * @Date: 2023-09-14
 * @Version: V1.0
 */
@Data
@TableName("store_user_counselor")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreUserCounselor {

    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键id")
    private String id;
    /**
     * 登录账号
     */
    @Excel(name = "登录账号", width = 15)
    @Schema(title =  "登录账号")
    @NotBlank
    private String username;
    /**
     * 昵称
     */
    @NotBlank
    @Excel(name = "昵称", width = 15)
    @Schema(title =  "昵称")
    private String nickname;
    /**
     * 二维码
     */
    @NotBlank
    @Excel(name = "二维码", width = 15)
    @Schema(title =  "二维码")
    private String qrCode;
    /**
     * 手机号
     */
    @NotBlank
    @Excel(name = "手机号", width = 15)
    @Schema(title =  "手机号")
    private String phone;
    /**
     * 商务人员
     */
    @NotBlank
    @Excel(name = "商务人员", width = 15)
    @Schema(title =  "商务人员")
    private String sysUserId;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(title =  "备注")
    private String remark;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @Schema(title =  "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "创建时间")
    private java.util.Date createTime;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @Schema(title =  "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title =  "修改时间")
    private java.util.Date updateTime;
    /**
     * 商务人员id
     */
    @TableField(exist = false)
    private String counselorUserId;
    @TableField(exist = false)
    private String counselorUserName;
}
