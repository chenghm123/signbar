package com.accelerator.signbar.util;


import com.accelerator.signbar.Constants;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MailUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailUtils.class);

    private static final Lock COOKIE_EXPIRED_LOCK = new ReentrantLock();

    private static Configuration freeMarkerConfiguration;

    private static JavaMailSender javaMailSender;

    private static String sendFrom;

    private static String sendTo;

    public static void sendCookieExpired() {
        COOKIE_EXPIRED_LOCK.lock();
        try {
            if (Constants.COOKIE_EXPIRED.compareAndSet(false, true)) {
                Template template = freeMarkerConfiguration.getTemplate("CookieExpired.ftl");
                String subject = "百度COOKIE过期提醒！";
                Map<String, Object> model = Maps.newHashMapWithExpectedSize(1);
                model.put("title", subject);
                String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, null);
                MimeMessage mimeMessage = getMimeMessage(subject, html);
                javaMailSender.send(mimeMessage);
            }
        } catch (IOException e) {
            LOGGER.error("获取COOKIE过期模板异常！", e);
        } catch (TemplateException e) {
            LOGGER.error("解析COOKIE过期模板异常！", e);
        } catch (MessagingException e) {
            LOGGER.error("设置发送COOKIE过期提醒邮件的属性异常！", e);
        } finally {
            COOKIE_EXPIRED_LOCK.unlock();
        }
    }

    public static void sendSignbarStatus(Map<String, Boolean> signInfo) {
        try {
            Template template = freeMarkerConfiguration.getTemplate("SignbarFailure.ftl");
            Map<String, Object> model = Maps.newHashMapWithExpectedSize(2);
            String subject = "百度贴吧签到报告！";
            model.put("title", subject);
            model.put("signInfo", signInfo);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            MimeMessage mimeMessage = getMimeMessage(subject, html);
            javaMailSender.send(mimeMessage);
        } catch (IOException e) {
            LOGGER.error("获取签到失败模板异常！", e);
        } catch (TemplateException e) {
            LOGGER.error("解析签到失败模板异常！", e);
        } catch (MessagingException e) {
            LOGGER.error("设置发送签到失败提醒邮件的属性异常！", e);
        }
    }

    private static MimeMessage getMimeMessage(String subject, String html) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(sendFrom);
        mimeMessageHelper.setTo(sendTo);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(html, true);
        mimeMessageHelper.setSentDate(new Date());
        return mimeMessage;
    }

    public void setFreeMarkerConfiguration(Configuration freeMarkerConfiguration) {
        MailUtils.freeMarkerConfiguration = freeMarkerConfiguration;
    }

    public void setJavaMailSender(JavaMailSender javaMailSender) {
        MailUtils.javaMailSender = javaMailSender;
    }

    public void setSendFrom(String sendFrom) {
        MailUtils.sendFrom = sendFrom;
    }

    public void setSendTo(String sendTo) {
        MailUtils.sendTo = sendTo;
    }
}
