package org.jeecg.modules.instagram.storecelebrity.utils;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


public class SendCloudSmtp2 {

    private static final String SENDCLOUD_SMTP_HOST = "smtp.sendcloud.net";
    private static final int SENDCLOUD_SMTP_PORT = 587;

    private static String getMessage(String reply) {
        System.out.println(reply);
        String[] arr = reply.split("#");

        String messageId = null;

        if (arr[0].equalsIgnoreCase("250 ")) {
            messageId = arr[1];
        }

        return messageId;
    }

    public static boolean send(String apiUser, String apiKey, String from, String to, String fromname, String emailTitle, String richContent) throws MessagingException,
            UnsupportedEncodingException {
        //configure javamail
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SENDCLOUD_SMTP_HOST);
        props.put("mail.smtp.port", SENDCLOUD_SMTP_PORT);
        props.setProperty("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", 180);
        props.put("mail.smtp.timeout", 600);
        props.setProperty("mail.mime.encodefilename", "true");

        Session mailSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(apiUser, apiKey);
            }
        });

        SMTPTransport transport = (SMTPTransport) mailSession.getTransport("smtp");

        MimeMessage message = new MimeMessage(mailSession);
        // The from, use the correct email address instead
        message.setFrom(new InternetAddress(from, "KOLBOX", "UTF-8"));
        // The recipient address,use the correct email address instead
        message.addRecipient(RecipientType.TO, new InternetAddress(to));
        // Mail theme
        message.setSubject(emailTitle, "UTF-8");

        // Set up email reply
//        message.setReplyTo(new Address[] { new InternetAddress(replyTo) });

        // Set xsmtpapi
//        System.out.println(xsmtpapi.toString());
//        message.setHeader("X-SMTPAPI", new String(Base64.encodeBase64(xsmtpapi.toString().getBytes())));

        Multipart multipart = new MimeMultipart("alternative");

        // Add HTML message body
        String html = "<html><head></head><body>" + richContent + "</body></html> ";
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setHeader("Content-Type", "text/html;charset=UTF-8");
        contentPart.setHeader("Content-Transfer-Encoding", "base64");
        contentPart.setContent(html, "text/html;charset=UTF-8");
        multipart.addBodyPart(contentPart);

        message.setContent(multipart);

        // Connect to sendcloud server and send mail
        transport.connect();
        transport.sendMessage(message, message.getRecipients(RecipientType.TO));
        System.out.println(transport.getLastReturnCode());
        String messageId = getMessage(transport.getLastServerResponse());
        String emailId = messageId + "0$" + to;
        System.out.println("messageId:" + messageId);
        System.out.println("emailId:" + emailId);
        transport.close();
        return true;
    }

}

