package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

import java.util.Date;

@Data
public class AnswerEmail {
    /**
     * 标题
     */
    private String title;
    /**
     * 概要
     */
    private String summary;
    /**
     * 修改时间
     */
    private Date modified;
    /**
     * 发送时间
     */
    private String issued;
    /**
     * 邮件ID
     */
    private String id;
    /**
     * 邮件发送人，发送邮箱
     */
    private AuthorModel author;

    @Override
    public String toString() {
        return "AnswerEmail{" +
                "authorJson=" + author.toString() +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", modified='" + modified + '\'' +
                ", issued='" + issued + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
