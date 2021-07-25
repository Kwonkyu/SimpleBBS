package com.haruhiism.bbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SimpleBbsApplication {
	 // -Dspring.profiles.active=publish on VM option to enable property.
	public static void main(String[] args) {
		SpringApplication.run(SimpleBbsApplication.class, args);
	}

}
