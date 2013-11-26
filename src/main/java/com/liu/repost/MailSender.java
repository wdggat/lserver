package com.liu.repost;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.PasswordAuthentication;

import org.apache.log4j.Logger;

public class MailSender {
    private static Logger logger = Logger.getLogger(MailSender.class);

    private static String sendFrom = "monitort@163.com";
    private static String passwd = "neteasemonitort";

    private static Properties getSmtpSettings() {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", "smtp.163.com");
        properties.setProperty("mail.smtp.port", "25");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.auth", "true");
        return properties;
    }

    public static boolean sendFile(Set<String> recipients, String subject,
                                   String path, String fileName) {
        try{
    	    MimeMessage message = getMimeMessage(recipients);
    	    
    	    message.setSubject(subject);
            BodyPart messagePart = new MimeBodyPart();
            messagePart.setText("\n抓取记录请查看附件。");

            BodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(path);
            attachmentPart.setDataHandler(new DataHandler(source));
            // Chinese file name need to be encoded
            attachmentPart.setFileName(MimeUtility.encodeText(fileName));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messagePart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);

            Transport.send(message);
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return false;
        }
    }

	public static void sendFilesInOneMail(Set<String> recipients,
			List<File> files, String subject) {
		if (files == null || files.size() == 0) {
			logger.warn("No file specified");
			return;
		}

		if (recipients.size() == 0) {
			logger.warn("No mail recipient specified");
			return;
		}

		if (send(recipients, files, subject))
			logger.info("Sending " + getFileNames(files) + " to "
					+ recipients.toString() + " did succeed");
		else
			logger.error("Sending " + getFileNames(files) + " to "
					+ recipients.toString() + " did not succeed");
	}

	private static boolean send(Set<String> recipients, List<File> files,
			String subject) {
		try {
			MimeMessage message = getMimeMessage(recipients);
			message.setSubject(subject);

			BodyPart messagePart = new MimeBodyPart();
			messagePart.setText("\n抓取记录请查看附件。");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messagePart);
			for (File file : files) {
				BodyPart attachmentPart = new MimeBodyPart();
				DataSource source = new FileDataSource(file);
				attachmentPart.setDataHandler(new DataHandler(source));
				attachmentPart.setFileName(MimeUtility.encodeText(file
						.getName()));
				multipart.addBodyPart(attachmentPart);
			}
			message.setContent(multipart);

			Transport.send(message);
			return true;
		} catch (AddressException e) {
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}
    
    private static String getFileNames(List<File> files)
    {
    	String filesname = "";
    	for(File file : files)
    	{
    		if (!file.exists()) {
                logger.warn("File doesn't exsit: " + file.getPath());
            }
    		else
    		{
    			filesname += file.getName() + ", ";
    		}
    	}
    	filesname = filesname.substring(0, filesname.length() - 2);
    	return filesname;
    }
    
	private static MimeMessage getMimeMessage(Set<String> recipients)
			throws AddressException, MessagingException {
		Session session = Session.getDefaultInstance(getSmtpSettings(),
				new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(sendFrom, passwd);
					}
				});

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sendFrom));
		for (String sendTo : recipients)
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					sendTo));
		return message;
	}

    public static void sendFile(File file, Set<String> recipients) {
        if (file == null) {
            logger.warn("No file specified");
            return;
        }

        if (!file.exists()) {
            logger.warn("File doesn't exsit: " + file.getPath());
            return;
        }

        if (recipients.size() == 0) {
            logger.warn("No mail recipient specified");
            return;
        }

        if (sendFile(recipients, file.getName(), file.getPath(),
                file.getName()))
            logger.info("Sending " + file.getParent() + " to "
                    + recipients.toString()
                    + " did succeed");
        else
            logger.error("Sending " + file.getParent() + " to "
                    + recipients.toString()
                    + " did not succeed");
    }

    public static void sendFiles(List<File> files,
                               Set<String> recipients) {
        for (File file : files) {
            sendFile(file, recipients);
        }
    }
}
