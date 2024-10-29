package tn.esprit.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
@EnableScheduling
public class RegistrationManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrationManagementApplication.class, args);
	}

}
