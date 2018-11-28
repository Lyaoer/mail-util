package com.founder.util.mail;

import org.springframework.amqp.core.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MailUtilApplication {
	/**
	 * 启动
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(MailUtilApplication.class, args);
	}

	@Bean
	public Queue mailQueue(){
		return new Queue("mailQueue");
	}

	@Bean
	public Queue redmineQueue(){
		return new Queue("redmineQueue");
	}

}
