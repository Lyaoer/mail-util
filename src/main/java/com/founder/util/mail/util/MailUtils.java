package com.founder.util.mail.util;

import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by li.yao on 2017/12/5.
 */
public class MailUtils {

    private MimeMessage mimeMsg; // MIME邮件对象

    private Session session; // 邮件会话对象

    private Properties props; // 系统属性

    private boolean needAuth = false; // smtp是否需要认证

    private String username; // smtp认证用户名和密码

    private String password;

    private Multipart mp; // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成

    private String log;

    public MailUtils() {

    }

    public MailUtils(String smtp) {
        setSmtpHost(smtp);
        createMimeMessage();
    }

    public void setSmtpHost(String hostName) {
        System.out.println("设置系统属性：mail.smtp.host = " + hostName);
        if (props == null)
            props = System.getProperties(); // 获得系统属性对象
        props.put("mail.smtp.host", hostName); // 设置SMTP主机
        props.put("mail.smtp.localhost", "localHostAdress");
    }

    public boolean createMimeMessage() {
        try {
            session = Session.getDefaultInstance(props, null); // 获得邮件会话对象
        } catch (Exception e) {
            log = "获取邮件会话对象时发生错误！" + e.toString();
            System.err.println(log);
            return false;
        }
        try {
            mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
            mp = new MimeMultipart(); // mp 一个multipart对象
            // Multipart is a container that holds multiple body parts.
            return true;
        } catch (Exception e) {
            log = "创建MIME邮件对象失败！" + e;
            System.err.println(log);
            return false;
        }
    }

    public void setNeedAuth(boolean need) {
        System.out.println("设置smtp身份认证：mail.smtp.auth = " + need);
        if (props == null)
            props = System.getProperties();
        if (need) {
            props.put("mail.smtp.auth", "true");
        } else {
            props.put("mail.smtp.auth", "false");
        }
    }

    public void setNamePass(String name, String pass) {
        username = name;
        password = pass;
    }

    public boolean setSubject(String mailSubject) {
        try {
            mimeMsg.setSubject(MimeUtility.encodeText(mailSubject, "utf-8", "B"));
            return true;
        } catch (Exception e) {
            log = "设置邮件主题发生错误！" + e;
            System.err.println(log);
            return false;
        }
    }

    public boolean setBody(String mailBody) {
        try {
            BodyPart bp = new MimeBodyPart();
            // 转换成中文格式
            bp.setContent(mailBody, "text/html;charset=utf-8");
            mp.addBodyPart(bp);
            return true;
        } catch (Exception e) {
            log = "设置邮件正文时发生错误！" + e;
            System.err.println(log);
            return false;
        }
    }

    public List<File> setFiles(List<MultipartFile> multipartfiles ) {
        List<File> localFiles= new ArrayList<>();
        BufferedOutputStream bufferedOutputStream = null;
        try {
            for (MultipartFile multipartFile : multipartfiles ){
                if (multipartFile != null && multipartFile.getSize() > 0){
                    String fileName = multipartFile.getOriginalFilename();
                    File file = new File(fileName);
                    byte[] bypes = multipartFile.getBytes();
                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                    bufferedOutputStream.write(bypes);
//                    bufferedOutputStream.close();

                    MimeBodyPart mbp = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(fileName);  //得到数据源
                    mbp.setDataHandler(new DataHandler(fileDataSource)); //得到附件本身并至入BodyPart
                    mbp.setFileName(MimeUtility.encodeText(fileName,"utf-8", "B"));  //得到文件名同样至入BodyPart
                    mp.addBodyPart(mbp);
                    localFiles.add(file);
                }
            }
            return localFiles;
        } catch (Exception e1) {
            log = "增加附件时出错：" + e1;
            e1.printStackTrace();
            return localFiles;
        } finally {
            if (bufferedOutputStream != null){
                try {
                    bufferedOutputStream.close();
                }catch (IOException e3){
                    e3.printStackTrace();
                }
            }

        }
    }

    public boolean addFile(String path, String name) {
        try {
            MimeBodyPart mbp = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(path); //得到数据源
            mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart
            mbp.setFileName(MimeUtility.encodeText(name, "utf-8", "B"));
            mp.addBodyPart(mbp);
            return true;
        } catch (Exception e) {
            log = "增加附件时出错：" + e;
            e.printStackTrace();
            return false;
        }
    }

    public boolean setFrom(String from) {
        try {
            mimeMsg.setFrom(new InternetAddress(from)); // 设置发信人
            return true;
        } catch (Exception e) {
            log = "设置发信人出错:" + e;
            return false;
        }
    }

    public boolean setTo(String to) {
        if (to == null)
            return false;
        try {
            mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean setCopyTo(String copyto) {
        if (copyto == null)
            return false;
        try {
            mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(copyto));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean sendout() {
        try {
            mimeMsg.setContent(mp);
            mimeMsg.saveChanges();
            Session mailSession = Session.getInstance(props, null);
            Transport transport = mailSession.getTransport("smtp");
            transport.connect((String) props.get("mail.smtp.host"), username,
                    password);
            transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
            if(mimeMsg.getRecipients(Message.RecipientType.CC) != null){
                transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.CC));
            }
            System.out.println("发送邮件成功！");
            transport.close();
            return true;
        } catch (Exception e) {
            log = "邮件发送失败！" + e;
            System.err.println(log);
            return false;
        }
    }

    public String getLog() {
        return log;
    }

}