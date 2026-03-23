package org.jeecg.modules.system.util;

import com.sun.mail.util.MailSSLSocketFactory;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storecelebrity.entity.MessageTemplate;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

public class MailUtils {
    //    // 登录账户
//    private static String account = "zingdeal@upinghui.cn";
//    // 登录密码
//    private static String password = "Fanmeng435";
    // 登录账户
//    private static String account = "kolbox@torpedocross.com";
    //private static String account = "yulei@kolbox.com";
    private static String account = "hello@kolbox.com";

    // 登录密码
    private static String password = "Fanmeng+435";
    /**
     * 服务器地址
     */
    private static String host = "smtp.exmail.qq.com";
    // 端口
    private static String port = "465";
    // 协议
    private static String protocol = "smtp";

    //初始化参数
    public static Session initProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        // 使用smtp身份验证
        properties.put("mail.smtp.auth", "true");
        // 使用SSL,企业邮箱必需 start
        // 开启安全协议
        MailSSLSocketFactory mailSSLSocketFactory = null;
        try {
            mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        properties.put("mail.smtp.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.socketFactory.port", port);
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, password);
            }
        });
        // 使用SSL,企业邮箱必需 end
        session.setDebug(true);
        return session;
    }

    /**
     * @param email 接收者列表,多个接收者之间用","隔开
     */
    public static void toMail(String email, String username, String code, Integer type) {
        String title;
        if (type == 0) {
            title = "用户注册";
        } else {
            title = "修改密码";
        }
        if(oConvertUtils.isEmpty(username)){
            username = "";
        }
        try {
            Session session = initProperties();
            MimeMessage mimeMessage = new MimeMessage(session);
            // 发件人,可以设置发件人的别名
            mimeMessage.setFrom(new InternetAddress(account, "海玛国际"));
            // 收件人,多人接收
            InternetAddress[] internetAddressTo = InternetAddress.parse(email);
            mimeMessage.setRecipients(Message.RecipientType.TO, internetAddressTo);
            // 主题
            mimeMessage.setSubject(title);
            // 时间
            mimeMessage.setSentDate(new Date());
            // 容器类 附件
            MimeMultipart mimeMultipart = new MimeMultipart();
            // 可以包装文本,图片,附件
            MimeBodyPart bodyPart = new MimeBodyPart();
            // 2.4设置邮件内容
            String content = "<head>\n" +
                    "\t\t<base target=\"_blank\" />\n" +
                    "\t\t<style type=\"text/css\">\n" +
                    "\t\t\t::-webkit-scrollbar {\n" +
                    "\t\t\t\tdisplay: none;\n" +
                    "\t\t\t}\n" +
                    "\t\t</style>\n" +
                    "\t\t<style id=\"cloudAttachStyle\" type=\"text/css\">\n" +
                    "\t\t\t#divNeteaseBigAttach,\n" +
                    "\t\t\t#divNeteaseBigAttach_bak {\n" +
                    "\t\t\t\tdisplay: none;\n" +
                    "\t\t\t}\n" +
                    "\t\t</style>\n" +
                    "\t\t<style id=\"blockquoteStyle\" type=\"text/css\">\n" +
                    "\t\t\tblockquote {\n" +
                    "\t\t\t\tdisplay: none;\n" +
                    "\t\t\t}\n" +
                    "\t\t</style>\n" +
                    "\t\t<style type=\"text/css\">\n" +
                    "\t\t\tbody {\n" +
                    "\t\t\t\tfont-size: 14px;\n" +
                    "\t\t\t\tfont-family: arial, verdana, sans-serif;\n" +
                    "\t\t\t\tline-height: 1.666;\n" +
                    "\t\t\t\tpadding: 0;\n" +
                    "\t\t\t\tmargin: 0;\n" +
                    "\t\t\t\toverflow: auto;\n" +
                    "\t\t\t\twhite-space: normal;\n" +
                    "\t\t\t\tword-wrap: break-word;\n" +
                    "\t\t\t\tmin-height: 100px\n" +
                    "\t\t\t}\n" +
                    "\n" +
                    "\t\t\ttd,\n" +
                    "\t\t\tinput,\n" +
                    "\t\t\tbutton,\n" +
                    "\t\t\tselect,\n" +
                    "\t\t\tbody {\n" +
                    "\t\t\t\tfont-family: Helvetica, 'Microsoft Yahei', verdana\n" +
                    "\t\t\t}\n" +
                    "\n" +
                    "\t\t\tpre {\n" +
                    "\t\t\t\twhite-space: pre-wrap;\n" +
                    "\t\t\t\twhite-space: -moz-pre-wrap;\n" +
                    "\t\t\t\twhite-space: -pre-wrap;\n" +
                    "\t\t\t\twhite-space: -o-pre-wrap;\n" +
                    "\t\t\t\tword-wrap: break-word;\n" +
                    "\t\t\t\twidth: 95%\n" +
                    "\t\t\t}\n" +
                    "\n" +
                    "\t\t\tth,\n" +
                    "\t\t\ttd {\n" +
                    "\t\t\t\tfont-family: arial, verdana, sans-serif;\n" +
                    "\t\t\t\tline-height: 1.666\n" +
                    "\t\t\t}\n" +
                    "\n" +
                    "\t\t\timg {\n" +
                    "\t\t\t\tborder: 0\n" +
                    "\t\t\t}\n" +
                    "\n" +
                    "\t\t\theader,\n" +
                    "\t\t\tfooter,\n" +
                    "\t\t\tsection,\n" +
                    "\t\t\taside,\n" +
                    "\t\t\tarticle,\n" +
                    "\t\t\tnav,\n" +
                    "\t\t\thgroup,\n" +
                    "\t\t\tfigure,\n" +
                    "\t\t\tfigcaption {\n" +
                    "\t\t\t\tdisplay: block\n" +
                    "\t\t\t}\n" +
                    "\n" +
                    "\t\t\tblockquote {\n" +
                    "\t\t\t\tmargin-right: 0px\n" +
                    "\t\t\t}\n" +
                    "\t\t</style>\n" +
                    "\t</head>\n" +
                    "\t<body tabindex=\"0\" role=\"listitem\">\n" +
                    "\t\t<table width=\"700\" border=\"0\" align=\"center\" cellspacing=\"0\" style=\"width:700px;\">\n" +
                    "\t\t\t<tbody>\n" +
                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<td>\n" +
                    "\t\t\t\t\t\t<div style=\"width:700px;margin:0 auto;border-bottom:1px solid #ccc;margin-bottom:30px;\">\n" +
                    "\t\t\t\t\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"700\" height=\"39\" style=\"font:12px Tahoma, Arial, 宋体;\">\n" +
                    "\t\t\t\t\t\t\t\t<tbody>\n" +
                    "\t\t\t\t\t\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t\t\t\t\t\t<td width=\"210\"></td>\n" +
                    "\t\t\t\t\t\t\t\t\t</tr>\n" +
                    "\t\t\t\t\t\t\t\t</tbody>\n" +
                    "\t\t\t\t\t\t\t</table>\n" +
                    "\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t\t<div style=\"width:680px;padding:0 10px;margin:0 auto;\">\n" +
                    "\t\t\t\t\t\t\t<div style=\"line-height:1.5;font-size:14px;margin-bottom:25px;color:#4d4d4d;\">\n" +
                    "\t\t\t\t\t\t\t\t<strong style=\"display:block;margin-bottom:15px;\">亲爱的用户 " + username + "，您好！</strong>\n" +
                    "\t\t\t\t\t\t\t\t<strong style=\"display:block;margin-bottom:15px;\">\n" +
                    "\t\t\t\t\t\t\t\t\t您的验证码是：<span style=\"color:#f60;font-size: 24px\">" + code + "</span>\n" +
                    "\t\t\t\t\t\t\t\t</strong>\n" +
                    "\t\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t\t\t<div style=\"margin-bottom:30px;\">\n" +
                    "\t\t\t\t\t\t\t\t<small style=\"display:block;margin-bottom:20px;font-size:12px;\">\n" +
                    "\t\t\t\t\t\t\t\t\t<p style=\"color:#747474;\">\n" +
                    "\t\t\t\t\t\t\t\t\t\t为了保障您帐号的安全性，请在5分钟内完成验证<br>\n" +
                    "\t\t\t\t\t\t\t\t\t\t（注意：工作人员不会向你索取此验证码，请勿泄漏！)\n" +
                    "\t\t\t\t\t\t\t\t\t</p>\n" +
                    "\t\t\t\t\t\t\t\t</small>\n" +
                    "\t\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t\t<div style=\"width:700px;margin:0 auto;\">\n" +
                    "\t\t\t\t\t\t\t<div style=\"padding:10px 10px 0;border-top:1px solid #ccc;color:#747474;margin-bottom:20px;line-height:1.3em;font-size:12px;\">\n" +
                    "\t\t\t\t\t\t\t\t<p>感谢您使用，祝您使用愉快，谢谢！</p>\n" +
                    "\t\t\t\t\t\t\t\t<p>海玛国际</p>\n" +
                    "\t\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t</td>\n" +
                    "\t\t\t\t</tr>\n" +
                    "\t\t\t</tbody>\n" +
                    "\t\t</table>\n" +
                    "\t</body>";
            // 设置内容
            bodyPart.setContent(content, "text/html; charset=UTF-8");
            // 添加图片&附件
            // bodyPart = new MimeBodyPart();
            // bodyPart.attachFile(fileSrc);
            mimeMultipart.addBodyPart(bodyPart);
            mimeMessage.setContent(mimeMultipart);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void toMailNotify(MessageTemplate messageTemplate, String email) {
        try {
            Session session = initProperties();
            MimeMessage mimeMessage = new MimeMessage(session);
            // 发件人,可以设置发件人的别名
            mimeMessage.setFrom(new InternetAddress(account, messageTemplate.getTemplateTitle()));
            // 收件人,多人接收
            InternetAddress[] internetAddressTo = InternetAddress.parse(email);
            mimeMessage.setRecipients(Message.RecipientType.TO, internetAddressTo);
            // 主题
            mimeMessage.setSubject(messageTemplate.getTemplateTitle());
            // 时间
            mimeMessage.setSentDate(new Date());
            // 容器类 附件
            MimeMultipart mimeMultipart = new MimeMultipart();
            // 可以包装文本,图片,附件
            MimeBodyPart bodyPart = new MimeBodyPart();
            // 2.4设置邮件内容
            String content = "<head>\n" +
                    "    <base target=\"_blank\" />\n" +
                    "    <style type=\"text/css\">::-webkit-scrollbar{ display: none; }</style>\n" +
                    "    <style id=\"cloudAttachStyle\" type=\"text/css\">#divNeteaseBigAttach, #divNeteaseBigAttach_bak{display:none;}</style>\n" +
                    "    <style id=\"blockquoteStyle\" type=\"text/css\">blockquote{display:none;}</style>\n" +
                    "    <style type=\"text/css\">\n" +
                    "        body{font-size:14px;font-family:arial,verdana,sans-serif;line-height:1.666;padding:0;margin:0;overflow:auto;white-space:normal;word-wrap:break-word;min-height:100px}\n" +
                    "        td, input, button, select, body{font-family:Helvetica, 'Microsoft Yahei', verdana}\n" +
                    "        pre {white-space:pre-wrap;white-space:-moz-pre-wrap;white-space:-pre-wrap;white-space:-o-pre-wrap;word-wrap:break-word;width:95%}\n" +
                    "        th,td{font-family:arial,verdana,sans-serif;line-height:1.666}\n" +
                    "        img{ border:0}\n" +
                    "        header,footer,section,aside,article,nav,hgroup,figure,figcaption{display:block}\n" +
                    "        blockquote{margin-right:0px}\n" +
                    "        a:link {text-decoration: none;color: blue}\n" +
                    "        a:active {text-decoration: blink}\n" +
                    "        a:hover {text-decoration: underline;color: red}\n" +
                    "        a:visited {text-decoration: none;color: green}" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body tabindex=\"0\" role=\"listitem\">\n" +
                    "<table width=\"700\" border=\"0\" align=\"center\" cellspacing=\"0\" style=\"width:700px;\">\n" +
                    "    <tbody>\n" +
                    "    <tr>\n" +
                    "        <td>\n" +
                    "            <div style=\"width:700px;margin:0 auto;border-bottom:1px solid #ccc;margin-bottom:30px;\">\n" +
                    "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"700\" height=\"39\" style=\"font:12px Tahoma, Arial, 宋体;\">\n" +
                    "                    <tbody><tr><td width=\"210\"></td></tr></tbody>\n" +
                    "                </table>\n" +
                    "            </div>\n" +
                    "            <div style=\"width:680px;padding:0 10px;margin:0 auto;\">\n" +
                    "                <div style=\"line-height:1.5;font-size:14px;margin-bottom:25px;color:#4d4d4d;\">\n" +
                    "                    <strong style=\"display:block;margin-bottom:15px;\">" + messageTemplate.getTemplateRichContent() +
                    "                    </strong>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "            <div style=\"width:700px;margin:0 auto;\">\n" +
                    "                <div style=\"padding:10px 10px 0;border-top:1px solid #ccc;color:#747474;margin-bottom:20px;line-height:1.3em;font-size:12px;\">\n" +
                    "                    <p>Kind Regards!</p>\n" +
                    "                    <p>Zingdeal</p>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    </tbody>\n" +
                    "</table>\n" +
                    "</body>";
            // 设置内容
            bodyPart.setContent(content, "text/html; charset=UTF-8");
            // 添加图片&附件
            // bodyPart = new MimeBodyPart();
            // bodyPart.attachFile(fileSrc);
            mimeMultipart.addBodyPart(bodyPart);
            mimeMessage.setContent(mimeMultipart);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}