package com.founder.util.mail.controller;

import com.founder.util.mail.bean.SendMailBean;
import com.founder.util.mail.rabbitmq.Sender;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by li.yao on 2017/12/14.
 */
@Controller
public class TestMailController {
    @RequestMapping(value = "/testmail", method = RequestMethod.GET)
    public String  testmail(){
        return "testmail";
    }

    @Autowired
    private Sender sender;

    @RequestMapping(value = "/sendmailmq", method = RequestMethod.GET)
    public void sendmailMQ(){
        try {
            SendMailBean mailBean = new SendMailBean();
            mailBean.setTo("li.yao@founder.com.cn");
            mailBean.setSubject("test rabbitmq");
            mailBean.setBody("this is a body");
            mailBean.setCc("li.yao@founder.com.cn");
            mailBean.setFrom("test");
            sender.sendmail(mailBean);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void testpost(){
        try {
            formpost();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void formpost()throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            //要上传的文件路径
            String filePath = "pom.xml";
            //把一个普通参数和文件上传给下面这个地址
            HttpPost httpPost = new HttpPost("http://172.19.58.205:9025/V1/sendmail");
            //把文件转换成流对象
            FileBody bin = new FileBody(new File(filePath));
            //普通字段，重新设置了编码方式
            StringBody from = new StringBody("from", ContentType.create("text/plain", Consts.UTF_8));
            StringBody to = new StringBody("li.yao@founder.com.cn", ContentType.create("text/plain", Consts.UTF_8));
            StringBody cc = new StringBody("cc", ContentType.create("text/plain", Consts.UTF_8));
            StringBody subject = new StringBody("subject", ContentType.create("text/plain", Consts.UTF_8));
            StringBody body = new StringBody("body", ContentType.create("text/plain", Consts.UTF_8));

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("files", bin)
                    .addPart("from" ,from)
                    .addPart("to", to)
                    .addPart("subject", subject)
                    .addPart("body", body)
                    .build();
            httpPost.setEntity(reqEntity);
            //发起请求并返回请求响应
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                //打印响应状态
                System.out.println(response.getStatusLine());
                //获取相应对象
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null){
                    //打印响应长度
                    System.out.println("response content length:" + resEntity.getContentLength());
                    //打印相应内容
                    System.out.println(EntityUtils.toString(resEntity, Charset.forName("UTF-8")));
                }
                //销毁
                EntityUtils.consume(resEntity);
            }finally {
                response.close();
            }
        }finally {
            httpClient.close();
        }
    }
}
