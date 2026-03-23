package org.jeecg.common.constant.enums;

/**
 * 功能描述: KOLBOX微信公众号账号
 *
 * @Author: nqr
 * @Date 2021-04-28 09:47:28
 */
@SuppressWarnings("ALL")
public enum WXType {
    // 开发者ID
    APPID(0, "wxacaf881e7577dd0e"),
    // 开发者密码
    SECRET(1, "b38af142d843a143fcad07a01f38c88e"),
    // 监控模板消息ID
    MONITORING_ID(2, "L1_sjW8bxWoYVkZIbNrpQniN37-2wCzuWni8HsK2_t8"),
    // 绑定账户模板消息ID
    BIND_SUCCESS_ID(3, "WcXjppMhoyPd1ZafKdLVg7Aaxd9jOBNyScXLx254Ndk"),
    // wechat 回调字段
    ECHOSTR(4, "echostr"),
    MSGTYPE(5, "MsgType"),
    EVENT_K(6, "Event"),
    EVENT_V(7, "event"),
    SUBSCRIBE(8, "subscribe"),
    FROMUSERNAME(9, "FromUserName"),
    EVENTKEY(10, "EventKey"),
    TOUSERNAME(11, "ToUserName"),
    MSGID(12, "MsgId"),
    CREATETIME(13, "CreateTime"),
    // 绑定失败模板ID
    BIND_ERROR_ID(14, "emguM_izZ-yui69IAOixKlmKS4MAVo1SxRn7OZQbsKI"),
    // 解绑通知
    UNBIND_ID(15, "eCnb3xYM2sEGg-Rw3MfRxyvUUj6Mn_gvlqIqX53Fd9k"),
    // 取消关注
    UNSUBSCRIBE(16, "unsubscribe"),

    DUE_REMINDER_ID(17, "DF_Fil8Do8A24oegC7uivItPFFUndIMoXxoFRal_1PU");

    private final int code;
    private final String info;

    WXType(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
