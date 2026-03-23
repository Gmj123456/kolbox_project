package org.jeecg.modules.instagram.utils;

import com.sun.mail.smtp.SMTPTransport;
import org.jeecg.common.util.oConvertUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class SendEmailTLS {

    public static boolean pushEmail(String account, String password, String email, String title, String content) {
        System.err.println(account);
        System.err.println(password);
        System.err.println(title);
        System.err.println(content);
        Properties prop = new Properties();
        //设置代理------begin--------
        String proxy = getProxy();
        if (oConvertUtils.isNotEmpty(proxy)) {
            System.out.println("设置代理 ："+proxy);
            String[] split = proxy.split(":");
            prop.setProperty("http.proxyHost", split[0]);
            prop.setProperty("http.proxyPort", split[1]);
        }
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        Session session = Session.getInstance(prop,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(account, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(account));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject(title);
            message.setText(content);
            Transport.send(message);
            System.err.println("发送成功！");
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 功能描述: 发送html邮件（APP密码）
     *
     * @param account     发送邮箱
     * @param password    APP密码
     * @param email       接收邮箱
     * @param title       标题
     * @param richContent 富文本内容
     * @return boolean
     * @Author: nqr
     * @Date 2020-11-11 08:18:37
     */
    public static boolean pushRichEmail(String account, String password, String email, String title, String richContent) {
        System.err.println(account);
        System.err.println(password);
        System.err.println(title);
        System.err.println(richContent);
        Properties prop = new Properties();

        //设置代理------begin--------
        String proxy = getProxy();
        if (oConvertUtils.isNotEmpty(proxy)) {
            System.out.println("设置代理 ："+proxy);
            String[] split = proxy.split(":");
            prop.setProperty("http.proxyHost", split[0]);
            prop.setProperty("http.proxyPort", split[1]);
        }
        //设置代理--------end--------

        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(prop,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(account, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            //发件人
            message.setFrom(new InternetAddress(account));
            //收件人
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            //邮件主题
            message.setSubject(title);
            //邮件内容，混合模式
            MimeMultipart msgMultipart = new MimeMultipart("mixed");
            message.setContent(msgMultipart);
            //设置消息正文
            MimeBodyPart text = new MimeBodyPart();
            msgMultipart.addBodyPart(text);
            //设置正文格式
            MimeMultipart bodyMultipart = new MimeMultipart("related");
            text.setContent(bodyMultipart);
            //设置正文内容
            MimeBodyPart htmlPart = new MimeBodyPart();
            bodyMultipart.addBodyPart(htmlPart);
            htmlPart.setContent("<html>\n" +
                    "\t<head>\n" +
                    "\t\t<metacharset=\"utf-8\">\n" +
                    "\t\t\t<title></title>\n" +
                    "\t</head>\n" +
                    "\t<body>\n" +
                    richContent +
                    "\t</body>\n" +
                    "</html>", "text/html;charset=UTF-8");
            Transport.send(message);
            System.err.println("发送成功！");
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getProxy() {
        String[] proxyArr = {
                "167.160.178.9:3128",
                "163.198.35.141:3128",
                "192.126.139.229:3128",
                "163.198.35.146:3128",
                "167.160.178.242:3128",
                "192.126.142.253:3128",
                "163.198.35.103:3128",
                "192.126.139.214:3128",
                "163.198.35.142:3128",
                "192.126.142.83:3128"
        };
        int index = (int) (Math.random() * proxyArr.length);
        return proxyArr[index];
    }

}