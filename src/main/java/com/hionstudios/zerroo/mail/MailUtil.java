package com.hionstudios.zerroo.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailUtil {
    private static final Logger LOGGER = Logger.getLogger(MailUtil.class.getName());
    private static final String USERNAME = "no-reply@zerroo.in";
    private static final String PASSWORD = "SpntdXzjLBWp";

    private static JavaMailSenderImpl getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.zoho.in");
        mailSender.setPort(587);
        mailSender.setUsername(USERNAME);
        mailSender.setPassword(PASSWORD);
        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }

    public static void sendMailAsync(MailSenderFrom from, String to, String subject, String html, boolean isHtml) {
        new Thread(() -> sendMail(from, to, subject, html, isHtml)).start();
    }

    public static void sendMail(MailSenderFrom from, String to, String subject, String html, boolean isHtml) {
        sendMail(from, to, subject, html, isHtml, null);
    }

    public static void sendMail(
            MailSenderFrom from, String to, String subject, String html, boolean isHtml, String reply) {
        try {
            JavaMailSenderImpl sender = getMailSender();
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(html, isHtml);
            helper.setTo(to);
            helper.setSubject(subject);
            String name = from.getName();
            if (name != null) {
                helper.setFrom(from.getEmail(), from.getName());
            } else {
                helper.setFrom(from.getEmail());
            }
            if (reply != null) {
                helper.setReplyTo(reply);
            }
            sender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static void resetPassword(String email, String password) {
        String html = "<link href=\"https://fonts.googleapis.com/css2?family=Jost\" rel=\"stylesheet\" type=\"text/css\"><div style=\"margin:0;background-color:#f2f3f8\"><table cellspacing=\"0\" border=\"0\" cellpadding=\"0\" width=\"100%%\" bgcolor=\"#f2f3f8\" style=\"font-family:Jost,sans-serif\"><tr><td><table style=\"background-color:#f2f3f8;max-width:670px;margin:0 auto\" width=\"100%%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"><tr><td style=\"height:80px\">&nbsp;</td></tr><tr><td style=\"text-align:center\"><a href=\"https://www.zerroo.in\" title=\"logo\" target=\"_blank\"><img src=\"https://www.zerroo.in/zerroo.png\" title=\"logo\" alt=\"logo\" width=\"170\"></a></td></tr><tr><td style=\"height:20px\">&nbsp;</td></tr><tr><td><table width=\"95%%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:670px;background:#fff;border-radius:3px;text-align:center;-webkit-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);-moz-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);box-shadow:0 6px 18px 0 rgba(0,0,0,.06)\"><tr><td style=\"height:40px\">&nbsp;</td></tr><tr><td style=\"padding:0 35px\"><h1 style=\"color:#1e1e2d;font-weight:500;margin:0;font-size:32px;font-family:Jost,sans-serif\">You have requested your password</h1><span style=\"display:inline-block;vertical-align:middle;margin:29px 0 26px;border-bottom:1px solid #cecece;width:100px\"></span><p style=\"color:#455056;font-size:15px;line-height:24px;margin:0\">Your Password is: %s</p></td></tr><tr><td style=\"height:40px\">&nbsp;</td></tr></table></td><tr><td style=\"height:20px\">&nbsp;</td></tr><tr><td style=\"text-align:center\"><p style=\"font-size:14px;color:rgba(69,80,86,0.7411764705882353);line-height:18px;margin:0\">&copy; <strong>Zerroo</strong></p></td></tr><tr><td style=\"height:80px\">&nbsp;</td></tr></table></td></tr></table></div>";

        sendMailAsync(MailSenderFrom.noReply(),
                email,
                "Zerroo Password",
                String.format(html, password),
                true);
    }
}