package org.jeecg.modules.kol.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

/**
 * @Description: 网红品牌开发表-重命名
 * @Author: xyc
 * @Date: 2024年12月4日 14:10:41
 * @Version: V1.0
 */
@Data
@TableName("kol_contact")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KolContact {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(title =  "主键")
    private String id;
    /**
     * 唯一id
     */
    @Excel(name = "网红账号", width = 15)
    @Schema(title =  "网红账号")
    private String uniqueId;
    /**
     * 品牌ID
     */
    @Excel(name = "品牌ID", width = 15)
    @Schema(title =  "品牌ID")
    private String brandId;
    /**
     * 品牌名称
     */
    @Excel(name = "品牌", width = 15)
    @Schema(title =  "品牌")
    private String brandName;
    /**
     * 产品ID
     */
    @Excel(name = "产品ID", width = 15)
    @Schema(title =  "品牌ID")
    private String productId;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称", width = 15)
    @Schema(title =  "产品名称")
    private String productName;
    /**
     * Tag名称
     */
    @Excel(name = "Tag", width = 15)
    @Schema(title =  "Tag")
    private String tagName;
    /**
     * 网红顾问_ID
     */
    @Excel(name = "网红顾问_ID", width = 15)
    @Schema(title =  "网红顾问_ID")
    private String CounselorId;
    /**
     * 网红顾问名称
     */
    @Excel(name = "网红顾问", width = 15)
    @Schema(title =  "网红顾问")
    private String CounselorName;
    /**
     * 网红顾问短名称
     */
    @Excel(name = "网红顾问短名称", width = 15)
    @Schema(title =  "网红顾问短名称")
    private String CounselorShortName;

    /**
     * 开发日期
     */
    @Excel(name = "开发日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(title =  "开发日期")
    private Date contactDate;
    /**
     * 是否回复（0=未回复,1=已回复）
     */
    @Excel(name = "是否回复（0=未回复,1=已回复）", width = 15)
    @Schema(title =  "是否回复（0=未回复,1=已回复）")
    private Integer isReply;
    /**
     * 建联方式（0=邮件，1=私信，9=其他）
     */
    @Excel(name = "建联方式（0=邮件，1=私信，9=其他）", width = 15)
    @Schema(title =  "建联方式（0=邮件，1=私信，9=其他）")
    private Integer contactWay;

    /**
     * 开发建联标记时的邮箱
     */
    @Excel(name = "开发建联标记时的邮箱", width = 15)
    @Schema(title =  "开发建联标记时的邮箱")
    private String kolEmail;
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
    private Date createTime;
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
    private Date updateTime;
    /**
     * 删除状态（0=未删除,1=已删除）
     */
    @Excel(name = "删除状态（0=未删除,1=已删除）", width = 15)
    @Schema(title =  "删除状态（0=未删除,1=已删除）")
    private Integer isDelete;
    /**
     * 平台类型（0:IG;1:YT;2:TK）
     */
    @Excel(name = "平台类型", width = 15)
    @Schema(title =  "平台类型")
    private Integer platformType;

    private String celebrityId;

    /**
     * 发送邮件主表ID
     */
    @Excel(name = "发送邮件主表ID", width = 15)
    @Schema(title =  "发送邮件主表ID")
    private String emailPushMainId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        KolContact that = (KolContact) object;
        return Objects.equals(uniqueId, that.uniqueId) && Objects.equals(brandId, that.brandId) && Objects.equals(productId, that.productId) && Objects.equals(celebrityId, that.celebrityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId, brandId, productId, celebrityId);
    }
}
