package org.jeecg.modules.instagram.storecelebrity.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Excel导入网红模板类
 * */
@Data
public class StoreCelebrityExcelImprotModel{

    /**
     * 用户名
     * */
    @ExcelProperty(value = "Username")
    private String username;

    /**
     * 全名
     * */
    @ExcelProperty(value = "Full Name")
    private String fullName;

    /**
     * 电话号码
     * */
    @ExcelProperty(value = "Phone No.")
    private String phoneNo;

    /**
     * 电子邮箱
     * */
    @ExcelProperty(value = "Email")
    private String email;

    /**
     * 网站
     * */
    @ExcelProperty(value = "Website")
    private String website;

    /**
     * 种类
     * */
    @ExcelProperty(value = "Category")
    private String category;

    /**
     * 个人经历
     * */
    @ExcelProperty(value = "Bio")
    private String bio;

    /**
     * 语言
     * */
    @ExcelProperty(value = "Language")
    private String language;

    /**
     * 帖子数
     * */
    @ExcelProperty(value = "Post Count")
    private Integer postCount;

    /**
     * 粉丝数
     * */
    @ExcelProperty(value = "Followers")
    private Long followers;

    /**
     * 关注数
     * */
    @ExcelProperty(value = "Following")
    private Long following;

    /**
     * 是否私有
     * */
    @ExcelProperty(value = "Is Private")
    private String isPrivate;

    /**
     * 是否核实
     * */
    @ExcelProperty(value = "Is Verified")
    private String isVerified;

    /**
     * 是否商务
     * */
    @ExcelProperty(value = "Is Business")
    private String isBusiness;

    /**
     * 商务潜质
     */
    @ExcelProperty(value = "Business Potential")
    private String businessPotential;

    /**
     * 国家
     * */
    @ExcelProperty(value = "Country")
    private String country;

    /**
     * 城市
     * */
    @ExcelProperty(value = "City")
    private String city;

    /**
     * 邮政编码
     * */
    @ExcelProperty(value = "Zip Code")
    private String zipCode;

    /**
     * 地址
     * */
    @ExcelProperty(value = "Address")
    private String address;

    /**
     * 头像
     */
    @ExcelProperty(value = "Profile Pic Link")
    private String profilePicLink;

    /**
     * 推文话题
     */
    @ExcelProperty(value = "Hashtag")
    private String hashTag;

    /**
     * 推文地址
     */
    @ExcelProperty(value = "Hashtag Post")
    private String hashTagPost;

}
