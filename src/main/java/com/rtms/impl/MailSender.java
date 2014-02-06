package com.rtms.impl;

import com.rtms.component.IMailSender;
import com.rtms.core.contract.AbstractConfigManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;


/**
 * User: yanghua
 * Date: 1/31/14
 * Time: 7:19 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class MailSender implements IMailSender {

    private static final Logger logger = Logger.getLogger(MailSender.class);
    private AbstractConfigManager configManager;

    public MailSender(AbstractConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void sendFrom(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("the arg:filePath can not be null or empty");
        }

        logger.debug("sending mail from path: " + filePath);
        MailUtil.send(filePath, (ConfigManager) this.configManager);
    }

    /**
     * inner static util class
     */
    private static class MailUtil {

        /**
         * send mail
         * @param filePath the attachment file's full path
         * @param config mail config file
         */
        public static void send(String filePath, final ConfigManager config) {
            Session session = Session.getInstance(config.mailConfig(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        config.mailConfig().getProperty("mail.userName"),
                        config.mailConfig().getProperty("mail.password")
                    );
                }
            });

            try {
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setSubject("rss for kindle", "UTF-8");
                mimeMessage.setFrom(new InternetAddress(config.mailConfig().getProperty("mail.from")));
                mimeMessage.setReplyTo(new Address[]{new InternetAddress(config.mailConfig().getProperty("mail.from"))});
                mimeMessage.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(
                    config.mailConfig().getProperty("mail.to")
                ));

                MimeMultipart mimeMultipart = new MimeMultipart("mixed");
                MimeBodyPart attch1 = new MimeBodyPart();
                mimeMultipart.addBodyPart(attch1);
                mimeMessage.setContent(mimeMultipart);

                DataSource ds1 = new FileDataSource(filePath);
                DataHandler dataHandler1 = new DataHandler(ds1);
                attch1.setDataHandler(dataHandler1);
                attch1.setFileName(MimeUtility.encodeText(FilenameUtils.getName(filePath)));

                mimeMessage.saveChanges();

                Transport.send(mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

    }
}
