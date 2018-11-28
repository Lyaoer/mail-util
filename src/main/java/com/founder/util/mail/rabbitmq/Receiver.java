package com.founder.util.mail.rabbitmq;

import com.founder.util.mail.bean.SendMailBean;
import com.founder.util.mail.util.MailUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by li.yao on 2017/12/26.
 */
@Component
public class Receiver {

    @Value("${mail.sendNamePass}")
    private String sendNamePass;

    @Value("${mail.sendPassword}")
    private String sendPassword;

    Gson gson = new GsonBuilder().create();

    @RabbitListener(queues = "mailQueue")
    public void process(String json) {
        SendMailBean sendMailBean = gson.fromJson(json, new TypeToken<SendMailBean>() {
        }.getType());

        MailUtils utils = new MailUtils("mail02.hold.founder.com");
        utils.setNamePass(sendNamePass, sendPassword);
        utils.setSubject(sendMailBean.getSubject());
        utils.setBody(sendMailBean.getBody());
        utils.setFrom(sendMailBean.getFrom());
        utils.setTo(sendMailBean.getTo());
        utils.setCopyTo(sendMailBean.getCc());
        utils.sendout();
    }

    @RabbitListener(queues = "redmineQueue")
    public void myQueue(String content) {
        System.out.println("####################");
        System.out.println("接收到的信息是" + content);

        SendMailBean sendMailBean = gson.fromJson(content.toString(), new TypeToken<SendMailBean>() {
        }.getType());

        MailUtils utils = new MailUtils("mail02.hold.founder.com");
        utils.setNamePass(sendNamePass, sendPassword);
        utils.setSubject(sendMailBean.getSubject());
        utils.setBody(sendMailBean.getBody());
        utils.setFrom(sendMailBean.getFrom());
        if(sendMailBean.getTo().isEmpty())
            utils.setTo("null");
        else
            utils.setTo(sendMailBean.getTo());
        utils.setCopyTo(sendMailBean.getCc());
        utils.sendout();
    }

}
