package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.MessageTemplate;

import java.util.List;
import java.util.Set;

@Data
public class MessageTemplateModel extends MessageTemplate {
    private String accountId;
    private String toEmail;
    private String emailTitle;
    private String emailTextContent;
    private String emailRichContent;
    /**
     * 语法检测更改的字段
     * */
    private String templateRichContentText;

    private Set<String>  errText;
    private List<String> textList;

    private Integer textLen;

    /**
     * 商家顾问Id
     * */
    private String sellerCounselorId;

}
