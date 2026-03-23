package org.jeecg.modules.instagram.storepromotion.model;

import lombok.Data;
import org.jeecg.modules.system.entity.SysUser;

@Data
public class InviteNewRecordMenuMode extends SysUser {

    /**
     * 邀请新人开始时间
     * */
    private String startCreateTime;
    /**
     * 邀请新人结束时间
     * */
    private String endCreateTime;
}
