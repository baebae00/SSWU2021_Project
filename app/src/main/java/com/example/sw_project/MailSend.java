package com.example.sw_project;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSend {
    private static String TAG = "email send success";

    public static class MailAuth extends Authenticator {

        PasswordAuthentication pa;

        public MailAuth() {
            String mail_id = "pfull.pro@gmail.com";
            String mail_pw = "roqkfvmfhwprxmdm20!";

            pa = new PasswordAuthentication(mail_id, mail_pw);
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return pa;
        }
    }

    public static void mailSend(String sswuEmail, String certification) {

        Properties prop = System.getProperties();
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "587");

        Authenticator auth = new MailAuth();

        Session session = Session.getDefaultInstance(prop, auth);

        MimeMessage msg = new MimeMessage(session);

        try {
            msg.setSentDate(new Date());
            msg.setFrom(new InternetAddress("pfull.pro@gmail.com", "DEVELOPER"));
            InternetAddress to = new InternetAddress(sswuEmail);
            msg.setRecipient(Message.RecipientType.TO, to);
            msg.setSubject("성공하자 어플 성신여대 학생 인증을 위한 메일입니다.", "UTF-8");

            Log.e("uh","uh");

            //메일 본문
            msg.setText("안녕하세요 성공하자 어플 관리자입니다.\n" +
                    "인증번호는 " +
                    certification + " 입니다.", "UTF-8");

//            msg.setText("인증번호는 ", "UTF-8");
//            msg.setText(certification, "UTF-8");

            Transport.send(msg);
            Log.e(TAG,TAG);

        } catch(AddressException ae) {
            System.out.println("AddressException : " + ae.getMessage());
        } catch(MessagingException me) {
            System.out.println("MessagingException : " + me.getMessage());
        } catch(UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException : " + e.getMessage());
        }

    }

}
