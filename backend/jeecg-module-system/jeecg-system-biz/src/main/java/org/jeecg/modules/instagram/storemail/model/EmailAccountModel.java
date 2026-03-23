package org.jeecg.modules.instagram.storemail.model;

import lombok.Data;
import org.jeecg.modules.instagram.storemail.entity.Emailaccount;

@Data
public class EmailAccountModel extends Emailaccount {
    private String idToken;
    private String code;
    private String state;
}
