package com.haruhiism.bbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SimpleBbsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleBbsApplication.class, args);
	}

}
