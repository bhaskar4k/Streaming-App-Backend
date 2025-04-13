package com.app.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.app.authentication")
public class AuthenticationApplication {
	static String data = "";
	public static void main(String[] args) {
		data = "MKC";
		SpringApplication.run(AuthenticationApplication.class, args);
	}

	public static String temp(){
		return data;
	}
}
