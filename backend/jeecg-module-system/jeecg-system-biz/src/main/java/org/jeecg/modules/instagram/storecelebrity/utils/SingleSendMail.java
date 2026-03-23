package org.jeecg.modules.instagram.storecelebrity.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SingleSendMail {
    private static final String ACCESS_KEY_ID = "xxx";
    private static final String ACCESS_KEY_SECRET = "xxx";
    private static final String ACCOUNT_NAME = "xxx";
    private static final String FROM_ALIAS = "xx";

    public static boolean send(String toAddress, String emailTitle, String emailRichContent, String emailTextContent) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("Action", "SingleSendMail");
            params.put("Version", "2015-11-23");
            params.put("RegionId", "cn-hangzhou");
            params.put("AccountName", ACCOUNT_NAME);
            params.put("AddressType", "1");
            params.put("ReplyToAddress", "false");
            params.put("ToAddress", toAddress);
            params.put("Subject", emailTitle);
            params.put("HtmlBody", emailRichContent);
            params.put("TextBody", emailTextContent);
            params.put("FromAlias", FROM_ALIAS);
            params.put("ReplyAddress", "");
            params.put("ReplyAddressAlias", "");
            params.put("ClickTrace", "1");

            // 添加公共参数
            params.put("AccessKeyId", ACCESS_KEY_ID);
            params.put("Format", "JSON");
            params.put("SignatureMethod", "HMAC-SHA1");
            params.put("SignatureVersion", "1.0");
            params.put("SignatureNonce", UUID.randomUUID().toString());
            params.put("Timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .format(new Date().toInstant().atZone(ZoneOffset.UTC)));

            // 生成签名
            String signature = generateSignature(params, "POST", ACCESS_KEY_SECRET);
            params.put("Signature", signature);

            // 构建请求体（application/x-www-form-urlencoded）
            StringBuilder body = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (body.length() > 0) body.append("&");
                body.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://dm.aliyuncs.com/"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response: " + response.body());
            // 简单判断：只要不是 5xx 且包含 "Message": "OK" 即认为成功
            return response.statusCode() == 200 && response.body().contains("\"Message\":\"OK\"");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String generateSignature(Map<String, String> params, String method, String secret) throws Exception {
        // 1. 对参数按键排序
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        // 2. 构造待签名字符串
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            query.append("&")
                    .append(percentEncode(key))
                    .append("=")
                    .append(percentEncode(params.get(key)));
        }
        String stringToSign = method + "&%2F&" + percentEncode(query.toString().substring(1));

        // 3. HMAC-SHA1 签名
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec((secret + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }

    private static String percentEncode(String value) {
        if (value == null) return null;
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*public static boolean send(String toAddress, String emailTitle, String emailRichContent, String emailTextContent) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "xx", "xx");
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        request.setRegionId("cn-hangzhou");
        request.setAccountName("yulei@algmail.aimgmail.com");
        request.setAddressType(1);
        request.setReplyToAddress(false);
        request.setToAddress(toAddress);
        request.setSubject(emailTitle);
        request.setHtmlBody(emailRichContent);
        request.setTextBody(emailTextContent);
        request.setFromAlias("yulei");
        request.setReplyAddress("");
        request.setReplyAddressAlias("");
        request.setClickTrace("1");
        try {
            SingleSendMailResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
            return true;
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
        return false;
    }*/

    public static boolean smtpSend(String from, String toAddress, String emailTitle, String emailRichContent, String fromAliUser,String fromname) {
        // 配置发送邮件的环境属性
        final Properties props = new Properties();
        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtpdm.aliyun.com");
        props.put("mail.smtp.port", 80);
        // 发件人的账号
        props.put("mail.user", fromAliUser);
        // 访问SMTP服务时需要提供的密码(在控制台选择发信地址进行设置)
        props.put("mail.password", "FANmeng123456");
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession) {
        };
        try {
            InternetAddress fromAli = new InternetAddress(fromAliUser, fromname, "UTF-8");
            message.setFrom(fromAli);
            //设置回信地址
            Address[] a = new Address[1];
            a[0] = new InternetAddress(from);
            message.setReplyTo(a);
            // 设置收件人邮件地址
            InternetAddress to = new InternetAddress(toAddress);
            message.setRecipient(MimeMessage.RecipientType.TO, to);
            // 设置邮件标题
            message.setSubject(emailTitle);
            // 设置邮件的内容体
            message.setContent(emailRichContent, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            String err = e.getMessage();
            // 在这里处理message内容， 格式是固定的
            System.out.println(err);
        }
        return false;
    }
}

