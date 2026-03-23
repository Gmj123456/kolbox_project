package org.jeecg.modules.instagram.utils;

import com.alibaba.fastjson.JSONObject;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.jeecg.common.constant.enums.WXType;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserRemind;
import org.jeecg.common.util.DateUtils;
import java.util.HashMap;
import java.util.List;

/**
 * 功能描述:微信推送
 *
 * @Date 2021-04-28 09:04:24
 */
public class PushUtils {
    /**
     * 功能描述: 获取access_token
     *
     * @return String
     * @Date 2021-04-28 09:44:40
     */
    public static String getAccessToken() {
        /*
         * {
         * "access_token": "44_DcSIaWrXFltUcRtv28IRUrsqVj7l1XYVRvjtkarrNKMSKeAd8tqNVfY9u0GMnSfSl_NBUf-Sp-oMNg_fX7qheRsA8c4z3f888NuKwIWWfVRMed0m10-Is4IQfb0lswQjrUDiKALxjStHi_6UMNSfAFAZAF",
         * "expires_in": 7200
         * }
         */
        RedisUtil redisUtil = new RedisUtil();
        String accessToken = "";
        //判断redis中是否存在access_token
        if (redisUtil.hasKey("wx_access_token")) {
            //存在
            accessToken = (String) redisUtil.get("wx_access_token");
        } else {
            //不存在,获取access_token
            JSONObject jsonObject = (JSONObject) ToolHttpClient.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WXType.APPID.getInfo() + "&secret=" + WXType.SECRET.getInfo());
            if (oConvertUtils.isNotEmpty(jsonObject)) {
                accessToken = jsonObject.getString("access_token");
                //添加到redis中
                redisUtil.set("wx_access_token", accessToken, 7100);
            }
        }
        return accessToken;
    }

    /**
     * 功能描述: 获取二维码
     *
     * @Date 2021-04-28 10:20:34
     */
    public static String getQrCode(String userId) {
        /*
         * {
         *      "action_name": "QR_LIMIT_STR_SCENE", //永久二维码
         *      "action_info": {
         *          "scene": {
         *              "scene_str": 用户id
         *           }
         *       }
         * }
         */
        String qrCode = "";
        //构造请求体
        JSONObject json = new JSONObject();
        JSONObject infoJson = new JSONObject();
        JSONObject strJson = new JSONObject();
        //设置参数为用户ID
        strJson.put("scene_str", userId);
        infoJson.put("scene", strJson);
        json.put("action_name", "QR_LIMIT_STR_SCENE");
        json.put("action_info", infoJson);
        //获取access_token
        String accessToken = getAccessToken();
        //判断当access_token不为空时，获取二维码连接
        if (oConvertUtils.isNotEmpty(accessToken)) {
            String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;
            String response = String.valueOf(ToolHttpClient.postJson(url, json.toJSONString()));
            if (oConvertUtils.isNotEmpty(response)) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                String ticket = jsonObject.getString("ticket");
                //构造二维码链接
                qrCode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
            }
        }
        return qrCode;
    }

    /**
     * 功能描述:微信公众号消息推送
     *
     * @param list      需要推送的用户列表
     * @param messageId 模板id
     * @return void
     * @Date 2021-04-29 12:00:19
     */
    public static void push(List<StoreUserRemind> list, String messageId) {
        //推送消息
        for (StoreUserRemind storeUserRemind : list) {
            WxMpInMemoryConfigStorage wxStorage = new WxMpInMemoryConfigStorage();
            //设置app_id和secret
            wxStorage.setAppId(WXType.APPID.getInfo());
            wxStorage.setSecret(WXType.SECRET.getInfo());
            WxMpService wxMpService = new WxMpServiceImpl();
            wxMpService.setWxMpConfigStorage(wxStorage);
            /*
             * toUser      要推送的用户openid
             * templateId  模版id
             * url         点击模版消息要访问的网址
             */
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                    .toUser(storeUserRemind.getMpOpenId())
                    .templateId(messageId)
                    .url("")
                    .build();
            //判断是否是监控提醒
            if (messageId.equals(WXType.MONITORING_ID.getInfo())) {
                templateMessage.addData(new WxMpTemplateData("first", "KOLBOX平台监控任务通知", "#000"));
                templateMessage.addData(new WxMpTemplateData("keyword1", storeUserRemind.getPushWxContent(), "#000"));
                templateMessage.addData(new WxMpTemplateData("keyword2", DateUtils.getNowDate(), "#000"));
                templateMessage.addData(new WxMpTemplateData("remark", "", "#000"));
            } else if (messageId.equals(WXType.BIND_SUCCESS_ID.getInfo())) {
                //账号绑定成功提醒
                templateMessage.addData(new WxMpTemplateData("first", "绑定成功", "#000"));
                templateMessage.addData(new WxMpTemplateData("keyword1", storeUserRemind.getUserName(), "#000"));
                templateMessage.addData(new WxMpTemplateData("keyword2", DateUtils.getNowDate(), "#000"));
                templateMessage.addData(new WxMpTemplateData("keyword3", "您可以登录KOLBOX后台进行查看并设置监控任务提醒。", "#000"));
                templateMessage.addData(new WxMpTemplateData("remark", "", "#000"));
            } else if (messageId.equals(WXType.BIND_ERROR_ID.getInfo())) {
                //账号绑定失败提醒
                templateMessage.addData(new WxMpTemplateData("first", "绑定失败", "#000"));
                templateMessage.addData(new WxMpTemplateData("keyword1", DateUtils.getNowDate(), "#000"));
                templateMessage.addData(new WxMpTemplateData("keyword2", "该账号已绑定微信号，请勿重复绑定！", "#ee3131"));
                templateMessage.addData(new WxMpTemplateData("remark", "", "#000"));
            } else if (messageId.equals(WXType.UNBIND_ID.getInfo())) {
                //账号解绑提醒
                templateMessage.addData(new WxMpTemplateData("first", "账号解绑通知", "#000"));
                templateMessage.addData(new WxMpTemplateData("keyword1", storeUserRemind.getUserName(), "#000"));
                templateMessage.addData(new WxMpTemplateData("keyword2", "您已成功解绑此微信，如想继续接收微信提醒请重新绑定。", "#000"));
                templateMessage.addData(new WxMpTemplateData("remark", "", "#000"));
            }
            try {
                wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            } catch (Exception e) {
                System.out.println(storeUserRemind.getMpOpenId());
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
