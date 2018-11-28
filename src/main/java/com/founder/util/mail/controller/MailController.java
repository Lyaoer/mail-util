package com.founder.util.mail.controller;

import com.founder.ark.common.utils.bean.ResponseObject;
import com.founder.util.mail.bean.SendMailBean;
import com.founder.util.mail.util.MailUtils;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@RestController
public class MailController {
	
	@Value("${mail.sendNamePass}")
    private String sendNamePass;

    @Value("${mail.sendPassword}")
    private String sendPassword;

    private String result;

	@RequestMapping(value = "/sendmail", method = RequestMethod.POST)
    public String sendmail(SendMailBean mailBean){
        try {
            MailUtils utils = new MailUtils("mail02.hold.founder.com");
            utils.setNamePass(sendNamePass, sendPassword);
            utils.setSubject(mailBean.getSubject());
            utils.setBody(mailBean.getBody());
            utils.setFrom(mailBean.getFrom());
            utils.setTo(mailBean.getTo());
            utils.setCopyTo(mailBean.getCc());

            List<MultipartFile> files= mailBean.getFiles();
            List<File> localFiles = new ArrayList<>();
            localFiles = utils.setFiles(files);

            Boolean resultBoolean= utils.sendout();
            if (resultBoolean){
                result = "sendSuccess";
                for (File localFile : localFiles){
                    localFile.delete();
                }
            }else
                result = "sendDefeat";
        } catch (Exception e) {
            e.printStackTrace();
            result = "sendDefeat";
        }
        return result;

    }


    /**
     * 版本一：可以接收json数据，参数详情见http://fzkb.founder.com.cn/kb/projects/mail/wiki/MailDoc
     * 返回数据为ResponseObject格式
     * @param mailBean
     * @return
     */
    @RequestMapping(value = "/V1/sendmail", method = RequestMethod.POST)
    public ResponseObject<?> sendmail_v1(SendMailBean mailBean){
        try {
            MailUtils utils = new MailUtils("mail02.hold.founder.com");
            utils.setNamePass(sendNamePass, sendPassword);
            utils.setSubject(mailBean.getSubject());
            utils.setBody(mailBean.getBody());
            utils.setFrom(mailBean.getFrom());
            utils.setTo(mailBean.getTo());
            utils.setCopyTo(mailBean.getCc());

            List<MultipartFile> files= mailBean.getFiles();
            List<File> localFiles = new ArrayList<>();
            localFiles = utils.setFiles(files);

            Boolean resultBoolean= utils.sendout();
            if (resultBoolean){
                for (File localFile : localFiles){
                    localFile.delete();
                }
            }else{
                return ResponseObject.newErrorResponseObject(205404,"sendDefeat");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseObject.newErrorResponseObject(205404,e.toString());
        }
        return ResponseObject.newSuccessResponseObject("sendSuccess");
    }

}
