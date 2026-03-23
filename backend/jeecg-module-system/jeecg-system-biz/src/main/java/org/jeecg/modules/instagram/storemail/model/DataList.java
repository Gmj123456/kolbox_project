package org.jeecg.modules.instagram.storemail.model;

import lombok.Data;

@Data
public class DataList {
    private String requestNum;
    private String deliveredNum;
    private String invalidEmailsNum;
    private String deliveredPercent;
    private String invalidEmailsPercent;

    @Override
    public String toString() {
        return "{" +
                "requestNum:'" + requestNum + '\'' +
                ", deliveredNum:'" + deliveredNum + '\'' +
                ", invalidEmailsNum:'" + invalidEmailsNum + '\'' +
                ", deliveredPercent:'" + deliveredPercent + '\'' +
                ", invalidEmailsPercent:'" + invalidEmailsPercent + '\'' +
                '}';
    }
}
