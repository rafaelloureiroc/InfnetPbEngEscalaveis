package com.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@SpringBootApplication
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@EnableDiscoveryClient
public class NotificationsForInfnetPbApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationsForInfnetPbApplication.class, args);
	}

}
