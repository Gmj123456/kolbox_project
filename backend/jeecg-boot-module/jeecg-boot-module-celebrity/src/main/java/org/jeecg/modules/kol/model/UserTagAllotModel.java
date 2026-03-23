package org.jeecg.modules.kol.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @Description: 用户（网红顾问）标签网红分配视图模型
 * @Author: xyc
 * @Date: 2024年12月7日 16:54:10
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserTagAllotModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 网红tag表主键id
     */
    private String id;
    /**
     * 网红账户id
     */
    private String account;
    /**
     * 网红账户id
     */
    private String uniqueId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签类型
     * tag类型：0=hashtag 1=keyword 2=rootVideo 3=rootAccount
     */
    private Integer tagType;

    /**
     * 私有网红Id
     */
    private String celebrityPrivateId;

    /**
     * 粉丝数
     */
    private BigInteger authorFollowerCount;

    /**
     * 国家简码
     */
    private String country;

    /**
     * 网红顾问_ID
     */
    private String counselorId;
    /**
     * 网红顾问名称
     */
    private String counselorName;

    private String authorId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contractTime;
}
