package org.gatlin.jumpre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"org.gatlin"}, exclude = MongoAutoConfiguration.class)
public class SpringBoot {

	public static void main(String[] args) {
		System.setProperty("spring.config.location", "classpath:/conf/spring.properties");
		SpringApplication.run(SpringBoot.class, args);
	}
}
