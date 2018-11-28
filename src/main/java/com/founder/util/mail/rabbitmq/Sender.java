package com.founder.util.mail.rabbitmq;

import com.founder.util.mail.bean.SendMailBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by li.yao on 2017/12/26.
 */
@Component
public class Sender {

    Gson gson = new GsonBuilder().create();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendmail(SendMailBean sendMailBean){
        String json = gson.toJson(sendMailBean);
        rabbitTemplate.convertAndSend("mailQueue", json);
    }
}
