package org.jeecg.modules.instagram.storecelebrity.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.MessagePushDetail;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class DetailsMerchantEmailModel extends MessagePushDetail {



        /**
        * 头像
        */
        @Excel(name = "头像", width = 15)
        @Schema(title =  "头像")
        private String avatarUrl;
        /**
        * 国家
        */
        @Excel(name = "国家", width = 15)
        @Schema(title =  "国家")
        private String country;



        /**
        *额度来源 private String packageId;
        * */
        /**
        * 账号
        */
        @Excel(name = "账号", width = 15)
        @Schema(title =  "账号")
        private String account;

        /**
        * 模板名称
        */
        @Excel(name = "模板名称", width = 15)
        @Schema(title =  "模板名称")
        private String templateName;
        /**
        * 创建时间
        */
        @Excel(name = "创建时间", width = 15)
        @Schema(title =  "创建时间")
        private String createDate;
        /**
        * 发送时间
        */
        @Excel(name = "发送时间", width = 15)
        @Schema(title =  "发送时间")
        private String emailPushTime;

        /**
         * 发送日期开始时间
         */
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private String startSendTime;
        /**
         * 发送日期结束时间
         */
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private String endSendTime;

        private Integer userType; //是否会员

        /*=1、email_push_count>0）*/
}
